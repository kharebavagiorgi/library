package com.solvd.library.service;

import com.solvd.library.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User create(User user);

    Optional<User> findById(Long id);

    List<User> findAll();

    void update(User user);

    void delete(Long id);
}
