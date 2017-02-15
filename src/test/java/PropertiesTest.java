import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PropertiesTest {

    @After
    public void cleanup() {
        Properties.clear();
    }

    @Test
    public void testHasExpectedDefaults() {
        assertEquals("data/scala_values.txt", Properties.getScalaValuesFilePath());
        assertEquals("data/mappings.txt", Properties.getMappingFilePath());
    }

    @Test
    public void tesInitialize() {
        String[] args = {
                "src_path/src.xlsx",
                "dst_path/dst.xlsx",
                "mapping_path/map.txt",
                "scala_path/scala.txt"
        };
        Properties.initialize(args);
        assertEquals(args[0], Properties.getFromExcelFilePath());
        assertEquals(args[1], Properties.getToExcelFilePath());
        assertEquals(args[2], Properties.getMappingFilePath());
        assertEquals(args[3], Properties.getScalaValuesFilePath());
    }
}
