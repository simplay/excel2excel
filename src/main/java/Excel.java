public abstract class Excel {

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
    public abstract int findEmptyCellColumnAtFixedRow(int rowIdx, int startColIdx);

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
    public abstract String getCellValue(int rowIdx, int columnIdx);

    /**
     * Write a given string to a cell at a given location.
     *
     * Calling this function will not overwrite the excel file.
     * It only updates its state.
     *
     * @param content new cell value
     * @param rowIdx cell row index
     * @param columnIdx cell column index
     */
    public abstract void writeCell(String content, int rowIdx, int columnIdx);
}
