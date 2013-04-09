package de.paluch.maven.configurator;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public class TemplateProcessorTest {

    @Test
    public void testProcessString() throws Exception {

        System.setProperty("user.blubb", "zack");
        String myString = "a=b\n" + "x=@user.blubb@";

        TemplateProcessor sut = new TemplateProcessor(System.getProperties(), "@", "@");

        ByteArrayInputStream bais = new ByteArrayInputStream(myString.getBytes());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        sut.processStream(bais, baos);

        assertEquals("a=b\n" + "x=zack", baos.toString());
    }

    @Test
    public void testProcessStringExtendedPlaceholder() throws Exception {

        System.setProperty("user.blubb", "zack");
        String myString = "a=b\n" + "x=${user.blubb}";

        ByteArrayOutputStream baos = invokeTest(myString);

        assertEquals("a=b\n" + "x=zack", baos.toString());
    }

    @Test
    public void testProcessStringExtendedPlaceholderTooLongIdentifier() throws Exception {

        System.setProperty("user.blubb", "zack");
        String myString = "a=b\n" + "x=${user.blubbsfdgsfdglkjfgjklfgdjklgfdjkldfjkldjldfjkldldkfjglkdfjglkjjj" +
                "user.blubbsfdgsfdglkjfgjklfgdjklgfdjkldfjkldjldfjkldldkfjglkdfjglkjjj" +
                "user.blubbsfdgsfdglkjfgjklfgdjklgfdjkldfjkldjldfjkldldkfjglkdfjglkjjj" +
                "user.blubbsfdgsfdglkjfgjklfgdjklgfdjkldfjkldjldfjkldldkfjglkdfjglkjjj" +
                "user.blubbsfdgsfdglkjfgjklfgdjklgfdjkldfjkldjldfjkldldkfjglkdfjglkjjj" +
                "user.blubbsfdgsfdglkjfgjklfgdjklgfdjkldfjkldjldfjkldldkfjglkdfjglkjjj" +
                "user.blubbsfdgsfdglkjfgjklfgdjklgfdjkldfjkldjldfjkldldkfjglkdfjglkjjj" +
                "user.blubbsfdgsfdglkjfgjklfgdjklgfdjkldfjkldjldfjkldldkfjglkdfjglkjjj" +
                "user.blubbsfdgsfdglkjfgjklfgdjklgfdjkldfjkldjldfjkldldkfjglkdfjglkjjj" +
                "user.blubbsfdgsfdglkjfgjklfgdjklgfdjkldfjkldjldfjkldldkfjglkdfjglkjjj" +
                "user.blubbsfdgsfdglkjfgjklfgdjklgfdjkldfjkldjldfjkldldkfjglkdfjglkjjj}";

        ByteArrayOutputStream baos = invokeTest(myString);

        assertEquals(myString, baos.toString());
    }

    private ByteArrayOutputStream invokeTest(String myString) throws IOException {
        TemplateProcessor sut = new TemplateProcessor(System.getProperties(), "${", "}");

        ByteArrayInputStream bais = new ByteArrayInputStream(myString.getBytes());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        sut.processStream(bais, baos);
        return baos;
    }

    @Test
    public void testProcessStringExtendedPlaceholderNoSecondChar() throws Exception {

        System.setProperty("user.blubb", "zack");
        String myString = "a=b\n" + "x=$user.blubb}";

        ByteArrayOutputStream baos = invokeTest(myString);

        assertEquals("a=b\n" + "x=$user.blubb}", baos.toString());
    }

    @Test
    public void testProcessStringWithTextBehind() throws Exception {

        System.setProperty("user.blubb", "zack");
        String myString = "a=b\n" + "x=${user.blubb}sdfadfasdfasdfadf";

        ByteArrayOutputStream baos = invokeTest(myString);

        assertEquals("a=b\n" + "x=zacksdfadfasdfasdfadf", baos.toString());
    }

    @Test
    public void testProcessStringTooLongIdentifier() throws Exception {

        System.setProperty("user.blubb", "zack");
        String myString = "a=b\n" + "x=${user.blubb}sdfadfasdfasdfadf";

        ByteArrayOutputStream baos = invokeTest(myString);

        assertEquals("a=b\n" + "x=zacksdfadfasdfasdfadf", baos.toString());
    }

    @Test(expected = IOException.class)
    public void testProcessStringUnresolved() throws IOException {

        String myString = "a=b\n" + "x=${user.blubb.aaaa}";

        ByteArrayOutputStream baos = invokeTest(myString);

    }
}
