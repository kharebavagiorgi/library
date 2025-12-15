package com.solvd.library.service.impl;

import com.solvd.library.domain.Review;
import com.solvd.library.persistence.ReviewRepository;
import com.solvd.library.persistence.impl.ReviewRepositoryImpl;
import com.solvd.library.service.ReviewService;

import java.util.List;
import java.util.Optional;

public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository = new ReviewRepositoryImpl();

    @Override
    public Review create(Review review) {
        reviewRepository.create(review);
        return review;
    }

    @Override
    public Optional<Review> findById(Long id) {
        return reviewRepository.findById(id);
    }

    @Override
    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    @Override
    public void update(Review review) {
        reviewRepository.update(review);
    }

    @Override
    public void delete(Long id) {
        reviewRepository.delete(id);
    }

    @Override
    public void deleteByBookId(Long bookId) {
        reviewRepository.deleteByBookId(bookId);
    }
}
