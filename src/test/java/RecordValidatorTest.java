


import org.example.invoiceapp.data.DataValidator;
import org.junit.jupiter.api.*;


import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class RecordValidatorTest {



    @Test
    void testValidateRecordsWithInvalidFile() {
        List<String> validRecords = DataValidator.validateRecords(Collections.singletonList("invalid_input.txt"));
        assertTrue(validRecords.isEmpty(), "Valid records should be empty for an invalid file");
    }

    @Test
    void testValidateRecordsWithEmptyFile() {
        List<String> validRecords = DataValidator.validateRecords(Collections.singletonList("empty_input.txt"));
        assertTrue(validRecords.isEmpty(), "Valid records should be empty for an empty file");
    }
}

