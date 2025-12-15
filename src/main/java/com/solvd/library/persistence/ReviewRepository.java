package com.solvd.library.persistence;

import com.solvd.library.domain.Review;

public interface ReviewRepository extends BaseRepository<Review, Long> {
    void deleteByBookId(Long bookId);
}