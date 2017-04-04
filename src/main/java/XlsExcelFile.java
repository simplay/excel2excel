import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import java.nio.file.*;
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
    private Path filePath;

    /**
     * Load a excel file by its path and sheet nr.
     *
     * @param filePath path to target file. Please stick to
     *  the OS specific path naming conventions.
     * @param sheetNr relevant sheet inside excel file that should be loaded.
     */
    public XlsExcelFile(String filePath, int sheetNr) {
        this.filePath = Paths.get(filePath);

        InputStream file = null;
        try {
            file = Files.newInputStream(this.filePath, StandardOpenOption.READ);
            workbook = new HSSFWorkbook(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setSheetAt(sheetNr);
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

    @Override
    protected void setSheetAt(int sheetIndex) {
        this.sheet = workbook.getSheetAt(sheetIndex);
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
            OutputStream stream = Files.newOutputStream(filePath, StandardOpenOption.WRITE);
            HSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
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
    @Override
    public HSSFRow getRow(int rowIdx) {
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
    @Override
    public CellContent getCellValue(int rowIdx, int columnIdx) {
        HSSFRow row = getRow(rowIdx);
        if (row == null) return null;

        HSSFCell cell = getRow(rowIdx).getCell(columnIdx);
        if (cell == null) return null;
        
        return new CellContent(cell);
    }

    /**
     * Get the currently loaded sheet of the loaded excel file.
     *
     * @return the current excel sheet
     */
    @Override
    public HSSFSheet getSheet() {
        return sheet;
    }
}

