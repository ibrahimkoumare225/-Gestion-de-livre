# Gestion de Livre

Application de gestion de livres développée en Kotlin avec Spring Boot, suivant l'architecture hexagonale (ports et adaptateurs).

## Architecture

L'application suit les principes de l'architecture hexagonale :

- **Domaine** : Modèle métier et logique métier
- **Infrastructure** :
  - **Driving** : Contrôleurs REST (adapters entrants)
  - **Driven** : Accès aux données JPA (adapters sortants)
  - **Application** : Configuration Spring

## Fonctionnalités

- Ajouter un livre (titre et auteur requis)
- Lister tous les livres (triés par titre)

## Technologies

- Kotlin
- Spring Boot 3
- Spring Data JPA
- H2/PostgreSQL
- Liquibase
- JUnit 5
- Kotest
- Mockk
- ArchUnit
- Cucumber
- JaCoCo
- Pitest
- Docker & Docker Compose
- GitHub Actions

## Tests

### Tests unitaires
```bash
./gradlew test
```

### Tests d'architecture
```bash
./gradlew testArchitecture
```

### Tests de composants (Cucumber)
```bash
./gradlew testComponent
```

### Tests d'intégration
```bash
./gradlew testIntegration
```

## Build et exécution

### Build
```bash
./gradlew build
```

### Exécution locale
```bash
./gradlew bootRun
```

## Docker

### Prérequis
- Docker
- Docker Compose

### Démarrer l'environnement local complet
```bash
docker-compose up -d
```

Cela démarre :
- PostgreSQL sur `localhost:5432`
- PgAdmin sur `localhost:5050`
- Gestion de Livre App sur `localhost:8080`

### Arrêter les conteneurs
```bash
docker-compose down
```

Pour plus de détails, voir [DOCKER.md](DOCKER.md)

## CI/CD

Le projet utilise GitHub Actions avec deux workflows :

### build.yml
Déclenché sur push/PR vers `main` et `develop` :
- Build avec Gradle
- Tests unitaires, d'architecture, d'intégration
- Génération des rapports JaCoCo et Pitest
- Upload des artefacts

### release.yml
Déclenché sur création de tag `v*` :
- Build de la version finale
- Création automatique d'une release GitHub

## API

### POST /books
Ajoute un livre.

**Request Body :**
```json
{
  "title": "Clean Code",
  "author": "Robert C. Martin"
}
```

**Response :** 201 Created

### GET /books
Liste tous les livres triés par titre.

**Response :**
```json
[
  {
    "title": "Book A",
    "author": "Author A"
  },
  {
    "title": "Book B",
    "author": "Author B"
  }
]
```

## Base de données

La base de données est gérée par Liquibase. Le schéma est défini dans `src/main/resources/db/changelog.xml`.

### Configuration de la base de données

Pour PostgreSQL (production/Docker) :
```yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/gestion_de_livre
    username: gestion_user
    password: gestion_password
    driver-class-name: org.postgresql.Driver
```

Pour H2 (tests) :
```yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
```

## Couverture de code

Rapports JaCoCo générés dans `build/reports/jacoco/`.

## Mutation testing

Rapports Pitest générés avec `./gradlew pitest`.