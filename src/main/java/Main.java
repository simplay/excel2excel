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
        System.out.println("Reading excel files...");
        ExcelFile excel = null;
        try {
            Path path = Paths.get(args[0]);
            // excel = new ExcelFile(path.toString(), 5);
            excel = new ExcelFile(path.toString(), 0);
            System.out.println(" => Excel files read.");

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        System.out.println("Fetching cell values...");

        excel.writeCell("13", 0, 2);
        System.out.println(excel.getCellValue(0, 2));
        excel.save();
    }
}
