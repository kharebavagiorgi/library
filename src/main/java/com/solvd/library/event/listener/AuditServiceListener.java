package com.solvd.library.event.listener;

import com.solvd.library.event.BookDeletionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuditServiceListener implements BookDeletionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditServiceListener.class);

    @Override
    public void onBookDeleted(Long bookId) {
        LOGGER.warn("LISTENER (AUDIT): Book deletion detected. Logging audit record for Book ID: {}", bookId);
    }
}