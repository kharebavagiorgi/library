package com.solvd.library.factory;

import com.solvd.library.service.AuthorService;
import com.solvd.library.service.BookService;
import com.solvd.library.service.ReviewService;
import com.solvd.library.service.UserService;
import com.solvd.library.service.impl.AuthorServiceImpl;
import com.solvd.library.service.impl.BookServiceImpl;
import com.solvd.library.service.impl.ReviewServiceImpl;
import com.solvd.library.service.impl.UserServiceImpl;

public class MyBatisFactory implements PersistenceFactory {

    @Override
    public BookService createBookService() {
        return new BookServiceImpl();
    }

    @Override
    public AuthorService createAuthorService() {
        return new AuthorServiceImpl();
    }

    @Override
    public ReviewService createReviewService() {
        return new ReviewServiceImpl();
    }

    @Override
    public UserService createUserService() {
        return new UserServiceImpl();
    }
}