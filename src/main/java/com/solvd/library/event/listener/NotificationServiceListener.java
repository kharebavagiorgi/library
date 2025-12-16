package com.solvd.library.event.listener;

import com.solvd.library.event.BookDeletionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationServiceListener implements BookDeletionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceListener.class);

    @Override
    public void onBookDeleted(Long bookId) {
        LOGGER.info("LISTENER (NOTIFICATION): Deletion of Book ID {} confirmed. Sending alert to administrators.", bookId);
    }
}