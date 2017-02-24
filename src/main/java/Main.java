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
        if (args.length > 0) {
            Properties.initialize(args);
        }

        Properties.initializeLogger();
        Logger.println("Starting Excel2Excel");
        new Gui(args);
    }
}
