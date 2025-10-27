package com.solvd.library.parser;

import com.solvd.library.model.*;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;

public class JaxbLibraryParser {

    public Library parse(String xmlFilePath) throws Exception {
        JAXBContext context = JAXBContext.newInstance(Library.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (Library) unmarshaller.unmarshal(new File(xmlFilePath));
    }
}
