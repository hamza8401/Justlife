# Justlife

## Project Overview

Justlife is a system designed to manage cleaning professionals, vehicles, and booking appointments. It handles scheduling, availability checks, and manages multiple professionals and vehicles for cleaning services.

## Project Setup

### Prerequisites

- **Java 17** or higher
- **Maven**
- **MySQL**
- **Spring Boot**

### Cloning the Repository

1. **Clone the repository:**

    ```bash
    git clone https://github.com/hamza8401/Justlife.git
    ```

2. **Navigate to the project directory:**

    ```bash
    cd Justlife
    ```

### Configuration

1. **Create a MySQL Database:**
   
   Set up a MySQL database for the project. You can name it according to your preference, e.g., `justlife_db`.

2. **Configure Application Properties:**

   Update the `src/main/resources/application.properties` file with your database configuration:

    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/justlife_db
    spring.datasource.username=<your-username>
    spring.datasource.password=<your-password>
    spring.jpa.hibernate.ddl-auto=update
    ```

### Building the Project

1. **Build the project using Maven:**

    ```bash
    mvn clean install
    ```

2. **Package the project into a JAR file:**

    ```bash
    mvn package
    ```

### Running the Application

1. **Run the application using Maven:**

    ```bash
    mvn spring-boot:run
    ```

2. **Alternatively, run the JAR file:**

    ```bash
    java -jar target/justlife-0.0.1-SNAPSHOT.jar
    ```

### API Documentation

API documentation is available through Swagger. After starting the application, you can access it at:

- **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### Testing

1. **Run unit tests and functional tests:**

    ```bash
    mvn test
    ```

2. **Run tests with code coverage:**

    ```bash
    mvn clean test jacoco:report
    ```

### Contact

For any questions or support, please contact [hamzaahmed.cs@gmail.com](mailto:hamzaahmed.cs@gmail.com).
