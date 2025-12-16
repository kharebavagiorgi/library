package com.solvd.library.decorator;

import com.solvd.library.domain.Author;
import com.solvd.library.domain.Book;
import com.solvd.library.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;

public class LoggingBookServiceDecorator implements BookService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingBookServiceDecorator.class);

    private final BookService wrappee;

    public LoggingBookServiceDecorator(BookService wrappee) {
        this.wrappee = wrappee;
    }

    @Override
    public Book create(Book entity) {
        LOGGER.info("DECORATOR LOG: Attempting to create book with title: {}", entity.getTitle());

        Book createdBook = wrappee.create(entity);

        LOGGER.info("DECORATOR LOG: Book created successfully with ID: {}", createdBook.getId());

        return createdBook;
    }

    @Override
    public Book createBookAndAuthor(Book book, Author author) {
        LOGGER.info("DECORATOR LOG: Starting COMPLEX creation of book '{}' and author '{}'.",
                book.getTitle(),
                author != null ? author.getName() : "None");

        Book createdBook = wrappee.createBookAndAuthor(book, author);

        LOGGER.info("DECORATOR LOG: COMPLEX creation completed. New Book ID: {}", createdBook.getId());

        return createdBook;
    }
    // -------------------------------------------------------------------

    @Override
    public Optional<Book> findById(Long id) {
        LOGGER.info("DECORATOR LOG: Searching for book with ID: {}", id);
        Optional<Book> result = wrappee.findById(id);
        result.ifPresentOrElse(
                book -> LOGGER.info("DECORATOR LOG: Found book: {}", book.getTitle()),
                () -> LOGGER.warn("DECORATOR LOG: Book ID {} not found.", id)
        );
        return result;
    }

    @Override
    public List<Book> findAll() {
        return wrappee.findAll();
    }

    @Override
    public void update(Book entity) {
        LOGGER.info("DECORATOR LOG: Attempting to update book with ID: {}", entity.getId());
        wrappee.update(entity);
        LOGGER.info("DECORATOR LOG: Book ID {} updated successfully.", entity.getId());
    }

    @Override
    public void delete(Long id) {
        LOGGER.warn("DECORATOR LOG: Attempting to delete book with ID: {}", id);
        wrappee.delete(id);
        LOGGER.warn("DECORATOR LOG: Book ID {} deleted successfully.", id);
    }

    @Override
    public Optional<Book> findBookWithAllDetails(Long id) {
        return wrappee.findBookWithAllDetails(id);
    }
}
