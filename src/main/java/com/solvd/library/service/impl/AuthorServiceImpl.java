package com.solvd.library.service.impl;

import com.solvd.library.domain.Author;
import com.solvd.library.persistence.AuthorRepository;
import com.solvd.library.persistence.impl.AuthorRepositoryImpl;
import com.solvd.library.service.AuthorService;

import java.util.List;
import java.util.Optional;

public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository = new AuthorRepositoryImpl();

    @Override
    public Author create(Author author) {
        authorRepository.create(author);
        return author;
    }

    @Override
    public Optional<Author> findById(Long id) {
        return authorRepository.findById(id);
    }

    @Override
    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    @Override
    public void update(Author author) {
        authorRepository.update(author);
    }

    @Override
    public void delete(Long id) {
        authorRepository.delete(id);
    }
}
