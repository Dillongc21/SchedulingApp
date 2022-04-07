package com.scheduler.common.model;

import java.sql.Timestamp;

public class Customer {
    private final int id;
    private final String name;
    private final String address;
    private final String postalCode;
    private final String phone;
    private final Timestamp createDate;
    private final String createdBy;
    private final Timestamp lastUpdate;
    private final String lastUpdatedBy;
    private final int divisionId;
    private String extendedAddress;

    public Customer(int id, String name, String address, String postalCode, String phone, Timestamp createDate,
                    String createdBy, Timestamp lastUpdate, String lastUpdatedBy, int divisionId) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.divisionId = divisionId;
    }

    public Customer(String name, String address, String postalCode, String phone, int divisionId) {
        this.id = -1;
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.createDate = null;
        this.createdBy = "";
        this.lastUpdate = null;
        this.lastUpdatedBy = "";
        this.divisionId = divisionId;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPostalCode() { return postalCode; }
    public String getPhone() { return phone; }
    public Timestamp getCreateDate() { return createDate; }
    public String getCreatedBy() { return createdBy; }
    public Timestamp getLastUpdate() { return lastUpdate; }
    public String getLastUpdatedBy() { return lastUpdatedBy; }
    public int getDivisionId() { return divisionId; }
    public String getExtendedAddress() { return extendedAddress; }

    public void setExtendedAddress(String extendedAddress) {
        this.extendedAddress = extendedAddress;
    }
}
