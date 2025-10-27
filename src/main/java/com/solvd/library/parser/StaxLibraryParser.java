package com.solvd.library.parser;

import com.solvd.library.model.*;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.*;

/**
 * Parses the library.xml file using the StAX (Streaming API for XML) parser.
 * It builds a complete Library object hierarchy from the XML data.
 *
 * Javadoc with 5 XPaths for library.xml:
 * 1. Find all book titles: /library/books/book/title
 * 2. Find the ISBN of the second book: /library/books/book[2]/@isbn
 * 3. Find the registration date/time of the user with userId="U001": /library/users/user[@userId='U001']/registrationDateTime
 * 4. Find all cities where users live: /library/users/user/address/@city
 * 5. Find the page count of the book with genre 'SCIENCE': /library/books/book[genre='SCIENCE']/pageCount
 */
public class StaxLibraryParser {

    public Library parse(String xmlFilePath) throws Exception {
        Library library = null;
        Book currentBook = null;
        User currentUser = null;

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

                    case "book":
                        currentBook = new Book();
                        Attribute isbnAttr = startElement.getAttributeByName(new QName("isbn"));
                        if (isbnAttr != null) {
                            currentBook.setIsbn(isbnAttr.getValue());
                        }
                        break;

                    case "user":
                        currentUser = new User();
                        Iterator<Attribute> userAttrs = startElement.getAttributes();
                        while(userAttrs.hasNext()){
                            Attribute attr = userAttrs.next();
                            String attrName = attr.getName().getLocalPart();
                            if (attrName.equals("userId")) {
                                currentUser.setUserId(attr.getValue());
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

                    if (currentBook != null) {
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
                    }
                }
            }

            else if (event.isEndElement()) {
                EndElement endElement = event.asEndElement();
                String localPart = endElement.getName().getLocalPart();

                if (library != null) {
                    if (localPart.equals("book")) {
                        library.getBooks().add(currentBook);
                        currentBook = null;
                    } else if (localPart.equals("user")) {
                        library.getUsers().add(currentUser);
                        currentUser = null;
                    }
                }
            }
        }
        return library;
    }
}