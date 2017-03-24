import java.util.ArrayList;

public class CellMappingBlock {
	public int toExcelIdx;
    public int fromSheetIdx;
    public int toSheetIdx;
    public boolean insertAsColumn;
    public boolean requireNonEmptySource;
    private ArrayList<CellMapping> cellMappings;
    
    public CellMappingBlock(int toExcelIdx, int fromSheetIdx, int toSheetIdx) {
    	this.toExcelIdx = toExcelIdx;
    	this.fromSheetIdx = fromSheetIdx;
    	this.toSheetIdx = toSheetIdx;
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
