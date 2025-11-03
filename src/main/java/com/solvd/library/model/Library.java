package com.solvd.library.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.*;

@XmlType(propOrder = {"isbn", "title", "publicationDate", "pageCount", "genre", "reviews"}) //
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Book {
    private String isbn;
    private String title;
    private LocalDate publicationDate;
    private int pageCount;
    private Genre genre;

    private List<Review> reviews = new ArrayList<>();

    public Book() {}

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

    @XmlElementWrapper(name = "reviews")
    @XmlElement(name = "review")
    public List<Review> getReviews() {
        return reviews;
    }
    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
