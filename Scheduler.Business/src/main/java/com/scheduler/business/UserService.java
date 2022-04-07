package com.scheduler.business;

import com.scheduler.access.object.mysql.UserDAO;
import com.scheduler.common.model.User;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Class performs business logic operations on User objects. Implemented using a Singleton pattern to simplify
 * keeping User objects in sync across Front End and Business Logic, and between those objects and the Database.
 *
 * @author Dillon Christensen
 */
public class UserService {

    private static volatile UserService instance;
    private final List<User> users;

    /**
     * Constructor: Sets up DAO to create a list of users, pulled from the database.
     */
    private UserService() {
        UserDAO dao = new UserDAO();
        users = dao.readAll();
    }

    /**
     * Creates singleton instance if it was not created already, then gets the instance.
     *
     * @return Singleton instance
     */
    public static UserService getInstance() {
        if (instance == null) {
            synchronized (UserService.class) {
                if (instance == null) {
                    instance = new UserService();
                }
            }
        }
        return instance;
    }

    /**
     * Get a user by its unique username.
     * @param username The unique identifier used to query the user instance
     * @return Matching user instance or <em>null</em> if not found
     */
    public User getUserByUsername(String username) {
        List<User> filtered = users.stream()
                .filter(u -> Objects.equals(u.getUsername(), username))
                .toList();
        return filtered.isEmpty() ? null : filtered.get(0);
    }

    /**
     * Get a list of all usernames.
     * @return List of usernames or empty list if no names were found
     */
    public List<String> getUsernames() {
        return users.stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
    }

    /**
     * Get ID value from the user associated with the given username.
     * @param username Unique identifier used to query for the user instance
     * @return ID value or value of -1 if no user is found
     */
    public int getIdByUsername(String username) {
        List<User> filtered = users.stream()
                .filter(u -> Objects.equals(u.getUsername(), username))
                .toList();
        return filtered.isEmpty() ? -1 : filtered.get(0).getId();
    }

    /**
     * Get username value from the user associated with the given ID
     * @param id Unique identifier used to query for user instance
     * @return Username value or empty string literal if no user is found
     */
    public String getUsernameById(int id) {
        List<User> filtered =  users.stream()
                .filter(u -> u.getId() == id)
                .toList();
        return filtered.isEmpty() ? "" : filtered.get(0).getUsername();
    }
}
