package ru.extoozy.repository;

import java.util.List;

public interface CrudRepository<E, I> {
    void save(E entity);

    void update(E entity);

    boolean delete(I id);

    List<E> findAll();

    E findById(I id);
}
