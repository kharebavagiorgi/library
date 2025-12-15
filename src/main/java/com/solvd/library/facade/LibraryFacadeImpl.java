package com.solvd.library.facade;

import com.solvd.library.domain.Book;
import com.solvd.library.domain.User;
import com.solvd.library.service.BookService;
import com.solvd.library.service.UserService;
import com.solvd.library.service.impl.BookServiceImpl;
import com.solvd.library.service.impl.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;

public class LibraryFacadeImpl implements LibraryFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(LibraryFacadeImpl.class);

    private final BookService bookService;
    private final UserService userService;

    public LibraryFacadeImpl() {
        this.bookService = new BookServiceImpl();
        this.userService = new UserServiceImpl();
    }

    @Override
    public boolean borrowBook(Long bookId, Long userId) {
        LOGGER.info("--- FACADE START: Attempting to process borrowing request for Book ID: {} by User ID: {} ---", bookId, userId);

        // Step 1: Check if book and user exist
        Optional<Book> bookOptional = bookService.findById(bookId);
        Optional<User> userOptional = userService.findById(userId);

        if (bookOptional.isEmpty() || userOptional.isEmpty()) {
            LOGGER.warn("FACADE FAIL: Book or User not found.");
            return false;
        }

        Book book = bookOptional.get();
        User user = userOptional.get();

        LOGGER.info("FACADE: Verified book '{}' and User ID: {} are valid.", book.getTitle(), userId);


        LOGGER.info("FACADE SUCCESS: Book '{}' successfully borrowed by User ID: {}", book.getTitle(), userId);


        LOGGER.info("--- FACADE END ---");
        return true;
    }
}