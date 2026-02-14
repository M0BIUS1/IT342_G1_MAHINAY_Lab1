# IT342_G1_MAHINAY_Lab1

User Registration and Authentication system with Web and Mobile clients.

## Backend (Spring Boot — Java 21)
- `POST /api/auth/register` — Register a new user
- `POST /api/auth/login` — Login and receive JWT token
- `GET /api/user/me` — Get current user details (protected)
- MySQL database connection
- BCrypt password encryption
- JWT authentication with Spring Security

### Running the Backend
```bash
cd backend/backend
# Requires Java 21
./mvnw spring-boot:run
```

## Web (React + Vite)
- Register page
- Login page
- Dashboard page (protected) — displays Account ID, Name, Email
- Logout

### Running the Web App
```bash
cd web
npm install
npm run dev
```

## Mobile (Android — Kotlin)
- Login screen with email/password
- Register screen with name/email/password
- Dashboard screen — displays Account ID, Name, Email
- Logout screen with back-to-login navigation
- OkHttp API client connecting to Spring Boot backend
- SharedPreferences-based session/token management

### Running the Mobile App
1. Open `mobile/Mobile` in Android Studio
2. Sync Gradle
3. Run on emulator (uses `10.0.2.2:8080` for backend) or physical device (update `BASE_URL` in `ApiClient.kt`)

## Tech Stack
| Layer    | Technology                        |
|----------|-----------------------------------|
| Backend  | Spring Boot 4.0.2, Java 21, MySQL |
| Web      | React, Vite                       |
| Mobile   | Kotlin, OkHttp, Gson, Material    |
