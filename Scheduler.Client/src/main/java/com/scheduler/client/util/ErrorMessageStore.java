package com.scheduler.client.util;

public class ErrorMessageStore {
    final static public String NO_CUSTOMER_SELECTED = "Please select a customer to modify.";
    final static public String NO_APPOINTMENT_SELECTED = "Please select an appointment to modify.";
    final static public String REQUIRED_FIELDS = "Please complete all required fields.";
    final static public String APPOINTMENTS_NOT_DELETED = "An error occurred and not all associated appointments " +
            "were deleted. Customer deletion aborted.";
    final static public String APPOINTMENT_HOURS_INVALID = "WARNING: Appointment does not fall within business " +
            "hours between 8 AM and 10 PM EST. Would you like to proceed?";
    final static public String APPOINTMENT_OVERLAP = "WARNING: Appointment overlaps with another appointment the " +
            "customer has scheduled. Would you like to proceed?";
    final static public String APPOINTMENT_END_BEFORE_START = "End time cannot be before the Start time.";
    final static public String SET_FILE_WRITABLE_FAILED = "Setting file to writable failed.";
    final static public String SET_FILE_READONLY_FAILED = "Setting file to read-only failed.";
}
