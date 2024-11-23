package org.example.invoiceapp.billing;

import org.example.invoiceapp.data.CustomerUsage;

import java.util.*;

/**
 * The `MonthlyUsageCalculator` class is responsible for calculating the monthly electricity usage for each customer.
 * It processes consumption data and computes the aggregated daytime and nighttime usage for every customer.
 *
 * <p>
 * The method `calculateMonthlyUsage` aggregates electricity usage data for each customer based on specific time periods.
 * It processes consumption records that contain energy usage values for different time slots during the day and night.
 * </p>
 *
 * <p>
 * Time periods for usage data:
 * - **Nighttime**: 00:00 - 05:59 (Usage1) and 18:00 - 23:59 (Usage4)
 * - **Daytime**: 06:00 - 11:59 (Usage2) and 12:00 - 17:59 (Usage3)
 * </p>
 *
 * <p>
 * The customer usage data is returned as a map where the keys are customer IDs, and the values are `CustomerUsage` objects,
 * which store the aggregated daytime and nighttime usage.
 * </p>
 *
 * <p>
 * The consumption data records follow this format:
 * </p>
 * <pre>
 * Field Number  | Name              | Meaning
 * 1             | Customer ID       | Unique ID for each customer (e.g., 300)
 * 2             | Date              | Date of the consumption in format dd.mm.yyyy
 * 3             | Usage1            | Usage in the period 00:00 - 05:59 (nighttime)
 * 4             | Usage2            | Usage in the period 06:00 - 11:59 (daytime)
 * 5             | Usage3            | Usage in the period 12:00 - 17:59 (daytime)
 * 6             | Usage4            | Usage in the period 18:00 - 23:59 (nighttime)
 * 7             | Quality           | "A" = Actual reading (from the smart meter), "E" = Estimated reading
 * 8             | Error Code        | Error code: 76 = communication failure, 75 = other error
 * 9             | Error Description | Free text description of the error (if applicable)
 * </pre>
 *
 * @param records a list of strings where each string represents a consumption record in the format:
 *                "Customer ID,Date,Usage1,Usage2,Usage3,Usage4,Quality,Error Code,Error Description"
 * @return a map where the keys are customer IDs and the values are `CustomerUsage` objects containing the aggregated daytime and nighttime usage.
 */
public class MonthlyUsageCalculator {

    /**
     * Calculates the daytime and nighttime electricity usage for each customer based on the provided consumption data.
     *
     * <p>
     * This method processes a list of consumption records and aggregates the usage for each customer. The usage is split into
     * daytime and nighttime periods. The nighttime usage is the sum of `Usage1` (00:00 - 05:59) and `Usage4` (18:00 - 23:59).
     * The daytime usage is the sum of `Usage2` (06:00 - 11:59) and `Usage3` (12:00 - 17:59).
     * </p>
     *
     * @param records a list of strings representing consumption records in the format:
     *                "Customer ID,Date,Usage1,Usage2,Usage3,Usage4,Quality,Error Code,Error Description"
     * @return a map where the keys are customer IDs and the values are `CustomerUsage` objects containing the aggregated daytime and nighttime usage.
     */
    public static Map<String, CustomerUsage> calculateMonthlyUsage(List<String> records) {
        Map<String, CustomerUsage> usageMap = new HashMap<>();

        // Process each record in the consumption data
        for (String record : records) {
            String[] fields = record.split(",");

            // Extract customer ID and usage values for the four time periods
            String customerId = fields[0];
            int energyUsageEarlyMorning = Integer.parseInt(fields[2]); // Nighttime: 00:00 - 05:59
            int energyUsageMorning = Integer.parseInt(fields[3]); // Daytime: 06:00 - 11:59
            int energyUsageAfternoon = Integer.parseInt(fields[4]); // Daytime: 12:00 - 17:59
            int energyUsageEvening = Integer.parseInt(fields[5]); // Nighttime: 18:00 - 23:59

            // Calculate the total daytime and nighttime usage for the current record
            int daytimeUsage = energyUsageMorning + energyUsageAfternoon;
            int nighttimeUsage = energyUsageEarlyMorning + energyUsageEvening;

            // Get existing customer usage data or create a new one
            CustomerUsage usage = usageMap.getOrDefault(customerId, new CustomerUsage());

            // Add the calculated usage to the existing data
            usage.addDaytimeUsage(daytimeUsage);
            usage.addNighttimeUsage(nighttimeUsage);

            // Update the map with the customer's usage data
            usageMap.put(customerId, usage);
        }

        // Return the map with aggregated usage data for all customers
        return usageMap;
    }
}
