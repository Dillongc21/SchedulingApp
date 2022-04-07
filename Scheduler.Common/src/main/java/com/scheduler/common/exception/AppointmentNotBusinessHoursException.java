package com.scheduler.common.exception;

public class AppointmentNotBusinessHoursException extends Exception {
    public AppointmentNotBusinessHoursException(String msg) { super(msg); }
}
