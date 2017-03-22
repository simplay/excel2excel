import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;

import java.io.*;

/**
 * This class models an excel file and allows to easily load and write cells
 * and update the original file.
 *
 * Indices start counting at 0 whereas in excel the first index is 1.
 * This implies one has to apply manually add an offset of one.
 */
public class XlsxExcelFile extends Excel {
    protected XSSFSheet sheet;
    protected XSSFWorkbook workbook;
    protected String filePath;

    /**
     * Load a excel file by its path and sheet nr.
     *
     * @param filePath path to target file. Please stick to
     *  the OS specific path naming conventions.
     * @param sheetNr relevant sheet inside excel file that should be loaded.
     */
    public XlsxExcelFile(String filePath, int sheetNr) {
        this.filePath = filePath;
        workbook = loadWorkbook();
        setSheetAt(sheetNr);
    }

    protected XSSFWorkbook loadWorkbook() {
        XSSFWorkbook workbook = null;
        try {
            FileInputStream file = new FileInputStream(new File(filePath));
            workbook = new XSSFWorkbook(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return workbook;
    }

    /**
     * Load the first excel sheet
     *
     * @param filePath path to target file. Please stick to
     *  the OS specific path naming conventions.
     */
    public XlsxExcelFile(String filePath) {
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
    public void writeCell(CellContent content, int rowIdx, int columnIdx) {
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
        
		cell.setCellType(content.type);
        switch(content.type) {
    		case BLANK:
    			break;
    		
        	case NUMERIC:
        		cell.setCellValue(content.numeric);
        		break;
        		
        	case BOOLEAN:
        		cell.setCellValue(content.bool);
        		break;
        		
        	case STRING:
        		cell.setCellValue(content.string);
        		break;
        		
        	default:
        		Logger.printError("Cell at column " + columnIdx + " could not be written. Invalid cell content given.");
        		break;
        }
    }

    @Override
    protected void setSheetAt(int sheetIndex) {
        sheet = workbook.getSheetAt(sheetIndex);
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
            XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
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
    public XSSFRow getRow(int rowIdx) {
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
        XSSFCell cell;
        CellContent content;
        XSSFRow row = getRow(rowIdx);
        if(row == null)
        	return colIdx;
        colIdx--;
        do {
            colIdx++;
            cell = row.getCell(colIdx);
            content = new CellContent(cell);
        } while(!content.isBlank());
        
        return colIdx;
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
    public CellContent getCellValue(int rowIdx, int columnIdx) {
        XSSFRow row = getRow(rowIdx);
        if (row == null) return null;

        XSSFCell cell = getRow(rowIdx).getCell(columnIdx);
        if (cell == null) return null;
        
        return new CellContent(cell);
    }

    /**
     * Get the currently loaded sheet of the loaded excel file.
     *
     * @retur the current excel sheet
     */
    public XSSFSheet getSheet() {
        return sheet;
    }
}
