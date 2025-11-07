package com.solvd.library.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.solvd.library.model.*;
import com.solvd.library.parser.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final String XML_PATH = "src/main/resources/library.xml";
    private static final String XSD_PATH = "src/main/resources/library.xsd";
    private static final String JSON_PATH = "src/main/resources/library.json";

    public static void main(String[] args) {
        LOGGER.info("XML Validation");
        boolean isValid = XmlValidator.validate(XML_PATH, XSD_PATH);

        if (!isValid) {
            LOGGER.error("Validation failed. Aborting parsing process.");
            return;
        }

        LOGGER.info("\nStAX Parsing");
        try {
            StaxLibraryParser staxParser = new StaxLibraryParser();
            LOGGER.info("StAX Parser Javadoc contains 5 XPaths (see StaxLibraryParser.java).");
            Library libraryStax = staxParser.parse(XML_PATH);

            LOGGER.info("   StAX Parsing successful. Root object populated:");
            LOGGER.info("   Library Name: " + libraryStax.getName());
            LOGGER.info("   Books Loaded: " + libraryStax.getBooks().size());
            LOGGER.info("   Users Loaded: " + libraryStax.getUsers().size());

            LOGGER.info("   Departments Loaded: " + libraryStax.getDepartments().size());
            LOGGER.info("   Employees Loaded: " + libraryStax.getEmployees().size());

            if (libraryStax.getUsers() != null && !libraryStax.getUsers().isEmpty()) {
                LOGGER.info("   (First User's City: " + libraryStax.getUsers().get(0).getAddress().getCity() + ")");
            } else {
                LOGGER.info("(User list is empty or null)");
            }

            if (libraryStax.getBooks().size() > 0 && libraryStax.getBooks().get(0).getReviews() != null) {
                LOGGER.info("   (First Book Reviews: " + libraryStax.getBooks().get(0).getReviews().size() + ")");
            }


        } catch (Exception e) {
            LOGGER.error("StAX Parsing failed: " + e.getMessage());
            e.printStackTrace();
        }

        LOGGER.info("\nJAXB Parsing");
        try {
            JaxbLibraryParser jaxbParser = new JaxbLibraryParser();
            Library libraryJaxb = jaxbParser.parse(XML_PATH);

            LOGGER.info("   JAXB Parsing successful. Root object populated:");
            LOGGER.info("   Library Name: " + libraryJaxb.getName());
            LOGGER.info("   Books Loaded: " + libraryJaxb.getBooks().size());
            LOGGER.info("   Users Loaded: " + libraryJaxb.getUsers().size());

            // NEW: Department and Employee checks
            LOGGER.info("   Departments Loaded: " + libraryJaxb.getDepartments().size());
            LOGGER.info("   Employees Loaded: " + libraryJaxb.getEmployees().size());


            if (libraryJaxb.getBooks() != null && libraryJaxb.getBooks().size() > 1) {
                LOGGER.info("   (Second Book Title: " + libraryJaxb.getBooks().get(1).getTitle() + ")");
            } else {
                LOGGER.info("(Not enough books to check second title)");
            }

            if (libraryJaxb.getBooks().size() > 0 && libraryJaxb.getBooks().get(0).getReviews() != null) {
                LOGGER.info("   (First Book Reviews: " + libraryJaxb.getBooks().get(0).getReviews().size() + ")");
            }


        } catch (Exception e) {
            LOGGER.error("JAXB Parsing failed.");
            e.printStackTrace();
        }

        LOGGER.info("\nJackson Parsing & JSONPath");
        try {
            JacksonLibraryParser parser = new JacksonLibraryParser();
            Library library = parser.parse(JSON_PATH); // Use the correct parse method

            LOGGER.info("   Jackson Parsing successful. Root object populated:");
            LOGGER.info("   Library name: " + library.getName());
            LOGGER.info("   Books count: " + library.getBooks().size());
            LOGGER.info("   Users count: " + library.getUsers().size());

            LOGGER.info("   Departments count: " + library.getDepartments().size());
            LOGGER.info("   Employees count: " + library.getEmployees().size());

            File jsonFile = new File(JSON_PATH);
            String json = new ObjectMapper().readTree(jsonFile).toString();

            List<String> titles = JsonPath.read(json, "$.books[*].title");
            List<String> userIds = JsonPath.read(json, "$.users[*].userId");
            List<String> cities = JsonPath.read(json, "$.users[*].address.city");
            String firstIsbn = JsonPath.read(json, "$.books[0].isbn");
            List<String> genres = JsonPath.read(json, "$.books[*].genre");

            List<String> departmentNames = JsonPath.read(json, "$.departments[*].name");
            List<String> employeeLastNames = JsonPath.read(json, "$.employees[*].lastName");
            List<Integer> firstBookRatings = JsonPath.read(json, "$.books[0].reviews[*].rating");

            LOGGER.info("Book titles: " + titles);
            LOGGER.info("User IDs: " + userIds);
            LOGGER.info("Cities: " + cities);
            LOGGER.info("First ISBN: " + firstIsbn);
            LOGGER.info("Genres: " + genres);

            LOGGER.info("Department Names: " + departmentNames);
            LOGGER.info("Employee Last Names: " + employeeLastNames);
            LOGGER.info("First Book Ratings: " + firstBookRatings);


        } catch (Exception e) {
            LOGGER.error("Failed to parse JSON");
            e.printStackTrace();
        }

    }
}
