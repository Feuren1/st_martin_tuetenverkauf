# St.Martin Treat Bags

Web application for the St. Martin association in Viersen, Germany, to simplify the pre-ordering and management of **St. Martins-Tüten** — traditional bags filled with treats picked up by people attending the St. Martin's Day event.

The project covers the ordering workflow and the underlying administration required to manage orders.

This readme shows the current status of the project and will be kept up to date. For the full planning please go to [Analysis](backend/analysis) and [Design](backend/design).

## Current Tasks
* Deployment
* CI/CD via GitHub Actions

## Tech Stack

### Frontend

* React (Not Implemented yet)

### Backend

* Java
* Spring Boot
* Spring Data JPA / Hibernate
* PostgreSQL
* JJWT — JWT creation and validation

### Deployment

* Hetzner Server
* Docker
* GitHub Actions (Coming soon)
* GitHub Container Registry (GHCR) (Coming soon)

## Development

The backend is built with Maven and can be started directly from the project directory.

PostgreSQL is provided through Docker Compose for local development.

```bash
cd backend/tuetenverkauf
docker compose up -d
```
