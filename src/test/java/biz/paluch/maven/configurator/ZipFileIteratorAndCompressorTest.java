package biz.paluch.maven.configurator;

import biz.paluch.maven.configurator.model.Container;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 * @since 08.04.13 13:00
 */
public class ZipFileIteratorAndCompressorTest {

    @Test
    public void testExtract() throws Exception {
        String baseDir = PlexusTestCase.getBasedir();


        Container container = new Container("simple.war");

        File file = new File(baseDir + "/target/test/" + ZipFileIteratorAndCompressorTest.class.getSimpleName());
        FileUtils.deleteDirectory(file);
        file.mkdirs();

        ZipFileIteratorAndExtractor zi = new ZipFileIteratorAndExtractor(container, new ZipInputStream(getClass().getResourceAsStream("/simple.war")), file);
        zi.extract();


        File target = new File(baseDir + "/target/test/" + ZipFileIteratorAndCompressorTest.class.getSimpleName() + ".zip");

        ZipFileCompressor compressor = new ZipFileCompressor(container, new ZipOutputStream(new FileOutputStream(target)), file);
        compressor.compress();
    }
}
