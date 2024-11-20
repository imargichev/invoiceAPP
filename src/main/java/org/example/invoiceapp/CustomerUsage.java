package org.example.invoiceapp;

public class CustomerUsage {
    private int daytimeUsage;
    private int nighttimeUsage;

    public void addDaytimeUsage(int usage) {
        this.daytimeUsage += usage;
    }

    public void addNighttimeUsage(int usage) {
        this.nighttimeUsage += usage;
    }

    public int getDaytimeUsage() {
        return daytimeUsage;
    }

    public int getNighttimeUsage() {
        return nighttimeUsage;
    }
}
