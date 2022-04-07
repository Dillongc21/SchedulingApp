package com.scheduler.common.model;

import java.sql.Timestamp;

public class Division {
    private final int id;
    private final String name;
    private final Timestamp createDate;
    private final String createdBy;
    private final Timestamp lastUpdate;
    private final String lastUpdatedBy;
    private final int countryId;
    private Country country;

    public Division(int id, String name, Timestamp createDate, String createdBy, Timestamp lastUpdate,
                    String lastUpdatedBy, int countryId) {
        this.id = id;
        this.name = name;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.countryId = countryId;
        this.country = null;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public Timestamp getCreateDate() { return createDate; }
    public String getCreatedBy() { return createdBy; }
    public Timestamp getLastUpdate() { return lastUpdate; }
    public String getLastUpdatedBy() { return lastUpdatedBy; }
    public int getCountryId() { return countryId; }
    public Country getCountry() { return country; }

    public void setCountry(Country country) {
        this.country = (country.getId() == countryId) ? country : null;
    }
}