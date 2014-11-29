package org.kedar.kai;

/**
 * A Unit Test for {@link org.kedar.kai.ComponentBuilder}.
 * @author kedar
 * @since  11/28/14.
 */

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class ComponentBuilderTest {

    private static BufferedReader reader;
    private static final String graph1 =
            "4\n" +
            "1 2\n" +
            "2 3\n" +
            "4\n";
    @BeforeClass
    public static void initialize() {
        reader = new BufferedReader(new StringReader(graph1));
    }
    @Test
    public void assertGraph1HasThreeVertices() throws IOException, NoSuchUserException {
        ComponentBuilder b = new ComponentBuilder();
        b.process(reader);
        assertEquals(4, b.getNumberOfVertices());
        assertEquals(1, b.getUserVersion(1));
        assertEquals(2, b.getNumberOfComponents());
    }
}
