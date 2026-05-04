# Deploy your portfolio to a public URL (free)

By the end of this you'll have:

- **Frontend** at something like `https://niral-portfolio.vercel.app`
- **Backend API** at something like `https://niral-portfolio-backend.onrender.com`
- **PostgreSQL database** managed by Supabase
- Auto-deploys: every time you push to GitHub, both sites update themselves

Total time: ~45 minutes the first time. All free, no credit card required.

> **The free trade-off:** Render's free backend goes to sleep after 15 minutes of inactivity. The first visitor after that waits ~30 seconds for it to wake up. Vercel and Supabase don't sleep — only the backend.

---

## Step 1 — Put your code on GitHub

1. **Create a GitHub account** if you don't have one already, at https://github.com/signup. Use the username `Niral32` if it's still available (it matches your resume).

2. **Install GitHub Desktop** (easiest GUI way) from https://desktop.github.com/. Sign in with your new account.

3. **Add your project to GitHub Desktop:**
   - File → Add Local Repository → choose `Documents/Personal INFO/niral-portfolio` → click **Add Repository**.
   - It will say "this directory does not appear to be a Git repository". Click **create a repository**.
   - Name: `niral-portfolio`. Description: `Personal portfolio — Angular + Spring Boot + PostgreSQL`. Click **Create Repository**.

4. **Publish to GitHub:**
   - Click the **Publish repository** button at the top.
   - Untick "Keep this code private" if you want the repo public (recommended — recruiters look at GitHub).
   - Click **Publish Repository**.

Your code is now at `https://github.com/<your-username>/niral-portfolio`. Keep this tab open — you'll need the URL.

---

## Step 2 — Free PostgreSQL database (Supabase)

