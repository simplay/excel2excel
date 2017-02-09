import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

/**
 * This class models an excel file and allows to easily load and write cells
 * and update the original file.
 *
 * Indices start counting at 0 whereas in excel the first index is 1.
 * This implies one has to apply manually add an offset of one.
 */
public class ExcelFile {
    private XSSFSheet sheet;
    private XSSFWorkbook workbook;
    private String filePath;

    /**
     *
     * @param filePath path to target file. Please stick to
     *  the OS specific path naming conventions.
     * @param sheetNr relevant sheet inside excel file
     */
    public ExcelFile(String filePath, int sheetNr) {
        this.filePath = filePath;

        FileInputStream file = null;
        try {
            file = new FileInputStream(new File(filePath));
            workbook = new XSSFWorkbook(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Return first sheet from the XLSX workbook
        sheet = workbook.getSheetAt(sheetNr);
    }

    /**
     * Write a given string to a cell at a given location.
     *
     * Calling this function will not overwrite the excel file.
     * It only updates its state.
     *
     * @param content new cell value
     * @param rowIdx cell row index
     * @param columnIdx cell column index
     */
    public void writeCell(String content, int rowIdx, int columnIdx) {
        Row row = null;
        Cell cell = null;

        row = getRow(rowIdx);
        if (row == null) {
            System.err.println("Row " + rowIdx + " does yet not exist. Creating new row...");
            row = getSheet().createRow(rowIdx);
        }

        cell = row.getCell(columnIdx);
        if (cell == null) {
            System.err.println("Cell at column " + columnIdx + " does yet not exist. Creating new cell...");
            cell = row.createCell(columnIdx);
        }

        cell.setCellValue(content);
    }

    /**
     * Save the cells the loaded / updated excel file.
     *
     * Please close the file your are about to write,
     * before actually invoking the writer function.
     */
    public void save() {
        try {
            FileOutputStream stream = new FileOutputStream(filePath);
            workbook.write(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the row by an index from the current sheet.
     *
     * @param rowIdx
     * @return
     */
    public XSSFRow getRow(int rowIdx) {
        return getSheet().getRow(rowIdx);
    }

    /**
     * Fetch the string value of the target cell's value.
     *
     * @example In order to access the excel cell with index (m,n)
     *  you have to pass getCellValue(m-1, n-1)
     *
     * @param rowIdx row index in current excel sheet
     * @param columnIdx column index in current excel sheet.
     * @return string representation of target cell.
     */
    public String getCellValue(int rowIdx, int columnIdx) {
        String cellContent = "";
        XSSFCell cell = getRow(rowIdx).getCell(columnIdx);
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                cellContent = String.valueOf(cell.getStringCellValue());
                break;
            case Cell.CELL_TYPE_NUMERIC:
                cellContent = String.valueOf(cell.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                cellContent = String.valueOf(cell.getBooleanCellValue());
                break;
            default:
        }
        return cellContent;
    }

    /**
     * @retur the current excel sheet
     */
    public XSSFSheet getSheet() {
        return sheet;
    }
}
