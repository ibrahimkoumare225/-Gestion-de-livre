# Gestion de Livre - Docker Setup

## Local Development with Docker Compose

### Prerequisites
- Docker
- Docker Compose

### Running the application locally
```bash
docker-compose up -d
```

This will start:
- **PostgreSQL** on `localhost:5432`
- **PgAdmin** on `localhost:5050` (admin@example.com / admin)
- **Gestion de Livre App** on `localhost:8080`

### Stopping the application
```bash
docker-compose down
```

### Removing volumes (database data)
```bash
docker-compose down -v
```

### Environment variables
Create a `.env` file in the project root:
```
POSTGRES_USER=gestion_user
POSTGRES_PASSWORD=gestion_password
POSTGRES_DB=gestion_de_livre
```

### Building the Docker image manually
```bash
docker build -t gestion-de-livre:latest .
```

### Running Docker image standalone
```bash
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/gestion_de_livre \
  -e SPRING_DATASOURCE_USERNAME=gestion_user \
  -e SPRING_DATASOURCE_PASSWORD=gestion_password \
  gestion-de-livre:latest
```

### API Endpoints
- **POST** /books - Add a new book
- **GET** /books - Get all books

### PgAdmin Access
URL: http://localhost:5050
- Email: admin@example.com
- Password: admin

To connect to the database in PgAdmin:
- Host: postgres
- Port: 5432
- Username: gestion_user
- Password: gestion_password
- Database: gestion_de_livre

### Troubleshooting
If the database is not ready when the app starts:
```bash
docker-compose up postgres -d
docker-compose up app
```

Check logs:
```bash
docker-compose logs -f app
docker-compose logs -f postgres
```
