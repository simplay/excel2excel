import com.sun.jmx.snmp.Timestamp;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

/**
 * A logger is responsible for printing the runtime state of a given execution.
 * It prints a given status report file (to have some reconstructive information).
 */
public class Logger {

    // The logger singleton
    private static Logger instance = null;

    // Unique identifier used for the status filename.
    private long timestamp;

    // Logger messages that should be written to a status file.
    private final LinkedList<String> buffer = new LinkedList<String>();

    // Should the logger print the messages to the console.
    private boolean isMuted = false;

    /**
     * Returns un-muted singleton logger
     *
     * @return the logger
     */
    public static Logger getInstance() {
        return getInstance(false);
    }

    /**
     * Returns a singleton logger
     *
     * @param isMuted is the logger allowed to
     *  write status messages to the console.
     * @return the logger
     */
    public static Logger getInstance(boolean isMuted) {
        if (instance == null) {
            instance = new Logger(isMuted);
        }
        return instance;
    }

    /**
     * Releases the logger singleton and its references/state.
     */
    public static void release() {
        instance = null;
    }

    /**
     * Write log file via singleton access.
     */
    public static void writeLog() {
        getInstance().writeLogFile();
    }

    /**
     * Saves a given string in the internal logger buffer
     * and if not muted it prints it out into the console.
     *
     * @param msg given message string.
     */
    public static void print(String msg) {
        if (getInstance().mayPrint()) System.out.print(msg);
        getInstance().writeBuffer(msg);
    }

    /**
     * Prints an error string in the console if allowed.
     *
     * @param msg given error message.
     */
    public static void printError(String msg) {
        if (getInstance().mayPrint()) System.err.println(msg);
        getInstance().writeBuffer(msg);
        
        if(Properties.showErrorDialogue()) {
        	int confirmDialogueState = JOptionPane.showConfirmDialog(null, "An error has occured: \"" + msg + "\"\n\nDo you wish to continue anyways?", "Error", JOptionPane.YES_NO_OPTION);
        	if(confirmDialogueState == JOptionPane.NO_OPTION) {
        		Properties.requestAbort();
        	}
        }
    }
    
    public static void printException(Exception e) {
    	printError(e.getClass().getSimpleName() + ": " + e.getMessage());
        if (getInstance().mayPrint()) e.printStackTrace();
        getInstance().writeBuffer(e.getStackTrace().toString());
    }

    /**
     * Prints a formatted path error.
     *
     * @param format String format
     * @param msg message that should be displayed in given format.
     */
    public static void printFormattedError(String format, Path msg) {
        if (getInstance().mayPrint()) System.err.format(format, msg);
        getInstance().writeBuffer(String.format(format, msg));
    }

    /**
     * Saves a given string appended by a new line character
     * in the internal logger buffer
     * and if not muted it prints it out into the console.
     *
     * @param msg given message string.
     */
    public static void println(String msg) {
        print(msg + "\n");
    }

    public static void println() {
        print("\n");
    }
    /**
     * Creates a new logger instance
     *
     * @param isMuted should status be printed into console
     */
    public Logger(boolean isMuted) {
        this.isMuted = isMuted;
        timestamp = new Timestamp(System.currentTimeMillis()).getDate().getTime();
    }

    /**
     * Is the logger allowed to print received messages into the console?
     *
     * @return true if it may print messages, otherwise false.
     */
    public boolean mayPrint() {
        return !isMuted;
    }

    /**
     * Appends a string to the internal buffer.
     * Every message contained in the buffer will written into the output file.
     *
     * @param msg a message that should be added to the buffer.
     */
    public void writeBuffer(String msg) {
        buffer.add(msg);
    }

    /**
     * Write the log file prefixed by `core_` followed by the timestamp.
     * to `../output/logs/`
     */
    public void writeLogFile() {
        String fileName = Paths.get("logs", "log_" + timestamp + ".txt").toString();
        try {
            writeFile(buffer, fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes fast a huge collection of Strings into a files
     *
     * @param strLines collection of file lines
     * @param fileName file path name with extension
     * @throws IOException
     */
    private void writeFile(List<String> strLines, String fileName) throws IOException {
        // number of bytes apart from the starting position.
        int offset = 0;

        FileChannel rwChannel = new RandomAccessFile(fileName, "rw").getChannel();
        for (String item : strLines) {
            byte[] buffer = item.getBytes();

            // Number of bytes the current line requires
            int buff_len = buffer.length;
            ByteBuffer wrBuf = rwChannel.map(FileChannel.MapMode.READ_WRITE, offset, buff_len);
            offset += buff_len;
            wrBuf.put(buffer);
        }
        rwChannel.close();
    }
}
