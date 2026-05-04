# Email Notifications Setup

When someone submits the contact form, the backend will email you at `niralpatel988@gmail.com` with the message — name, email, subject, body, and a Reply-To header so hitting "Reply" in your inbox sends straight back to the sender.

You only need to do this once.

## Step 1 — Turn on 2-Step Verification on your Google account

App passwords only work when 2-Step Verification is on. If you already have it on, skip to Step 2.

1. Open https://myaccount.google.com/security
2. Under **"How you sign in to Google"**, click **2-Step Verification**.
3. Follow the prompts (phone number for codes, etc.).

## Step 2 — Generate an App Password

1. Open https://myaccount.google.com/apppasswords (you'll be asked to sign in again).
2. In **"App name"**, type something like `Portfolio Backend`.
3. Click **Create**.
4. Google shows a 16-character password like `abcd efgh ijkl mnop`. **Copy it.** You won't be able to see it again. The spaces don't matter — Google ignores them.

This password is **not** your Google account password. It only lets the app send mail through your account, and you can revoke it any time from the same page.

## Step 3 — Tell the backend about it

You have two options. Pick one.

### Option A — Set a `.env` file (recommended for local dev)

Create a file called `.env` in the project root (it's already in `.gitignore` so it won't be committed):

```bash
cd "/Users/niralpatel/Documents/Personal INFO/niral-portfolio"
cat > .env <<'EOF'
MAIL_PASSWORD=abcdefghijklmnop
MAIL_USERNAME=niralpatel988@gmail.com
NOTIFY_EMAIL_TO=niralpatel988@gmail.com
NOTIFY_EMAIL_FROM=niralpatel988@gmail.com
EOF
```

Replace `abcdefghijklmnop` with your real app password (no spaces).

Then update `run.sh` to load it — or just export the variables before running:

```bash
set -a; source .env; set +a
./run.sh
```

### Option B — Export it inline (one-off)

```bash
MAIL_PASSWORD='abcdefghijklmnop' ./run.sh
```

## Step 4 — Test it

1. Make sure the backend is running.
2. Open the contact form on http://localhost:4200/contact and submit a test message.
3. Check `niralpatel988@gmail.com` — you should get an email with subject `[Portfolio] <whatever subject you typed>` within a few seconds.
4. If nothing arrives, look at `.logs/backend.log` for lines containing `EmailNotificationService` or `MailException`. Common causes:
   - Wrong app password (double-check, no spaces)
   - 2-Step Verification not enabled
   - Firewall blocking port 587

## Step 5 — Use the admin dashboard (optional)

Even without email, you can view all messages at http://localhost:4200/admin

- Sign in with the username/password from `application.yml` (default `admin` / `changeme` — **change these in production!**)
- Click a message to expand and read it
- Mark messages as read/unread, delete spam, or click "Reply via email" to open your mail client pre-addressed to the sender

## Production deployment

When you deploy:

1. **Never commit your app password.** Use your hosting provider's secret/env-var system (Heroku Config Vars, AWS Parameter Store, Render env vars, etc.) to set `MAIL_PASSWORD`.
2. **Change the admin password** by setting `ADMIN_PASSWORD` env var (or editing `application.yml` and redeploying).
3. **Rotate the JWT secret** by setting `JWT_SECRET` to at least 256 bits of random data. Generate one with:
   ```bash
   openssl rand -base64 32
   ```
