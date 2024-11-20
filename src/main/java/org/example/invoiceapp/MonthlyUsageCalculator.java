package org.example.invoiceapp;

import java.util.*;

public class MonthlyUsageCalculator {
    public static Map<String, CustomerUsage> calculateMonthlyUsage(List<String> records) {
        Map<String, CustomerUsage> usageMap = new HashMap<>();
        for (String record : records) {
            String[] fields = record.split(",");
            String customerId = fields[0];
            int usage1 = Integer.parseInt(fields[2]);
            int usage2 = Integer.parseInt(fields[3]);
            int usage3 = Integer.parseInt(fields[4]);
            int usage4 = Integer.parseInt(fields[5]);

            int daytimeUsage = usage2 + usage3;
            int nighttimeUsage = usage1 + usage4;

            CustomerUsage usage = usageMap.getOrDefault(customerId, new CustomerUsage());
            usage.addDaytimeUsage(daytimeUsage);
            usage.addNighttimeUsage(nighttimeUsage);
            usageMap.put(customerId, usage);
        }
        return usageMap;
    }
}
