import java.io.*;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

public class TestHelper {

    // Load clean state test files
    public static void prepare() {
        try {
            copyFile("src/test/data/empty_file.xlsx", "src/test/writer_test.xlsx");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getWritableFilePath() {
       return  "src/test/writer_test.xlsx";
    }

    public static String getReadOnlyFilePath() {
        return  "src/test/data/dummy_input.xlsx";
    }

    public static String getMappingFilePath() {
        return "src/test/data/mappings.txt";
    }

    // Delete dirty output files, generated when running certain tests.
    public static void cleanup() {
        deleteFile("src/test/writer_test.xlsx");
    }

    private static void copyFile(String sourcePath, String destPath) throws IOException {
        String basePath = System.getProperty("user.dir");

        File source = new File(Paths.get(basePath + "/" + sourcePath).toString());
        File dest = new File(Paths.get(basePath + "/" + destPath).toString());
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(source);
            outputStream = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } finally {
            inputStream.close();
            outputStream.close();
        }
    }

    private static void deleteFile(String path) {
        try {
            String basePath = System.getProperty("user.dir");
            Files.delete(Paths.get(basePath + "/" + path));
        } catch (NoSuchFileException e) {
            System.err.format("%s: no such" + " file or directory%n", path);
        } catch (DirectoryNotEmptyException e) {
            System.err.format("%s not empty%n", path);
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
