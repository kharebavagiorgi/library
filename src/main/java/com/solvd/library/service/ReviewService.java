package com.solvd.library.service;

import com.solvd.library.domain.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewService {

    Review create(Review review);

    Optional<Review> findById(Long id);

    List<Review> findAll();

    void update(Review review);

    void delete(Long id);

    /**
     * Deletes all reviews associated with a specific book ID.
     * This is required for cascade deletion logic in the BookService.
     * @param bookId The ID of the parent book whose reviews should be deleted.
     */
    void deleteByBookId(Long bookId); // <-- NEW METHOD ADDED
}