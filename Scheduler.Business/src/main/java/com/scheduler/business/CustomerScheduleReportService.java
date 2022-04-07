package com.scheduler.business;

import com.scheduler.common.model.*;
import com.scheduler.common.util.Formatter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CustomerScheduleReportService {
    private final List<CustomerScheduleReportItem> items = new ArrayList<>();

    public CustomerScheduleReportService() {
        CustomerService service = CustomerService.getInstance();
        AppointmentService appointmentService = AppointmentService.getInstance();
        List<Customer> customers = service.getAllCustomers();
        List<Appointment> appointments = appointmentService.getAllAppointments();

        customers.forEach(customer -> appointments.stream()
                .filter(a -> a.getCustomerId() == customer.getId())
                .sorted(Comparator.comparing(Appointment::getStart))
                .forEach(appointment -> items.add(new CustomerScheduleReportItem(customer.getName(), appointment.getId(),
                        appointment.getTitle(), appointment.getType(), appointment.getDescription(),
                        Formatter.toDisplay(appointment.getStartDefault()),
                        Formatter.toDisplay(appointment.getEndDefault()), appointment.getContactId()))));
    }

    public List<CustomerScheduleReportItem> getItems() { return items; }


}
