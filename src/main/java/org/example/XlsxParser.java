package org.example;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.model.CompletedSlot;
import org.example.model.EmptySlot;

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

    private static Map<String, List<Integer[]>> countRowIndexRange(Row row) {
        Map<String, List<Integer[]>> indexMap = new HashMap<>();

        String prevValue = row.getCell(0).getStringCellValue();
        int prevIndex = 0;

        for (Cell cell : row) {
            String currentValue = cell.getStringCellValue();
            List<Integer[]> buff;

            if (!prevValue.equals(currentValue) && !currentValue.equals("")) {
                if (indexMap.containsKey(prevValue)) {
                    buff = indexMap.get(prevValue);
                    buff.add(new Integer[]{prevIndex, cell.getColumnIndex() - 1});
                    indexMap.put(prevValue, buff);
                } else {
                    buff = new ArrayList<>();
                }

                buff.add(new Integer[]{prevIndex, cell.getColumnIndex() - 1});
                indexMap.put(prevValue, buff)
                ;
                prevValue = currentValue;
                prevIndex = cell.getColumnIndex();
            }
        }

        return indexMap;
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

    private static Map<String, List<Integer[]>> countCellIndexRange(Sheet sheet, Integer column, Integer offset) {
        Map<String, List<Integer[]>> indexMap = new HashMap<>();

        String prevValue = sheet.getRow(offset).getCell(column).getStringCellValue();
        int prevIndex = offset;

        for (int i = offset + 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            String currentValue = sheet.getRow(i).getCell(column).getStringCellValue();

            if (!prevValue.equals(currentValue) && !currentValue.equals("")) {
                if (indexMap.containsKey(prevValue)) {
                    List<Integer[]> buff = indexMap.get(prevValue);
                    buff.add(new Integer[]{prevIndex, i - 1});
                    indexMap.put(prevValue, buff);
                } else {
                    List<Integer[]> buff = new ArrayList<>();
                    buff.add(new Integer[]{prevIndex, i - 1});
                    indexMap.put(prevValue, buff);
                }
                prevValue = currentValue;
                prevIndex = i;
            }
        }

        return indexMap;
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
        // cells 2 - lesson times
        // row 1 - courses
        // row 2 - groups
        // row 3-4 - направления и специальности excluded for now
        Map<String, List<Integer[]>> coursesIndexRange = countRowIndexRange(sheet.getRow(0));     // все курсы занимают несколько ячеек
        Map<String, List<Integer[]>> groupsIndexRange = countRowIndexRange(sheet.getRow(1));  // есть группы, занимающие либо 1, либо 2 ячейки
        Map<String, List<Integer[]>> weekdaysIndexRange = countCellIndexRange(sheet, 0, 4);
        Map<String, List<Integer[]>> timesIndexRange = countCellIndexRange(sheet, 1, 4);

        var mergedRegions = processMergedRegions(sheet);

        for (int i = 4; i < sheet.getPhysicalNumberOfRows(); i++) {
            int count = sheet.getRow(i).getLastCellNum();
            for (int j = 2; j < count; j++) {
                Cell currentCell = sheet.getRow(i).getCell(j);
                // todo fix streaming bug
//                var addressesInvolved = includedInMergedRegion(currentCell, mergedRegions);

//                if (addressesInvolved.size() != 0) {

                // todo check if it's first


//                } else {
                if (!currentCell.getStringCellValue().equals("")) {
                    boolean denominator = currentCell.getRowIndex() % 2 != 0;

                    int rowIndex = currentCell.getRowIndex();
                    int columnIndex = currentCell.getColumnIndex();

                    String startTime = null;
                    String endTime = null;

                    boolean flag = false;

                    for (var timesList : timesIndexRange.entrySet()) {
                        for (var pair : timesList.getValue()) {
                            if (rowIndex <= pair[1] && rowIndex >= pair[0]) {
                                startTime = timesList.getKey().split("-")[0].trim();
                                endTime = timesList.getKey().split("-")[1].trim();
                                flag = true;
                                break;
                            }
                        }
                        if (flag) {
                            flag = false;
                            break;
                        }
                    }

                    String weekdayName = "";
                    for (var weekdaysList : weekdaysIndexRange.entrySet()) {
                        for (var pair : weekdaysList.getValue()) {
                            if (rowIndex <= pair[1] && rowIndex >= pair[0]) {
                                weekdayName = weekdaysList.getKey();
                                flag = true;
                                break;
                            }
                        }
                        if (flag) {
                            flag = false;
                            break;
                        }
                    }

                    EmptySlot emptySlot = new EmptySlot(denominator, startTime, endTime, weekdayName);

                    Integer course = null;
                    Integer group = null;
                    Integer subgroup = null;

                    for (var entry : coursesIndexRange.entrySet()) {
                        for (var pair : entry.getValue()) {
                            if (columnIndex <= pair[1] &&
                                    columnIndex >= pair[0]) {
                                course = Integer.parseInt(entry.getKey().split(" ")[0]);
                                flag = true;
                                break;
                            }
                        }
                        if (flag) {
                            flag = false;
                            break;
                        }
                    }
                    for (var entry : groupsIndexRange.entrySet()) {
                        for (var pair : entry.getValue()) {
                            if (columnIndex <= pair[1] &&
                                    columnIndex >= pair[0]) {
                                group = Integer.parseInt(entry.getKey().split(" ")[0]);
                                subgroup = columnIndex % 2 + 1;
                                flag = true;
                                break;
                            }
                        }
                        if (flag) {
                            break;
                        }
                    }
                    // empty slot goes here
                    CompletedSlot completedSlot = new CompletedSlot(0, 0, "", 0, 0, course, group, subgroup);
                }
            }

            /* Algorithm:
             * get cell
             * check if it's empty -> check if it's in merged region
             *      if in merged region: check if its first, then take info from it
             *                           form a Completed Slot
             *                           duplicate it for merged region
             *                           if not first, let it go, because first cell already was there before
             *      if no merged region:
             * if it's not empty: check if it's in merged region, then take info, form a Completed Slot, duplicate
             *
             * to form a completed slot need to create empty slot, subject and teacher (in real project will be searching those in db
             * */

            // check if cell in merged region
            //              yes: check if it's first
            //                                yes: duplicate and form Completed Slots
            //no: let it be
            //              NO: if it's not empty, form completed slot

        }
//        }


        return null;
    }
}