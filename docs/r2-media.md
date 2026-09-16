# Cloudflare R2 media

Set these environment variables before starting the backend:

```bash
R2_ENABLED=true
R2_ACCOUNT_ID=50b40b39cb4aa8ab3252e4a598ef812c
R2_ACCESS_KEY_ID=<r2-access-key>
R2_SECRET_ACCESS_KEY=<r2-secret-key>
R2_BUCKET=global-bucket
R2_PUBLIC_BASE_URL=https://pub-5ac40ccea3fd44ebab05452a4efbd198.r2.dev
```

`R2_PUBLIC_BASE_URL` must point to a public R2 custom domain (or an R2 public URL). The backend never exposes the access key to the browser.

After the backend is running, sign in as an admin and call `POST /api/v1/admin/media/images/migrate-existing` once. The endpoint downloads existing external product images, uploads them to R2, and replaces their database URLs. It is safe to call again; R2 URLs are skipped.
