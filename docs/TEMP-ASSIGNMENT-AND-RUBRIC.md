# FlexPath Final Project - Assignment + Rubric Reference

> Temporary reference file for development. This combines the assignment requirements with the rubric targets for a highest-level submission.

## Project Overview

Build a full-stack application that allows users to create, curate, and retrieve items and groupings of items.

Required stack:
- Java
- Spring Boot
- React
- MySQL

Starter areas:
- `database/` - SQL and authentication tables
- `backend/` - Spring Boot REST API with authentication/user-role management
- `frontend/` - React app

MediaVault project domain:
- Item: MediaItem
- Grouping: MediaCollection

---

## Important Constraints

- The project must be your own work.
- You must understand and be able to explain all submitted code.
- Previous project code may be reused only when modified to fit this project and understood.
- AI tools may be used for learning and understanding code, but not to generate submitted project code.
- Do not add unapproved third-party libraries.

---

# APPLICATION REQUIREMENTS

## Creation

- Users can create items.
- Items can be public or private.
- Users can add, edit, and delete their own items.
- Administrators can view, edit, and delete all items.

## Curation

- Users can create groupings/collections.
- Users can add items to collections.
- Collections can be public or private.
- Users can create, edit, and delete their own groups and items.
- Users can view other users' public items and groups.
- Administrators can view, edit, and delete all groups.

## Retrieval

- Users can navigate their own items and groups.
- Users can view other users' public items and groups.
- Search items and groups using at least two query parameters.
- At least one search must use partial matching / SQL LIKE behavior.
- Sort groups by at least two fields.
- Group sorting must support ascending and descending.
- Sort items by at least two fields.
- Item sorting must support ascending and descending.

---

# BACKEND REQUIREMENTS

- Build a RESTful API with Spring Boot.
- Create controllers, services, and data-access classes as needed.
- Frontend must use the backend API.
- Use role-based authorization.
- Backend Java unit-test coverage must be at least 50%.

## Highest-level target

Aim for:
- 70%+ backend test coverage.
- Edge-case and error-path tests.
- Clear validation and error handling.
- Secure ownership checks.
- Complete admin permissions.
- Strong API organization.
- Helpful HTTP status/error responses.

---

# FRONTEND REQUIREMENTS

- React frontend interacts with the backend API.
- At least 3 pages.
- Use React Router.
- Repeated UI must be broken into reusable components.

Allowed styling:
- Bootstrap
- Tailwind CSS
- Custom CSS

Allowed icons:
- Free Font Awesome v5
- Bootstrap Icons

Frontend React/JS unit-test coverage must be at least 50%.

## Highest-level target

Aim for:
- 70%+ frontend test coverage.
- Reusable component hierarchy.
- Loading states.
- Error states.
- Empty states.
- Protected routes where appropriate.
- Clear public/private indicators.
- Delete confirmation.
- Consistent responsive styling.
- Clean navigation and UX.

---

# DATABASE REQUIREMENTS

- Use MySQL.
- Create tables for items and groupings.
- Use appropriate data types.
- Include primary keys.
- Include foreign keys where appropriate.
- Include all SQL required to recreate the database.
- Include starter data if needed.

## Highest-level target

Aim for:
- Thoughtful schema design.
- Clear relationships.
- Strong PK/FK design.
- Appropriate constraints.
- Useful indexes.
- Useful comments/documentation.
- Appropriate cascade behavior.

---

# MEDIAVAULT PLANNED STRUCTURE

## MediaItem

Possible fields:
- id
- title
- creator
- mediaType
- description
- isPublic
- owner
- createdDate

## MediaCollection

Possible fields:
- id
- name
- description
- isPublic
- owner
- createdDate
- items

## Planned Pages

- DashboardPage
- ItemsPage
- CollectionsPage
- PublicLibraryPage
- AdminPage

## Planned Reusable Components

- NavigationBar
- MediaItemCard
- MediaCollectionCard
- ItemSearchControls
- SortControls

---

# PERMISSIONS MATRIX

## Normal user - own item

- View: YES
- Edit: YES
- Delete: YES

## Normal user - another user's public item

- View: YES
- Edit: NO
- Delete: NO

## Normal user - another user's private item

- View: NO
- Edit: NO
- Delete: NO

## Admin

- View all: YES
- Edit all: YES
- Delete all: YES

Apply the same ownership/admin rules to collections.

---

# SEARCH AND SORT PLAN

## Items

Search:
- title -> partial / LIKE
- mediaType -> exact/filter

Sort:
- title ASC/DESC
- createdDate ASC/DESC

## Collections

Search:
- name -> partial / LIKE
- visibility or another second parameter

Sort:
- name ASC/DESC
- createdDate ASC/DESC

