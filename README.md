# Project Setup and Running Guide

## Introduction

The purpose of this README file is to explain how to run this project from scratch. You will need minimal configuration on your end to run the service and start testing.

## Technologies Used

This project uses the following technologies:

- **Java**: The primary programming language used for the application.
- **Spring Boot**: A framework for building Java-based applications.
- **PostgreSQL**: The relational database used to store data.
- **Flyway**: A database migration tool.
- **Docker**: Used to containerize the application and its dependencies.
- **Docker Compose**: Used to define and run multi-container Docker applications.
- **pgAdmin**: A web-based database management tool for PostgreSQL.
- **Swagger**: Used for API documentation and testing.
- **OAuth 2.0**: Used for authentication via Google.
- **GitHub Actions**: Used for continuous integration and deployment.

## Prerequisites

- Docker installed on your machine
- Docker Compose installed on your machine
- A google account for OAuth 2.0 authentication
- Maven

### Including user as ADMIN

There is an v1/user/ route that is only allowed for priviliged users, to be able to access you will need to add your google email account into the Default Admin Roles of this app.

**Steps:**

Go to ./docker-compose.yml

add your email into the `app.admin.emails`, each email needs to be separated by comma.
save the file, once you login to the swagger page will the email will have an ADMIN role by default.

`ex:
app.admin.emails=my@email.com,mysecond@mail.com`

## Building the Application

Before running the application with Docker Compose, you need to build the application using Maven. This will create the necessary JAR file in the `target` directory.

```sh
mvn clean install
```

## Log in to Docker

Ensure you are logged in to Docker. If you are not logged in, execute the following command and provide your Docker credentials:

```bash
docker login
```

## Build the Docker Image

Use Docker Compose to build the Docker image and run the containers. This will start both the PostgreSQL and Java application services. Execute the following command:

```bash
docker-compose up
```

This will start the following services:

- postgres: PostgreSQL database
- flyway: Flyway for database migrations
- syntheticapi: The main application

## Access the Endpoints

Once the services are up and running, you can access the application at http://localhost:8080. The Swagger UI is available at
http://localhost:8080/swagger-ui/index.html.

Click the link to: Login with OAuth 2.0: **GOOGLE**.

After log in you will have access to all the list of endpoints, by default any user will have a USER role, in case you
need to have access to the v1/user endpoints will need to have an ADMIN role assign, previously explain in the **Including user as ADMIN section**, otherwise when
trying to access v1/user/ endpoints with USER role, the endpoint will respond with a 403 FORBIDDEN error.

### Get All Customers

**Endpoint:**

```http
GET http://localhost:8080/api/v1/customer
```

### Get Customer by ID

**Endpoint:**

```http
GET http://localhost:8080/api/v1/customer/{customerId}
```

Replace `{customerId}` with the actual customer ID.

### Save Customer

**Endpoint:**

```http
POST http://localhost:8080/api/v1/customer
```

**Form Data:**

- `name`: Customer's name (required)
- `surname`: Customer's surname (required)
- `file`: Multipart file (image)

### Update Customer

**Endpoint:**

```http
PUT http://localhost:8080/api/v1/customer
```

**Form Data:**

- `id`: Customer's ID (required)
- `name`: Customer's name (required)
- `surname`: Customer's surname (required)
- `file`: Multipart file (image)

### GET Customer image

**Endpoint:**

```http
GET http://localhost:8080/api/v1/customer/{customerId}/image
```

Replace `{customerId}` with the actual customer ID.

### PUT Customer image

**Endpoint:**

```http
PUT http://localhost:8080/api/v1/customer/{customerId}/image
```

Replace `{customerId}` with the actual customer ID.

### DELETE Customer image

**Endpoint:**

```http
DELETE http://localhost:8080/api/v1/customer/{customerId}/image
```

Replace `{customerId}` with the actual customer ID.

User Endpoints

You need an ADMIN Role to ge access to these endpoints.

### Get All Users

**Endpoint:**

```http
GET http://localhost:8080/api/v1/user
```

### Get User by ID

**Endpoint:**

```http
GET http://localhost:8080/api/v1/user/{userId}
```

Replace {userId} with the actual user ID.

### Save User

**Endpoint:**

```http
POST http://localhost:8080/api/v1/user
```

```Json
name: User's name
email: User's email
```

### Update User

**Endpoint:**

```http
PUT http://localhost:8080/api/v1/user/{userId}
```

```Json
name: User's name
email: User's email
```

### Delete User

**Endpoint:**

```http
DELETE http://localhost:8080/api/v1/user/{userId}
```

Replace {userId} with the actual user ID.

### Admin

**Endpoint:**

```http
PUT http://localhost:8080/api/v1/user/{userId}/admin
```

Replace {userId} with the actual user ID.

## Notes

- Ensure that port 8080 is not in use by any other service before running the container.

These steps will help you build, run, and test this application locally using Docker.

## GitHub Actions

This project is set up to use GitHub Actions for continuous integration. The workflow file is located at `.github/workflows/` ci.yml and includes steps to build the project, run tests, and build the Docker image.

## Stopping the Application

```bash
docker-compose down
```
