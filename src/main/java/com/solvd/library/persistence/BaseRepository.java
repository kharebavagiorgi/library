package com.solvd.library.persistence;

import java.util.List;
import java.util.Optional;

public interface BaseRepository<T, ID> {

    void create(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    void update(T entity);

    void delete(ID id);
}