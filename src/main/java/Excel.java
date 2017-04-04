import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public abstract class Excel {

    protected int sheetIndex;

    /**
     * Save the cells the loaded / updated excel file.
     *
     * Please close the file your are about to write,
     * before actually invoking the writer function.
     */
    public abstract void save();

    /**
     * Find the next free cell-column index for a given row index.
     * Please note that the first index value is represented by the value 0.
     *
     * @param rowIdx cell row index.
     * @param startColIdx cell column index we want to start our search.
     * @return free column index.
     */
    public int findEmptyCellColumnAtFixedRow(int rowIdx, int startColIdx, boolean treatFormulaAsBlank) {
        int colIdx = startColIdx;
        Cell cell;
        CellContent content;
        Row row = getRow(rowIdx);
        if(row == null) {
        	return colIdx;
        }
        colIdx--;
        do {
            colIdx++;
            cell = row.getCell(colIdx);
            content = new CellContent(cell);
        } while(!content.isBlank(treatFormulaAsBlank));
        
        return colIdx;
    }
    
    public int findEmptyCellColumnAtFixedRow(int rowIdx, int startColIdx) {
    	return findEmptyCellColumnAtFixedRow(rowIdx, startColIdx, false);
    }

    /**
     * Find the next free destination column index for the given list of mappings.
     */
    public int findEmptyColumnForMappingBlock(CellMappingBlock cellMappingBlock) {
        int colIdx = 0;
        for(CellMapping mapping: cellMappingBlock.getMappings()) {
        	if(mapping.usesOffset()) {
        		colIdx = Math.max(findEmptyCellColumnAtFixedRow(mapping.getToRowIndex(), mapping.getToColumnIndex(), cellMappingBlock.treatFormulaAsBlank), colIdx);
        	}
        }
        return colIdx;
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
    public abstract CellContent getCellValue(int rowIdx, int columnIdx);
    
    /**
     * Get the currently loaded sheet of the loaded excel file.
     *
     * @return the current excel sheet
     */
    public abstract Sheet getSheet();

    /**
     * Get the row by an index from the current sheet.
     * First row index is 0.
     *
     * @param rowIdx cell row index
     * @return row at given row index.
     */
    public abstract Row getRow(int rowIdx);
    
    /**
     * Write a given content to a cell at a given location.s
     *
     * Calling this function will not overwrite the excel file.
     * It only updates its state.
     *
     * @param content new cell value
     * @param rowIdx cell row index
     * @param columnIdx cell column index
     */
    public void writeCell(CellContent content, int rowIdx, int columnIdx) {
        Row row = null;
        Cell cell = null;

        row = getRow(rowIdx);
        if (row == null) {
            Logger.printError("Row " + rowIdx + " does yet not exist. Creating new row...");
            row = getSheet().createRow(rowIdx);
        }

        cell = row.getCell(columnIdx);
        if (cell == null) {
            Logger.printError("Cell at column " + columnIdx + " does yet not exist. Creating new cell...");
            cell = row.createCell(columnIdx);
        }
        
    	if(content == null) {
    		cell.setCellType(CellType.BLANK);
    		return;
    	}
        
        switch(content.type) {
    		case BLANK:
    			break;
    		
        	case NUMERIC:
        		cell.setCellValue(content.numeric);
        		break;
        		
        	case BOOLEAN:
        		cell.setCellValue(content.bool);
        		break;
        		
        	case STRING:
        		cell.setCellValue(content.string);
        		break;
        		
        	default:
        		Logger.printError("Cell at column " + columnIdx + " could not be written. Invalid cell content given.");
        		break;
        }
		cell.setCellType(content.type);
    }

    public void setLookupSheetIndex(int sheetIndex) {
        this.sheetIndex = sheetIndex;
        setSheetAt(sheetIndex);
    }

    public boolean hasEmptySourceCells(ArrayList<CellMapping> cellMappings) {
    	for(CellMapping mapping: cellMappings) {
    		//Don't check static fields without a source cell
    		if(mapping.hasDefaultValue() && !mapping.hasDateFormatConversion()) {
    			continue;
    		}
    		CellContent content = getCellValue(mapping.getFromRowIndex(), mapping.getFromColumnIndex());
    		if(content.isBlank()) {
    			return true;
    		} else if(mapping.hasTranslation()) {
    			return Translator.lookup(mapping.getTranslationRow(), content).isBlank();
    		}
    	}
    	return false;
    }

    protected abstract void setSheetAt(int sheetIndex);
}
