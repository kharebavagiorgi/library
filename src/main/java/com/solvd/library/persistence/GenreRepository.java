package com.solvd.library.persistence;

import com.solvd.library.domain.Genre;

import java.util.Optional;

public interface GenreRepository {

    Optional<Long> findIdByName(String genreName);

    Optional<Genre> findById(Long id);
}