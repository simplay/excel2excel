import java.util.ArrayList;

/**
 * A consolidator knows the cell mapping between
 * two excel files and performs the actual mapping between
 * the files (from file A to file B).
 */
public class Consolidator extends FileReader{
    private final ArrayList<CellMapping> cellMappings = new ArrayList<CellMapping>();
    private Excel inExcel;
    private Excel outExcel;

    /**
     *
     * @param mappingFilePath a text file located at data/
     * @param inExcel
     * @param outExcel
     */
    public Consolidator(String mappingFilePath, Excel inExcel, Excel outExcel) {
        this.inExcel = inExcel;
        this.outExcel = outExcel;
        readFile(mappingFilePath);
        mergeExcelFiles();
    }

    /**
     * Takes ever FROM cell and writes it to the corresponding location in the TO excel file.
     */
    private void mergeExcelFiles() {
        for (CellMapping cellMapping : cellMappings) {
            String fromValue = cellMapping.getDefaultValue();
            if (!cellMapping.hasDefaultValue()) {
                fromValue = inExcel.getCellValue(cellMapping.getFromRowIndex(), cellMapping.getFromColumnIndex());
                if (cellMapping.hasTranslation()) {
                    fromValue = Translator.lookup(cellMapping.getTranslationRow(), fromValue);
                }
            }

            int toColumnIndex = cellMapping.getToColumnIndex();

            // TODO: Do this just once in the very beginning to determine the target column
            if (cellMapping.usesOffset()) {
                toColumnIndex = outExcel.findEmptyCellColumnAtFixedRow(cellMapping.getToRowIndex(), cellMapping.getToColumnIndex());
            }
            outExcel.writeCell(fromValue, cellMapping.getToRowIndex(), toColumnIndex);
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
            cellMappings.add(new CellMapping(toRowIdx, toColIdx, usesOffset, defaultValue));
            return;
        }

        int[] items = parseToIntegerArray(row);
        if (items.length > 5) {
            boolean usesOffset = (items[4] == 1);
            cellMappings.add(new CellMapping(items[0], items[1], items[2], items[3], usesOffset, items[5]));
        } else if (items.length > 4) {
            boolean usesOffset = (items[4] == 1);
            cellMappings.add(new CellMapping(items[0], items[1], items[2], items[3], usesOffset));
        } else {
            cellMappings.add(new CellMapping(items[0], items[1], items[2], items[3]));
        }
    }
}
