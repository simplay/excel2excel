/**
 * A CellMapping models the association between two cells
 * that are in two different excel files.
 *
 * The content of a FROM cell position is supposed be mapped to the given TO cell position.
 *
 * This class is used to copy the cell content in a FROM excel to another cell
 * in a TO excel file. Cells are represented by their column- and row indices.
 *
 * The dynamic column computation works as follows:
 * look for the first column index at a fixed row that has a empty cell.
 * Start the lookup at the given index
 */
public class CellMapping {

    private int outExcelFileIdx;
    private int fromRowIdx;
    private int fromColIdx;
    private int toRowIdx;
    private int toColIdx;
    private boolean usesOffset;
    private String defaultValue;
    private int translationRow;

    /**
     * Define a cell to cell mapping between two excel files.
     *
     * @param fromRowIdx cell row in the FROM excel file
     * @param fromColIdx cell column in the FROM excel file
     * @param toRowIdx cell row in TO the excel file
     * @param toColIdx cell column in TO the excel file
     * @param usesOffset indicates whether the TO column index is computed dynamically.
     */
    public CellMapping(int outExcelFileIdx, int fromRowIdx, int fromColIdx, int toRowIdx, int toColIdx, boolean usesOffset, String defaultValue, int translationRow) {
        this.outExcelFileIdx = outExcelFileIdx;
        this.fromRowIdx = fromRowIdx;
        this.fromColIdx = fromColIdx;
        this.toRowIdx = toRowIdx;
        this.toColIdx = toColIdx;
        this.usesOffset = usesOffset;
        this.defaultValue = defaultValue;
        this.translationRow = translationRow;
    }

    /**
     * CellMapping with optional propagation of the column
     *
     * @param fromRowIdx
     * @param fromColIdx
     * @param toRowIdx
     * @param toColIdx
     * @param usesOffset
     */
    public CellMapping(int outExcelFileIdx, int fromRowIdx, int fromColIdx, int toRowIdx, int toColIdx, boolean usesOffset) {
        this(outExcelFileIdx, fromRowIdx, fromColIdx, toRowIdx, toColIdx, usesOffset, "", -1);
    }

    /**
     * CellMapping with translation
     *
     * @param fromRowIdx
     * @param fromColIdx
     * @param toRowIdx
     * @param toColIdx
     * @param usesOffset
     * @param translationRow
     */
    public CellMapping(int outExcelFileIdx, int fromRowIdx, int fromColIdx, int toRowIdx, int toColIdx, boolean usesOffset, int translationRow) {
        this(outExcelFileIdx, fromRowIdx, fromColIdx, toRowIdx, toColIdx, usesOffset, "", translationRow);
    }

    /**
     * Simple mapping: use the content of one cell and copy it to another cell.
     *
     * @param fromRowIdx
     * @param fromColIdx
     * @param toRowIdx
     * @param toColIdx
     */
    public CellMapping(int outExcelFileIdx, int fromRowIdx, int fromColIdx, int toRowIdx, int toColIdx) {
        this(outExcelFileIdx, fromRowIdx, fromColIdx, toRowIdx, toColIdx, false, "", -1);
    }

    /**
     * Apply a fixed value to a cell
     *
     * @param toRowIdx
     * @param toColIdx
     * @param usesOffset
     * @param defaultValue
     */
    public CellMapping(int outExcelFileIdx, int toRowIdx, int toColIdx, boolean usesOffset, String defaultValue) {
        this(outExcelFileIdx, -1, -1, toRowIdx, toColIdx, usesOffset, defaultValue, -1);
    }

    public int getFromRowIndex() {
        return fromRowIdx;
    }

    public int getFromColumnIndex() {
        return fromColIdx;
    }

    public int getToRowIndex() {
        return toRowIdx;
    }

    public int getOutExcelFileIdx() {
        return outExcelFileIdx;
    }

    public int getToColumnIndex() {
        return toColIdx;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public boolean usesOffset() {
        return usesOffset;
    }

    public boolean hasDefaultValue() {
        return !defaultValue.equals("");
    }

    public boolean hasTranslation() {
        return translationRow > -1;
    }

    public int getTranslationRow() {
        return translationRow;
    }
}
