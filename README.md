VolunTrack

VolunTrack is a volunteer project management system built with JavaFX and SQLite.
It allows users to register for volunteer projects, manage their participation, and view activity history.
Administrators can manage all projects, enable or disable listings, and view all user registrations.

Features
User

Browse all available volunteer projects

Add projects to cart and confirm registration with a 6-digit code

View participation history

Change password

Logout securely

Admin

View, create, edit, and delete projects

Enable or disable project visibility

View all user registration records

Architecture

Frontend: JavaFX (UI built with controls and scenes)

Backend: Java 21 with SQLite database

Pattern: MVC (Model–View–Controller) + Repository layer

Persistence: JDBC + SQLite

Tech Stack
Component	Technology
Language	Java 21
GUI	JavaFX 21
Database	SQLite 3
Logging	SLF4J
Testing	JUnit 5
Directory Structure
VolunTrack/
 ├── src/
 │   ├── voluntrack/
 │   │   ├── model/             # Data models
 │   │   ├── repository/        # Database operations
 │   │   ├── service/           # Business logic
 │   │   ├── view/              # JavaFX views (UI)
 │   │   ├── db/                # Database setup and seed data
 │   │   └── Main.java          # Application entry point
 │
 ├── data/
 │   ├── voluntrack.db          # SQLite database
 │   └── projects.csv           # Seed data
 │
 ├── libs/                      # External libraries
 └── test/                      # JUnit tests

How to Run

Clone or import the project into IntelliJ IDEA.

Ensure JavaFX SDK and SQLite JDBC libraries are configured under Project Settings → Modules → Dependencies.

Run the Main.java file.

The database will auto-initialize on first launch.

Default Credentials:

Admin:

Username: admin

Password: admin123

Test User:

Username: user

Password: user123

Testing

JUnit 5 tests are located under /test.
To run tests:

Right-click the test folder → Run ‘All Tests’.

IntelliJ or Gradle will execute all test cases automatically.

Tests include:

ID padding logic (IdUtil.zeroPad4)

Date formatting (TimeUtil.nowIso)

Cart operations (CartService)

Confirmation code validation (RegistrationService)

Author

Alongkorn Sirimuntanakul
RMIT University
s3933113@student.rmit.edu.au
