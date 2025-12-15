package com.solvd.library.domain;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class Review {

    @XmlAttribute
    private Long id;

    private Long bookId;

    @XmlElement
    private int rating;

    @XmlElement
    private String comment;

    private Book book;
    private User user;

    public Review() {}

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Long getBookId() {
        return bookId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
