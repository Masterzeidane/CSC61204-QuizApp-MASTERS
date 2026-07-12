# Educational Testing System

## Project Description
This project is an **Educational Testing System** (Scenario 2) built using Java Swing. It demonstrates strict adherence to the **Model-View-Controller (MVC)** architectural pattern and utilizes essential Object-Oriented Design Patterns, including the **Builder Pattern** for robust quiz construction and the **Strategy Pattern** for flexible question selection. 

The application integrates seamlessly with the **Open Trivia Database (OpenTDB) API** via Java 11's `HttpClient` to fetch dynamic, multi-category quiz questions. It features graceful degradation to ensure stability; if the API is unreachable, the system automatically falls back to a locally bundled question bank covering core Software Engineering principles (SOLID, MVC, OOP).

## Requirements
* **Java:** Java 17 or higher (Java 21 is currently configured in the POM)
* **Maven:** (Only required if you wish to compile or run tests from source)

## Run Instructions

For your convenience, a pre-compiled, fully executable "Fat Jar" (`quizapp.jar`) is provided directly in the root directory. It bundles all external dependencies (like Gson) internally.

Please follow these instructions to run the application:

### Method 1: Run the Pre-compiled Jar (Quickest)
1. Open your terminal or command prompt.
2. Navigate to the root directory of this project.
3. Run the following command:
   ```bash
   java -jar quizapp.jar
   ```

### Method 2: Build and Run from Source
If you wish to recompile the project from source:
1. Ensure you have Maven installed.
2. Run the following command in the project root directory to build the project:
   ```bash
   mvn clean package -DskipTests
   ```
3. Run the newly compiled jar:
   ```bash
   java -jar target/quizapp-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

### Using the Application:
1. **Setup View:** Select your desired number of questions and difficulty level from the drop-downs, then click **"Start Quiz"**.
2. **Testing View:** Read the question, select an answer via the radio buttons, and click **"Next"** to advance.
3. **Result View:** Once finished, your final score is displayed. You may click **"Play Again"** to return to the setup screen.
