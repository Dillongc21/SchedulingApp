package com.scheduler.business;

import com.scheduler.access.object.mysql.AppointmentDAO;
import com.scheduler.common.model.Appointment;
import com.scheduler.common.util.AppointmentTimeSpan;

import org.javatuples.Pair;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Class performs business logic operations on Appointment objects. Implemented using a Singleton pattern to simplify
 * keeping Appointment objects in sync across Front End and Business Logic, and between those objects and the Database.
 *
 * @author Dillon Christensen
 */
public class AppointmentService {
    private static volatile AppointmentService instance = null;
    private final ContactService contactService;
    private final AppointmentDAO dao;
    private final List<Appointment> appointments;

    /**
     * Constructor: Sets up DAO to create a list of appointments, pulled from the database. Sets up an instance of the
     * Contact Service. Populates fields that help with front end functionality.
     *
     * @see com.scheduler.business.ContactService Contact Service
     */
    private AppointmentService() {
        contactService = ContactService.getInstance();
        dao = new AppointmentDAO();
        appointments = dao.readAll();
        appointments.forEach(this::populateContactNameField);
        appointments.forEach(this::populateDateTimeFields);
    }

    /**
     * Creates singleton instance if it was not created already, then gets the instance.
     *
     * @return Singleton instance
     */
    public static AppointmentService getInstance() {
        if (instance == null) {
            synchronized (AppointmentService.class) {
                if (instance == null) {
                    instance = new AppointmentService();
                }
            }
        }
        return instance;
    }

    /**
     * Getter for the appointments list.
     * @return Appointments list
     * @see Appointment
     */
    public List<Appointment> getAllAppointments() { return appointments; }

    /**
     * Gets an appointment by its unique ID.
     * @param id unique identifier used to query appointment
     * @return Matching appointment or <code>null</code> if no matching appointment was found
     */
    public Appointment getAppointmentById(int id) {
        List<Appointment> filtered = appointments.stream()
                .filter(appointment -> appointment.getId() == id)
                .toList();
        return filtered.isEmpty() ? null : filtered.get(0);
    }

    /**
     * Create a new appointment in the database. If the new DB appointment is created successfully, update the internal
     * list of appointments in the CustomerService instance with the newly created one.
     * @param appointment Appointment to be created on Database
     * @return Newly created appointment or <code>null</code> if creation was unsuccessful
     * @see #appointments
     */
    public Appointment createAppointment(Appointment appointment) {
        Appointment created = dao.create(appointment);
        if (created != null) {
            populateContactNameField(created);
            populateDateTimeFields(created);
            appointments.add(created);
        }
        return created;
    }

    /**
     * Delete appointment from database. If the appointment is successfully deleted, it is removed from the list of
     * appointments in the CustomerService instance.
     * @param id unique identifier used to query appointment
     * @return boolean value; true if deletion was successful
     * @see #appointments
     */
    public Appointment deleteAppointment(int id) {
        Appointment toDelete = null;
        boolean wasDeleted = dao.delete(id);
        if (wasDeleted) {
            toDelete = appointments.stream()
                    .filter(a -> a.getId() == id)
                    .toList().get(0);
        }
        return toDelete;
    }

    /**
     * Update appointment from database. If the appointment is successfully updated, the defunct one is removed from the
     * list of appointments in the CustomerService instance and the updated one is added.
     * @param appointment appointment with updated values and same ID field as existing appointment
     * @return updated appointment if update was successful or <em>null</em> if not
     * @see #appointments
     */
    public Appointment updateAppointment(Appointment appointment) {
        Appointment updated = dao.update(appointment);
        if (updated != null) {
            populateContactNameField(updated);
            populateDateTimeFields(updated);
            Appointment toRemove = appointments.stream()
                    .filter(a -> a.getId() == updated.getId())
                    .toList().get(0);
            appointments.remove(toRemove);
            appointments.add(updated);
        }
        return updated;
    }

    /**
     * Sets the given appointment's {@link com.scheduler.common.model.Appointment#contactName contactName} with the
     * name associated with the existing {@link com.scheduler.common.model.Appointment#contactId contactId}.
     * @param appointment appointment to be modified
     */
    public void populateContactNameField(Appointment appointment) {
        String contactName = contactService.getNameById(appointment.getContactId());
        appointment.setContactName(contactName);
    }

    /**
     * Sets the given appointment's {@link com.scheduler.common.model.Appointment#startDefault startDefault} and
     * {@link com.scheduler.common.model.Appointment#endDefault endDefault} with
     * {@link ZonedDateTime} objects, created from the appointment's
     * {@link com.scheduler.common.model.Appointment#start start} and
     * {@link com.scheduler.common.model.Appointment#end end}. Time zone set to {@link ZoneId#systemDefault()}.
     * @param appointment appointment to be modified
     */
    public void populateDateTimeFields(Appointment appointment) {
        LocalDateTime start = appointment.getStart().toLocalDateTime();
        LocalDateTime end = appointment.getEnd().toLocalDateTime();

        ZonedDateTime startUTC = ZonedDateTime.of(start, ZoneId.of("UTC"));
        ZonedDateTime endUTC = ZonedDateTime.of(end, ZoneId.of("UTC"));

        ZonedDateTime startDefault = startUTC.withZoneSameInstant(ZoneId.systemDefault());
        ZonedDateTime endDefault = endUTC.withZoneSameInstant(ZoneId.systemDefault());

        appointment.setStartDefault(startDefault);
        appointment.setEndDefault(endDefault);
    }

