
package saptomsp;

import java.io.File;
import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author rapnik
 */
public class SapToMspTest {
    
    public SapToMspTest() {
    }

    @Test
    public void testSomeMethod() {
        SapTask t1 = new SapTask("1", "1", "x", "x", "stuff", null, null, null, null, 0.0, "DAY");
        SapTask t2 = new SapTask("1", "2", "x", "x", "stuff", null, null, null, null, 0.0, "DAY");
        Relationship rel = new Relationship(t1, t2, "SF", "DAY", 0.0, "DAY");
        t2.addPredecessor(rel);
        final String testxml = "test.xml";
        
        SapToMsp.writeMsXml(Arrays.asList(new SapTask[]{t1,t2}), testxml);
        System.out.println((new File(testxml)).getAbsolutePath());
        
    }
    
}
