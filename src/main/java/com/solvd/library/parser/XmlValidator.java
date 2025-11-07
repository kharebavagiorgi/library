package com.solvd.library.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;

public class XmlValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlValidator.class);

    public static boolean validate(String xmlFilePath, String xsdFilePath) {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(xsdFilePath));
            Validator validator = schema.newValidator();
            LOGGER.debug("Attempting to validate {}...", xmlFilePath);
            validator.validate(new StreamSource(new File(xmlFilePath)));
            LOGGER.info("XML is valid against XSD.");
            return true;
        } catch (Exception e) {
            LOGGER.error("XML is NOT valid. Reason: " + e.getMessage());
            return false;
        }
    }
}
