package org.example.invoiceapp.data;

/*The CustomerUsage class is designed to store and manage the daytime and nighttime electricity usage for a customer.
 It provides methods to add usage and retrieve the current usage values.*/
public class CustomerUsage {

    //An integer representing the amount of electricity used during the daytime.
    private int daytimeUsage;
    //An integer representing the amount of electricity used during the
    private int nighttimeUsage;


    //Adds a specified amount of usage to the current daytime usage.
    public void addDaytimeUsage(int usage) {
        this.daytimeUsage += usage;
    }

    //Adds a specified amount of usage to the current nighttime usage.
    public void addNighttimeUsage(int usage) {
        this.nighttimeUsage += usage;
    }

    //Returns the current daytime usage.
    public int getDaytimeUsage() {
        return daytimeUsage;
    }

    //Returns the current nighttime usage.
    public int getNighttimeUsage() {
        return nighttimeUsage;
    }
}
