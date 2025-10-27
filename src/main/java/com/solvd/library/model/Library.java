package com.solvd.library.model;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.*;

@XmlRootElement(
        name = "library",
        namespace = "http://www.library.com/schema"
)
@XmlAccessorType(XmlAccessType.FIELD)
public class Library {

    @XmlAttribute
    private String name;

    @XmlElementWrapper(name = "books", namespace = "http://www.library.com/schema")
    @XmlElement(name = "book", namespace = "http://www.library.com/schema")
    private List<Book> books = new ArrayList<>();

    @XmlElementWrapper(name = "users", namespace = "http://www.library.com/schema")
    @XmlElement(name = "user", namespace = "http://www.library.com/schema")
    private List<User> users = new ArrayList<>();

    public Library() {
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Book> getBooks() { return books; }
    public void setBooks(List<Book> books) { this.books = books; }

    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }
}
