// Generates src/environments/environment.ts at build time so the Angular
// production bundle hits the right backend URL on Vercel.
//
// Set the env var PROD_API_URL in the Vercel dashboard (Settings → Environment Variables)
// to your Render backend, e.g. https://portfolio-backend-xxxx.onrender.com
//
// If PROD_API_URL is not set, we leave the existing file alone so local
// `ng build` still works without surprises.

const fs = require('fs');
const path = require('path');

const apiUrl = process.env.PROD_API_URL;
if (!apiUrl) {
  console.log('[write-env] PROD_API_URL not set; leaving environment.ts unchanged.');
  process.exit(0);
}

const envFile = path.join(__dirname, '..', 'src', 'environments', 'environment.ts');
const contents =
  '// Auto-generated at build time by scripts/write-env.js — do not edit by hand.\n' +
  'export const environment = {\n' +
  '  production: true,\n' +
  `  apiUrl: '${apiUrl.replace(/'/g, "\\'")}'\n` +
  '};\n';

fs.writeFileSync(envFile, contents, 'utf8');
console.log(`[write-env] Wrote production apiUrl: ${apiUrl}`);
