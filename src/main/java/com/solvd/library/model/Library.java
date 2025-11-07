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

    private static final String LIBRARY_NAMESPACE = "http://www.library.com/schema";
    @XmlAttribute
    private Long id;
    @XmlAttribute
    private String name;

    @XmlElementWrapper(name = "books", namespace = LIBRARY_NAMESPACE)
    @XmlElement(name = "book", namespace = LIBRARY_NAMESPACE)
    private List<Book> books = new ArrayList<>();

    @XmlElementWrapper(name = "users", namespace = LIBRARY_NAMESPACE)
    @XmlElement(name = "user", namespace = LIBRARY_NAMESPACE)
    private List<User> users = new ArrayList<>();

    @XmlElementWrapper(name = "departments", namespace = LIBRARY_NAMESPACE)
    @XmlElement(name = "department", namespace = LIBRARY_NAMESPACE)
    private List<Department> departments = new ArrayList<>();

    @XmlElementWrapper(name = "employees", namespace = LIBRARY_NAMESPACE)
    @XmlElement(name = "employee", namespace = LIBRARY_NAMESPACE)
    private List<Employee> employees = new ArrayList<>();

    public Library() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Book> getBooks() { return books; }
    public void setBooks(List<Book> books) { this.books = books; }

    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }

    public List<Department> getDepartments() { return departments; }
    public void setDepartments(List<Department> departments) { this.departments = departments; }

    public List<Employee> getEmployees() { return employees; }
    public void setEmployees(List<Employee> employees) { this.employees = employees; }
}
