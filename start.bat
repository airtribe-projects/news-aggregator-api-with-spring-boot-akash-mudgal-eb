@echo off
echo =====================================================
echo           News Aggregator API Setup
echo =====================================================
echo.

echo Checking requirements...

:: Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 24 and try again.
    pause
    exit /b 1
)

:: Check if Maven is installed
mvn --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Maven is not installed or not in PATH
    echo Please install Maven and try again.
    pause
    exit /b 1
)

echo ✓ Java found
echo ✓ Maven found
echo.

echo Checking NewsAPI configuration...
findstr /C:"YOUR_NEWS_API_KEY_HERE" src\main\resources\application.properties >nul
if not errorlevel 1 (
    echo.
    echo ⚠️  WARNING: NewsAPI key not configured!
    echo.
    echo Please update src\main\resources\application.properties
    echo Replace YOUR_NEWS_API_KEY_HERE with your actual NewsAPI key
    echo.
    echo You can get a free API key from: https://newsapi.org/register
    echo.
    echo Press any key to continue anyway or Ctrl+C to exit...
    pause >nul
)

echo.
echo Building the application...
mvn clean compile

if errorlevel 1 (
    echo.
    echo ❌ Build failed! Please check the error messages above.
    pause
    exit /b 1
)

echo.
echo ✅ Build successful!
echo.
echo Starting the News Aggregator API...
echo.
echo The application will be available at: http://localhost:8080
echo H2 Console (for development): http://localhost:8080/h2-console
echo.
echo Press Ctrl+C to stop the application
echo.

mvn spring-boot:run
