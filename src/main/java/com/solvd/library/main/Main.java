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
        // User starts as GUEST
        BookService bookService = new SecureBookServiceProxy(decoratedBookService, "GUEST");

        // --- 4. FACADE IMPLEMENTATION ---
        LibraryFacade libraryFacade = new LibraryFacadeImpl();

        // Declaring IDs (newAuthorId is no longer needed separately)
        final Long newBookId;
        final Long secondBookId;
        final Long newReviewId;
        final Long facadeUserId = 1L;

        LOGGER.info("Starting Persistence Layer Demonstration...");


        // 1. COMPLEX CREATE: Book and Author

        Author mainAuthor = new Author();
        mainAuthor.setName("Arthur Conan Doyle 2");

        Book newBook = Book.builder()
                .title("Sherlock Holmes")
                .isbn("978-034539180")
                .pageCount(200)
                .publicationDate(LocalDate.of(1979, 10, 12))
                .genre(Genre.SCIENCE)
                .build();

        try {
            bookService.createBookAndAuthor(newBook, mainAuthor);

            newBookId = newBook.getId();
            LOGGER.info("✅ COMPLEX CREATE: Book ID {} and Author ID {} created and linked.",
                    newBookId, mainAuthor.getId());

        } catch (RuntimeException e) {
            LOGGER.error("COMPLEX CREATE FAILED: Database error during book/author creation.", e);
            return;
        }

        Book secondBook = Book.builder()
                .title("A Study in Scarlet")
                .isbn("978-150329056")
                .pageCount(150)
                .publicationDate(LocalDate.of(1887, 12, 1))
                .genre(Genre.SCIENCE)
                .author(mainAuthor)
                .build();

        bookService.create(secondBook);
        secondBookId = secondBook.getId();
        LOGGER.info("✅ CREATE: Second book created with ID: {}", secondBookId);

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

        // READ Author (Use the author object from the complex create)
        Optional<Author> optionalAuthor = authorService.findById(mainAuthor.getId());
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

        // Ensure Author Link is updated again (This block is kept if linking logic isn't fully integrated into update)
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
