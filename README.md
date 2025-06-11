# Beer Scanner

A self-hosted application that tracks beer availability at bars. Users can register via Google OAuth, track bars and beers, and receive notifications when their favorite beers become available.

## Features

- Track beer availability at bars
- Daily checks of bar menus using OpenAI to extract beer information
- User authentication via Google OAuth
- Notifications for beer availability and menu changes
- Search functionality for bars and beers
- User preferences for tracking specific bars and beers

## Technology Stack

- **Backend**: Spring Boot
- **Database**: PostgreSQL
- **Authentication**: OAuth2 with Google
- **AI Integration**: OpenAI API for menu parsing
- **Containerization**: Docker

## Running with Docker

The application is containerized using Docker and can be easily run using Docker Compose.

### Prerequisites

- Docker and Docker Compose installed
- Google OAuth credentials (Client ID and Secret)
- OpenAI API key

### Environment Variables

Create a `.env` file in the root directory with the following variables:

```
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
OPENAI_API_KEY=your_openai_api_key
JWT_SECRET=your_jwt_secret
FRONTEND_URL=http://localhost:3000
MAIL_USERNAME=your_email_username
MAIL_PASSWORD=your_email_password
```

### Starting the Application

1. Build and start the containers:

```bash
docker-compose up -d
```

2. The application will be available at http://localhost:8080
3. The API documentation will be available at http://localhost:8080/swagger-ui.html

### Stopping the Application

```bash
docker-compose down
```

## API Documentation

The API is documented using OpenAPI (Swagger). Once the application is running, you can access the documentation at:

http://localhost:8080/swagger-ui.html

## Development

### Building the Application

```bash
./gradlew build
```

### Running Tests

```bash
./gradlew test
```

## License

MIT