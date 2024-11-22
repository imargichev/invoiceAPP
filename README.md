# InvoiceApp

InvoiceApp is a Java-based application that generates and manages customer invoices. It supports generating both text and PDF bills based on customer usage data and saving the bill information to a database.

## Features

- Generate text and PDF bills for customers
- Save bill information to a database
- Configurable file paths and database connection details
- Logging for tracking bill generation and database operations

## Technologies Used

- Java
- Spring Boot
- Maven
- iText (for PDF generation)
- MySQL (for database)

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven
- MySQL

### Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/yourusername/invoiceapp.git
    cd invoiceapp
    ```

2. Configure the application properties:
    Update the `src/main/resources/application.properties` file with your database connection details and file paths.

3. Build the project:
    ```sh
    mvn clean install
    ```

4. Run the application:
    ```sh
    mvn spring-boot:run
    ```

## Configuration

The application properties are configured in the `src/main/resources/application.properties` file. Here are some key properties:

```ini
# File paths configuration
consumption.data.path=src/main/resources/input.txt
customer.lookup.path=src/main/resources/lookup.txt
error.file.path=src/main/resources/output/error_records/E_records.txt

# Necessary paths
pdf.output.path=src/main/resources/output/pdf/
txt.output.path=src/main/resources/output/txt/
mainDir.output=src/main/resources/output/
errorTxt.file.path=src/main/resources/output/error_records/

# Log level configuration
log.level=INFO

Usage
Place the customer usage data file at the path specified in consumption.data.path.
Run the application using the command mentioned above.
The generated bills will be saved in the directories specified in pdf.output.path and txt.output.path.
Logging
The application uses Java's built-in logging framework. Log messages are configured to be displayed at the INFO level by default. You can change the log level in the application.properties file.
