package com.scheduler.common.model;

public class CustomerScheduleReportItem {
    private final String customerName;
    private final Integer appointmentId;
    private final String title;
    private final String type;
    private final String description;
    private final String start;
    private final String end;
    private final Integer contactId;

    public CustomerScheduleReportItem(String customerName, Integer appointmentId, String title, String type,
                                      String description, String start, String end, Integer contactId) {
        this.customerName = customerName;
        this.appointmentId = appointmentId;
        this.title = title;
        this.type = type;
        this.description = description;
        this.start = start;
        this.end = end;
        this.contactId = contactId;
    }

    public String getCustomerName() { return customerName; }
    public Integer getAppointmentId() { return appointmentId; }
    public String getTitle() { return title; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public String getStart() { return start; }
    public String getEnd() { return end; }
    public Integer getContactId() { return contactId; }
}