---

# TESTING PLAN

## Backend tests should cover

- create item
- update own item
- delete own item
- reject editing another user's item
- reject deleting another user's item
- public item retrieval
- private item protection
- admin can manage any item
- collection CRUD
- add items to collection
- search
- partial/LIKE search
- sorting ASC/DESC
- invalid IDs
- invalid input
- authorization failures

## Frontend tests should cover

- page rendering
- routing
- API success states
- API failure states
- loading states
- empty states
- public/private labels
- owner/admin controls
- search controls
- sort controls
- form behavior
- reusable components

---

# SUBMISSION REQUIREMENTS

- Submit GitHub repo link.
- Submit walkthrough video as `.mp4`.
- Project must be cloneable and buildable on Windows or Mac.
- Include all SQL needed to create tables, relationships, and starter data.
- Course staff will recreate the database from the SQL.
- Repo must build and run successfully after cloning.

---

# WALKTHROUGH VIDEO REQUIREMENTS

- 15 to 25 minutes.
- Start the application on your device.
- Open the browser and show the running React app.
- Explain:
  - what the project is
  - what problem/business case it addresses
  - what tools were used
  - why those tools were used
- Show VS Code organization.
- Walk through your most complicated React file.
- Walk through your most complicated Java file.
- Be ready to explain:
  - why code is structured that way
  - Java access modifiers
  - where IDs are set
  - how features could be improved
- Explain the hardest requirements and how you solved them.
- Record directly on the computer.
- Audio must be understandable.
- Code text must be readable.

---

# RUBRIC GOAL FOR 100%

You need at least a 3 / Meeting Expectations on every criterion to pass.

For the highest rubric level, target 4 / Exceeding Expectations across the categories below.

## General Full Stack

Target:
- Strong understanding of Java, Spring Boot, React, and MySQL.
- Useful functionality beyond bare minimums.
- Clear polished UX.
- Precise descriptive naming.
- No unnecessary dependencies.

## Creation

Target:
- Strong public/private controls.
- Clear visibility indicators.
- Strong ownership enforcement.
- Complete admin management.
- Good validation and feedback.
- Safe handling of destructive actions.

## Curation

Target:
- Collections easy to manage.
- Items easy to add/remove/organize.
- Public/private collection visibility clear.
- Useful public browsing.
- Complete admin collection management.
- Extra filtering/sorting polish where appropriate.

## Retrieval

Target:
- Easy navigation.
- Multiple search parameters.
- Partial matching works.
- Item sorting complete.
- Collection sorting complete.
- Optional pagination, breadcrumbs, filtering, or saved sort preferences if useful.

## Backend

Target:
- Complete REST integration.
- Robust authorization.
- Clear error handling.
- 70%+ backend test coverage.
- Edge/error cases covered.

## Frontend

Target:
- Full API integration.
- 3+ routed pages.
- Strong reusable components.
- Good error/loading/empty states.
- Responsive and consistent presentation.
- 70%+ frontend test coverage.
- Protected/nested routes if appropriate.

## Database

Target:
- Strong MySQL design.
- Proper PKs and FKs.
- Thoughtful constraints.
- Complete setup SQL.
- Useful starter data.
- Indexes/cascades/metadata where appropriate.

---

# FINAL 100% AUDIT CHECKLIST

- [ ] Public/private items
- [ ] User item CRUD
- [ ] Admin item management
- [ ] Public/private collections
- [ ] User collection CRUD
- [ ] Add/remove items in collections
- [ ] Public browsing
- [ ] 2+ search parameters
- [ ] LIKE/partial search
- [ ] 2+ item sort fields ASC/DESC
- [ ] 2+ collection sort fields ASC/DESC
- [ ] Spring Boot REST API
- [ ] Role-based authorization
- [ ] Ownership protection
- [ ] 3+ React pages
- [ ] React Router
- [ ] Reusable components
- [ ] Clear styling and UX
- [ ] 50%+ backend tests minimum
- [ ] 70%+ backend tests target
- [ ] 50%+ frontend tests minimum
- [ ] 70%+ frontend tests target
- [ ] MySQL schema complete
- [ ] PK/FK relationships correct
- [ ] SQL setup recreates the database
- [ ] Starter data included if needed
- [ ] Clean clone builds
- [ ] Backend runs
- [ ] Frontend runs
- [ ] Walkthrough video ready
- [ ] Can explain all submitted code

---

# CURRENT PROJECT

Application:
MediaVault

Repository:
`chrismusic1325/flexpath-final-project-mediavault-christopher-h`

Original LaunchCode assignment:
`docs/LAUNCHCODE-ORIGINAL-README.md`

This file is a convenience reference for development.
