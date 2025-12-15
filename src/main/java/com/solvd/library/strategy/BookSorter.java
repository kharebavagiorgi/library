package com.solvd.library.strategy;

import com.solvd.library.domain.Book;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BookSorter {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookSorter.class);

    private BookSortStrategy strategy;

    public void setStrategy(BookSortStrategy strategy) {
        LOGGER.info("CONTEXT: Setting sorting strategy to {}", strategy.getClass().getSimpleName());
        this.strategy = strategy;
    }

    public List<Book> executeSort(List<Book> books) {
        if (strategy == null) {
            LOGGER.warn("CONTEXT: No sorting strategy set. Returning unsorted list.");
            return books;
        }
        return strategy.sort(books);
    }
}