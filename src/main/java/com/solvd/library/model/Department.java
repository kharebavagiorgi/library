package com.solvd.library.model;

import jakarta.xml.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;

@XmlAccessorType(XmlAccessType.FIELD)
public class Department {

    @XmlAttribute
    private Long id;

    @XmlAttribute
    private String name;

    @XmlElement(name = "book")
    private List<Book> books = new ArrayList<>();

    public Department() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Book> getBooks() { return books; }
    public void setBooks(List<Book> books) { this.books = books; }
}
