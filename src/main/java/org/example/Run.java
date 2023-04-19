package org.example;

import java.io.IOException;

public class Run {
    public static void main(String[] args) throws IOException {
        XlsxParser parser = new XlsxParser();
        parser.parse();
    }
}
