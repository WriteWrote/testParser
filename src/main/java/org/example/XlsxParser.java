package org.example;

import org.apache.poi.examples.ss.html.ToHtml;
import org.apache.poi.hssf.converter.ExcelToHtmlConverter;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.Document;
import org.example.model.CompletedSlot;
import org.example.model.EmptySlot;
import org.example.model.Timetable;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class XlsxParser {
    public void parse() throws IOException, ParserConfigurationException, TransformerException {
        System.out.println("Hello world!");

//        String filepath = "src/main/resources/Saspisanie.xlsx";
        String filepath = "src/main/resources/1pers.xlsx";

        Workbook workbook = getFromFile(filepath);
        workbook = new XSSFWorkbook(filepath);

//        List<CompletedSlot> bachelor = this.getCompletedSlots(workbook.getSheetAt(0));

        List<EmptySlot> emptySlots = new ArrayList<>();
        emptySlots.add(new EmptySlot(true, "8:00", "9:45", "Monday"));
        emptySlots.add(new EmptySlot(true, "9:45", "11:20", "Tuesday"));
        emptySlots.add(new EmptySlot(true, "11:30", "13:05", "Wednesday"));

        List<Timetable> timetables = new ArrayList<>();
        timetables.add(new Timetable("",Date.from(Instant.now()), true, -1));
        timetables.add(new Timetable("",Date.from(Instant.now()), false, -1));

        List<CompletedSlot> testSlots = new ArrayList<>();
        testSlots.add(new CompletedSlot(1, emptySlots.get(0), "305П", 1, 1, 1, 1, 1, timetables.get(1)));
        testSlots.add(new CompletedSlot(1, emptySlots.get(1), "301П", 1, 1, 1, 12, 1, timetables.get(1)));
        testSlots.add(new CompletedSlot(1, emptySlots.get(2), "292", 5, 4, 4, 6, null, timetables.get(0)));
        testSlots.add(new CompletedSlot(1, emptySlots.get(0), "302П", 1, 1, 1, 12, 2, timetables.get(0)));
        testSlots.add(new CompletedSlot(1, emptySlots.get(1), "292", 5, 6, 3, 7, 2, timetables.get(0)));
        testSlots.add(new CompletedSlot(1, emptySlots.get(2), "303П", 1, 3, 2, 3, 1, timetables.get(0)));
        testSlots.add(new CompletedSlot(1, emptySlots.get(0), "292", 5, 4, 4, 1, null, timetables.get(0)));

        String html = toHtmlFromHssfWorkbook(emptySlots, testSlots);

        System.out.println("Finish hiiiim!");
    }

    private static Workbook getFromFile(String filepath) throws IOException {
        FileInputStream file = new FileInputStream(new File(filepath));
        return new XSSFWorkbook(file);
    }

    private static void setBordersOnCell(Integer i, Integer j, Sheet sheet) {
        RegionUtil.setBorderBottom(BorderStyle.MEDIUM, new CellRangeAddress(i, i, j, j), sheet);
        RegionUtil.setBorderLeft(BorderStyle.MEDIUM, new CellRangeAddress(i, i, j, j), sheet);
        RegionUtil.setBorderRight(BorderStyle.MEDIUM, new CellRangeAddress(i, i, j, j), sheet);
        RegionUtil.setBorderTop(BorderStyle.MEDIUM, new CellRangeAddress(i, i, j, j), sheet);
    }

    private static String toHtmlFromHssfWorkbook(List<EmptySlot> emptySlots, List<CompletedSlot> completedSlots) {
        final String[] timesArray = {"8:00 - 9:30",
                "9:45 - 11:20",
                "11:30 - 13:05",
                "13:25 - 15:00",
                "15:10 - 16:45",
                "16:55 - 18:30",
                "18:45 - 20:00"};

        List<CompletedSlot> lessons = completedSlots.stream()
                .filter(lesson -> lesson.getSchedule().getIsActual())
                .sorted(Comparator.comparing(lesson -> lesson.getSlotId().getWeekDayNumber()))
                .sorted(Comparator.comparing(lesson -> lesson.getSlotId().getStartTime()))
                .collect(Collectors.toList());

        HSSFWorkbook workbook = new HSSFWorkbook();

        String safeSheetName = WorkbookUtil.createSafeSheetName("");
        Sheet sheet = workbook.createSheet(safeSheetName);

        sheet.createRow(0).setHeight((short) 500);
        sheet.setColumnWidth(0, 5000);

        CreationHelper helper = workbook.getCreationHelper();

        for (int i = 1; i < 7; i++) {
            sheet.getRow(0).createCell(i).setCellValue(helper.createRichTextString(String.valueOf(WeekDaysEnum.values()[i])));
            setBordersOnCell(0, i, sheet);
        }

        for (int i = 1; i < 15; i++) {
            Row row = sheet.createRow(i);
            sheet.setColumnWidth(i, 5000);

            row.createCell(0);

            for (int j = 1; j < 7; j++) {
                setBordersOnCell(i, j, sheet);

                if (i % 2 == 0) {
                    sheet.addMergedRegion(new CellRangeAddress(
                            i - 1,
                            i,
                            j,
                            j
                    ));
                }
            }
        }

        int counter = 0;
        for (int i = 1; i < 15; i++) {
            setBordersOnCell(i, 0, sheet);
            if (i % 2 == 0) {
                sheet.addMergedRegion(new CellRangeAddress(
                        i - 1,
                        i,
                        0,
                        0
                ));
            } else {
                if (counter < timesArray.length) {
                    sheet.getRow(i).getCell(0).setCellValue(helper.createRichTextString(timesArray[counter]));
                    ++counter;
                }
            }
        }

        try (OutputStream fileOut = new FileOutputStream("htmlToDoc.xls")) {
            workbook.write(fileOut);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            return convertXlsxToHtml2(workbook);
        } catch (ParserConfigurationException | TransformerException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String convertXlsxToHtml2(HSSFWorkbook excelDoc) throws ParserConfigurationException, TransformerException, IOException {
        ExcelToHtmlConverter converter = new ExcelToHtmlConverter(
                DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
        );

        converter.processWorkbook(excelDoc);

        org.w3c.dom.Document htmlDoc = converter.getDocument();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DOMSource domSource = new DOMSource(htmlDoc);
        StreamResult streamResult = new StreamResult(out);
        TransformerFactory transfFactory = TransformerFactory.newInstance();
        Transformer serializer = transfFactory.newTransformer();

        serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty(OutputKeys.METHOD, "html");
        serializer.transform(domSource, streamResult);

        out.close();

        return out.toString();
    }

    //    private void convertXlsxToHtml1(){
//        ExcelToHtmlConverter excelToHtmlConverter = new ExcelToHtmlConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
//        excelToHtmlConverter.processWorkbook(workbook);
//
//        Document htmlDocument = excelToHtmlConverter.getDocument();
//
//        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//        DOMSource domSource = new DOMSource (htmlDocument);
//        StreamResult streamResult = new StreamResult(outStream);
//        TransformerFactory tf = TransformerFactory.newInstance();
//        Transformer serializer = tf.newTransformer();
//        serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
//        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
//        serializer.setOutputProperty(OutputKeys.METHOD, "html");
//        serializer.transform (domSource, streamResult);
//
//        outStream.close();
//
//        OutputStream os = new FileOutputStream(new File(ctxPath, htmlid+".html"));
//        final PrintStream printStream = new PrintStream(os);
//        printStream.print(new String(outStream.toByteArray()));
//        printStream.close();
//    }
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

        sortedByCell.removeAll(sortedByCell.stream()
                .filter(List::isEmpty)
                .collect(Collectors.toList()));

        return sortedByCell;
    }

    private static Map<String, List<Integer[]>> countCellIndexRange(Sheet sheet, Integer column, Integer offset) {
        Map<String, List<Integer[]>> indexMap = new HashMap<>();

        String prevValue = sheet.getRow(offset).getCell(column).getStringCellValue();
        int prevIndex = offset;

        for (int i = offset; i < sheet.getPhysicalNumberOfRows(); i++) {
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

    private static CellRangeAddress includedInMergedRegion(Cell slot, List<List<CellRangeAddress>> regions) {
        int cell = slot.getColumnIndex();
        int row = slot.getRowIndex();

        for (var rowLine : regions) {
            if (rowLine.get(0).getFirstRow() <= row &&
                    rowLine.get(0).getLastRow() >= row) {
                for (var region : rowLine) {
                    if (region.getFirstColumn() <= cell &&
                            region.getLastColumn() >= cell) {
                        return region;
                    }
                }
            }
        }
        return null;
//        return regions.stream().
//                filter(el -> el.get(0).getFirstRow() <= row && el.get(0).getLastRow() >= row)
//                .collect(Collectors.toList());
    }

    private static Integer findCourse(Map<String, List<Integer[]>> coursesIndexRange, Integer columnIndex) {
        int course = -1;
        for (var entry : coursesIndexRange.entrySet()) {
            for (var pair : entry.getValue()) {
                if (columnIndex <= pair[1] &&
                        columnIndex >= pair[0]) {
                    course = Integer.parseInt(entry.getKey().split(" ")[0]);
                    return course;
                }
            }
        }
        return course;
    }

    private static Integer[] findGroupAndSubgroup(Map<String, List<Integer[]>> groupsIndexRange, Integer columnIndex) {
        for (var entry : groupsIndexRange.entrySet()) {
            for (var pair : entry.getValue()) {
                if (columnIndex <= pair[1] &&
                        columnIndex >= pair[0]) {
                    if (entry.getKey().length() >= 6) {
                        int group = Integer.parseInt(entry.getKey().split(" ")[0]);
                        int subgroup = columnIndex % 2 + 1;
                        return new Integer[]{group, subgroup};
                    } else {
                        return new Integer[]{0, 0};
                    }
                }
            }
        }
        return new Integer[]{-1, -1};
    }

    private static String[] findTimes(Map<String, List<Integer[]>> timesIndexRange, Integer rowIndex) {
        for (var timesList : timesIndexRange.entrySet()) {
            for (var pair : timesList.getValue()) {
                if (rowIndex <= pair[1] && rowIndex >= pair[0]) {
                    String startTime = timesList.getKey().split("-")[0].trim();
                    String endTime = timesList.getKey().split("-")[1].trim();
                    return new String[]{startTime, endTime};
                }
            }
        }
        return new String[]{"", ""};
    }

    private static String findWeekdayNum(Map<String, List<Integer[]>> weekdaysIndexRange, Integer rowIndex) {
        for (var weekdaysList : weekdaysIndexRange.entrySet()) {
            for (var pair : weekdaysList.getValue()) {
                if (rowIndex <= pair[1] && rowIndex >= pair[0]) {
                    return weekdaysList.getKey();
                }
            }
        }
        return "";
    }

    private CompletedSlot parseCompletedSlot(Cell currentCell,
                                             Map<String, List<Integer[]>> timesIndexRange,
                                             Map<String, List<Integer[]>> weekdaysIndexRange,
                                             Map<String, List<Integer[]>> coursesIndexRange,
                                             Map<String, List<Integer[]>> groupsIndexRange) {
        boolean denominator = currentCell.getRowIndex() % 2 != 0;

        int rowIndex = currentCell.getRowIndex();
        int columnIndex = currentCell.getColumnIndex();

        String[] startEndTimes = findTimes(timesIndexRange, rowIndex);

        String weekdayName = findWeekdayNum(weekdaysIndexRange, rowIndex);

        EmptySlot emptySlot = new EmptySlot(denominator, startEndTimes[0], startEndTimes[1], weekdayName);

        Integer course = findCourse(coursesIndexRange, columnIndex);
        Integer[] groupAndSubgroup = findGroupAndSubgroup(groupsIndexRange, columnIndex);


        // empty slot goes here
        if (!currentCell.getStringCellValue().equals("")) {
            String[] params = currentCell.getStringCellValue().split(" ");
            String classNum = params[params.length - 1];
            String teacherName;
            if (params.length > 2) {
                teacherName = params[params.length - 4] + " " + params[params.length - 3] + " " + params[params.length - 2];

                String subjectName = "";
                for (int k = 0; k < params.length - 4; k++) {
                    subjectName += params[k] + " ";
                }
            }
        }
        CompletedSlot completedSlot = new CompletedSlot(0, new EmptySlot(), "", 0, 0, course, groupAndSubgroup[0], groupAndSubgroup[1]);
        return completedSlot;
    }

    private List<CompletedSlot> getCompletedSlots(Sheet sheet) {
        List<CompletedSlot> result = new LinkedList<>();

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
                if (currentCell != null) {
                    var addressInvolved = includedInMergedRegion(currentCell, mergedRegions);

                    if (addressInvolved != null) {
                        if (currentCell.getColumnIndex() == addressInvolved.getFirstColumn() &&
                                currentCell.getRowIndex() == addressInvolved.getFirstRow()) {
//                            CompletedSlot slot = parseCompletedSlot(currentCell, timesIndexRange, weekdaysIndexRange, coursesIndexRange, groupsIndexRange);
//                            result.add(slot);

                            for (int cellIndex = addressInvolved.getFirstColumn(); cellIndex <= addressInvolved.getLastColumn(); cellIndex++) {
                                for (int rowInde = addressInvolved.getFirstRow(); rowInde <= addressInvolved.getLastRow(); rowInde++) {
                                    CompletedSlot duplicatedSlot = parseCompletedSlot(sheet.getRow(rowInde).getCell(cellIndex),
                                            timesIndexRange,
                                            weekdaysIndexRange,
                                            coursesIndexRange,
                                            groupsIndexRange);
                                    // todo: create parsing the string value itself
                                    // todo: set all slot params, except course, group, subgroup
                                    result.add(duplicatedSlot);
                                }
                            }
                        }
                    } else {
                        if (!currentCell.getStringCellValue().equals("")) {
                            CompletedSlot slot = this.parseCompletedSlot(currentCell, timesIndexRange, weekdaysIndexRange, coursesIndexRange, groupsIndexRange);
                            result.add(slot);
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
            }
        }


        return result;
    }
}