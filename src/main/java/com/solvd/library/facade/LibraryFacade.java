package com.solvd.library.facade;

public interface LibraryFacade {
    boolean borrowBook(Long bookId, Long userId);
}