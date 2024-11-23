package org.example.invoiceapp.data;

/**
 * The `CustomerUsage` class represents a customer's electricity usage,
 * specifically divided into daytime and nighttime usage.
 */
public class CustomerUsage {

    /**
     * The amount of electricity (in kWh) consumed during the daytime.
     */
    private int daytimeUsage;

    /**
     * The amount of electricity (in kWh) consumed during the nighttime.
     */
    private int nighttimeUsage;

    /**
     * Constructs a new `CustomerUsage` object with specified initial values for
     * daytime and nighttime usage.
     *
     * @param daytimeUsage   initial amount of daytime electricity usage (in kWh)
     * @param nighttimeUsage initial amount of nighttime electricity usage (in kWh)
     */
    public CustomerUsage(int daytimeUsage, int nighttimeUsage) {
        this.daytimeUsage = daytimeUsage;
        this.nighttimeUsage = nighttimeUsage;
    }

    /**
     * Constructs a new `CustomerUsage` object with zero usage for both daytime
     * and nighttime.
     */
    public CustomerUsage() {
        this.daytimeUsage = 0;
        this.nighttimeUsage = 0;
    }

    /**
     * Adds the specified amount of usage to the current daytime usage.
     *
     * @param usage the amount of electricity to add to daytime usage (in kWh)
     */
    public void addDaytimeUsage(int usage) {
        this.daytimeUsage += usage;
    }

    /**
     * Adds the specified amount of usage to the current nighttime usage.
     *
     * @param usage the amount of electricity to add to nighttime usage (in kWh)
     */
    public void addNighttimeUsage(int usage) {
        this.nighttimeUsage += usage;
    }

    /**
     * Returns the current amount of daytime electricity usage.
     *
     * @return the daytime electricity usage (in kWh)
     */
    public int getDaytimeUsage() {
        return daytimeUsage;
    }

    /**
     * Returns the current amount of nighttime electricity usage.
     *
     * @return the nighttime electricity usage (in kWh)
     */
    public int getNighttimeUsage() {
        return nighttimeUsage;
    }
}
