import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

public class CellContent {
	public CellType type;
	public boolean bool;
	public double numeric;
	public String string;
	
	public boolean wasFormula;
	
	public CellContent(Cell cell) {
		if(cell == null) {
			type = CellType.BLANK;
			return;
		}
		
		CellType temp_type = cell.getCellTypeEnum();
		
		wasFormula = false;
		if(temp_type == CellType.FORMULA) {
			temp_type = cell.getCachedFormulaResultTypeEnum();
			wasFormula = true;
		}
		
		type = temp_type;
		switch(type) {
			case BLANK:
				break;
				
			case BOOLEAN:
				bool = cell.getBooleanCellValue();
				break;
				
			case NUMERIC:
				numeric = cell.getNumericCellValue();
				break;
				
			case STRING:
				string = cell.getStringCellValue();
				break;
				
			default:
				break;
		}
	}
	
	public CellContent(String content) {
		try { 
			int numeric_value = Integer.parseInt(content);
			type = CellType.NUMERIC;
			numeric = numeric_value;
		} catch(NumberFormatException e) {
			type = CellType.STRING;
			string = content;
		}
	}
	
	public boolean isBlank() {
		if(wasFormula)
			return false;
		
		switch(type) {
			case BLANK:
				return true;
				
			case BOOLEAN:
			case NUMERIC:
				return false;
				
			case STRING:
				return string == "";
				
			default:
				return false;
		}
	}
	
	public String toString() {
		switch(type) {
			case BLANK:
				return "";
		
			case BOOLEAN:
				return String.valueOf(bool);
				
			case NUMERIC:
				return String.valueOf(numeric);
				
			case STRING:
				return string;
				
			default:
				return "[unknown]";
		}
	}
}
