package com.solvd.library.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.solvd.library.service.localdateadapter.LocalDateAdapter;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlType(propOrder = {"id", "isbn", "title", "publicationDate", "pageCount", "genre", "reviews"})
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Book {

    private Long id;
    private String isbn;
    private String title;
    private LocalDate publicationDate;
    private int pageCount;
    private Genre genre;
    private Author author;
    private List<Review> reviews = new ArrayList<>();

    private static final String LIBRARY_NAMESPACE = "http://www.library.com/schema";

    // 1. Private Constructor (used by the Builder)
    private Book(Builder builder) {
        this.isbn = builder.isbn;
        this.title = builder.title;
        this.publicationDate = builder.publicationDate;
        this.pageCount = builder.pageCount;
        this.genre = builder.genre;
        this.author = builder.author;
        // Reviews are optional in Builder; initialize if passed, otherwise use empty list
        this.reviews = builder.reviews != null ? builder.reviews : new ArrayList<>();
    }

    public Book() {}

    public static Builder builder() {
        return new Builder();
    }

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
    public void setReviews(List<Review> reviews) {this.reviews = reviews;}

    @XmlElement(namespace = LIBRARY_NAMESPACE)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Author getAuthor() { return author; }
    public void setAuthor(Author author) { this.author = author; }

    public static class Builder {
        private String isbn;
        private String title;
        private LocalDate publicationDate;
        private int pageCount; // Primitives are fine since JAXB/MyBatis use setters
        private Genre genre;
        private Author author;
        private List<Review> reviews;

        public Builder isbn(String isbn) {
            this.isbn = isbn;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder publicationDate(LocalDate publicationDate) {
            this.publicationDate = publicationDate;
            return this;
        }

        public Builder pageCount(int pageCount) {
            this.pageCount = pageCount;
            return this;
        }

        public Builder genre(Genre genre) {
            this.genre = genre;
            return this;
        }

        public Builder author(Author author) {
            this.author = author;
            return this;
        }

        public Builder reviews(List<Review> reviews) {
            this.reviews = reviews;
            return this;
        }

        public Book build() {
            Objects.requireNonNull(title, "Book title must be set.");
            return new Book(this);
        }
    }
}