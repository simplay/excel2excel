/**
 * This class models the association between two cells
 * of two different excel files.
 *
 * The FROM coordinates are supposed be mapped to the given TO coordinates.
 *
 * This class is used to copy the cell content in a FROM excel to another cell
 * in a TO excel file. The cells are representated by their column- and row indices.
 */
public class Mapping {

    private int fromRowIdx;
    private int fromColIdx;
    private int toRowIdx;
    private int toColIdx;

    /**
     * Define a cell to cell mapping between two excel files.
     *
     * @param fromRowIdx cell row in the FROM excel file
     * @param fromColIdx cell column in the FROM excel file
     * @param toRowIdx cell row in TO the excel file
     * @param toColIdx cell column in TO the excel file
     */
    public Mapping(int fromRowIdx, int fromColIdx, int toRowIdx, int toColIdx) {
        this.fromRowIdx = fromRowIdx;
        this.fromColIdx = fromColIdx;
        this.toRowIdx = toRowIdx;
        this.toColIdx = toColIdx;
    }

    public int getFromRowIndex() {
        return fromRowIdx;
    }

    public int getFromColumnIndex() {
        return fromColIdx;
    }

    public int getToRowIdx() {
        return toRowIdx;
    }

    public int getToColumnIndex() {
        return toColIdx;
    }
}
