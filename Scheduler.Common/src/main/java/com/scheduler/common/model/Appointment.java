package com.scheduler.common.model;

import java.sql.Timestamp;
import java.time.ZonedDateTime;

public class Appointment {
    private final int id;
    private final String title;
    private final String description;
    private final String location;
    private final String type;
    private final Timestamp start;
    private final Timestamp end;
    private final Timestamp createDate;
    private final String createdBy;
    private final Timestamp lastUpdate;
    private final String lastUpdatedBy;
    private final int customerId;
    private final int userId;
    private final int contactId;
    private ZonedDateTime startDefault;
    private ZonedDateTime endDefault;
    private String contactName;

    public Appointment(int id, String title, String description, String location, String type, Timestamp start,
                       Timestamp end, Timestamp createDate, String createdBy, Timestamp lastUpdate,
                       String lastUpdatedBy, int customerId, int userId, int contactId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.start = start;
        this.end = end;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.customerId = customerId;
        this.userId = userId;
        this.contactId = contactId;
    }

    public Appointment(String title, String description, String location, String type, Timestamp start,
                       Timestamp end, int customerId, int userId, int contactId) {
        this.id = -1;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.start = start;
        this.end = end;
        this.customerId = customerId;
        this.userId = userId;
        this.contactId = contactId;
        this.createDate = null;
        this.createdBy = "";
        this.lastUpdate = null;
        this.lastUpdatedBy = "";
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public String getType() { return type; }
    public Timestamp getStart() { return start; }
    public Timestamp getEnd() { return end; }
    public Timestamp getCreateDate() { return createDate; }
    public String getCreatedBy() { return createdBy; }
    public Timestamp getLastUpdate() { return lastUpdate; }
    public String getLastUpdatedBy() { return lastUpdatedBy; }
    public int getCustomerId() { return customerId; }
    public int getUserId() { return userId; }
    public int getContactId() { return contactId; }
    public ZonedDateTime getStartDefault() { return startDefault; }
    public ZonedDateTime getEndDefault() { return endDefault; }
    public String getContactName() { return contactName; }

    public void setStartDefault(ZonedDateTime startDefault) { this.startDefault = startDefault; }
    public void setEndDefault(ZonedDateTime endDefault) { this.endDefault = endDefault; }
    public void setContactName(String contactName) { this.contactName = contactName; }
}
