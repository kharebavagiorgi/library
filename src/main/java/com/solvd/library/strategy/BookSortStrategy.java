package com.solvd.library.strategy;

import com.solvd.library.domain.Book;
import java.util.List;

public interface BookSortStrategy {
    List<Book> sort(List<Book> books);
}