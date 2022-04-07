package com.scheduler.common.model;

import java.sql.Timestamp;

public class User {
    private final int id;
    private final String username;
    private final String password;
    private final Timestamp createDate;
    private final String createdBy;
    private final Timestamp lastUpdate;
    private final String lastUpdatedBy;

    public User(int id, String username, String password, Timestamp createDate,
                String createdBy, Timestamp lastUpdate, String lastUpdatedBy) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public User(String username, String password) {
        this.id = -1;
        this.username = username;
        this.password = password;
        this.createDate = null;
        this.createdBy = null;
        this.lastUpdate = null;
        this.lastUpdatedBy = null;
    }

    public int getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public Timestamp getCreateDate() {
        return createDate;
    }
    public String getCreatedBy() {
        return createdBy;
    }
    public Timestamp getLastUpdate() {
        return lastUpdate;
    }
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }


}
