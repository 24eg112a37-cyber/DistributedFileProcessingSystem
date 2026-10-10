# Distributed File Processing System with Worker Health Monitoring

Java 21 + Spring Boot + PostgreSQL + React/Vite. This starter follows the supplied abstract:
users upload files, jobs are queued, workers poll for work, report results and send heartbeats,
and the dashboard displays job and worker status.

## Project layout
- `backend/` — Spring Boot coordinator REST API
- `worker/` — Java worker process that polls coordinator APIs and sends heartbeats
- `frontend/` — React/Vite portal
- `database/schema.sql` — reference schema
- `docker-compose.yml` — local PostgreSQL

## Requirements
- JDK 21
- Maven 3.9+
- Node.js 20+
- Docker Desktop (recommended for PostgreSQL), or local PostgreSQL

## 1. Start PostgreSQL
From the project root:
```bash
docker compose up -d postgres
```
Database: `file_processing`, user: `appuser`, password: `app_password`, port: `5432`.

## 2. Run the backend in IntelliJ
Open `backend/pom.xml` as a Maven project. Run `com.example.dfps.DfpsApplication`.
Default local settings are in `backend/src/main/resources/application.properties`.
API base: `http://localhost:8080/api`.

For local development, `ddl-auto=update` creates/updates tables automatically.
For a real deployment, set environment variables rather than committing credentials.

## 3. Run the worker
In a separate terminal:
```bash
cd worker
mvn spring-boot:run
```
The worker registers itself, sends heartbeats and polls for jobs. Run multiple worker instances
with different `WORKER_NAME` values to demonstrate a worker fleet.

## 4. Run the frontend in VS Code
```bash
cd frontend
npm install
npm run dev
```
Open the local URL printed by Vite. The frontend uses `VITE_API_URL` or defaults to
`http://localhost:8080/api`.

## Demo credentials
Register a user from the Register page. The first account is a normal user.
For admin functionality, set `ADMIN_EMAIL` to an email address and register that address;
the backend marks that account as ADMIN. Do not use a public/shared email for this.

## Deployment
### PostgreSQL
Create a hosted PostgreSQL database and copy its connection values. Keep credentials private.

### Render backend
Create a Web Service from the repository, set Root Directory to `backend`, choose Docker,
and use `backend/Dockerfile`. Configure:
- `SPRING_DATASOURCE_URL=jdbc:postgresql://HOST:5432/DATABASE?sslmode=require`
- `SPRING_DATASOURCE_USERNAME=...`
- `SPRING_DATASOURCE_PASSWORD=...`
- `APP_CORS_ALLOWED_ORIGINS=https://YOUR-APP.vercel.app`
- `ADMIN_EMAIL=your-admin-email`
- `APP_UPLOAD_DIR=/var/data/uploads` if you attach a persistent disk at `/var/data`

For file persistence, attach a Render persistent disk mounted at `/var/data`, or change the
storage implementation to object storage (recommended for production). Ephemeral container
storage may be lost on redeploy/restart.

### Vercel frontend
Import the `frontend` directory as the project root. Build command `npm run build`,
output directory `dist`. Set `VITE_API_URL=https://YOUR-BACKEND.onrender.com/api`.
`frontend/vercel.json` configures SPA routing.

### Worker deployment note
The worker is a separate long-running process. Deploy it as a separate Docker service or run
it on a machine/container that can reach the coordinator URL. Set `COORDINATOR_URL` to the
backend base URL ending in `/api`. Do not rely on serverless request handlers for a permanent
worker process.

## Important implementation note
This is a runnable academic MVP. Worker dispatch uses coordinator REST polling to keep local
setup straightforward. The supplied abstract specifies gRPC/Protocol Buffers; `worker/src/main/proto/worker.proto`
documents the intended gRPC contract, but the MVP's polling transport is REST and is not a
production gRPC implementation. To meet a strict gRPC evaluation, implement the gRPC transport
and generated stubs before final submission.
