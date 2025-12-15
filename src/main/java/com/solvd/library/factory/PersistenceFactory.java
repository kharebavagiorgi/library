package com.solvd.library.factory;

import com.solvd.library.service.AuthorService;
import com.solvd.library.service.BookService;
import com.solvd.library.service.ReviewService;
import com.solvd.library.service.UserService;

public interface PersistenceFactory {

    BookService createBookService();

    AuthorService createAuthorService();

    ReviewService createReviewService();

    UserService createUserService();

}