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
		
		type = cell.getCellTypeEnum();
		
		wasFormula = false;
		if(type == CellType.FORMULA) {
			type = cell.getCachedFormulaResultTypeEnum();
			wasFormula = true;
		}
		
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
	
	public boolean isBlank(boolean treatFormulaAsBlank) {
		if(!treatFormulaAsBlank && wasFormula) {
			return false;
		}
		
		switch(type) {
			case BLANK:
				return true;
				
			case BOOLEAN:
			case NUMERIC:
				return false;
				
			case STRING:
				return string.equals("");
				
			default:
				return false;
		}
	}
	
	public boolean isBlank() {
		return isBlank(false);
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
	
	@Override
	public boolean equals(Object other) {
		if(other instanceof CellContent) {
			return this.equals((CellContent) other);
		}
		return false;
	}
	
	public boolean equals(CellContent other) {
		if(type == CellType.NUMERIC && other.type == CellType.STRING) {
			try {
				double other_numeric = Integer.parseInt(other.string);
				return numeric == other_numeric;
			} catch(NumberFormatException e) {
				return false;
			}
		}
		
		if(type == CellType.STRING && other.type == CellType.NUMERIC) {
			return other.equals(this);
		}
		
		if(type == other.type) {
			switch(type) {
				case BLANK:
					return true;
			
				case BOOLEAN:
					return bool == other.bool;
				
				case NUMERIC:
					return numeric == other.numeric;
					
				case STRING:
					return string == other.string;
					
				default:
					return false;
			}
		}
		return false;
	}
}
