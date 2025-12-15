package com.solvd.library.mapper;

import com.solvd.library.domain.Author;
import java.util.List;
import java.util.Optional;

public interface AuthorMapper {

    void create(Author author);

    Optional<Author> findById(Long id);

    List<Author> findAll();

    void update(Author author);

    void delete(Long id);
}