# MediaVault

MediaVault is a full-stack media collection and curation application.

## Stack

- Java 17+
- Spring Boot
- Spring JDBC
- MySQL
- React
- React Router
- Vite

## Features

- Public and private media items
- User-owned item creation, editing, and deletion
- Public and private collections
- Add and remove items from collections
- Public library browsing
- Multi-parameter item and collection search
- Partial-match search using SQL `LIKE`
- Item and collection sorting
- Role-based administrator access
- Registration and JWT authentication

## Database

Run:

`database/create-database.sql`

The backend supports environment variables:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`

If they are not supplied, it defaults to:

- URL: `jdbc:mysql://localhost:3306/flexpath_final`
- Username: `root`
- Password: `your-password`

Set `DB_PASSWORD` to the local MySQL root password before running the backend.

## Backend

From `backend`:

`mvn spring-boot:run`

The API runs on:

`http://localhost:8080`

## Frontend

From `frontend`:

`npm install`

`npm run dev`

The React app runs on:

`http://localhost:5173`

Vite proxies `/api` and `/auth` to the Spring Boot backend.

## Main API Resources

- `/auth/login`
- `/api/users`
- `/api/profile`
- `/api/items`
- `/api/items/public`
- `/api/collections`
- `/api/collections/public`

## Test / build

Backend:

`mvn test`

Frontend:

`npm test`

`npm run build`

`npm run lint`
