package com.scheduler.common.model;

public class ContactScheduleReportItem {
    private final String contactName;
    private final Integer appointmentId;
    private final String title;
    private final String type;
    private final String description;
    private final String start;
    private final String end;
    private final Integer customerId;

    public ContactScheduleReportItem(String contactName, Integer appointmentId, String title, String type,
                                     String description, String start, String end, Integer customerId) {
        this.contactName = contactName;
        this.appointmentId = appointmentId;
        this.title = title;
        this.type = type;
        this.description = description;
        this.start = start;
        this.end = end;
        this.customerId = customerId;
    }

    public String getContactName() { return contactName; }
    public Integer getAppointmentId() { return appointmentId; }
    public String getTitle() { return title; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public String getStart() { return start; }
    public String getEnd() { return end; }
    public Integer getCustomerId() { return customerId; }
}
