


import org.example.invoiceapp.data.RecordValidator;
import org.junit.jupiter.api.*;


import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class RecordValidatorTest {

    @Test
    void testValidateRecords() {
        List<String> validRecords = RecordValidator.validateRecords(Collections.singletonList("input.txt"));
        assertFalse(validRecords.isEmpty(), "Valid records should not be empty");
    }

    @Test
    void testValidateRecordsWithInvalidFile() {
        List<String> validRecords = RecordValidator.validateRecords(Collections.singletonList("invalid_input.txt"));
        assertTrue(validRecords.isEmpty(), "Valid records should be empty for an invalid file");
    }

    @Test
    void testValidateRecordsWithEmptyFile() {
        List<String> validRecords = RecordValidator.validateRecords(Collections.singletonList("empty_input.txt"));
        assertTrue(validRecords.isEmpty(), "Valid records should be empty for an empty file");
    }
}

