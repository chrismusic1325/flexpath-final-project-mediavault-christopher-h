# Requirements Checklist

## Application

- [x] Public/private items
- [x] User item CRUD
- [x] Admin item access through role checks
- [x] Collections/groupings
- [x] Public/private collections
- [x] User collection CRUD
- [x] Add/remove collection membership API
- [x] Public browsing
- [x] Multiple search parameters
- [x] SQL LIKE partial search
- [x] Multiple item sort fields ASC/DESC
- [x] Multiple collection sort fields ASC/DESC

## Backend

- [x] Spring Boot REST API
- [x] Role-based authorization
- [x] Ownership protection
- [x] Validation/error responses
- [x] JaCoCo coverage gate configured at 70% line coverage
- [ ] Latest `mvn clean verify` must pass

## Frontend

- [x] React API integration
- [x] 3+ routed pages
- [x] React Router
- [x] Reusable navigation/component structure
- [x] Error states
- [x] Public/private labels
- [x] Delete confirmation
- [x] Responsive custom CSS
- [x] Jest coverage gate configured at 70%
- [ ] Latest `npm run build` must pass
- [ ] Latest `npm test` must pass
- [ ] Latest `npm run lint` must pass

## Database

- [x] MySQL schema
- [x] PK/FK relationships
- [x] Cascades
- [x] Useful indexes
- [x] Starter data
- [x] Re-creatable setup SQL

## Submission/manual gates

- [ ] Recreate MySQL database from `database/create-database.sql`
- [ ] Run backend against MySQL
- [ ] Run frontend against backend
- [ ] Browser walkthrough of CRUD, visibility, collections, search, sort and admin
- [ ] 15–25 minute walkthrough video
- [ ] Be prepared to explain submitted Java, React, SQL and architecture
