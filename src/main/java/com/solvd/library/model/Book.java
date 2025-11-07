package com.solvd.library.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.solvd.library.localdateadapter.LocalDateAdapter;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlType(propOrder = {"id", "isbn", "title", "publicationDate", "pageCount", "genre", "reviews"}) //
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Book {
    private Long id;
    private String isbn;
    private String title;
    private LocalDate publicationDate;
    private int pageCount;
    private Genre genre;


    private static final String LIBRARY_NAMESPACE = "http://www.library.com/schema";
    private List<Review> reviews = new ArrayList<>();

    public Book() {}

    @XmlElement(namespace = LIBRARY_NAMESPACE)
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    @XmlElement(namespace = LIBRARY_NAMESPACE)
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    @XmlElement(namespace = LIBRARY_NAMESPACE)
    public int getPageCount() { return pageCount; }
    public void setPageCount(int pageCount) { this.pageCount = pageCount; }

    @XmlElement(namespace = LIBRARY_NAMESPACE)
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    public LocalDate getPublicationDate() { return publicationDate; }
    public void setPublicationDate(LocalDate publicationDate) { this.publicationDate = publicationDate; }

    @XmlElement(namespace = LIBRARY_NAMESPACE)
    public Genre getGenre() { return genre; }
    public void setGenre(Genre genre) { this.genre = genre; }

    @XmlElementWrapper(name = "reviews", namespace = LIBRARY_NAMESPACE)
    @XmlElement(name = "review", namespace = LIBRARY_NAMESPACE)
    public List<Review> getReviews() {
        return reviews;
    }

    @XmlElement(namespace = LIBRARY_NAMESPACE)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
