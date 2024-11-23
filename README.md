# InvoiceApp

InvoiceApp is a Java-based application that generates and manages customer invoices. It supports generating text and PDF bills based on customer usage data and saving the bill information to a database.

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
- MySQL 

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven
- MySQL

# Data Field Definitions

The table below describes the fields used in the provided data files:

| **Field Number** | **Name**            | **Description**                                                                                  |
|-------------------|---------------------|--------------------------------------------------------------------------------------------------|
| 1                 | **Customer ID**     | The unique ID of the customer (e.g., `300` refers to a customer with ID `300`).                 |
| 2                 | **Date**            | The date of consumption in the format `dd.mm.yyyy`.                                             |
| 3                 | **Usage1**          | Energy usage during the period `00:00 - 05:59`.                                                 |
| 4                 | **Usage2**          | Energy usage during the period `06:00 - 11:59`.                                                 |
| 5                 | **Usage3**          | Energy usage during the period `12:00 - 17:59`.                                                 |
| 6                 | **Usage4**          | Energy usage during the period `18:00 - 23:59`.                                                 |
| 7                 | **Quality**         | Data quality indicator:                                                                         |
|                   |                     | - **A**: Actual reading (reported by the smart meter).                                          |
|                   |                     | - **E**: Estimated reading (precise data unavailable).                                          |
| 8                 | **Error Code**      | Error codes indicating issues:                                                                  |
|                   |                     | - `76`: Communication failure.                                                                 |
|                   |                     | - `75`: Other errors (see "Error Description").                                                 |
| 9                 | **Error Description** | Free-text description of errors for cases where `Error Code = 0`.                              |
 

### Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/imargichev/invoiceApp.git
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

The application properties are configured in the `src/main/resources/application.properties` file.
Usage
Place the customer usage data file at the consumption.data.path.
Run the application using the command mentioned above.
The generated bills will be saved in the directories specified in pdf.output.path and txt.output.path.
Logging

Pleaese find the result from the application :

<img width="296" alt="image" src="https://github.com/user-attachments/assets/acbca8ca-cfa2-47d8-a6fb-bf96e8aa2e79">

<img width="239" alt="image" src="https://github.com/user-attachments/assets/ab4dc450-7e32-43fd-a4c0-f2b7c1dc7517">


The application uses Java's built-in logging framework. Log messages are configured to be displayed at the INFO level by default. You can change the log level in the application.properties file.



