# Invoice Generator Application

This is an Invoice Generator application that processes electricity consumption data, calculates the usage costs for customers, generates bills, and stores the data in a database. It validates records, generates PDF and text bills, and saves invalid records for auditing.

## Features
- **Record Validation:** Validates consumption records based on predefined criteria (e.g., non-negative usage values, valid quality).
- **Bill Generation:** Generates both text and PDF bills based on electricity usage data.
- **Database Integration:** Saves bill details (usage data and costs) into a MySQL database.
- **Error Handling:** Writes invalid records to a separate file for auditing.

## Technologies
- Java 11+
- iText PDF library
- MySQL database
- Logging (Java `Logger`)



markdown
Copy code

## Installation

### Prerequisites
- Java 11 or higher
- MySQL database setup
- Maven for building the project

### Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/invoice-generator.git
   cd invoice-generator
Set up your MySQL database:

Create a database named invoiceapp.
Ensure your MySQL credentials (username, password) are correct in application.properties:
properties
Copy code
db.url=jdbc:mysql://localhost:3306/invoiceapp
db.username=root
db.password=yourpassword
Configure paths in application.properties: Ensure the paths to the input files (input.txt, lookup.txt) and output directories (e.g., pdf/, txt/) are correct for your environment.

Build the project using Maven:

bash
Copy code
mvn clean install
Run the application:

bash
Copy code
mvn exec:java -Dexec.mainClass="org.example.invoiceapp.Main"
This will:

Read the consumption data from input.txt.
Validate and process the data.
Generate text and PDF bills.
Save the valid records to the database.
Write invalid records to error_records/E_records.txt.
Configuration
All file paths and other configurations are externalized in the application.properties file. Modify the properties to match your environment:

properties
Copy code
# Path to the consumption data file
consumption.data.path=src/main/resources/input.txt

# Path to the customer lookup file
customer.lookup.path=src/main/resources/lookup.txt

# Path to output directories
pdf.output.path=src/main/resources/output/pdf/
txt.output.path=src/main/resources/output/txt/

# Error file path for invalid records
error.file.path=src/main/resources/output/error_records/E_records.txt

# Database credentials
db.url=jdbc:mysql://localhost:3306/invoiceapp
db.username=root
db.password=yourpassword
Usage
1. Reading Data:
The DataReader class is used to read both consumption data and customer lookup data from the specified files.
2. Validating Records:
The RecordValidator class ensures that only valid records are processed based on usage and quality criteria. Invalid records are written to an error file.
3. Bill Generation:
The BillGenerator and PdfBillGenerator classes generate text and PDF bills for each customer.
4. Storing Data:
Valid records are stored in the database for future reference.
Contributing
We welcome contributions! If you have any improvements or suggestions, feel free to fork this project, submit an issue, or open a pull request.
