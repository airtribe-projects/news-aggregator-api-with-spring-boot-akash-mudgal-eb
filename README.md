# News Aggregator API

A comprehensive Spring Boot REST API that aggregates news from multiple sources using NewsAPI.org, featuring user authentication, personalized news preferences, and article management.

## Features

- **User Authentication**: JWT-based authentication with Spring Security
- **News Aggregation**: Fetch news from NewsAPI.org based on user preferences
- **User Preferences**: Customizable news categories, sources, countries, and languages
- **Article Management**: Mark articles as read or favorite
- **Caching**: Intelligent caching using Caffeine to reduce API calls
- **Background Updates**: Scheduled tasks to keep cached news fresh
- **Search Functionality**: Search for news articles by keywords
- **Exception Handling**: Comprehensive error handling and validation

## Technology Stack

- **Java 24** with Maven
- **Spring Boot 3.2.2**
- **Spring Security** with JWT authentication
- **Spring Data JPA** with H2 in-memory database
- **Spring WebFlux** for async HTTP requests
- **Caffeine Cache** for caching
- **Jackson** for JSON processing
- **JUnit 5** for testing

## Prerequisites

1. Java 24 installed
2. Maven 3.6+ installed
3. NewsAPI.org API key (free tier: 100 requests/day)

## Quick Start

1. **Clone and navigate to the project**
   ```bash
   cd news-aggregator-api
   ```

2. **Configure NewsAPI Key**
   
   Update `src/main/resources/application.properties`:
   ```properties
   newsapi.key=YOUR_ACTUAL_NEWS_API_KEY_HERE
   ```
   
   Get your free API key from: https://newsapi.org/register

3. **Build and run the application**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. **Access the application**
   - API Base URL: `http://localhost:8080`
   - H2 Console: `http://localhost:8080/h2-console` (for development)
     - JDBC URL: `jdbc:h2:mem:newsdb`
     - Username: `sa`
     - Password: `password`

## API Endpoints

### Authentication

#### Register User
```http
POST /api/register
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123"
}
```

#### Login User
```http
POST /api/login
Content-Type: application/json

{
  "username": "johndoe",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "johndoe",
  "email": "john@example.com"
}
```

### News Preferences (Authenticated)

#### Get User Preferences
```http
GET /api/preferences
Authorization: Bearer <your-jwt-token>
```

#### Update User Preferences
```http
PUT /api/preferences
Authorization: Bearer <your-jwt-token>
Content-Type: application/json

{
  "categories": ["technology", "business", "science"],
  "sources": ["techcrunch", "bbc-news"],
  "countries": ["us", "gb"],
  "languages": ["en"]
}
```

### News Articles (Authenticated)

#### Get Personalized News
```http
GET /api/news
Authorization: Bearer <your-jwt-token>
```

#### Search News by Keyword
```http
GET /api/news/search/{keyword}?categories=technology,business&sources=techcrunch
Authorization: Bearer <your-jwt-token>
```

#### Get Available News Sources
```http
GET /api/news/sources
Authorization: Bearer <your-jwt-token>
```

### Article Interactions (Authenticated)

#### Mark Article as Read
```http
POST /api/news/{articleId}/read?title=Article Title&url=https://example.com/article
Authorization: Bearer <your-jwt-token>
```

#### Mark Article as Favorite
```http
POST /api/news/{articleId}/favorite?title=Article Title&url=https://example.com/article
Authorization: Bearer <your-jwt-token>
```

#### Remove Article from Favorites
```http
DELETE /api/news/{articleId}/favorite
Authorization: Bearer <your-jwt-token>
```

#### Get Read Articles
```http
GET /api/news/read
Authorization: Bearer <your-jwt-token>
```

#### Get Favorite Articles
```http
GET /api/news/favorites
Authorization: Bearer <your-jwt-token>
```

## Available News Categories

- `business`
- `entertainment`
- `general`
- `health`
- `science`
- `sports`
- `technology`

## Available Countries

- `us` (United States)
- `gb` (United Kingdom)
- `ca` (Canada)
- `au` (Australia)
- `de` (Germany)
- `fr` (France)
- And many more...

## Available Languages

- `en` (English)
- `es` (Spanish)
- `fr` (French)
- `de` (German)
- `it` (Italian)
- And many more...

## Testing with Postman/cURL

### 1. Register a new user
```bash
curl -X POST http://localhost:8080/api/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }'
```

### 2. Login and get JWT token
```bash
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

### 3. Use the token to access protected endpoints
```bash
curl -X GET http://localhost:8080/api/news \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

### 4. Update news preferences
```bash
curl -X PUT http://localhost:8080/api/preferences \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "categories": ["technology", "business"],
    "countries": ["us"],
    "languages": ["en"]
  }'
```

### 5. Search for specific news
```bash
curl -X GET "http://localhost:8080/api/news/search/artificial intelligence" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

## Configuration

### Database Configuration
The application uses H2 in-memory database by default. For production, consider using PostgreSQL or MySQL.

### Cache Configuration
- Cache expires after 1 hour
- Maximum 1000 entries per cache
- Background refresh every hour

### JWT Configuration
- Token expiration: 24 hours (86400000 ms)
- Secret key configured in `application.properties`

### NewsAPI Configuration
- Base URL: `https://newsapi.org/v2`
- Rate limit: 100 requests/day (free tier)

## Development

### Running Tests
```bash
mvn test
```

### Building for Production
```bash
mvn clean package
java -jar target/news-aggregator-api-1.0-SNAPSHOT.jar
```

### Database Access (Development)
Access H2 console at `http://localhost:8080/h2-console`:
- JDBC URL: `jdbc:h2:mem:newsdb`
- Username: `sa`
- Password: `password`

## Error Handling

The API provides comprehensive error handling with appropriate HTTP status codes:

- **400 Bad Request**: Validation errors, user already exists
- **401 Unauthorized**: Invalid credentials, missing/invalid JWT token
- **403 Forbidden**: Access denied
- **404 Not Found**: Resource not found
- **503 Service Unavailable**: External API unavailable

## Rate Limiting

Be mindful of NewsAPI.org rate limits:
- Free tier: 100 requests/day
- Caching helps reduce actual API calls
- Background updates refresh popular content

## Next Steps

1. **Get NewsAPI Key**: Register at https://newsapi.org/register
2. **Update Configuration**: Add your API key to `application.properties`
3. **Test Endpoints**: Use Postman or cURL to test the API
4. **Customize Preferences**: Set up your news preferences
5. **Explore Features**: Try searching, favoriting, and marking articles as read

## Support

For issues or questions:
1. Check the logs for detailed error messages
2. Verify your NewsAPI key is valid and has remaining quota
3. Ensure proper JWT token usage for authenticated endpoints
4. Review the API documentation for correct request formats
