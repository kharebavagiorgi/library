package com.solvd.library.service.impl;

import com.solvd.library.domain.Author;
import com.solvd.library.domain.Book;
import com.solvd.library.mapper.BookMapper;
import com.solvd.library.service.AuthorService;
import com.solvd.library.service.BookService;
import com.solvd.library.service.ReviewService;
import com.solvd.library.config.MyBatisUtil;
import com.solvd.library.factory.MyBatisFactory;
import com.solvd.library.factory.PersistenceFactory;
import com.solvd.library.event.EventPublisher;
import com.solvd.library.event.listener.AuditServiceListener;
import com.solvd.library.event.listener.NotificationServiceListener;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class BookServiceImpl implements BookService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookServiceImpl.class);

    private final ReviewService reviewService;
    private final EventPublisher eventPublisher;
    private final AuthorService authorService; 

    public BookServiceImpl() {
        PersistenceFactory factory = new MyBatisFactory();

        this.reviewService = factory.createReviewService();
        this.authorService = factory.createAuthorService();

        this.eventPublisher = new EventPublisher();
        this.eventPublisher.subscribe(new AuditServiceListener());
        this.eventPublisher.subscribe(new NotificationServiceListener());
    }

    @Override
    public Book create(Book book) {
        try (SqlSession session = MyBatisUtil.openSession()) {
            BookMapper mapper = session.getMapper(BookMapper.class);
            mapper.create(book);
            session.commit();
            return book;
        }
    }

    @Override
    public Book createBookAndAuthor(Book book, Author author) {
        LOGGER.info("Starting creation of Book and Author.");

        if (author == null) {
            LOGGER.warn("Attempting to create a book, but the Author object is null. Creating book only.");
        } else {
            authorService.create(author);
            LOGGER.info("Created Author with ID: {}", author.getId());
            book.setAuthor(author);
        }

        Book newBook = this.create(book);
        LOGGER.info("Created Book with ID: {}", newBook.getId());

        return newBook;
    }

    @Override
    public Optional<Book> findById(Long id) {
        try (var session = MyBatisUtil.getSqlSessionFactory().openSession()) {
            BookMapper bookMapper = session.getMapper(BookMapper.class);
            return bookMapper.findById(id);
        }
    }

    @Override
    public List<Book> findAll() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            return session.getMapper(BookMapper.class).findAll();
        }
    }

    @Override
    public void update(Book book) {
        try (SqlSession session = MyBatisUtil.openSession()) {
            session.getMapper(BookMapper.class).update(book);
            session.commit();
        }
    }

    @Override
    public void delete(Long id) {
        reviewService.deleteByBookId(id);
        LOGGER.info("Deleted all reviews for Book ID: {}", id);

        try (SqlSession session = MyBatisUtil.openSession()) {
            session.getMapper(BookMapper.class).delete(id);
            session.commit();
            LOGGER.info("Successfully deleted Book ID: {}", id);
        }

        eventPublisher.publishBookDeletion(id);
    }

    @Override
    public Optional<Book> findBookWithAllDetails(Long bookId) {
        try (var session = MyBatisUtil.getSqlSessionFactory().openSession()) {
            return session.getMapper(BookMapper.class)
                    .findBookWithAllDetails(bookId);
        }
    }
}
