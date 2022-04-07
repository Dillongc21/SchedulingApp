package com.scheduler.business;

import com.scheduler.access.object.mysql.CustomerDAO;
import com.scheduler.common.model.Country;
import com.scheduler.common.model.Customer;
import com.scheduler.common.model.Division;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Class performs business logic operations on Customer objects. Implemented using a Singleton pattern to simplify
 * keeping Customer objects in sync across Front End and Business Logic, and between those objects and the Database.
 *
 * @author Dillon Christensen
 */
public class CustomerService {
    private static volatile CustomerService instance = null;
    private final CustomerDAO dao;
    private final DivisionService divisionService;
    private final CountryService countryService;
    private final List<Customer> customers;

    /**
     * Constructor: Sets up DAO to create a list of customers, pulled from the database. Sets up instances of Division
     * and Country Services. Populate front end helper field.
     *
     * @see com.scheduler.business.DivisionService Division Service
     * @see com.scheduler.business.CountryService Country Service
     */
    private CustomerService() {
        dao = new CustomerDAO();
        customers = dao.readAll();
        divisionService = DivisionService.getInstance();
        countryService = CountryService.getInstance();
        customers.forEach(this::populateExtendedAddressField);
    }

    /**
     * Creates singleton instance if it was not created already, then gets the instance.
     *
     * @return Singleton instance
     */
    public static CustomerService getInstance() {
        if (instance == null) {
            synchronized (CustomerService.class) {
                if (instance == null) {
                    instance = new CustomerService();
                }
            }
        }
        return instance;
    }

    /**
     * Creates a string from existing Customer fields and sets the 'extendedAddress' field as the newly created string.
     *
     * @param customer Customer instance to be modified
     * @see com.scheduler.common.model.Customer Customer
     */
    private void populateExtendedAddressField(Customer customer) {
        Division division = divisionService.getDivisionById(customer.getDivisionId());
        Country country = countryService.getCountryById(division.getCountryId());

        String countryName = ("Canada".equals(country.getName())) ? "Canadian" : country.getName();
        String extendedAddress = countryName + " address: " + customer.getAddress() + ", " + division.getName();

        customer.setExtendedAddress(extendedAddress);
    }

    /**
     * Gets a list of all {@link #customers}.
     * @return list of customers or empty list if none are found
     */
    public List<Customer> getAllCustomers() { return customers; }

    /**
     * Gets the {@link Customer} associated with the given ID value.
     * @param id unique identifier used to query {@link Customer}
     * @return Matching {@link Customer} instance or <code>null</code> if none was found
     */
    public Customer getCustomerById(int id) {
        List<Customer> filtered = customers.stream()
                .filter(c -> c.getId() == id)
                .toList();
        return filtered.isEmpty() ? null : filtered.get(0);
    }

    /**
     * Creates a {@link Customer} in the database from the given instance. If successful, populate front end helper
     * fields, such as <code>extendedAddress</code> and add the newly created instance to {@link #customers}.
     * @param customer {@link Customer} to be created on database
     * @return newly created {@link Customer} instance or <code>null</code> if creation was not successful
     */
    public Customer createCustomer(Customer customer) {
        Customer created = dao.create(customer);
        if (created != null) {
            populateExtendedAddressField(created);
            customers.add(created);
        }
        return created;
    }

    /**
     * Deletes a {@link Customer} from the database. If successful, remove the {@link Customer} associated with the given
     * ID from {@link #customers}.
     * @param id unique identifier to query {@link Customer}
     * @return deleted {@link Customer} instance or <code>null</code> if deletion failed
     */
    public Customer deleteCustomer(int id) {
        Customer toDelete = null;
        boolean wasDeleted = dao.delete(id);
        if (wasDeleted) {
            toDelete = customers.stream()
                    .filter(c -> c.getId() == id)
                    .toList().get(0);
            customers.remove(toDelete);
        }
        return toDelete;
    }

    /**
     * Updates a {@link Customer} in the database. If successful, remove the defunct {@link Customer} from
     * {@link #customers} and add the updated one.
     * @param customer {@link Customer} instance to update
     * @return updated {@link Customer} instance or <code>null</code> if update failed
     */
    public Customer updateCustomer(Customer customer) {
        Customer updated = dao.update(customer);
        if (updated != null) {
            populateExtendedAddressField(updated);
            Customer defunct = customers.stream()
                    .filter(c -> c.getId() == updated.getId())
                    .toList().get(0);
            customers.remove(defunct);
            customers.add(updated);
        }
        return updated;
    }

    /**
     * Gets all {@link Customer Customers} of the given {@link Country} name from {@link #customers}.
     * @param countryName name {@link String} of the {@link Country} to filter {@link Customer Customers}
     * @return {@link List} of {@link Customer Customers} filtered by <code>countryName</code> or empty {@link List}
     * if none are found
     */
    public List<Customer> getCustomersByCountryName(String countryName) {
        return customers.stream().filter(customer -> {
            Division division = divisionService.getDivisionById(customer.getDivisionId());
            Country country = countryService.getCountryById(division.getCountryId());
            return Objects.equals(country.getName(), countryName);
        }).collect(Collectors.toList());
    }

    /**
     * Gets all {@link Customer} names from {@link #customers}.
     * @return {@link List} of name {@link String Strings} or empty {@link List} if none exist
     */
    public List<String> getCustomerNames() {
        return customers.stream()
                .map(Customer::getName)
                .collect(Collectors.toList());
    }

    /**
     * Gets {@link Customer} <code>id</code> by the given <code>name</code>.
     * @param name unique identifier used to query {@link Customer}
     * @return <code>id</code> of matching {@link Customer} or <code>-1</code> if not found
     */
    public int getIdByName(String name) {
        List<Customer> filtered = customers.stream()
                .filter(customer -> Objects.equals(customer.getName(), name))
                .toList();
        return filtered.isEmpty() ? -1 : filtered.get(0).getId();
    }

    /**
     * Gets {@link Customer} <code>name</code> by the given <code>id</code>.
     * @param id unique identifier used to query {@link Customer}
     * @return <code>name</code> of matching {@link Customer} or empty {@link String} literal if not found
     */
    public String getNameById(int id) {
        List<Customer> filtered = customers.stream()
                .filter(c -> c.getId() == id)
                .toList();
        return filtered.isEmpty() ? "" : filtered.get(0).getName();
    }
}
