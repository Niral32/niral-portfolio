#!/usr/bin/env bash
# One-time setup for the Niral Patel portfolio project on macOS + MySQL.
# Re-running this script is safe — every step is idempotent.

set -e

# Pretty output -------------------------------------------------------------
GREEN="\033[0;32m"; YELLOW="\033[1;33m"; RED="\033[0;31m"; NC="\033[0m"
say()  { printf "${GREEN}==>${NC} %s\n" "$*"; }
warn() { printf "${YELLOW}==>${NC} %s\n" "$*"; }
fail() { printf "${RED}!! ${NC} %s\n" "$*" >&2; exit 1; }

# Sanity ---------------------------------------------------------------------
if [[ "$(uname)" != "Darwin" ]]; then
  fail "This script is for macOS. Use the README for other systems."
fi

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_ROOT"

# 1. Homebrew ---------------------------------------------------------------
if ! command -v brew >/dev/null 2>&1; then
  say "Installing Homebrew (this needs your Mac password)..."
  /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
  # Add brew to PATH for this script (Apple Silicon vs Intel)
  if [[ -x /opt/homebrew/bin/brew ]]; then eval "$(/opt/homebrew/bin/brew shellenv)";
  elif [[ -x /usr/local/bin/brew ]]; then eval "$(/usr/local/bin/brew shellenv)"; fi
else
  say "Homebrew already installed."
fi

# 2. Tools -------------------------------------------------------------------
install_if_missing() {
  local cmd="$1" pkg="$2"
  if command -v "$cmd" >/dev/null 2>&1; then
    say "$cmd already installed."
  else
    say "Installing $pkg..."
    brew install "$pkg"
  fi
}

install_if_missing java   openjdk@17
install_if_missing mvn    maven
install_if_missing node   node
install_if_missing mysql  mysql

# Make Java visible system-wide (only if not already linked)
JDK_LINK="/Library/Java/JavaVirtualMachines/openjdk-17.jdk"
if [[ ! -L "$JDK_LINK" && ! -d "$JDK_LINK" ]]; then
  say "Linking Java 17 system-wide (may ask for your Mac password)..."
  sudo ln -sfn "$(brew --prefix)/opt/openjdk@17/libexec/openjdk.jdk" "$JDK_LINK" || \
    warn "Couldn't link Java system-wide. Build may still work via brew's Java."
fi

# 3. MySQL -------------------------------------------------------------------
say "Starting MySQL service..."
brew services start mysql >/dev/null 2>&1 || true
sleep 2  # give it a moment to come up

# Try common connection options to detect current root password
MYSQL_PW=""
if mysql -u root -e "SELECT 1" >/dev/null 2>&1; then
  MYSQL_PW=""
  say "Connected to MySQL with empty root password."
elif mysql -u root -proot -e "SELECT 1" >/dev/null 2>&1; then
  MYSQL_PW="root"
  say "Connected to MySQL with root password 'root'."
else
  warn "Couldn't connect as root automatically."
  read -r -s -p "Enter your current MySQL root password (or press Enter for blank): " MYSQL_PW
  echo ""
  if ! mysql -u root ${MYSQL_PW:+-p"$MYSQL_PW"} -e "SELECT 1" >/dev/null 2>&1; then
    fail "Still can't connect. Run 'mysql_secure_installation' manually, then re-run this script."
  fi
fi

# Set root password to "root" (the value the backend's mysql profile uses)
if [[ "$MYSQL_PW" != "root" ]]; then
  say "Setting MySQL root password to 'root' (matches application.yml)..."
  mysql -u root ${MYSQL_PW:+-p"$MYSQL_PW"} -e "ALTER USER 'root'@'localhost' IDENTIFIED BY 'root';"
fi

# Create the database, load schema and seed data ----------------------------
say "Creating database 'portfolio_db' if it doesn't exist..."
mysql -u root -proot -e "CREATE DATABASE IF NOT EXISTS portfolio_db CHARACTER SET utf8mb4;"

# Detect whether seed has already been loaded
ROW_COUNT=$(mysql -u root -proot -N -B portfolio_db \
  -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='portfolio_db';" 2>/dev/null || echo 0)

if [[ "$ROW_COUNT" -eq 0 ]]; then
  say "Loading schema (sql/schema-mysql.sql)..."
  mysql -u root -proot portfolio_db < sql/schema-mysql.sql
  say "Loading sample data (sql/sample-data-mysql.sql)..."
  mysql -u root -proot portfolio_db < sql/sample-data-mysql.sql
else
  warn "Database already has tables — skipping schema/seed load to avoid duplicates."
  warn "If you want a fresh DB, run:  mysql -u root -proot -e 'DROP DATABASE portfolio_db;'  then re-run this script."
fi

# 4. Frontend dependencies --------------------------------------------------
if [[ ! -d "frontend/node_modules" ]]; then
  say "Installing frontend npm packages (this takes a couple minutes)..."
  (cd frontend && npm install)
else
  say "Frontend node_modules already present."
fi

# Done -----------------------------------------------------------------------
echo ""
say "Setup complete!"
echo "Next step:  ./run.sh"
