/**
 * This program copies the content of some cells in a given excel file into some other cells
 * in another excel file. To determine which cell is mapped to which cell in the other file,
 * a mapping file has to be provided. This file is located at `./data/`.
 *
 * Currently, only .xlsx files are supported.
 *
 * This program requires two user arguments. Both arguments model paths to excel files.
 * The 1st path points to the excel file we want to read from,
 * The 2nd path points to the excel file we want to write to.
 */
public class Main {
    /**
     * @example
     *  args = ["data/b.xlsx"]
     * @param args contains the paths to the excel files.
     *  the first path directs to the parent form, the successor files to the children excel sheets.
     */
    public static void main(String[] args) {
        Properties.initialize(args);
        Logger.println("Reading excel files...");
        try {
            // excel = new ExcelFile(path.toString(), 5);
            ExcelFile fromExcel = new ExcelFile(Properties.getFromExcelFilePath());
            ExcelFile toExcel = new ExcelFile(Properties.getToExcelFilePath());
            Logger.println(" => Excel files read.");
            new Consolidator(Properties.getMappingFilePath(), fromExcel, toExcel);
            toExcel.save();
            Logger.writeLog();

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
