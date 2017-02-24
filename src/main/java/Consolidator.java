import java.util.ArrayList;

/**
 * A consolidator knows the cell mapping between
 * two excel files and performs the actual mapping between
 * the files (from file A to file B).
 */
public class Consolidator extends FileReader {
    private ArrayList<CellMapping> cellMappings;
    private Excel inExcel;
    private ArrayList<Excel> outExcels;

    // lookup indices extracted from the `mappings` file.
    // etermines which excel sheet should be used in the FROM and TO excel file
    // for applying the mapping between the excel cells.
    private ArrayList<Integer> fromSheetIndices;
    private ArrayList<Integer> toSheetIndices;
    private int indicesCouner = -1;

    /**
     *
     * @param mappingFilePath a text file located at data/
     * @param inExcel
     * @param outExcels
     */
    public Consolidator(String mappingFilePath, Excel inExcel, ArrayList<Excel> outExcels, int outExcelIdx) {
        this.inExcel = inExcel;
        this.outExcels = outExcels;
        this.fromSheetIndices = new ArrayList<>();
        this.toSheetIndices = new ArrayList<>();
        this.cellMappings = new ArrayList<>();

        readFile(mappingFilePath);

        // set sheet lookup index
        // TODO: refactor this: Set index when creating the excel instances.
        //  To do so, we have to change the control-flow of this class:
        //  1. constructor should only load the mapping file and extract mapping information.
        //  2. run the copy process by performing a method call.
        updateSheetIndices(outExcelIdx);

        ArrayList<CellMapping> sublistCellMappings = new ArrayList<>();
        for (CellMapping mapping : cellMappings) {
            if (mapping.getOutExcelFileIdx() == outExcelIdx) {
                sublistCellMappings.add(mapping);
            }
        }

        if (Properties.runNormalMode()) {
            copyFromCellsToToCells(outExcelIdx, sublistCellMappings);
        } else {
            Logger.println("Running in debug mode");
            runDebugMode(outExcelIdx, sublistCellMappings);
        }
    }

    private void runDebugMode(int outExcelIdx, ArrayList<CellMapping> sublistCellMappings) {
        inExcel.setLookupSheetIndex(fromSheetIndices.get(outExcelIdx));
        Excel outExcel = outExcels.get(outExcelIdx);
        outExcel.setLookupSheetIndex(toSheetIndices.get(outExcelIdx));
        Logger.println("FROM cells with values:");
        for (CellMapping cellMapping : sublistCellMappings) {
            String fromValue = cellMapping.getDefaultValue();
            if (!cellMapping.hasDefaultValue()) {
                fromValue = inExcel.getCellValue(cellMapping.getFromRowIndex(), cellMapping.getFromColumnIndex());
                if (cellMapping.hasTranslation()) {
                    fromValue = Translator.lookup(cellMapping.getTranslationRow(), fromValue);
                }
            }
            String fromCell = "(" + cellMapping.getFromRowIndex() + "," + cellMapping.getFromColumnIndex() + ")";
            String toCell = "(" + cellMapping.getToRowIndex() + "," + cellMapping.getToColumnIndex() + ")";
            Logger.println(" + " + fromValue + ": " + fromCell + " -> " + toCell);
        }
    }

    /**
     * Update sheet indices according to given mapping definition.
     * By default this the sheet index zero is loaded.
     */
    private void updateSheetIndices(int outExcelIdx) {
        inExcel.setLookupSheetIndex(fromSheetIndices.get(outExcelIdx));
        outExcels.get(outExcelIdx).setSheetAt(outExcelIdx);
    }

    /**
     * Takes ever FROM cell and writes it to the corresponding location in the TO excel file.
     */
    private void copyFromCellsToToCells(int outExcelIdx, ArrayList<CellMapping> sublistCellMappings) {
        Excel outExcel = outExcels.get(outExcelIdx);
        for (CellMapping cellMapping : sublistCellMappings) {
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
        String[] row = line.split(Properties.WHITESPACE_SEPARATOR);

        if (row[0].equals("m")) {
            int fromSheetIndex = Integer.parseInt(row[1]);
            int toSheetIndex = Integer.parseInt(row[2]);
            Logger.println("Using from sheet Index " + fromSheetIndex + " an TO sheet index: " + toSheetIndex + " for performing cell lookups.");
            fromSheetIndices.add(fromSheetIndex);
            toSheetIndices.add(toSheetIndex);
            indicesCouner++;
        } else {

            // there is a default value specified
            if (!lastRowItemIsNumeric(row)) {
                int toRowIdx = Integer.parseInt(row[0]);
                int toColIdx = Integer.parseInt(row[1]);
                boolean usesOffset = (row[row.length - 2].equals("1"));
                String defaultValue = row[row.length - 1];
                defaultValue = defaultValue.replace("\"", "");
                cellMappings.add(new CellMapping(indicesCouner, toRowIdx, toColIdx, usesOffset, defaultValue));
                return;
            }

            int[] items = parseToIntegerArray(row);
            if (items.length > 5) {
                boolean usesOffset = (items[4] == 1);
                cellMappings.add(new CellMapping(indicesCouner, items[0], items[1], items[2], items[3], usesOffset, items[5]));
            } else if (items.length > 4) {
                boolean usesOffset = (items[4] == 1);
                cellMappings.add(new CellMapping(indicesCouner, items[0], items[1], items[2], items[3], usesOffset));
            } else {
                cellMappings.add(new CellMapping(indicesCouner, items[0], items[1], items[2], items[3]));
            }
        }
    }
}
