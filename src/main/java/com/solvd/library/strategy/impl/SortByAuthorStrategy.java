package com.solvd.library.strategy.impl;

import com.solvd.library.domain.Book;
import com.solvd.library.strategy.BookSortStrategy;
import java.util.Comparator;
import java.util.List;

public class SortByAuthorStrategy implements BookSortStrategy {
    @Override
    public List<Book> sort(List<Book> books) {
        // Handle potential null authors
        books.sort(Comparator.comparing(
                b -> b.getAuthor() != null ? b.getAuthor().getName() : "",
                Comparator.nullsLast(String::compareTo)
        ));
        return books;
    }
}