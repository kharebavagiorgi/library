package com.solvd.library.localdateadapter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public LocalDateTime unmarshal(String v) throws Exception {
        return LocalDateTime.parse(v, FORMATTER);
    }

    @Override
    public String marshal(LocalDateTime v) throws Exception {
        return v.format(FORMATTER);
    }
}