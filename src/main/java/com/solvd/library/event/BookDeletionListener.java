package com.solvd.library.event;

/**
 * Listener interface for handling book deletion events (The Observer).
 */
public interface BookDeletionListener {
    void onBookDeleted(Long bookId);
}