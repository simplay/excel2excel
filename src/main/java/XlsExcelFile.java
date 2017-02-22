import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import java.io.*;

/**
 * This class models an excel file and allows to easily load and write cells
 * and update the original file.
 *
 * Indices start counting at 0 whereas in excel the first index is 1.
 * This implies one has to apply manually add an offset of one.
 */
public class XlsExcelFile extends Excel {
    private HSSFSheet sheet;
    private HSSFWorkbook workbook;
    private String filePath;

    /**
     * Load a excel file by its path and sheet nr.
     *
     * @param filePath path to target file. Please stick to
     *  the OS specific path naming conventions.
     * @param sheetNr relevant sheet inside excel file that should be loaded.
     */
    public XlsExcelFile(String filePath, int sheetNr) {
        this.filePath = filePath;

        FileInputStream file = null;
        try {
            file = new FileInputStream(new File(filePath));
            workbook = new HSSFWorkbook(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Return first sheet from the XLSX workbook
        sheet = workbook.getSheetAt(sheetNr);
    }

    /**
     * Load the first excel sheet
     *
     * @param filePath path to target file. Please stick to
     *  the OS specific path naming conventions.
     */
    public XlsExcelFile(String filePath) {
        this(filePath, 0);
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
    @Override
    public void writeCell(String content, int rowIdx, int columnIdx) {
        Row row = null;
        Cell cell = null;

        row = getRow(rowIdx);
        if (row == null) {
            Logger.printError("Row " + rowIdx + " does yet not exist. Creating new row...");
            row = getSheet().createRow(rowIdx);
        }

        cell = row.getCell(columnIdx);
        if (cell == null) {
            Logger.printError("Cell at column " + columnIdx + " does yet not exist. Creating new cell...");
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
    @Override
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
     * First row index is 0.
     *
     * @param rowIdx cell row index
     * @return row at given row index.
     */
    public HSSFRow getRow(int rowIdx) {
        return getSheet().getRow(rowIdx);
    }

    /**
     * Find the next free cell-column index for a given row index.
     * Please note that the first index value is represented by the value 0.
     *
     * @param rowIdx cell row index.
     * @param startColIdx cell column index we want to start our search.
     * @return free column index.
     */
    @Override
    public int findEmptyCellColumnAtFixedRow(int rowIdx, int startColIdx) {
        int colIdx = startColIdx;
        HSSFCell content;
        do {
            HSSFRow row = getRow(rowIdx);
            if (row == null) break;
            content = row.getCell(colIdx);

            colIdx++;
        } while (content != null);
        if (colIdx == startColIdx) return startColIdx;
        return colIdx - 1;
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
    @Override
    public String getCellValue(int rowIdx, int columnIdx) {
        String cellContent = "";
        HSSFRow row = getRow(rowIdx);
        if (row == null) return null;

        HSSFCell cell = getRow(rowIdx).getCell(columnIdx);
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                cellContent = String.valueOf(cell.getStringCellValue());
                break;
            case Cell.CELL_TYPE_NUMERIC:
                cellContent = cell.getStringCellValue();
                if (cellContent.contains(".")) {
                    cellContent = String.valueOf(cell.getNumericCellValue());
                }
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                cellContent = String.valueOf(cell.getBooleanCellValue());
                break;
            default:
        }
        return cellContent;
    }

    /**
     * Get the currently loaded sheet of the loaded excel file.
     *
     * @retur the current excel sheet
     */
    public HSSFSheet getSheet() {
        return sheet;
    }
}

