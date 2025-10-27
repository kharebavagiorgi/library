package com.solvd.library.model;

import java.time.LocalDate;
import jakarta.xml.bind.annotation.*;

@XmlType(propOrder = {"isbn", "title", "publicationDate", "pageCount", "genre"})
public class Book {
    private String isbn;
    private String title;
    private LocalDate publicationDate;
    private int pageCount;
    private Genre genre;

    @XmlAttribute
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    @XmlElement
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    @XmlElement
    public int getPageCount() { return pageCount; }
    public void setPageCount(int pageCount) { this.pageCount = pageCount; }

    @XmlElement
    public LocalDate getPublicationDate() { return publicationDate; }
    public void setPublicationDate(LocalDate publicationDate) { this.publicationDate = publicationDate; }

    @XmlElement
    public Genre getGenre() { return genre; }
    public void setGenre(Genre genre) { this.genre = genre; }
}
