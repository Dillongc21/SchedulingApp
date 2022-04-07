package com.scheduler.business;

import com.scheduler.common.model.Appointment;
import com.scheduler.common.model.MonthTypeReportItem;
import com.scheduler.common.util.Formatter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MonthTypeReportService {
    private final List<MonthTypeReportItem> items = new ArrayList<>();

    public MonthTypeReportService() {
        AppointmentService appointmentService = AppointmentService.getInstance();
        List<Appointment> appointments = appointmentService.getAllAppointments();

        appointments.stream()
                .collect(Collectors.groupingBy(a -> Formatter.toMonthName(a.getStartDefault()),
                        Collectors.groupingBy(Appointment::getType, Collectors.counting())))
                .forEach((month, map) -> {
                    map.forEach((type, total) -> {
                        items.add(new MonthTypeReportItem(month, type, total.intValue()));
                    });
                });
    }

    public List<MonthTypeReportItem> getItems() { return items; }


}
