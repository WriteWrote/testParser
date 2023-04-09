package org.example;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XlsxParser {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello world!");

        String filepath = "src/main/resources/Saspisanie.xlsx";

        // retrieving file; todo: think how it will be uploaded in actual service
        Workbook workbook = getFromFile(filepath);

        //bachelor
        Map<Integer, List<String>> bachelor = getStringCells(workbook, 0);


        //mastery
        Map<Integer, List<String>> mastery = getStringCells(workbook, 1);


        System.out.println("Finish hiiiim!");
    }

    private static Workbook getFromFile(String filepath) throws IOException {
        FileInputStream file = new FileInputStream(new File(filepath));
        return new XSSFWorkbook(file);
    }
    private static Map<Integer, List<String>> getStringCells(Workbook workbook, Integer pageIndex){
        // working with current sheet
        Sheet sheet = workbook.getSheetAt(pageIndex);

        Map<Integer, List<String>> data = new HashMap<>();
        int i = 0;

        for (Row row : sheet) {
            data.put(i, new ArrayList<String>());       // init the list of cells with index

            for (Cell cell : row) {
                System.out.println(cell.getStringCellValue() + " ");
                data.get(i).add(cell.getStringCellValue());
            }

//            System.out.println("\n");
            i++;
        }

        return data;
    }
}