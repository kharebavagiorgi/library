package com.solvd.library.localdateadapter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public LocalDate unmarshal(String v) throws Exception {
        return LocalDate.parse(v, FORMATTER);
    }

    @Override
    public String marshal(LocalDate v) throws Exception {
        return v.format(FORMATTER);
    }
}