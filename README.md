# ClutchMetrics Backend

**ClutchMetrics Backend** is a REST API developed with Java and Spring Boot, designed to support the ClutchMetrics web and mobile applications.

ClutchMetrics is an eSports platform that combines gaming performance analytics, video content management, community interaction, and subscription-based features.

The backend centralizes authentication, authorization, business logic, database operations, video processing, and auditing.

Both the Next.js web application and the React Native mobile application communicate with this API and share the same PostgreSQL database.

## Technologies

- Java 21
- Spring Boot 4.1.1
- Spring Security
- Spring Data JPA
- Hibernate ORM
- Spring Validation
- Spring Mail
- PostgreSQL
- JSON Web Tokens (JJWT 0.13.0)
- Maven
- Docker
- Mailpit
- FFmpeg

**Development tools:** IntelliJ IDEA, pgAdmin 4, Postman

## Architecture

The application follows a layered architecture, separating responsibilities into controllers, services, repositories, entities, DTOs, security components, and exception handlers.

```text
ClutchMetrics Web (Next.js)
             |
ClutchMetrics Mobile (React Native)
             |
             v
       REST Controllers
             |
             v
       Service Layer
             |
             v
       JPA Repositories
             |
             v
         PostgreSQL
```

### Main Components

- **Controllers:** Handle HTTP requests and return API responses.
- **Services:** Implement business logic and validation.
- **Repositories:** Provide database access using Spring Data JPA.
- **Entities:** Represent the database domain model.
- **DTOs:** Define request and response data structures.
- **Security:** Handle JWT authentication and authorization.
- **Exception Handling:** Provide consistent API error responses.

## Main Features

### Authentication and Security

- User registration
- Email verification
- Login and logout
- JWT access tokens
- Refresh token generation, rotation, and revocation
- Password hashing with BCrypt
- Forgot password and password reset
- Password changes requiring email confirmation
- Role-based authorization using USER and ADMIN roles
- Validation of incoming requests
- Custom exception handling and HTTP error responses

### User and Player Management

- User account management
- Player profile creation and updates
- Display name, biography, and avatar management
- Public player profile discovery
- Social media profile links
- Follow and unfollow functionality
- Followers and following lists

### Game Accounts

- Management of player game accounts
- Game, region, and platform information
- Connection and disconnection status
- Reconnection of previously disconnected accounts

Official gaming provider account synchronization is not implemented.

### Matches and Analytics

- Match history storage and retrieval
- Match results and categories
- Kills, deaths, assists, and score statistics
- Performance summaries and analytics
- Game-specific statistics
- Premium-restricted advanced analytics

The current implementation uses demonstration match data rather than automatic retrieval from official gaming APIs.

### Video Assets and Creator Studio

- Video file uploads
- Video asset management
- Video processing with FFmpeg
- Video trimming using start and end times
- Vertical video exports
- Original video preservation
- Clip creation and management
- Publishing clips to the community Feed
- Free plan: 720p exports
- Premium plan: 1080p exports

### Community Feed

- Published gaming highlights
- Clip visibility and publication status
- Likes and unlikes
- Comments
- Clip reporting
- Content moderation support

### Challenges

- Challenge management
- Game-specific challenges
- Challenge participation
- Progress tracking
- Challenge completion
- Rewards and sponsor information

### Subscriptions

- Free and Premium plans
- Subscription management
- Plan upgrades and downgrades
- Backend validation of Premium-restricted features
- Subscription change auditing

Subscriptions are currently simulated without real payment processing.

### Administration and Moderation

- Role-based administrator access
- User administration
- Reported content management
- Moderation operations
- Audit log consultation

Audit logs help track supported operations performed through both the web and mobile applications.

### Support Chatbot

The backend provides a rule-based support chatbot for common questions related to ClutchMetrics functionality.

The chatbot uses predefined responses and is not currently powered by a generative AI model.

## Supported Games

- League of Legends
- Valorant
- Counter-Strike 2

## Database

The project uses PostgreSQL as its relational database.

The database includes 24 tables covering authentication, player profiles, gaming accounts, matches, statistics, videos, clips, subscriptions, challenges, community interactions, and auditing.

The database schema and entity relationships are documented separately using a draw.io entity-relationship diagram.

## Getting Started

### Prerequisites

- JDK 21 or compatible newer version
- Maven
- PostgreSQL
- Docker, for running Mailpit
- FFmpeg and FFprobe, for video processing
- IntelliJ IDEA or another compatible Java IDE

### Installation

Clone the repository:

```bash
git clone https://github.com/AndreMiguelAlves/clutchmetrics-backend.git
cd clutchmetrics-backend
```

### PostgreSQL Configuration

Create a PostgreSQL database named:

```text
clutchmetrics
```

