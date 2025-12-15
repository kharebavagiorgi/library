package com.solvd.library.mapper;

import com.solvd.library.domain.Review;
import java.util.List;
import java.util.Optional; // ADD THIS IMPORT

public interface ReviewMapper {

    void create(Review review);

    Optional<Review> findById(Long id);

    List<Review> findAll();

    void update(Review review);

    void delete(Long id);
}