package com.solvd.library.parser;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;

public class XmlValidator {

    public static boolean validate(String xmlFilePath, String xsdFilePath) {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(xsdFilePath));
            Validator validator = schema.newValidator();
            System.out.println("Attempting to validate " + xmlFilePath + "...");
            validator.validate(new StreamSource(new File(xmlFilePath)));
            System.out.println("✅ XML is valid against XSD.");
            return true;
        } catch (Exception e) {
            System.err.println("❌ XML is NOT valid. Reason: " + e.getMessage());
            return false;
        }
    }
}
