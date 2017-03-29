import java.util.ArrayList;

public class CellMappingBlock {
    public String name;
	public int toExcelIdx;
    public int fromSheetIdx;
    public int toSheetIdx;
    private ArrayList<CellMapping> cellMappings;
    public boolean insertAsColumn;
    public boolean requireNonEmptySource;
    public boolean treatFormulaAsBlank;
    
    public CellMappingBlock(int toExcelIdx, int fromSheetIdx, int toSheetIdx) {
    	this.name = "FROM Sheet " + fromSheetIdx + " TO Sheet " + toSheetIdx + " on TO Excel " + toExcelIdx;
    	this.toExcelIdx = toExcelIdx;
    	this.fromSheetIdx = fromSheetIdx;
    	this.toSheetIdx = toSheetIdx;
    	this.cellMappings = new ArrayList<>();
    	this.insertAsColumn = false;
    	this.requireNonEmptySource = false;
    	this.treatFormulaAsBlank = false;
    }
    
    public void addMapping(CellMapping cellMapping) {
    	cellMappings.add(cellMapping);
    }
    
    public ArrayList<CellMapping> getMappings() {
    	return cellMappings;
    }
}
