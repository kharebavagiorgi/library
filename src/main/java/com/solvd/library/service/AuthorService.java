package com.solvd.library.service;

import com.solvd.library.domain.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorService {

    Author create(Author author);

    Optional<Author> findById(Long id);

    List<Author> findAll();

    void update(Author author);

    void delete(Long id);
}
