
# ClutchMetrics - Postman Demo Data

This folder contains a Postman collection used to populate
the ClutchMetrics database with sample match data.

## Collection

`ClutchMetrics_KDA_Postman_Collection.json`

The collection includes:

- 10 League of Legends matches
- 10 Valorant matches

These matches demonstrate the KDA Evolution charts,
performance statistics and match history.

## How to Run

1. Start PostgreSQL and the Spring Boot backend.
2. Log in to ClutchMetrics and obtain a JWT access token.
3. Create or identify the user's League of Legends and Valorant game accounts.
4. Import the JSON collection into Postman.
5. Configure the collection variables:
    - `baseUrl`: http://localhost:8080
    - `accessToken`: JWT access token
    - `lolAccountId`: League of Legends account ID
    - `valorantAccountId`: Valorant account ID
6. Run the League of Legends folder once.
7. Run the Valorant folder once.
8. Open the Dashboard to view the statistics.

## Important

- Run each folder only once per database.
- The collection uses fixed external match IDs.
- A valid JWT token is required.
- Never commit personal access tokens to GitHub.
- The matches are sample data for development and presentation.
