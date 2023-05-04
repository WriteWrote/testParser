package org.example;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

public class Run {
    public static void main(String[] args) throws IOException, ParserConfigurationException, TransformerException {
        XlsxParser parser = new XlsxParser();
        parser.parse();
    }
}
