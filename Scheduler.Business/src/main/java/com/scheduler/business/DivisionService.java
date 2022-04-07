package com.scheduler.business;

import com.scheduler.access.object.mysql.DivisionDAO;
import com.scheduler.common.model.Country;
import com.scheduler.common.model.Division;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Class performs business logic operations on Division objects. Implemented using a Singleton pattern to simplify
 * keeping Division objects in sync across Front End and Business Logic, and between those objects and the Database.
 *
 * @author Dillon Christensen
 */
public class DivisionService {
    private static volatile DivisionService instance;
    private final List<Division> divisions;

    /**
     * Constructor: Sets up DAO to create a list of divisions, pulled from the database.
     */
    private DivisionService() {
        DivisionDAO dao = new DivisionDAO();
        divisions = dao.readAll();
    }

    /**
     * Creates singleton instance if it was not created already, then gets the instance.
     *
     * @return Singleton instance
     */
    public static DivisionService getInstance() {
        if (instance == null) {
            synchronized (DivisionService.class) {
                if (instance == null) {
                    instance = new DivisionService();
                }
            }
        }
        return instance;
    }

    /**
     * Get a division by its unique ID.
     * @param id Unique identifier used to query for division
     * @return Matching division instance or <em>null</em> if no division is found
     */
    public Division getDivisionById(int id) {
        List<Division> filtered = divisions.stream()
                .filter(d -> d.getId() == id).toList();
        return filtered.isEmpty() ? null : filtered.get(0);
    }

    /**
     * Get a division by its unique name.
     * @param name Unique identifier used to query for division
     * @return Matching division instance or <em>null</em> if no division is found
     */
    public Division getDivisionByName(String name) {
        List<Division> filtered = divisions.stream()
                .filter(d -> Objects.equals(d.getName(), name))
                .toList();
        return filtered.isEmpty() ? null : filtered.get(0);
    }

    /**
     * Get all divisions of a given country.
     * @param country Country with which to query the list of divisions
     * @return List of divisions from the country
     */
    public List<String> getDivisionNamesByCountry(Country country) {
        return divisions.stream()
                .filter(d -> d.getCountryId() == country.getId())
                .toList().stream()
                .map(Division::getName)
                .collect(Collectors.toList());
    }
}