    /**
     * Delete all appointments with a {@link com.scheduler.common.model.Appointment#customerId customerId} of the given
     * customerId parameter.
     * @param customerId value of {@link com.scheduler.common.model.Appointment#customerId customerId} of appointments
     *                   to be deleted.
     * @return list of appointments successfully deleted and list of appointments that failed to delete
     */
    public Pair<List<Appointment>, List<Appointment>> deleteAppointmentsByCustomerId(int customerId) {
        List<Appointment> allDeleted = new ArrayList<>();
        List<Appointment> allNotDeleted = new ArrayList<>();

        List<Appointment> toDeleteAll = appointments.stream()
                .filter(a -> a.getCustomerId() == customerId)
                .toList();

        toDeleteAll.forEach(a -> {
            Appointment deleted = deleteAppointment(a.getId());
            if (deleted != null)
                allDeleted.add(deleted);
            else
                allNotDeleted.add(a);
        });
        return new Pair<>(allDeleted, allNotDeleted);
    }

    /**
     * Gets a list of appointments, filtered by an {@link com.scheduler.common.util.AppointmentTimeSpan} instance amount
     * of time after the current time.
     * @param span the amount of time after now, to filter the appointments
     * @return the filtered appointments or all appointments if the
     * {@link com.scheduler.common.util.AppointmentTimeSpan} value is not accounted for
     */
    public List<Appointment> getAppointmentsByTimeSpan(AppointmentTimeSpan span) {
        ZonedDateTime monthAfter = ZonedDateTime.now().plusMonths(1);
        ZonedDateTime weekAfter = ZonedDateTime.now().plusWeeks(1);
        ZonedDateTime now = ZonedDateTime.now();
        List<Appointment> filtered;

        switch (span) {
            case MONTH -> filtered = appointments.stream()
                    .filter(appointment -> appointment.getStartDefault().isBefore(monthAfter) &&
                            appointment.getStartDefault().isAfter(now))
                    .collect(Collectors.toList());
            case WEEK -> filtered = appointments.stream()
                    .filter(appointment -> appointment.getStartDefault().isBefore(weekAfter) &&
                            appointment.getStartDefault().isAfter(now))
                    .collect(Collectors.toList());
            default -> filtered = appointments;
        }

        return filtered;
    }

    /**
     * Get all appointments with a {@link com.scheduler.common.model.Appointment#customerId customerId} of the given
     * customerId parameter.
     * @param customerId value of {@link com.scheduler.common.model.Appointment#customerId customerId} of appointments
     *                   to be deleted.
     * @return list of appointments found; returns empty if none are found
     */
    public List<Appointment> getAppointmentsByCustomerId(int customerId) {
        return appointments.stream()
                .filter(a -> a.getCustomerId() == customerId)
                .toList();
    }

    /**
     * Verifies that the given {@link ZonedDateTime} falls within business hours in the "ETC" Timezone.
     * @param zonedDateTime Time to be checked
     * @return boolean value; true if time falls within ETC Business hours (8:00 AM - 10:00 PM)
     */
    public boolean checkForESTBusinessHours(ZonedDateTime zonedDateTime) {
        ZonedDateTime etc = zonedDateTime.withZoneSameInstant(ZoneId.of("EST5EDT"));
        return etc.getDayOfWeek() != DayOfWeek.SATURDAY && etc.getDayOfWeek() != DayOfWeek.SUNDAY &&
                etc.getHour() >= 8 && (etc.getHour() <= 22 && !(etc.getMinute() > 0));
    }

    /**
     * Verifies that the range that fall within given {@link com.scheduler.common.model.Appointment} start and end times
     * don't conflict with any appointments that the customer that is associated with the given customerId already has
     * scheduled.
     * @param startZDT start time of appointment to check
     * @param endZDT end time of appointment to check
     * @param customerId unique identifier of customer to check for appointment conflicts
     * @return boolean value; true if customer has no conflicting appointments
     */
    public boolean checkForNoOverlapByCustomer(ZonedDateTime startZDT, ZonedDateTime endZDT, int customerId) {
        AtomicBoolean overlaps = new AtomicBoolean(false);
        List<Appointment> customerAppointments = getAppointmentsByCustomerId(customerId);
        customerAppointments.forEach(a -> {
            if (isOverlapping(startZDT, endZDT, a.getStartDefault(), a.getEndDefault()))
                overlaps.set(true);
        });
        return !overlaps.get();
    }

    /**
     * Takes start and end times of two ranges  and checks whether the ranges overlap. Algorithm found on Stack Overflow:
     * <p>
     * <a href="https://stackoverflow.com/questions/17106670/how-to-check-a-timeperiod-is-overlapping-another-time-period-in-java">
     *     Stack Overflow Answer</a>
     *
     * @param start1 start DateTime for range 1
     * @param end1 end DateTime for range 1
     * @param start2 start DateTime for range 2
     * @param end2 end DateTime for range 2
     * @return boolean value; true if ranges overlap
     */
    private boolean isOverlapping(ZonedDateTime start1, ZonedDateTime end1,
                                  ZonedDateTime start2, ZonedDateTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }
}
