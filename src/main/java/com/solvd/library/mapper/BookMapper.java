package com.solvd.library.mapper;

import com.solvd.library.domain.Book;
import java.util.List;
import java.util.Optional; // ADD THIS IMPORT

public interface BookMapper {

    void create(Book book);

    Optional<Book> findById(Long id);

    List<Book> findAll();

    void update(Book book);

    void delete(Long id);

    Optional<Book> findBookWithAllDetails(Long id);
}