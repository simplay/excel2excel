import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;

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
    private ArrayList<Integer> toExcelIndices;
    private int currentOutIdx = -1;
    private int currentMappingIdx = -1;

    /**
     *
     * @param mappingFilePath a text file located at data/
     * @param inExcel
     * @param outExcels
     */
    public Consolidator(String mappingFilePath, Excel inExcel, ArrayList<Excel> outExcels) {
        this.inExcel = inExcel;
        this.outExcels = outExcels;
        this.fromSheetIndices = new ArrayList<>();
        this.toSheetIndices = new ArrayList<>();
        this.toExcelIndices = new ArrayList<>();
        this.cellMappings = new ArrayList<>();

        readFile(mappingFilePath);
        
        int mappingIdx = 0;
        for (int outExcelIdx: toExcelIndices) {
            updateSheetIndices(outExcelIdx, mappingIdx);
        	ArrayList<CellMapping> sublistCellMappings = new ArrayList<>();
	        for (CellMapping mapping : cellMappings) {
	            if (mapping.getMappingIdx() == mappingIdx) {
	                sublistCellMappings.add(mapping);
	            }
	        }
	
	        if (Properties.runNormalMode()) {
	            copyFromCellsToToCells(outExcelIdx, sublistCellMappings);
	        } else {
	            Logger.println("Running in debug mode");
	            runDebugMode(outExcelIdx, sublistCellMappings);
	        }
	        mappingIdx++;
        }
    }
    
    private CellContent processCellMapping(CellMapping cellMapping) {
    	CellContent fromValue = new CellContent(cellMapping.getDefaultValue());
        
        if(cellMapping.hasDateFormatConversion()) {
        	fromValue = inExcel.getCellValue(cellMapping.getFromRowIndex(), cellMapping.getFromColumnIndex());
        	if(fromValue.type != CellType.NUMERIC) {
        		Logger.printError("Source cell in date conversion did not contain a numeric.");
        	} else {
        		Date javaDate = DateUtil.getJavaDate(fromValue.numeric);
        		fromValue = new CellContent(new SimpleDateFormat(cellMapping.getDefaultValue()).format(javaDate));
        	}
        }
        
        if (!cellMapping.hasDefaultValue()) {
            fromValue = inExcel.getCellValue(cellMapping.getFromRowIndex(), cellMapping.getFromColumnIndex());
            if (cellMapping.hasTranslation() && fromValue.type == CellType.STRING) {
            	fromValue.type = CellType.NUMERIC;
                fromValue.numeric = Translator.lookup(cellMapping.getTranslationRow(), fromValue.string);
            }
        }
        
        return fromValue;
    }

    private void runDebugMode(int outExcelIdx, ArrayList<CellMapping> sublistCellMappings) {
        inExcel.setLookupSheetIndex(fromSheetIndices.get(outExcelIdx));
        Excel outExcel = outExcels.get(outExcelIdx);
        outExcel.setLookupSheetIndex(toSheetIndices.get(outExcelIdx));
        Logger.println("FROM cells with values:");
        for (CellMapping cellMapping : sublistCellMappings) {
            CellContent fromValue = processCellMapping(cellMapping);
            String fromCell = "(" + cellMapping.getFromRowIndex() + "," + cellMapping.getFromColumnIndex() + ")";
            String toCell = "(" + cellMapping.getToRowIndex() + "," + cellMapping.getToColumnIndex() + ")";
            Logger.println(" + " + fromValue + ": " + fromCell + " -> " + toCell);
        }
    }

    /**
     * Update sheet indices according to given mapping definition.
     * By default this the sheet index zero is loaded.
     */
    private void updateSheetIndices(int outExcelIdx, int mappingIdx) {
        inExcel.setLookupSheetIndex(fromSheetIndices.get(mappingIdx));
        outExcels.get(outExcelIdx).setSheetAt(toSheetIndices.get(mappingIdx));
    }

    /**
     * Takes ever FROM cell and writes it to the corresponding location in the TO excel file.
     */
    private void copyFromCellsToToCells(int outExcelIdx, ArrayList<CellMapping> sublistCellMappings) {
        Excel outExcel = outExcels.get(outExcelIdx);
        for (CellMapping cellMapping : sublistCellMappings) {
            CellContent fromValue = processCellMapping(cellMapping);
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
            currentOutIdx = Integer.parseInt(row[1]);
            int fromSheetIndex = Integer.parseInt(row[2]);
            int toSheetIndex = Integer.parseInt(row[3]);
            Logger.println("Using from sheet Index " + fromSheetIndex + " and TO sheet index: " + toSheetIndex + " for performing cell lookups in Excel TO " + currentOutIdx + ".");
            fromSheetIndices.add(fromSheetIndex);
            toSheetIndices.add(toSheetIndex);
            toExcelIndices.add(currentOutIdx);
            currentMappingIdx++;
        } else {

            // there is a default value specified or date format
            if (!lastRowItemIsNumeric(row)) {
            	if(row.length > 5) {
                    int fromRowIdx = Integer.parseInt(row[0]);
                    int fromColIdx = Integer.parseInt(row[1]);
                    int toRowIdx = Integer.parseInt(row[2]);
                    int toColIdx = Integer.parseInt(row[3]);
                    boolean usesOffset = (row[4].equals("1"));
                    String dateFormat = row[5];
                    dateFormat = dateFormat.replace("\"", "");
                    cellMappings.add(new CellMapping(currentMappingIdx, fromRowIdx, fromColIdx, toRowIdx, toColIdx, usesOffset, dateFormat, -1));
            		return;
            	}
                int toRowIdx = Integer.parseInt(row[0]);
                int toColIdx = Integer.parseInt(row[1]);
                boolean usesOffset = (row[row.length - 2].equals("1"));
                String defaultValue = row[row.length - 1];
                defaultValue = defaultValue.replace("\"", "");
                cellMappings.add(new CellMapping(currentMappingIdx, toRowIdx, toColIdx, usesOffset, defaultValue));
                return;
            }

            int[] items = parseToIntegerArray(row);
            if (items.length > 5) {
                boolean usesOffset = (items[4] == 1);
                cellMappings.add(new CellMapping(currentMappingIdx, items[0], items[1], items[2], items[3], usesOffset, items[5]));
            } else if (items.length > 4) {
                boolean usesOffset = (items[4] == 1);
                cellMappings.add(new CellMapping(currentMappingIdx, items[0], items[1], items[2], items[3], usesOffset));
            } else {
                cellMappings.add(new CellMapping(currentMappingIdx, items[0], items[1], items[2], items[3]));
            }
        }
    }
}
