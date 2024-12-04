# Group44

A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).

This project was generated with a template including simple application launchers and a main class extending `Game` that sets the first screen.

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.

## Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
Useful Gradle tasks and flags:

- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot versions.
- `build`: builds sources and archives of every project.
- `cleanEclipse`: removes Eclipse project data.
- `cleanIdea`: removes IntelliJ project data.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `eclipse`: generates Eclipse project data.
- `idea`: generates IntelliJ project data.
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `lwjgl3:run`: starts the application.
- `test`: runs unit tests (if any).

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.

# Virtual Pet Game: University Student Edition

The Virtual Pet Game: University Student Edition is an interactive simulation where players take care of a university student as their "pet." The game requires players to balance the student's health, happiness, energy, and stress while managing tasks like feeding, studying, exercising, and preparing for tests. Designed to entertain and educate, the game introduces players to routine management and responsibility in a fun and engaging way. It also includes parental controls to manage gameplay settings and track progress.

## Required Libraries and Tools

- Java Development Kit (JDK): Version 11 or higher.
- LibGDX Framework: Version 1.11.0
- Gradle: Version 7.5 or higher (for building the project).
- Integrated Development Environment (IDE): IntelliJ IDEA or Eclipse (optional but recommended).
- Assets:
    - Custom textures, fonts, and sounds (provided in the project directory).
- Operating System: Windows, macOS, or Linux.

## Building the Software

Clone the Repository:

Run the following command to clone the repository:

- bash

- git clone <repository-url>
- cd <repository-directory>

Install JDK:
- Download and install JDK 11 or higher from the Oracle website.

Install Gradle:
- Download and install Gradle from the Gradle website.

Set Up LibGDX:
- Include the LibGDX library in the build.gradle file of the project:

dependencies {
    
    implementation "com.badlogicgames.gdx:gdx:1.11.0"
    implementation "com.badlogicgames.gdx:gdx-backend-lwjgl3:1.11.0"
}

Run the following command to compile the code:
- bash
- gradle build

Check for Successful Build:
- The compiled .jar file will be available in the build/libs directory.
## Running the Software

Using the IDE:

Import the project into IntelliJ IDEA or Eclipse.
Configure the project to use JDK 11 or higher.
Set the Main class as the entry point and run the project.

Running from Command Line:

Navigate to the directory containing the compiled .jar file.
Run the game using the following command:

- bash
- java -jar <compiled-jar-name>.jar

Assets:
- Ensure all required assets (textures, fonts, and sounds) are in the correct relative paths as specified in the source code.

## User Guide

Navigating the Game:

Main Menu:
- Start a new game or load a saved game.
- Access instructions, credits, and parental controls.

Gameplay:
- Perform tasks like feeding, exercising, and studying by interacting with the UI.
- Monitor the student’s stats (health, happiness, energy, stress) and maintain balance.

Mini-Game:
- Tests are interactive mini-games where players must answer questions or solve puzzles to help their student pass exams.

Parental Controls:

Access:
- Navigate to "Parental Controls" from the main menu.
- Set or enter a 4-digit pin to access settings.

Features:
- Configure allowed play hours (mornings, afternoons, nights).
- View average and total playtime statistics.
- Reset playtime stats.
- Enable/disable gameplay blocks during restricted hours.

Default Credentials:
- Username/Password: None required.

Parental Pin:
- Default: 1234
- You can change the pin in the parental control settings.

## Helpful Notes

- Ensure all assets (images, sounds, fonts) are correctly placed in the assets folder before running the game.
- To enable parental controls, initialize them from the JSON handler by invoking initializeParentalControls().
- For troubleshooting, refer to the console output for debug logs.
- Contact team members for additional support or questions related to code functionality.
For example, `core:clean` removes `build` folder only from the `core` project.
