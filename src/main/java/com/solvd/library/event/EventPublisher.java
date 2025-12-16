package com.solvd.library.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages and notifies all registered BookDeletionListeners (The Subject).
 */
public class EventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventPublisher.class);

    private final List<BookDeletionListener> listeners = new ArrayList<>();

    public void subscribe(BookDeletionListener listener) {
        listeners.add(listener);
        LOGGER.info("EVENT PUBLISHER: Registered new listener: {}", listener.getClass().getSimpleName());
    }

    public void publishBookDeletion(Long bookId) {
        LOGGER.info("EVENT PUBLISHER: Notifying {} listeners about deletion of Book ID: {}",
                listeners.size(), bookId);

        for (BookDeletionListener listener : listeners) {
            listener.onBookDeleted(bookId);
        }
    }
}