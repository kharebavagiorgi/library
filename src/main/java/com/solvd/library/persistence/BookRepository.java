package com.solvd.library.persistence;

import com.solvd.library.domain.Book;

public interface BookRepository extends BaseRepository<Book, Long> {

    Book findBookWithAllDetails(Long bookId);

}