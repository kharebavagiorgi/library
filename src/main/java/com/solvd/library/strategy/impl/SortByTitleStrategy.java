package com.solvd.library.strategy.impl;

import com.solvd.library.domain.Book;
import com.solvd.library.strategy.BookSortStrategy;
import java.util.Comparator;
import java.util.List;

public class SortByTitleStrategy implements BookSortStrategy {
    @Override
    public List<Book> sort(List<Book> books) {
        books.sort(Comparator.comparing(Book::getTitle));
        return books;
    }
}