package com.solvd.library.persistence;

import com.solvd.library.mapper.BookMapper;
import com.solvd.library.mapper.AuthorMapper;
import com.solvd.library.mapper.ReviewMapper;

public interface AbstractDaoFactory {

    BookMapper createBookMapper();
    AuthorMapper createAuthorMapper();
    ReviewMapper createReviewMapper();
}