package com.scheduler.business;

import com.scheduler.access.object.mysql.CountryDAO;
import com.scheduler.common.model.Country;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Class performs business logic operations on Country objects. Implemented using a Singleton pattern to simplify
 * keeping Country objects in sync across Front End and Business Logic, and between those objects and the Database.
 *
 * @author Dillon Christensen
 */
public class CountryService {
    private static volatile CountryService instance;
    private final List<Country> countries;

    /**
     * Constructor: Sets up DAO to create a list of countries, pulled from the database.
     */
    private CountryService() {
        CountryDAO dao = new CountryDAO();
        countries = dao.readAll();
    }

    /**
     * Creates singleton instance if it was not created already, then gets the instance.
     *
     * @return Singleton instance
     */
    public static CountryService getInstance() {
        if (instance == null) {
            synchronized (CountryService.class) {
                if (instance == null) {
                    instance = new CountryService();
                }
            }
        }
        return instance;
    }

    /**
     * Gets the country instance with the given ID.
     * @param id unique identifier used to query {@link Country}
     * @return {@link Country} or <code>null</code> if not found
     */
    public Country getCountryById(int id) {
        List<Country> filtered = countries.stream()
                .filter(c -> c.getId() == id)
                .toList();
        return filtered.isEmpty() ? null : filtered.get(0);
    }

    /**
     * Gets the country instance with the given name.
     * @param name unique identifier used to query {@link Country}
     * @return {@link Country} or <code>null</code> if not found
     */
    public Country getCountryByName(String name) {
        List<Country> filtered = countries.stream()
                .filter(c -> Objects.equals(c.getName(), name))
                .toList();
        return filtered.isEmpty() ? null : filtered.get(0);
    }

    /**
     * Gets a list of all {@link Country} names from {@link #countries}.
     * @return list of name {@link String Strings}
     */
    public List<String> getCountryNames() {
        return countries.stream()
                .map(Country::getName)
                .collect(Collectors.toList());
    }
}
