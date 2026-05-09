# Niral Patel — Personal Portfolio

> Full-stack personal site I designed, built, and deployed end-to-end.
> Java / Spring Boot REST API · Angular 19 SPA · PostgreSQL on Supabase · Render + Vercel.

**🌐 Live site:** [niral-portfolio-five.vercel.app](https://niral-portfolio-five.vercel.app)

---

## About me

Java Full Stack Developer based in Barrie, Ontario — three years building secure REST APIs, microservices, and data-driven web apps with Spring Boot, Angular / React, and AWS. Open to mid-level full-stack roles in Canada (remote or Ontario-based).

## What this site has

- **About / Skills / Experience** — my background and the technologies I work with day-to-day.
- **Projects** — selected work with tech stack and links to repos or live demos.
- **Blog** — short write-ups on things I've built or learned.
- **Contact** — send me a message directly, or book a 15-minute chat through the integrated calendar.
- **Resume** — downloadable PDF.

## Tech stack

| Layer | Built with |
|-------|------------|
| Frontend | Angular 19, TypeScript, SCSS |
| Backend  | Spring Boot 3, Spring Security + JWT, Spring Data JPA, Hibernate |
| Database | PostgreSQL (Supabase) |
| Hosting  | Render (backend), Vercel (frontend) |
| Tooling  | Docker, GitHub Actions, Maven |

## Engineering highlights

A few decisions that show how I think about real-world software:

- **Cross-database codebase** — same backend runs on Postgres or MySQL by switching a Spring profile, using portable Hibernate type codes for BLOB / CLOB columns.
- **Stateless JWT authentication** — custom filter for the `/admin` area, secret + token TTL configurable via environment variables.
- **Pooler-aware JDBC** — disabled server-side prepared statements so the backend works correctly behind Supabase's transaction-mode connection pooler.
- **Wildcard CORS for preview deploys** — every Vercel preview URL hits the API without any redeploy.
- **Zero-cost production deploy** — runs on entirely free tiers (Render + Vercel + Supabase) and stays warm via a 5-minute uptime ping.

