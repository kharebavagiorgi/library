package com.solvd.library.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.solvd.library.model.*;
import com.solvd.library.parser.*;

import java.io.File;
import java.util.List;

public class Main {

    private static final String XML_PATH = "src/main/resources/library.xml";
    private static final String XSD_PATH = "src/main/resources/library.xsd";
    private static final String JSON_PATH = "src/main/resources/library.json";

    public static void main(String[] args) {
        System.out.println("XML Validation");
        boolean isValid = XmlValidator.validate(XML_PATH, XSD_PATH);

        if (!isValid) {
            System.out.println("Validation failed. Aborting parsing process.");
            return;
        }

        System.out.println("\nStAX Parsing");
        try {
            StaxLibraryParser staxParser = new StaxLibraryParser();
            System.out.println("StAX Parser Javadoc contains 5 XPaths (see StaxLibraryParser.java).");
            Library libraryStax = staxParser.parse(XML_PATH);
            System.out.println("   StAX Parsing successful. Root object populated:");
            System.out.println("   Library Name: " + libraryStax.getName());
            System.out.println("   Books Loaded: " + libraryStax.getBooks().size());
            System.out.println("   Users Loaded: " + libraryStax.getUsers().size());
            System.out.println("   (First User's City: " + libraryStax.getUsers().get(0).getAddress().getCity() + ")");

            if (libraryStax.getUsers() != null && !libraryStax.getUsers().isEmpty()) {
                System.out.println("   (First User's City: " + libraryStax.getUsers().get(0).getAddress().getCity() + ")");
            } else {
                System.out.println("(User list is empty or null)");
            }

        } catch (Exception e) {
            System.err.println("StAX Parsing failed: " + e.getMessage());
        }

        System.out.println("\nJAXB Parsing");
        try {
            JaxbLibraryParser jaxbParser = new JaxbLibraryParser();
            Library libraryJaxb = jaxbParser.parse(XML_PATH);

            System.out.println("   JAXB Parsing successful. Root object populated:");
            System.out.println("   Library Name: " + libraryJaxb.getName());
            System.out.println("   Books Loaded: " + libraryJaxb.getBooks().size());
            System.out.println("   Users Loaded: " + libraryJaxb.getUsers().size());
            System.out.println("   (Second Book Title: " + libraryJaxb.getBooks().get(1).getTitle() + ")");

        } catch (Exception e) {
            System.err.println("JAXB Parsing failed.");
            e.printStackTrace();
        }

        try {
            JacksonLibraryParser parser = new JacksonLibraryParser();
            Library library = parser.parse(JSON_PATH);

            System.out.println("Library name: " + library.getName());
            System.out.println("Books count: " + library.getBooks().size());
            System.out.println("Users count: " + library.getUsers().size());

            File jsonFile = new File(JSON_PATH);
            String json = new ObjectMapper().readTree(jsonFile).toString();
            List<String> titles = JsonPath.read(json, "$.books[*].title");
            List<String> userIds = JsonPath.read(json, "$.users[*].userId");
            List<String> cities = JsonPath.read(json, "$.users[*].address.city");
            String firstIsbn = JsonPath.read(json, "$.books[0].isbn");
            List<String> genres = JsonPath.read(json, "$.books[*].genre");

            System.out.println("\nJSONPath Results");
            System.out.println("Book titles: " + titles);
            System.out.println("User IDs: " + userIds);
            System.out.println("Cities: " + cities);
            System.out.println("First ISBN: " + firstIsbn);
            System.out.println("Genres: " + genres);

        } catch (Exception e) {
            System.err.println("Failed to parse JSON");
            e.printStackTrace();
        }

    }
}
