package com.solvd.library.proxy;

import com.solvd.library.domain.Author; 
import com.solvd.library.domain.Book;
import com.solvd.library.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;

public class SecureBookServiceProxy implements BookService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecureBookServiceProxy.class);

    private final BookService realBookService;

    private final String userRole;

    public SecureBookServiceProxy(BookService realBookService, String userRole) {
        this.realBookService = realBookService;
        this.userRole = userRole;
    }


    @Override
    public Book create(Book entity) {
        return realBookService.create(entity);
    }

    @Override
    public Book createBookAndAuthor(Book book, Author author) {
        return realBookService.createBookAndAuthor(book, author);
    }

    @Override
    public Optional<Book> findById(Long id) {
        return realBookService.findById(id);
    }

    @Override
    public List<Book> findAll() {
        return realBookService.findAll();
    }

    @Override
    public void update(Book entity) {
        realBookService.update(entity);
    }

    @Override
    public Optional<Book> findBookWithAllDetails(Long id) {
        return realBookService.findBookWithAllDetails(id);
    }


    @Override
    public void delete(Long id) {
        LOGGER.info("PROXY CHECK: Attempting to delete Book ID: {} with user role: {}", id, userRole);

        if ("ADMIN".equalsIgnoreCase(userRole)) {
            LOGGER.info("PROXY SUCCESS: User role '{}' is authorized. Deleting book...", userRole);
            realBookService.delete(id);
        } else {
            LOGGER.warn("PROXY DENIED: User role '{}' is NOT authorized to delete books. Operation blocked.", userRole);
        }
    }
}
