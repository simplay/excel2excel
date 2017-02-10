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
            String fromValue = mapping.getDefaultValue();
            if (!mapping.hasDefaultValue()) {
                fromValue = inExcel.getCellValue(mapping.getFromRowIndex(), mapping.getFromColumnIndex());
                if (mapping.hasTranslation()) {
                    fromValue = Translator.lookup(mapping.getTranslationRow(), fromValue);
                }
            }

            int toColumnIndex = mapping.getToColumnIndex();
            if (mapping.usesOffset()) {
                toColumnIndex = outExcel.findEmptyCellColumnAtFixedRow(mapping.getToRowIdx(), mapping.getToColumnIndex());
            }
            outExcel.writeCell(fromValue, mapping.getToRowIdx(), toColumnIndex);
        }
    }

    private boolean lastRowItemIsNumeric(String[] row) {
        String str = row[row.length - 1];
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    @Override
    protected void processLine(String line) {
        String[] row = line.split(" ");

        // there is a default value specified
        if (!lastRowItemIsNumeric(row)) {
            int toRowIdx = Integer.parseInt(row[0]);
            int toColIdx = Integer.parseInt(row[1]);
            boolean usesOffset = (row[row.length - 2].equals("1"));
            String defaultValue = row[row.length - 1];
            defaultValue = defaultValue.replace("\"", "");
            mappings.add(new Mapping(toRowIdx, toColIdx, usesOffset, defaultValue));
            return;
        }

        int[] items = parseToIntegerArray(row);
        if (items.length > 5) {
            boolean usesOffset = (items[4] == 1);
            mappings.add(new Mapping(items[0], items[1], items[2], items[3], usesOffset, items[5]));
        } else if (items.length > 4) {
            boolean usesOffset = (items[4] == 1);
            mappings.add(new Mapping(items[0], items[1], items[2], items[3], usesOffset));
        } else {
            mappings.add(new Mapping(items[0], items[1], items[2], items[3]));
        }
    }
}
