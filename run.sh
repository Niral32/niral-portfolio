#!/usr/bin/env bash
# Starts the backend (Spring Boot) and frontend (Angular) together.
# Press Ctrl+C once to stop both.

set -e

GREEN="\033[0;32m"; YELLOW="\033[1;33m"; CYAN="\033[0;36m"; NC="\033[0m"
say()  { printf "${GREEN}==>${NC} %s\n" "$*"; }
warn() { printf "${YELLOW}==>${NC} %s\n" "$*"; }

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_ROOT"

# Auto-load .env if present (so MAIL_PASSWORD etc. reach Spring Boot).
# See SETUP-EMAIL.md for the variables this expects.
if [[ -f ".env" ]]; then
  say "Loading environment variables from .env"
  set -a; source .env; set +a
fi

# Make sure MySQL is running
if ! mysqladmin ping -u root -proot --silent >/dev/null 2>&1; then
  warn "MySQL doesn't seem to be running — starting it..."
  brew services start mysql >/dev/null 2>&1 || true
  sleep 3
fi

# Check that the database exists
if ! mysql -u root -proot -e "USE portfolio_db" >/dev/null 2>&1; then
  warn "Database 'portfolio_db' not found. Run ./setup.sh first."
  exit 1
fi

# Free ports if anything is hanging on to them from a previous run
free_port() {
  local port="$1"
  local pids
  pids=$(lsof -ti tcp:"$port" 2>/dev/null || true)
  if [[ -n "$pids" ]]; then
    warn "Port $port is in use (PIDs: $pids) — killing leftover processes."
    kill -9 $pids 2>/dev/null || true
    sleep 1
  fi
}
free_port 8080
free_port 4200

# Logs go here
mkdir -p .logs
BACKEND_LOG="$PROJECT_ROOT/.logs/backend.log"
FRONTEND_LOG="$PROJECT_ROOT/.logs/frontend.log"

# Start backend -------------------------------------------------------------
say "Starting backend on http://localhost:8080  (logs: $BACKEND_LOG)"
(
  cd backend
  mvn -q spring-boot:run -Dspring-boot.run.profiles=mysql
) > "$BACKEND_LOG" 2>&1 &
BACKEND_PID=$!

# Start frontend ------------------------------------------------------------
say "Starting frontend on http://localhost:4200 (logs: $FRONTEND_LOG)"
(
  cd frontend
  npm start
) > "$FRONTEND_LOG" 2>&1 &
FRONTEND_PID=$!

# Cleanup on Ctrl+C ---------------------------------------------------------
cleanup() {
  echo ""
  warn "Stopping servers..."
  # Kill the wrapper subshells
  kill "$BACKEND_PID" "$FRONTEND_PID" 2>/dev/null || true
  pkill -P "$BACKEND_PID"  2>/dev/null || true
  pkill -P "$FRONTEND_PID" 2>/dev/null || true
  # Aggressively free both ports — Maven/npm spawn child processes that
  # often outlive their parent shell, so target by listening port.
  for port in 8080 4200; do
    pids=$(lsof -ti tcp:$port 2>/dev/null || true)
    [[ -n "$pids" ]] && kill -9 $pids 2>/dev/null || true
  done
  say "Stopped."
  exit 0
}
trap cleanup INT TERM

# Wait for both to be reachable, then print friendly banner ----------------
wait_for_url() {
  local url="$1" name="$2" tries=60
  while (( tries-- > 0 )); do
    if curl -fsS -o /dev/null --max-time 2 "$url"; then return 0; fi
    sleep 2
  done
  warn "$name didn't respond in time — check its log file."
  return 1
}

(
  wait_for_url "http://localhost:8080/api/projects" "Backend" || true
  wait_for_url "http://localhost:4200"              "Frontend" || true
  echo ""
  printf "${CYAN}========================================${NC}\n"
  printf "${CYAN}  Portfolio is running!${NC}\n"
  printf "${CYAN}  Open: http://localhost:4200${NC}\n"
  printf "${CYAN}  API:  http://localhost:8080${NC}\n"
  printf "${CYAN}  Stop: press Ctrl+C in this window${NC}\n"
  printf "${CYAN}========================================${NC}\n"
) &

# Block here until either child exits or user hits Ctrl+C.
# Polling loop instead of `wait -n` because macOS ships bash 3.2 (no -n flag).
while kill -0 "$BACKEND_PID" 2>/dev/null && kill -0 "$FRONTEND_PID" 2>/dev/null; do
  sleep 2
done

warn "One of the servers exited on its own — check the logs in .logs/."
cleanup
