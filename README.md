# AssetLedger — Asset Management System

A full-stack app for tracking company assets (furniture, vehicles, equipment), who they're
assigned to, and their maintenance history.

- **Backend:** Java 17, Spring Boot 3, Spring Security (JWT), Spring Data JPA, PostgreSQL
- **Frontend:** Vue 3, TypeScript, Vite, Pinia, Vue Router, Axios

## Features

- **Assets** — full CRUD, asset tags, category, location, manufacturer/serial, purchase cost
  and current value, warranty tracking, search + filter by category/status.
- **Assignment** — assign/unassign assets to employees, with a full assignment history log.
- **Maintenance** — log preventive/corrective/inspection/calibration work per asset, track
  scheduled vs. completed dates and cost, and see an org-wide maintenance queue.
- **Categories & Employees** — manage the lookup data assets are organized around.
- **Dashboard** — asset counts by status, total fleet value, assets by category, and what
  maintenance is coming up in the next 30 days.
- **Auth** — JWT login/registration with three roles: `ADMIN`, `MANAGER`, `USER`.
  - `USER` — read-only.
  - `MANAGER` — can create/edit assets, categories, employees, and maintenance records.
  - `ADMIN` — everything `MANAGER` can do, plus delete.

## Project layout

```
asset-management/
├── backend/    Spring Boot API (Maven project)
└── frontend/   Vue 3 + TypeScript SPA (Vite project)
```

## Running the backend

Requirements: Java 17+, Maven, PostgreSQL 14+.

1. Create a database:
   ```sql
   CREATE DATABASE asset_management;
   ```
2. Set connection details via environment variables (or edit
   `backend/src/main/resources/application.yml` directly):
   ```
   DB_USERNAME=postgres
   DB_PASSWORD=postgres
   JWT_SECRET=some-long-random-string-at-least-32-characters
   CORS_ORIGINS=http://localhost:5173
   ```
3. Run it:
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   The API starts on `http://localhost:8080`. Swagger UI is available at
   `http://localhost:8080/swagger-ui.html`.

On first startup, a default admin user is seeded automatically:

```
username: admin
password: admin123
```

**Change this password (or the user) before using this anywhere but your own machine.**

## Running the frontend

Requirements: Node.js 18+.

```bash
cd frontend
npm install
npm run dev
```

The app runs on `http://localhost:5173` and talks to the API at the URL configured in
`frontend/.env` (`VITE_API_BASE_URL`, defaults to `http://localhost:8080/api`).

To build for production:

```bash
npm run build
```

Output goes to `frontend/dist`, ready to serve from any static host (or behind the same
reverse proxy as the API).

## API overview

| Method | Path                          | Description                          |
|--------|-------------------------------|---------------------------------------|
| POST   | `/api/auth/login`              | Log in, returns a JWT                 |
| POST   | `/api/auth/register`           | Register a new (USER-role) account    |
| GET    | `/api/assets`                  | Search/filter/paginate assets         |
| POST   | `/api/assets`                  | Create an asset                       |
| PUT    | `/api/assets/{id}`             | Update an asset                       |
| DELETE | `/api/assets/{id}`             | Delete an asset (admin only)          |
| POST   | `/api/assets/{id}/assign`      | Assign an asset to an employee        |
| POST   | `/api/assets/{id}/unassign`    | Return an asset to the available pool |
| PATCH  | `/api/assets/{id}/status`      | Change asset status directly          |
| GET/POST/PUT/DELETE | `/api/categories`, `/api/employees` | Lookup data CRUD          |
| GET/POST/PUT/DELETE | `/api/maintenance`     | Maintenance records CRUD              |
| GET    | `/api/dashboard/stats`         | Aggregate stats for the dashboard      |

All `/api/**` routes except `/api/auth/**` require a `Bearer` token from login.

## Notes & next steps
These are going to  elevate my program to be more functional and realistic.

- Email verification / password reset flow
- File attachments (photos, invoices, warranty PDFs) per asset  for asset verification
- CSV import/export for bulk asset loads
- Audit log of who changed what (a nice change log with it's own table )
- Automated tests (use wiremock )
- Depreciation schedules :probably run monthly asset depreciation. scheduler implementation ...maybe use a cron job for 
- auto depreciation and allow user  to depreciate too
