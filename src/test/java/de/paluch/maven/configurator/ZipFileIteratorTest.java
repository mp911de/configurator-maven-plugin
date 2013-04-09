package de.paluch.maven.configurator;

import de.paluch.maven.configurator.model.Container;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Test;

import java.io.File;
import java.util.zip.ZipInputStream;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 * @since 08.04.13 13:00
 */
public class ZipFileIteratorTest {

    @Test
    public void testExtract() throws Exception {
        String baseDir = PlexusTestCase.getBasedir();


        Container container = new Container("simple.war");

        File file = new File(baseDir + "/target/test/" + ZipFileIterator.class.getSimpleName());
        FileUtils.deleteDirectory(file);
        file.mkdirs();

        ZipFileIterator zi = new ZipFileIterator(container, new ZipInputStream(getClass().getResourceAsStream("/simple.war")), file);
        zi.extract();

        assertEquals(5, container.getEntries().size());
        assertEquals("WEB-INF/config.properties", container.getEntries().get(0).getName());
        assertEquals("__MACOSX/WEB-INF/._config.properties", container.getEntries().get(1).getName());

    }

}
