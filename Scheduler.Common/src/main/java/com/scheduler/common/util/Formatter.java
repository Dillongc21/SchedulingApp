package com.scheduler.common.util;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Formatter {
    public static int toMilitaryHour(int hour, TimeMeridiem meridiem) {
        if (meridiem == TimeMeridiem.AM)
            return (hour == 12) ? 0 : hour;
        else if (meridiem == TimeMeridiem.PM)
            return (hour == 12) ? hour : hour + 12;
        else return -1;
    }

    public static int toStandardHour(ZonedDateTime dateTime) {
        DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("h");
        return Integer.parseInt(dtFormatter.format(dateTime));
    }

    public static int toStandardMinute(ZonedDateTime dateTime) {
        DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("mm");
        return Integer.parseInt(dtFormatter.format(dateTime));
    }

    public static String toStandardMeridiem(ZonedDateTime dateTime) {
        DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("a");
        return dtFormatter.format(dateTime);
    }

    public static String toMonthName(ZonedDateTime dateTime) {
        DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("MMMM");
        return dtFormatter.format(dateTime);
    }

    public static String toDisplay(ZonedDateTime dateTime) {
        DateTimeFormatter today = DateTimeFormatter.ofPattern("'Today' - h:mm a");
        DateTimeFormatter withinWeek = DateTimeFormatter.ofPattern("EEEE - h:mm a");
        DateTimeFormatter outsideWeek = DateTimeFormatter.ofPattern("dd/MM/yy - h:mm a");

        if (dateTime.isAfter(ZonedDateTime.now().with(LocalTime.of(0,0,0))) &&
                dateTime.isBefore(ZonedDateTime.now().with(LocalTime.of(23,59,59))))
            return today.format(dateTime);
        if (dateTime.isBefore(ZonedDateTime.now().plusDays(6).with(LocalTime.of(23,59,59))) &&
            dateTime.isAfter(ZonedDateTime.now().with(LocalTime.of(0,0,0))))
            return withinWeek.format(dateTime);
        else
            return outsideWeek.format(dateTime);

    }

    public static String toAttemptDisplay(ZonedDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy 'at' hh:mm a z");
        return formatter.format(dateTime);
    }
}