1. Go to https://supabase.com/ and click **Start your project**. Sign up with GitHub.
2. Click **New project**.
   - Organization: pick your default one
   - Name: `niral-portfolio`
   - Database Password: **generate a strong one and copy it somewhere safe** (you'll paste it into Render in Step 3)
   - Region: pick the closest to you (e.g. `East US` if you're in Ontario)
   - Click **Create new project**. Wait ~2 minutes for it to provision.

3. **Find your connection string:**
   - In the left sidebar, click **Project Settings** (gear icon) → **Database**.
   - Scroll to **Connection string** → click the **URI** tab.
   - You'll see something like `postgresql://postgres.xxxxxxx:[YOUR-PASSWORD]@aws-0-us-east-1.pooler.supabase.com:5432/postgres`.
   - Replace `[YOUR-PASSWORD]` with the password you saved. Copy the full string.

4. **Load the schema and seed data:**
   - In the Supabase sidebar, click **SQL Editor** → **New query**.
   - Open `sql/schema.sql` from your project, copy everything, paste into the SQL Editor, click **Run**. You should see "Success".
   - Open `sql/sample-data.sql`, do the same — paste, click **Run**.
   - Click **Table Editor** in the sidebar to verify: `skills` should have 47 rows, `work_experience` 3 rows.

---

## Step 3 — Backend on Render

1. Go to https://render.com/ and click **Get Started** → **GitHub**.
2. After signing in, click **New +** → **Blueprint**.
3. **Connect your repo:** click **Connect a repository**, find `niral-portfolio`, click **Connect**.
4. Render reads `render.yaml` and shows a service called **portfolio-backend**. Click **Apply**.
5. **Set the secret env vars** Render asks for. For each one, click "Add" and paste:

   | Variable | Value |
   |---|---|
   | `DB_URL` | `jdbc:postgresql://aws-0-...pooler.supabase.com:5432/postgres` (your Supabase connection but **prefix it with `jdbc:`** and **drop the username & password** — see note below) |
   | `DB_USERNAME` | `postgres.xxxxxxx` (the part before `:` in the Supabase URI) |
   | `DB_PASSWORD` | the password you saved in Step 2 |
   | `ADMIN_PASSWORD` | something only you know (replaces `niral2000`) |
   | `MAIL_PASSWORD` | your Gmail app password from `SETUP-EMAIL.md` |
   | `CORS_ALLOWED_ORIGINS` | leave empty for now — we'll fill it in Step 4 once we have the Vercel URL |

   > **Building DB_URL from the Supabase URI:** Supabase gives you `postgresql://USER:PASS@HOST:5432/postgres`. Render needs the URL split:
   > - `DB_URL` = `jdbc:postgresql://HOST:5432/postgres` (everything after the `@`, prefixed with `jdbc:`)
   > - `DB_USERNAME` = `USER` (the part before `:`)
   > - `DB_PASSWORD` = `PASS` (the part between `:` and `@`)

6. Click **Apply**. Render will start building (takes ~5–8 minutes the first time — it's compiling Java in the cloud).
7. When the status turns green, copy the URL at the top — something like `https://portfolio-backend-xxxx.onrender.com`. **Test it:** open `https://YOUR-URL.onrender.com/api/projects` in a browser — you should see `[]` (an empty JSON array). That means the backend is live and talking to the database.

---

## Step 4 — Frontend on Vercel

1. Go to https://vercel.com/signup and sign up with GitHub.
2. After signing in, click **Add New** → **Project**.
3. Find `niral-portfolio` → click **Import**.
4. **Configure:**
   - Framework Preset: leave as **Other**
   - Build Command: leave the default (Vercel reads `vercel.json`)
   - Root Directory: **leave as the project root** (don't set to `frontend` — `vercel.json` handles the path)
5. **Add the environment variable:**
   - Expand **Environment Variables**.
   - Name: `PROD_API_URL`
   - Value: the Render URL from Step 3, e.g. `https://portfolio-backend-xxxx.onrender.com`
   - Click **Add**.
6. Click **Deploy**. Takes ~2 minutes.
7. When it's done, click **Visit** at the top — your site is live!

---

## Step 5 — Connect Vercel back to Render (CORS)

Right now if someone opens your Vercel site, the browser will block it from talking to Render because of CORS. Fix:

1. **Copy your Vercel URL** — both the production one (`https://niral-portfolio.vercel.app`) and the preview one (anything ending in `.vercel.app`).
2. **Back in Render**, go to your `portfolio-backend` service → **Environment** tab.
3. Find `CORS_ALLOWED_ORIGINS` (or click **Add Environment Variable** if it's not there) and set the value to your Vercel URLs separated by commas:
   ```
   https://niral-portfolio.vercel.app,https://niral-portfolio-git-main-niral.vercel.app
   ```
4. Click **Save Changes** — Render auto-restarts the backend.

Test: open your Vercel URL → go to the **Skills** page. You should see all 47 skills loading from Render → Supabase.

---

## You're done!

Share your Vercel URL — that's your portfolio. Add it to your resume, LinkedIn, email signature.

### Future updates

- **Edit code on your Mac → push to GitHub** → both Vercel and Render redeploy automatically. With GitHub Desktop: make changes, type a summary, click **Commit to main**, click **Push origin**.
- **Add a new project** to the Projects page: edit `sql/sample-data.sql`, push to GitHub, then re-run that SQL block in the Supabase SQL Editor.
- **Custom domain** (optional, ~$12/year): buy a domain at Namecheap or Porkbun, then in Vercel → your project → Settings → Domains → Add. Vercel walks you through the DNS setup.

### Troubleshooting

| Problem | Fix |
|---|---|
| Render build fails with "out of memory" | Free tier has 512MB. Add `-XX:MaxRAMPercentage=75` to the Dockerfile JAVA_OPTS, or wait for the build retry. |
| Vercel build fails: "PROD_API_URL not set" | You forgot Step 4.5 — add the env var, then **Redeploy** from the Deployments tab. |
| Site loads but Skills/Experience are empty | Backend is sleeping. Wait 30 sec and refresh — the backend wakes up on first request. |
| Contact form succeeds but no email | Check Render logs (service → **Logs** tab) for `MailException`. Usually means `MAIL_PASSWORD` is wrong or empty. |
| Admin login returns 401 | The admin password you set in Render is different from what you're typing. Reset it in Render → Environment. |

If you're stuck on a step, tell me which step number and what you're seeing.
