package com.scheduler.business;

import com.scheduler.common.model.Appointment;
import com.scheduler.common.model.Contact;
import com.scheduler.common.model.ContactScheduleReportItem;
import com.scheduler.common.util.Formatter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ContactScheduleReportService {
    private final List<ContactScheduleReportItem> items = new ArrayList<>();

    public ContactScheduleReportService() {
        ContactService contactService = ContactService.getInstance();
        AppointmentService appointmentService = AppointmentService.getInstance();
        List<Contact> contacts = contactService.getContacts();
        List<Appointment> appointments = appointmentService.getAllAppointments();

        contacts.forEach(contact -> appointments.stream()
                    .filter(a -> a.getContactId() == contact.getId())
                    .sorted(Comparator.comparing(Appointment::getStart))
                    .forEach(appointment -> items.add(new ContactScheduleReportItem(contact.getName(), appointment.getId(),
                                appointment.getTitle(), appointment.getType(), appointment.getDescription(),
                                Formatter.toDisplay(appointment.getStartDefault()),
                                Formatter.toDisplay(appointment.getEndDefault()), appointment.getCustomerId()))));
    }

    public List<ContactScheduleReportItem> getItems() { return items; }


}
