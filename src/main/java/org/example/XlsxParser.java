package org.example;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
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

    private static Map<String, Integer> countRowIndexRange(Row row) {
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
                    .filter(el -> el.getFirstRow() == finalI)
                    .sorted(Comparator.comparing(CellRangeAddress::getFirstColumn))
                    .collect(Collectors.toList());
            sortedByCell.add(line);
        }
        return sortedByCell;
    }

    private static Map<String, Integer> countCellIndexRange(Sheet sheet, Integer column) {
        //todo: finish countCellIndexRange()
        return null;
    }

    private static List<List<CellRangeAddress>> includedInMergedRegion(Cell slot, List<List<CellRangeAddress>> regions) {
        int cell = slot.getColumnIndex();
        int row = slot.getRowIndex();

        return regions.stream().
                filter(el -> el.get(0).getFirstRow() <= row && el.get(0).getLastRow() >= row)
                .collect(Collectors.toList());
    }

    private static List<CompletedSlot> getCompletedSlots(Sheet sheet) {
        // cells 1 - weekdays
        // cells 2 - leson times
        // row 1 - courses
        // row 2 - groups
        // row 3-4 - направления и специальности excluded for now
        Map<String, Integer> coursesIndexRange = countRowIndexRange(sheet.getRow(0));
        Map<String, Integer> groupsIndexRange = countRowIndexRange(sheet.getRow(1));
        Map<String, Integer> weekdaysIndexRange = countCellIndexRange(sheet, 1);
        Map<String, Integer> timesIndexRange = countCellIndexRange(sheet, 2);

        var mergedRegions = processMergedRegions(sheet);

        for (int i = 5; i < sheet.getPhysicalNumberOfRows(); i++) {
            int count = sheet.getRow(i).getLastCellNum();
            for (int j = 3; j < count; j++) {
                Cell currentCell = sheet.getRow(i).getCell(j);
                if (currentCell.getStringCellValue().equals("")){
                    var addressesInvolved = includedInMergedRegion(currentCell, mergedRegions);
                    if (addressesInvolved.size()!=0){

                    }
                }

                /* Algorithm:
                 * get cell
                 * check if it's empty -> check if it's in merged region
                 * if in merged region: check if its first, then take info from it
                 *                      form a Completed Slot
                 *                      duplicate it for merged region
                 * if no merged region: take info, form a Completed Slot
                 *
                 * to form a completed slot need to create empty slot, subject and teacher (in real project will be searching those in db
                 * */

            }
        }


        return null;
    }
}