# Fit4Me

Fit4Me is a fitness tracking and workout matching Android app, built as part of **CS446: Software Design & Architecture** at the University of Waterloo.  
The app helps users discover and complete workouts, track exercise history, connect with others, and get matched based on personalized preferences.

---

## 🚀 Features

- **User Accounts & Authentication**  
  Secure JWT-based authentication with protected API routes.

- **Workouts & Exercises**  
  - Browse standard and custom workouts  
  - Filter exercises by body part and equipment  
  - Create, edit, and delete workouts and exercises

- **Workout Sessions**  
  - Start and complete sessions from templates  
  - Log sets with reps, weights, and durations  
  - Track duration and progress over time

- **Exercise History**  
  - View workout and exercise history  
  - Expandable dialogs for past sessions  
  - Personal record (PR) tracking per exercise

- **Matching Engine**  
  Six personalized scoring strategies:  
  - BalancedMatch  
  - ScheduleMatch  
  - ExperienceMatch  
  - LocalMatch  
  - BodyMatch  
  - BeginnerMatch  

- **Real-Time Chat**  
  - One-on-one messaging using Socket.IO  
  - Persistent message history stored in PostgreSQL  

---

## 🛠️ Tech Stack

### Frontend (Android)
- **Kotlin** + **Jetpack Compose** for UI
- **ViewModel** for state management
- **Retrofit** for API communication
- **Socket.IO client** for real-time chat

### Backend
- **Node.js** + **Express.js** (TypeScript)
- **Prisma ORM** with **PostgreSQL** database
- **JWT Authentication** middleware
- **Socket.IO server** for real-time events

---

## 🏛️ Architecture

The system follows a **client-server architecture** with modular design patterns:
- **Frontend**: MVVM (Model-View-ViewModel) with Compose
- **Backend**: Layered architecture (routers, controllers, services, database)
- **Design Patterns Used**: Strategy, Decorator, Event-Driven Architecture

---

## 📦 Installation & Setup

### Backend

1. **Clone the repo:**
   ```bash
   git clone https://github.com/grace-ful/cs446-team-project.git
   cd /backend
   ```

2. **Install dependencies:**
   ```bash
   npm install
   ```

3. **Setup environment variables in `.env`:**
   ```env
   DATABASE_URL="postgresql://user:password@host:port/dbname?sslmode=require"
   JWT_SECRET="your-secret"
   ```

4. **Run migrations & generate Prisma client:**
   ```bash
   npx prisma migrate dev --name init
   npx prisma generate
   ```

5. **Start server:**
   ```bash
   npm run dev
   ```

### Android App

1. Open the project in Android Studio
2. Sync Gradle and install dependencies
3. Update `baseUrl` in Retrofit config (`ApiClient.kt`) to point to backend
4. Run the app on emulator or physical device

---

## 👥 Contributors

1. Yash Harshal Barve
2. Aryaman Arora
3. Nicholas Rebello
4. Riyan Sunesara
5. Grace Feng
6. Lucy Li

---

## 📽️ Demo Videos

- [Full App Walkthrough](https://www.youtube.com/watch?v=_2dJb6zYpTw)

---

## 📚 Course Info

This project was developed for **CS446 – Software Design and Architecture**, University of Waterloo (Spring 2025).  
It implements core architectural concepts like modularity, maintainability, and design patterns in a real-world mobile app.

---

## 📄 License

MIT License – feel free to fork and modify for personal use.