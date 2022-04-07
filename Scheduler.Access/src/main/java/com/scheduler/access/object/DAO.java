package com.scheduler.access.object;

import java.util.List;

/**
 * Data access object Interface. Abstract representation of objects that act as an API to perform CRUD operations on
 * objects of type T.
 * <p>
 * Based on tutorial at https://www.baeldung.com/java-dao-pattern
 *
 * @param <T> Generic representation of Model objects that are generated and manipulated in conjunction with DB data
 */
public interface DAO<T> {
    T read(int id);
    List<T> readAll();
    T create(T t);
    T update(T t);
    boolean delete(int id);
}
