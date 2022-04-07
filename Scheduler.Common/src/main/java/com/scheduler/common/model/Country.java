package com.scheduler.common.model;

import java.sql.Timestamp;

public class Country {
    private final int id;
    private final String name;
    private final Timestamp createDate;
    private final String createdBy;
    private final Timestamp lastUpdate;
    private final String lastUpdatedBy;

    public Country(int id, String name, Timestamp createDate, String createdBy, Timestamp lastUpdate,
                   String lastUpdatedBy) {
        this.id = id;
        this.name = name;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public Timestamp getCreateDate() { return createDate; }
    public String getCreatedBy() { return createdBy; }
    public Timestamp getLastUpdate() { return lastUpdate; }
    public String getLastUpdatedBy() { return lastUpdatedBy; }
}
