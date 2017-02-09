/**
 * This class models the association between two cells
 * of two different excel files.
 * The FROM coordinates are supposed be mapped to the given TO coordinates.
 */
public class Mapping {

    private int fromRowIdx;
    private int fromColIdx;
    private int toRowIdx;
    private int toColIdx;

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
