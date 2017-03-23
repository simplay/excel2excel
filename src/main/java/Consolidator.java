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
    private Excel inExcel;
    private ArrayList<Excel> outExcels;

    // lookup indices extracted from the `mappings` file.
    // etermines which excel sheet should be used in the FROM and TO excel file
    // for applying the mapping between the excel cells.
    private ArrayList<CellMappingBlock> cellMappingBlocks;

    /**
     *
     * @param mappingFilePath a text file located at data/
     * @param inExcel
     * @param outExcels
     */
    public Consolidator(String mappingFilePath, Excel inExcel, ArrayList<Excel> outExcels) {
        this.inExcel = inExcel;
        this.outExcels = outExcels;
        this.cellMappingBlocks = new ArrayList<>();

        readFile(mappingFilePath);
        for (CellMappingBlock cellMappingBlock: cellMappingBlocks) {
            updateSheetIndices(cellMappingBlock);
	        if (Properties.runNormalMode()) {
	            copyFromCellsToToCells(cellMappingBlock);
	        } else {
	            Logger.println("Running in debug mode");
	            runDebugMode(cellMappingBlock);
	        }
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

    private void runDebugMode(CellMappingBlock cellMappingBlock) {
    	ArrayList<CellMapping> sublistCellMappings = cellMappingBlock.getMappings();
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
    private void updateSheetIndices(CellMappingBlock cellMappingBlock) {
        inExcel.setLookupSheetIndex(cellMappingBlock.fromSheetIdx);
        outExcels.get(cellMappingBlock.toExcelIdx).setSheetAt(cellMappingBlock.toSheetIdx);
    }

    /**
     * Takes ever FROM cell and writes it to the corresponding location in the TO excel file.
     */
    private void copyFromCellsToToCells(CellMappingBlock cellMappingBlock) {
    	ArrayList<CellMapping> sublistCellMappings = cellMappingBlock.getMappings();
        Excel outExcel = outExcels.get(cellMappingBlock.toExcelIdx);

        Logger.println("Using from sheet Index " + cellMappingBlock.fromSheetIdx + " and TO sheet index: " + cellMappingBlock.toSheetIdx + " for performing cell lookups in Excel TO " + cellMappingBlock.toExcelIdx + ".");
        if(cellMappingBlock.requireNonEmptySource && inExcel.hasEmptySourceCells(sublistCellMappings)) {
			Logger.println("FROM Excel contained empty cells for the given mapping. Did not copy anything for current mapping block.");
        	return;
        }
        int freeToColumn = 0;
        if(cellMappingBlock.insertAsColumn) {
        	freeToColumn = outExcel.findEmptyColumnForMappingBlock(sublistCellMappings);
        }
        for (CellMapping cellMapping : sublistCellMappings) {
            CellContent fromValue = processCellMapping(cellMapping);
            int toColumnIndex = cellMapping.getToColumnIndex();

            // TODO: Do this just once in the very beginning to determine the target column
            if (cellMapping.usesOffset()) {
            	if(cellMappingBlock.insertAsColumn) {
            		toColumnIndex = freeToColumn;
            	} else {
            		toColumnIndex = outExcel.findEmptyCellColumnAtFixedRow(cellMapping.getToRowIndex(), cellMapping.getToColumnIndex());
            	}
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
            int toExcelIdx = Integer.parseInt(row[1]);
            int fromSheetIdx = Integer.parseInt(row[2]);
            int toSheetIdx = Integer.parseInt(row[3]);
        	cellMappingBlocks.add(new CellMappingBlock(toExcelIdx, fromSheetIdx, toSheetIdx));
        } else {
        	CellMappingBlock currentBlock = cellMappingBlocks.get(cellMappingBlocks.size()-1);
        	
        	// config option for current cell mapping block
        	if(row[0].equals("c")) {
        		if(row[1].equals("insertAsColumn")) {
        			currentBlock.insertAsColumn = true;
        			return;
        		}
        		if(row[1].equals("requireNonEmptySource")) {
        			currentBlock.requireNonEmptySource = true;
        			return;
        		}
        		return;
        	}
        	
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
                    currentBlock.addMapping(new CellMapping(fromRowIdx, fromColIdx, toRowIdx, toColIdx, usesOffset, dateFormat, -1));
            		return;
            	}
                int toRowIdx = Integer.parseInt(row[0]);
                int toColIdx = Integer.parseInt(row[1]);
                boolean usesOffset = (row[row.length - 2].equals("1"));
                String defaultValue = row[row.length - 1];
                defaultValue = defaultValue.replace("\"", "");
                currentBlock.addMapping(new CellMapping(toRowIdx, toColIdx, usesOffset, defaultValue));
                return;
            }

            int[] items = parseToIntegerArray(row);
            if (items.length > 5) {
                boolean usesOffset = (items[4] == 1);
                currentBlock.addMapping(new CellMapping(items[0], items[1], items[2], items[3], usesOffset, items[5]));
            } else if (items.length > 4) {
                boolean usesOffset = (items[4] == 1);
                currentBlock.addMapping(new CellMapping(items[0], items[1], items[2], items[3], usesOffset));
            } else {
            	currentBlock.addMapping(new CellMapping(items[0], items[1], items[2], items[3]));
            }
        }
    }
}
