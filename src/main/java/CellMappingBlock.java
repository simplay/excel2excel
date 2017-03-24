import java.util.ArrayList;

public class CellMappingBlock {
	public int toExcelIdx;
    public int fromSheetIdx;
    public int toSheetIdx;
    public boolean insertAsColumn;
    public boolean requireNonEmptySource;
    public String name;
    private ArrayList<CellMapping> cellMappings;
    
    public CellMappingBlock(int toExcelIdx, int fromSheetIdx, int toSheetIdx) {
    	this.toExcelIdx = toExcelIdx;
    	this.fromSheetIdx = fromSheetIdx;
    	this.toSheetIdx = toSheetIdx;
    	this.name = "FROM Sheet " + fromSheetIdx + " TO Sheet " + toSheetIdx + " on TO Excel " + toExcelIdx;
    	cellMappings = new ArrayList<>();
    	insertAsColumn = false;
    	requireNonEmptySource = false;
    }
    
    public void addMapping(CellMapping cellMapping) {
    	cellMappings.add(cellMapping);
    }
    
    public ArrayList<CellMapping> getMappings() {
    	return cellMappings;
    }
}