The local database connection is configured as:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/clutchmetrics
spring.datasource.username=postgres
spring.datasource.password=${DB_PASSWORD}
```

The application uses Spring Data JPA and Hibernate, with the current local development setting:

```properties
spring.jpa.hibernate.ddl-auto=update
```

This setting allows Hibernate to update parts of the database schema during development. It is not intended as a production database migration strategy.

A SQL schema export is maintained separately as part of the project documentation.

### Environment Variables

The application requires the following environment variables:

| Variable | Description |
|---|---|
| `DB_PASSWORD` | Password for the local PostgreSQL user |
| `JWT_SECRET` | Base64-encoded secret key used to sign JWT tokens |

The JWT secret must decode to a sufficiently strong cryptographic key (at least 32 random bytes for HS256).

For local development using Windows PowerShell, the variables can be configured in the terminal session.

Example:

```powershell
$env:DB_PASSWORD = "YOUR_LOCAL_DATABASE_PASSWORD"
```

Generate a random JWT signing secret:

```powershell
$bytes = New-Object byte[] 32
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$rng.GetBytes($bytes)
$rng.Dispose()

$env:JWT_SECRET = [Convert]::ToBase64String($bytes)
```

These commands configure the variables for the current PowerShell session.

When running the project directly through IntelliJ, configure the environment variables in the application's Run Configuration.

**Never commit database passwords, JWT secrets, or other credentials to GitHub.**

### Email Configuration — Mailpit

The project uses Mailpit to test email verification, password recovery, and password change confirmation.

The local SMTP settings are:

```properties
spring.mail.host=localhost
spring.mail.port=1025
```

Mailpit can be launched with Docker:

```bash
docker run -d --name clutchmetrics-mailpit -p 1025:1025 -p 8025:8025 axllent/mailpit
```

If the container already exists but is stopped, use:

```bash
docker start clutchmetrics-mailpit
```

Open the Mailpit interface at:

```text
http://localhost:8025
```

Mailpit is intended for local development and testing. A real SMTP provider would be required for production email delivery.

### Video Processing — FFmpeg

The backend uses FFmpeg and FFprobe for video processing and duration validation.

Both tools must be installed and accessible from the system PATH.

Verify the installation:

```bash
ffmpeg -version
ffprobe -version
```

The current multipart upload settings are:

```properties
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=105MB
```

Uploaded and processed video files are stored separately from the PostgreSQL database.

### Running the Backend

Make sure PostgreSQL and Mailpit are available and that the required environment variables have been configured.

Start the application through IntelliJ or run:

```bash
mvn spring-boot:run
```

The API runs locally at:

```text
http://localhost:8080
```

### Additional Maven Commands

Compile the project:

```bash
mvn clean compile
```

Execute the available tests:

```bash
mvn test
```

Build the application:

```bash
mvn clean package
```

A successful build requires a compatible Java environment and access to the necessary Maven dependencies.

## Authentication API

The authentication controller exposes the following endpoints:

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/auth/register` | Register a new user |
| GET | `/api/auth/verify-email` | Confirm email address |
| POST | `/api/auth/login` | Authenticate and generate tokens |
| GET | `/api/auth/me` | Retrieve authenticated user information |
| POST | `/api/auth/refresh` | Refresh authentication tokens |
| POST | `/api/auth/logout` | Revoke a refresh token |
| POST | `/api/auth/forgot-password` | Request password recovery |
| POST | `/api/auth/reset-password` | Reset a forgotten password |
| POST | `/api/auth/change-password` | Request an authenticated password change |
| POST | `/api/auth/confirm-password-change` | Confirm a password change |

Protected endpoints require a valid JWT access token.

Other REST controllers provide the functionality for gaming accounts, profiles, matches, analytics, clips, subscriptions, challenges, administration, and community interactions.

## Authentication Token Expiration

The current local configuration defines:

| Token | Expiration |
|---|---|
| JWT Access Token | 15 minutes |
| Refresh Token | 7 days |

These settings are defined in `application.properties`:

```properties
app.jwt.access-token-expiration-ms=900000
app.jwt.refresh-token-expiration-ms=604800000
```

## Current Limitations and Future Improvements

The current version is an academic demonstration project and includes the following limitations:

- No automatic synchronization with official gaming APIs
- Match history includes fictional demonstration data
- Fully automated gameplay detection and highlight extraction are not implemented
- 4K video export is not implemented
- Automatic watermark removal is not implemented
- Automatic video thumbnail generation is not implemented
- Real subscription payments and billing are not implemented
- Production-ready subscription renewals require further development
- Direct publishing to external social media platforms is not implemented
- The support chatbot is rule-based rather than generative AI
- Additional automated testing and production security hardening would be required
- Local development configuration must be adapted before production deployment

## Project Context

ClutchMetrics was developed as a final academic project for the **Técnico/a Especialista em Tecnologias e Programação de Sistemas de Informação (TPSI)** course.

The complete project includes:

- **Backend:** Java Spring Boot
- **Web:** Next.js, React, and TypeScript
- **Mobile:** React Native, Expo, and TypeScript
- **Database:** PostgreSQL

The objective is to combine competitive gaming analytics, video content management, and community interaction within a shared platform.

## Author

André Miguel Nunes Alves

**ClutchMetrics — Your Game. Your Data. Your Edge.**