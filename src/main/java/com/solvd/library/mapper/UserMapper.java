package com.solvd.library.mapper;

import com.solvd.library.domain.User;
import java.util.List;
import java.util.Optional; // ADD THIS IMPORT

public interface UserMapper {

    void create(User user);

    Optional<User> findById(Long id);

    List<User> findAll();

    void update(User user);

    void delete(Long id);
}