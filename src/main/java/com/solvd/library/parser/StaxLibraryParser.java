package com.solvd.library.parser;

import com.solvd.library.model.*;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.*;

public class StaxLibraryParser {

    public Library parse(String xmlFilePath) throws Exception {
        Library library = null;
        Book currentBook = null;
        User currentUser = null;
        Department currentDepartment = null;
        Employee currentEmployee = null;
        Review currentReview = null;

        String currentElementTag = "";

        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        XMLEventReader reader = xmlInputFactory.createXMLEventReader(new FileInputStream(xmlFilePath));

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();

            if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();
                String localPart = startElement.getName().getLocalPart();

                currentElementTag = localPart;

                switch (localPart) {
                    case "library":
                        library = new Library();
                        Iterator<Attribute> libraryAttrs = startElement.getAttributes();
                        while(libraryAttrs.hasNext()){
                            Attribute attr = libraryAttrs.next();
                            if (attr.getName().getLocalPart().equals("name")) {
                                library.setName(attr.getValue());
                            }
                        }
                        break;

                    case "department":
                        currentDepartment = new Department();
                        Attribute deptIdAttr = startElement.getAttributeByName(new QName("deptId"));
                        Attribute deptNameAttr = startElement.getAttributeByName(new QName("name"));
                        if (deptIdAttr != null) { currentDepartment.setId(Long.valueOf(deptIdAttr.getValue())); }
                        if (deptNameAttr != null) { currentDepartment.setName(deptNameAttr.getValue()); }
                        break;

                    case "employee":
                        currentEmployee = new Employee();
                        Attribute empIdAttr = startElement.getAttributeByName(new QName("empId"));
                        if (empIdAttr != null) { currentEmployee.setId(Long.valueOf(empIdAttr.getValue())); }
                        break;

                    case "book":
                        currentBook = new Book();
                        Attribute isbnAttr = startElement.getAttributeByName(new QName("isbn"));
                        if (isbnAttr != null) {
                            currentBook.setIsbn(isbnAttr.getValue());
                        }
                        break;

                    case "review":
                        currentReview = new Review();
                        Attribute reviewIdAttr = startElement.getAttributeByName(new QName("reviewId"));
                        if (reviewIdAttr != null) { currentReview.setId(Long.valueOf(reviewIdAttr.getValue())); }
                        break;

                    case "user":
                        currentUser = new User();
                        Iterator<Attribute> userAttrs = startElement.getAttributes();
                        while(userAttrs.hasNext()){
                            Attribute attr = userAttrs.next();
                            String attrName = attr.getName().getLocalPart();
                            if (attrName.equals("userId")) {
                                currentUser.setId(Long.valueOf(attr.getValue()));
                            } else if (attrName.equals("isActive")) {
                                currentUser.setActive(Boolean.parseBoolean(attr.getValue()));
                            }
                        }
                        break;

                    case "address":
                        Address address = new Address();
                        Iterator<Attribute> addressAttrs = startElement.getAttributes();
                        while(addressAttrs.hasNext()){
                            Attribute attr = addressAttrs.next();
                            String attrName = attr.getName().getLocalPart();
                            if (attrName.equals("street")) {
                                address.setStreet(attr.getValue());
                            } else if (attrName.equals("city")) {
                                address.setCity(attr.getValue());
                            }
                        }
                        if (currentUser != null) {
                            currentUser.setAddress(address);
                        }
                        break;
                }
            }

            else if (event.isCharacters()) {
                Characters characters = event.asCharacters();
                if (!characters.isWhiteSpace()) {
                    String value = characters.getData();

                    if (currentReview != null) {
                        if (currentElementTag.equals("rating")) {
                            currentReview.setRating(Integer.parseInt(value));
                        } else if (currentElementTag.equals("comment")) {
                            currentReview.setComment(value);
                        }
                    }
                    else if (currentBook != null) {
                        switch (currentElementTag) {
                            case "title":
                                currentBook.setTitle(value);
                                break;
                            case "publicationDate":
                                currentBook.setPublicationDate(LocalDate.parse(value));
                                break;
                            case "pageCount":
                                currentBook.setPageCount(Integer.parseInt(value));
                                break;
                            case "genre":
                                currentBook.setGenre(Genre.valueOf(value));
                                break;
                        }
                    } else if (currentUser != null) {
                        switch (currentElementTag) {
                            case "registrationDateTime":
                                currentUser.setRegistrationDateTime(LocalDateTime.parse(value));
                                break;
                        }
                    } else if (currentEmployee != null) {
                        switch (currentElementTag) {
                            case "firstName":
                                currentEmployee.setFirstName(value);
                                break;
                            case "lastName":
                                currentEmployee.setLastName(value);
                                break;
                            case "departmentId":
                                currentEmployee.setDepartmentId(value);
                                break;
                        }
                    }
                }
            }

            else if (event.isEndElement()) {
                EndElement endElement = event.asEndElement();
                String localPart = endElement.getName().getLocalPart();

                if (library != null) {
                    if (localPart.equals("review")) {
                        if (currentBook != null && currentReview != null) {
                            currentBook.getReviews().add(currentReview);
                        }
                        currentReview = null;
                    }
                    else if (localPart.equals("book")) {
                        library.getBooks().add(currentBook);
                        currentBook = null;
                    } else if (localPart.equals("user")) {
                        library.getUsers().add(currentUser);
                        currentUser = null;
                    } else if (localPart.equals("department")) {
                        library.getDepartments().add(currentDepartment);
                        currentDepartment = null;
                    } else if (localPart.equals("employee")) {
                        library.getEmployees().add(currentEmployee);
                        currentEmployee = null;
                    }
                }
            }
        }
        return library;
    }
}
