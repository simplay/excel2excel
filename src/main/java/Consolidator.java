import java.util.ArrayList;

/**
 * A consolidator knows the cell mapping between
 * two excel files and performs the actual mapping between
 * the files (from file A to file B).
 */
public class Consolidator extends FileReader{
    private final ArrayList<Mapping> mappings = new ArrayList<Mapping>();
    private ExcelFile inExcel;
    private ExcelFile outExcel;

    /**
     *
     * @param mappingFilePath a text file located at data/
     * @param inExcel
     * @param outExcel
     */
    public Consolidator(String mappingFilePath, ExcelFile inExcel, ExcelFile outExcel) {
        this.inExcel = inExcel;
        this.outExcel = outExcel;
        readFile(mappingFilePath);
        mergeExcelFiles();
    }

    /**
     * Takes ever FROM cell and writes it to the corresponding location in the TO excel file.
     */
    private void mergeExcelFiles() {
        for (Mapping mapping : mappings) {
            String fromValue = inExcel.getCellValue(mapping.getFromRowIndex(), mapping.getFromColumnIndex());
            outExcel.writeCell(fromValue, mapping.getToRowIdx(), mapping.getToColumnIndex());
        }
    }

    @Override
    protected void processLine(String line) {
        String[] row = line.split(" ");
        int[] items = parseToIntegerArray(row);
        mappings.add(new Mapping(items[0], items[1], items[2], items[3]));
    }
}
