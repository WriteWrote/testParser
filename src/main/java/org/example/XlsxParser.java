package org.example;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.model.CompletedSlot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class XlsxParser {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello world!");

        String filepath = "src/main/resources/Saspisanie.xlsx";

        // retrieving file; todo: think how it will be uploaded in actual service
        Workbook workbook = getFromFile(filepath);

        //bachelor
//        Map<Integer, List<String>> bachelor = getStringCells(workbook, 0);
        //mastery
//        Map<Integer, List<String>> mastery = getStringCells(workbook, 1);

        List<CompletedSlot> bachelor = getCompletedSlots(workbook.getSheetAt(0));


        System.out.println("Finish hiiiim!");
    }

    private static Workbook getFromFile(String filepath) throws IOException {
        FileInputStream file = new FileInputStream(new File(filepath));
        return new XSSFWorkbook(file);
    }

    private static Map<Integer, List<String>> getStringCells(Workbook workbook, Integer pageIndex) {
        // working with current sheet
        Sheet sheet = workbook.getSheetAt(pageIndex);

        Map<Integer, List<String>> data = new HashMap<>();
        int i = 0;

        for (Row row : sheet) {
            data.put(i, new ArrayList<String>());       // init the list of cells with index

            for (Cell cell : row) {
//                System.out.println(cell.getStringCellValue() + " ");
                data.get(i).add(cell.getStringCellValue());
            }

//            System.out.println("\n");
            i++;
        }

        return data;
    }

    private static Map<String, Integer> countStartIndexRange(Row row) {
        Map<String, Integer> startIndexMap = new HashMap<>();

        String prevValue = row.getCell(0).getStringCellValue();
        startIndexMap.put(prevValue, 0);

        for (Cell cell : row) {
            String currentValue = cell.getStringCellValue();

            if (!prevValue.equals(currentValue) && !currentValue.equals("")) {
                startIndexMap.put(currentValue, cell.getColumnIndex());
            }
        }

        return startIndexMap;
    }

    private static List<List<CellRangeAddress>> processMergedRegions(Sheet sheet) {
        var sortedByRow = sheet.getMergedRegions().stream()
                .sorted(Comparator.comparing(CellRangeAddress::getFirstRow))
                .collect(Collectors.toList());

        List<List<CellRangeAddress>> sortedByCell = new LinkedList<>();

        for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
            int finalI = i;
            var line = sortedByRow.stream()
                    .filter(el -> el.getFirstRow()==finalI)
                    .sorted(Comparator.comparing(CellRangeAddress::getFirstColumn))
                    .collect(Collectors.toList());
            sortedByCell.add(line);
        }
        return sortedByCell;
    }

    private static List<CompletedSlot> getCompletedSlots(Sheet sheet) {
        Map<String, Integer> coursesIndexRange = countStartIndexRange(sheet.getRow(0));
        Map<String, Integer> groupsIndexRange = countStartIndexRange(sheet.getRow(1));

        var mergedRegions = processMergedRegions(sheet);

        var bannedRows = mergedRegions.stream().sorted()
                .filter(el -> el.size()==1)
                .collect(Collectors.toList());




        return null;
    }
}