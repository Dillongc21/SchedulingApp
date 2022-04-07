package com.scheduler.business;

import com.scheduler.access.object.mysql.ContactDAO;
import com.scheduler.common.model.Contact;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Class performs business logic operations on Contact objects. Implemented using a Singleton pattern to simplify
 * keeping Contact objects in sync across Front End and Business Logic, and between those objects and the Database.
 *
 * @author Dillon Christensen
 */
public class ContactService {
    private static volatile ContactService instance;
    private final List<Contact> contacts;

    /**
     * Constructor: Sets up DAO to create a list of contacts, pulled from the database.
     */
    private ContactService() {
        ContactDAO dao = new ContactDAO();
        contacts = dao.readAll();
    }

    /**
     * Creates singleton instance if it was not created already, then gets the instance.
     *
     * @return Singleton instance
     */
    public static ContactService getInstance() {
        if (instance == null) {
            synchronized (ContactService.class) {
                if (instance == null) {
                    instance = new ContactService();
                }
            }
        }
        return instance;
    }

    public List<Contact> getContacts() { return contacts; }

    /**
     * Gets a list of contact names from {@link #contacts}.
     * @return list of contact names
     */
    public List<String> getContactNames() {
        return contacts.stream()
                .map(Contact::getName)
                .collect(Collectors.toList());
    }

    /**
     * Get contact ID by the given name.
     * @param name unique identifier used to query {@link com.scheduler.common.model.Contact}
     * @return ID value or value of <code>-1</code> if no contact is found
     */
    public int getIdByName(String name) {
        List<Contact> filtered = contacts.stream()
                .filter(contact -> Objects.equals(contact.getName(), name))
                .toList();
        return filtered.isEmpty() ? -1 : filtered.get(0).getId();
    }

    /**
     * Get contact name by the given ID
     * @param id unique identifier used to query {@link com.scheduler.common.model.Contact}
     * @return name value or empty {@link String} if no contact is found
     */
    public String getNameById(int id) {
        return contacts.stream()
                .filter(c -> c.getId() == id)
                .toList()
                .get(0)
                .getName();
    }
}
