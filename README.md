# Niral Patel — Personal Portfolio (Angular + Spring Boot)

Full-stack portfolio site: **Angular** frontend, **Spring Boot 3** backend, **PostgreSQL** or **MySQL**, REST APIs, optional **JWT admin** login, and a **contact form** persisted to SQL.

## Repository layout

```
niral-portfolio/
├── README.md                 ← You are here
├── .gitignore
├── docs/
│   └── API.md                ← REST API reference
├── sql/
│   ├── schema.sql            ← PostgreSQL DDL
│   ├── schema-mysql.sql      ← MySQL DDL
│   ├── sample-data.sql       ← PostgreSQL seed data
│   └── sample-data-mysql.sql ← MySQL seed data
├── backend/                  ← Spring Boot (Java 17+)
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/niralpatel/portfolio/
│       │   ├── PortfolioApplication.java
│       │   ├── config/          (Security, CORS, JWT properties, beans)
│       │   ├── controller/      (REST controllers)
│       │   ├── dto/
│       │   ├── entity/
│       │   ├── exception/       (GlobalExceptionHandler)
│       │   ├── repository/
│       │   ├── security/        (JWT service + filter)
│       │   └── service/
│       └── resources/
│           └── application.yml
└── frontend/                 ← Angular 19 app
    ├── angular.json
    ├── package.json
    ├── public/
    │   ├── favicon.ico
    │   └── Niral_Patel_Resume.pdf   ← Replace with your real resume PDF
    └── src/
        ├── environments/
        ├── app/
        │   ├── app.config.ts
        │   ├── app.routes.ts
        │   ├── models/
        │   ├── services/
        │   └── components/    (navbar, home, about, skills, projects, experience, resume, contact, footer)
        ├── index.html
        └── styles.scss
```

## Prerequisites

- **Java 17+** and **Maven 3.9+**
- **Node.js 20+** and **npm**
- **PostgreSQL 14+** (default) or **MySQL 8+**

## Step-by-step: run locally

### 1. Database (PostgreSQL)

Create a database:

```bash
createdb portfolio_db
```

Apply schema and seed data:

```bash
psql -d portfolio_db -f sql/schema.sql
psql -d portfolio_db -f sql/sample-data.sql
```

Edit `backend/src/main/resources/application.yml` if your username, password, or database name differ.

For **first-time only** with Hibernate `ddl-auto: update`, you may instead start the backend once against an empty database, then run `sample-data.sql` to insert rows (avoid running seed twice or you will duplicate rows unless you truncate first).

### 2. Backend

```bash
cd backend
mvn spring-boot:run
```

API base URL: `http://localhost:8080`

Default **admin** credentials (change in `application.yml` before any public deployment):

- Username: `admin`
- Password: `changeme`

JWT login: `POST /api/auth/login` (see `docs/API.md`).

### 3. Frontend

Development uses `src/environments/environment.development.ts` (via `angular.json` `fileReplacements`) so the app calls `http://localhost:8080`.

```bash
cd frontend
npm install
npm start
```

Open `http://localhost:4200`.

### 4. MySQL instead of PostgreSQL

1. Create database `portfolio_db` and run `sql/schema-mysql.sql` + `sql/sample-data-mysql.sql`.
2. Start the backend with the MySQL profile:

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

Ensure `application.yml` MySQL block credentials match your server.

### 5. Production build (frontend)

Update `frontend/src/environments/environment.ts` with your real **API base URL** (no trailing slash), then:

```bash
cd frontend
npm run build
```

Static output: `frontend/dist/portfolio-frontend/browser/`.

## API documentation

See [docs/API.md](docs/API.md) for endpoints, payloads, and error format.

Summary:

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/projects` | List projects |
| GET | `/api/skills` | List skills |
| GET | `/api/experience` | List experience |
| POST | `/api/contact` | Save contact message |
| POST | `/api/auth/login` | Optional admin JWT |
| GET | `/api/admin/ping` | Example protected route |

## Deployment suggestions

### Backend

- **Container:** Multi-stage Docker build (Maven build → JRE 17 slim image), pass datasource URL and credentials as **environment variables** or Spring Cloud Config; never commit secrets.
- **Managed databases:** AWS RDS, Azure Database for PostgreSQL/MySQL, or Neon/Supabase (Postgres). Set `ddl-auto` to `validate` or use **Flyway/Liquibase** for migrations in production.
- **HTTPS:** Terminate TLS at a load balancer (ALB, Nginx, Caddy) and enforce secure cookies if you add browser sessions later.
- **Secrets:** Rotate `jwt.secret` and `admin.password`; use at least a 256-bit random secret for HS256.

### Frontend

- **Static hosting:** AWS S3 + CloudFront, Azure Static Web Apps, Netlify, Vercel, or GitHub Pages.
- **Same-origin API:** Put Angular static files and `/api` reverse-proxy to Spring Boot on one domain to simplify CORS (update `portfolio.cors.allowed-origins` to your domain).
- **Environment:** Keep `environment.ts` production `apiUrl` aligned with your deployed API (or use a build-time `--configuration` with `fileReplacements`).

### Full stack on one VPS

- Nginx serves `dist/.../browser` and proxies `/api` to `localhost:8080`.
- Run Spring Boot as a **systemd** service or behind **Docker Compose** alongside Postgres.

## Customization checklist

- [ ] Replace `frontend/public/Niral_Patel_Resume.pdf` with your real resume.
- [ ] Adjust copy on **Home**, **About**, and **Resume** if you want different wording than the prompt defaults.
- [ ] Update seed SQL or edit rows in the database for projects, skills, and experience.
- [ ] Set production `apiUrl` and CORS origins.
- [ ] Change JWT secret and admin password.

## License

Private portfolio project — use and modify freely for your own site.
