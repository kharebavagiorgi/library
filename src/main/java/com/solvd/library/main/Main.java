package com.solvd.library.main;

import com.solvd.library.decorator.LoggingBookServiceDecorator;
import com.solvd.library.domain.*;
import com.solvd.library.facade.LibraryFacade;
import com.solvd.library.facade.LibraryFacadeImpl;
import com.solvd.library.factory.MyBatisFactory;
import com.solvd.library.factory.PersistenceFactory;
import com.solvd.library.proxy.SecureBookServiceProxy;
import com.solvd.library.service.AuthorService;
import com.solvd.library.service.BookService;
import com.solvd.library.service.ReviewService;
import com.solvd.library.strategy.BookSorter;
import com.solvd.library.strategy.impl.SortByAuthorStrategy;
import com.solvd.library.strategy.impl.SortByTitleStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        // --- 1. ABSTRACT FACTORY IMPLEMENTATION ---
        PersistenceFactory factory = new MyBatisFactory();

        BookService baseBookService = factory.createBookService();
        AuthorService authorService = factory.createAuthorService();
        ReviewService reviewService = factory.createReviewService();

        // --- 2. DECORATOR IMPLEMENTATION ---
        BookService decoratedBookService = new LoggingBookServiceDecorator(baseBookService);

        // --- 3. PROXY IMPLEMENTATION ---
        BookService bookService = new SecureBookServiceProxy(decoratedBookService, "GUEST");

        // --- 4. FACADE IMPLEMENTATION ---
        LibraryFacade libraryFacade = new LibraryFacadeImpl();

        // Declaring IDs
        final Long newBookId;
        final Long secondBookId;
        final Long newAuthorId;
        final Long newReviewId;
        final Long facadeUserId = 1L;

        LOGGER.info("Starting Persistence Layer Demonstration...");

        // 1. CREATE: Book (Sherlock Holmes)
        Book newBook = Book.builder()
                .title("Sherlock Holmes")
                .isbn("978-034539180")
                .pageCount(200)
                .publicationDate(LocalDate.of(1979, 10, 12))
                .genre(Genre.SCIENCE)
                .build();

        try {
            bookService.create(newBook);
            newBookId = newBook.getId();
            LOGGER.info("✅ CREATE: New book created with ID: {}", newBookId);
        } catch (RuntimeException e) {
            LOGGER.error("CREATE FAILED: Database error during book creation.", e);
            return;
        }

        // 1b. CREATE a second book (A Study in Scarlet)
        Book secondBook = Book.builder()
                .title("A Study in Scarlet")
                .isbn("978-150329056")
                .pageCount(150)
                .publicationDate(LocalDate.of(1887, 12, 1))
                .genre(Genre.SCIENCE)
                .build();
        bookService.create(secondBook);
        secondBookId = secondBook.getId();

        // 2. CREATE: Author
        Author author = new Author();
        author.setName("Arthur Conan Doyle 2");

        try {
            authorService.create(author);
            newAuthorId = author.getId();
            LOGGER.info("✅ CREATE: New author created with ID: {}", newAuthorId);

            // Link Author to both books
            newBook.setAuthor(author);
            bookService.update(newBook);
            secondBook.setAuthor(author);
            bookService.update(secondBook);
            LOGGER.info("✅ UPDATE: Books linked to Author ID {}", newAuthorId);
        } catch (RuntimeException e) {
            LOGGER.error("Author creation/linking failed.", e);
            return;
        }

        // 3. CREATE: Review
        Review review = new Review();
        review.setRating(5);
        review.setComment("Absolutely amazing detective story! 2");
        review.setBookId(newBookId);

        try {
            reviewService.create(review);
            newReviewId = review.getId();
            LOGGER.info("✅ CREATE: New review created with ID: {} for Book ID {}", newReviewId, newBookId);
        } catch (RuntimeException e) {
            LOGGER.error("Review creation failed.", e);
            e.printStackTrace();
            return;
        }

        // 4. READ & UPDATE DEMONSTRATION

        // READ Author
        Optional<Author> optionalAuthor = authorService.findById(newAuthorId);
        optionalAuthor.ifPresent(a ->
                LOGGER.info("✅ READ: Retrieved Author ID {}: {}", a.getId(), a.getName())
        );

        // READ Review
        Optional<Review> optionalReview = reviewService.findById(newReviewId);
        optionalReview.ifPresent(r ->
                LOGGER.info("✅ READ: Retrieved Review ID {}: {}", r.getId(), r.getComment())
        );

        // UPDATE Author
        optionalAuthor.ifPresent(a -> {
            a.setName("Sir Arthur Conan Doyle 2");
            authorService.update(a);
            LOGGER.info("✅ UPDATE: Author ID {} updated to new name.", a.getId());
        });

        // UPDATE Review
        optionalReview.ifPresent(r -> {
            r.setRating(5);
            r.setComment("Absolutely amazing detective story! 2");
            reviewService.update(r);
            LOGGER.info("✅ UPDATE: Review ID {} updated.", r.getId());
        });

        // Ensure Author Link is updated again (This is the block that had syntax errors)
        optionalAuthor.ifPresent(a -> {
            newBook.setAuthor(a);
            bookService.update(newBook);
            LOGGER.info("✅ UPDATE: Book ID {} linked to Author ID {}.", newBookId, a.getId());
        });

        LOGGER.info("\nPersistence Layer Demonstration Complete.");

        // --- PROXY DEMONSTRATION START ---

        LOGGER.info("\n--- PROXY DEMONSTRATION: DELETE OPERATION (As GUEST) ---");
        bookService.delete(secondBookId);

        LOGGER.info("\n--- PROXY DEMONSTRATION: RERUN DELETE (As ADMIN) ---");

        BookService adminBookService = new SecureBookServiceProxy(decoratedBookService, "ADMIN");
        adminBookService.delete(newBookId);

        LOGGER.info("PROXY CHECK: Attempting to find deleted Book ID {}...", newBookId);
        decoratedBookService.findById(newBookId).ifPresentOrElse(
                b -> LOGGER.warn("PROXY FAILURE: Book ID {} was NOT deleted!", newBookId),
                () -> LOGGER.info("PROXY SUCCESS: Book ID {} was successfully deleted.", newBookId)
        );

        // --- PROXY DEMONSTRATION END ---

        // --- 5. DEMONSTRATE FACADE USAGE ---
        LOGGER.info("\n--- FACADE DEMONSTRATION: Borrow Book ---");
        libraryFacade.borrowBook(secondBookId, facadeUserId);

        // --- 6. DEMONSTRATE STRATEGY PATTERN USAGE ---

        LOGGER.info("\n--- STRATEGY DEMONSTRATION: Book Sorting ---");

        List<Book> allBooks = bookService.findAll();
        LOGGER.info("Found {} books to sort.", allBooks.size());

        if (!allBooks.isEmpty()) {
            BookSorter sorter = new BookSorter();

            // Strategy 1: Sort by Title
            sorter.setStrategy(new SortByTitleStrategy());
            List<Book> sortedByTitle = sorter.executeSort(allBooks);
            LOGGER.info("Sorted by Title. First book: {}", sortedByTitle.get(0).getTitle());

            // Strategy 2: Sort by Author
            sorter.setStrategy(new SortByAuthorStrategy());
            List<Book> sortedByAuthor = sorter.executeSort(allBooks);
            LOGGER.info("Sorted by Author. First book: {}",
                    sortedByAuthor.get(0).getAuthor() != null ? sortedByAuthor.get(0).getAuthor().getName() : "No Author");
        } else {
            LOGGER.warn("Cannot demonstrate Strategy pattern: No books found in the database.");
        }

        // 7. findBookWithAllDetails (Joins) - Check the remaining book
        if (secondBookId != null) {
            Optional<Book> optionalFullBook = decoratedBookService.findBookWithAllDetails(secondBookId);

            optionalFullBook.ifPresent(fullBook -> {
                LOGGER.info("\n✅ REMAINING BOOK (ID {}):", secondBookId);
                LOGGER.info("Title: {}", fullBook.getTitle());
                if (fullBook.getAuthor() != null) {
                    LOGGER.info("Author: {}", fullBook.getAuthor().getName());
                }
            });

            if (optionalFullBook.isEmpty()) {
                LOGGER.warn("Book with ID {} not found in final retrieval.", secondBookId);
            }
        }
    }
}