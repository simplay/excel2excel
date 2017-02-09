import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    /**
     * @example
     *  args = ["data/b.xlsx"]
     * @param args contains the paths to the excel files.
     *  the first path directs to the parent form, the successor files to the children excel sheets.
     */
    public static void main(String[] args) {
        // TODO export this to a file
        String mappingFilePath = "data/mappings.txt";

        System.out.println("Reading excel files...");
        ExcelFile fromExcel = null;
        ExcelFile toExcel = null;
        try {
            Path pathIn = Paths.get(args[0]);
            Path pathOut = Paths.get(args[1]);

            // excel = new ExcelFile(path.toString(), 5);

            fromExcel = new ExcelFile(pathIn.toString(), 0);
            toExcel = new ExcelFile(pathOut.toString(), 0);
            System.out.println(" => Excel files read.");

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        new Consolidator(mappingFilePath, fromExcel, toExcel);
        toExcel.save();
    }
}
