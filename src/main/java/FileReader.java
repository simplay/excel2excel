import java.io.*;

/**
 * FileReader implements the basic functionality in order to read a file.
 * Files are processes line by line. The line processing behaviour has to
 * be implemented by the extending file reader class.
 */
public abstract class FileReader {

    /**
     * Reads the file that maps to a provided file path name.
     *
     * The file is read line by line.
     *
     * @param fileNamePath the file path name we want to read
     */
    protected void readFile(String fileNamePath) {
        FileInputStream fstream = null;
        try {
            fstream = new FileInputStream(fileNamePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String strLine;
        try {
            while ((strLine = br.readLine()) != null) {
                processLine(strLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Defines the logic of how a file line should be processes.
     *
     * @param line a read file line.
     */
    protected abstract void processLine(String line);

    /**
     * Maps a given array of strings to an array of integers.
     *
     * @param items string items that should be mapped to doubles.
     * @return double array containing the integer version of the initially given string items.
     */
    protected int[] parseToIntegerArray(String[] items) {
        int[] intItems = new int[items.length];
        int idx = 0;
        for (String item : items) {
            intItems[idx] = Integer.parseInt(item);
            idx++;
        }
        return intItems;
    }

}
