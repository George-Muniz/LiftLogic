
# 💪 LiftLogic

LiftLogic is a Java-based fitness tracking application with a Swing GUI and MySQL backend. It helps users log workouts, track diet and calorie intake, set fitness goals, and visualize their progress using graphs.

---

## 🚀 Features

- 🧍 User Login and Profile
- 🏋️ Workout Logging (exercise, weight, duration, calories)
- 🍽️ Meal Logging (name, calories, macronutrients)
- 📈 Progress Tracking with Graphs (JFreeChart)
- 🧠 Goal Setting (weight, strength, timeframe)
- 🔐 Secure password storage using BCrypt

---

## 📦 Technologies

- Java 17
- Swing (GUI)
- MySQL (Database)
- JFreeChart (Graphing)
- Maven (Project management)
- jBCrypt (Password hashing)

---

## 🛠️ Setup Instructions

### 1️⃣ Clone the Repository

```bash
git clone https://github.com/George-Muniz/LiftLogic.git
cd LiftLogic
```

### 2️⃣ Set Up the Database

#### Option A: Using Command Line

```bash
mysql -u root -p
```

Inside MySQL:

```sql
CREATE DATABASE login_schema;
EXIT;
```

Then import the SQL schema:

```bash
mysql -u root -p login_schema < login_schema.sql
```

#### Option B: Using MySQL Workbench

1. Create a new schema named `login_schema`
2. Go to **File → Run SQL Script**
3. Select `login_schema.sql` and execute

---

### 3️⃣ Configure Database Credentials

Copy the example file and update it with your MySQL info:

```bash
cp src/main/resources/db.properties.example src/main/resources/db.properties
```

Edit `db.properties` with your credentials:

```properties
db.url=jdbc:mysql://localhost:3306/login_schema
db.user=root
db.password=your_password
```

> ⚠️ Do **not** commit your real `db.properties` to GitHub.

---

### 4️⃣ Build & Run the Project

#### Run from IntelliJ:

- Open project as Maven
- Build → Run `App.java` (or your Main class)

#### Or from terminal:

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.liftlogic.App"
```

---

## 📝 File Structure

```
LiftLogic/
├── src/
│   └── main/
│       ├── java/com/liftlogic/
│       └── resources/
│           ├── db.properties.example
│           └── db.properties (❌ Git ignored)
├── login_schema.sql
├── pom.xml
└── README.md
```

---

## 🤝 Contributions

Contributions are welcome! Fork the repo and submit a PR.

---

## 🔒 License

MIT License — do whatever you want with it. Just give credit.
