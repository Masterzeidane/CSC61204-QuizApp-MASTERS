# Educational Testing System

## Project Description
This project is an **Educational Testing System** (Scenario 2) built using Java Swing. It demonstrates strict adherence to the **Model-View-Controller (MVC)** architectural pattern and utilizes essential Object-Oriented Design Patterns, including the **Builder Pattern** for robust quiz construction and the **Strategy Pattern** for flexible question selection. 

The application integrates seamlessly with the **Open Trivia Database (OpenTDB) API** via Java 11's `HttpClient` to fetch dynamic, multi-category quiz questions. It features graceful degradation to ensure stability; if the API is unreachable, the system automatically falls back to a locally bundled question bank covering core Software Engineering principles (SOLID, MVC, OOP).

## Requirements
* **Java:** Java 17 or higher (Java 21 is currently configured in the POM)
* **Maven:** (Only required if you wish to recompile the project from source)

## Run Instructions

For your convenience, the project has been configured with the `maven-assembly-plugin` to compile into a fully executable "Fat Jar" that bundles all external dependencies (like Gson) internally.

Please follow these step-by-step instructions to run the application from your terminal:

### Step 1: Open Terminal
Open your terminal or command prompt and navigate to the root directory of this project.

### Step 2: (Optional) Build the Jar
If the `target/` directory does not already contain the compiled `.jar`, you can easily generate it using Maven:
```bash
mvn clean package -DskipTests
```

### Step 3: Execute the Application
Run the following command from the root of the project to launch the Java Swing GUI:
```bash
java -jar target/quizapp-1.0-SNAPSHOT-jar-with-dependencies.jar
```

### Using the Application:
1. **Setup View:** Select your desired number of questions and difficulty level from the drop-downs, then click **"Start Quiz"**.
2. **Testing View:** Read the question, select an answer via the radio buttons, and click **"Next"** to advance.
3. **Result View:** Once finished, your final score is displayed. You may click **"Play Again"** to return to the setup screen.
