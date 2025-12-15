package com.solvd.library.service.impl;

import com.solvd.library.domain.User;
import com.solvd.library.persistence.UserRepository;
import com.solvd.library.persistence.impl.UserRepositoryImpl;
import com.solvd.library.service.UserService;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository = new UserRepositoryImpl();

    @Override
    public User create(User user) {
        userRepository.create(user);
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void update(User user) {
        userRepository.update(user);
    }

    @Override
    public void delete(Long id) {
        userRepository.delete(id);
    }
}
