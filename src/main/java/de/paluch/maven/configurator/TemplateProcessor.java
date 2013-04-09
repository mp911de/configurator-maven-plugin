package de.paluch.maven.configurator;

import com.google.common.io.Closer;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Properties;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public class TemplateProcessor {
    private Properties properties;

    private String tokenStart;
    private String tokenEnd;

    public TemplateProcessor(Properties properties, String tokenStart, String tokenEnd) {
        this.properties = properties;
        this.tokenStart = tokenStart;
        this.tokenEnd = tokenEnd;
    }

    public void processFile(File input, File output) throws IOException {

        Closer closer = Closer.create();
        try {

            InputStream fis = closer.register(new FileInputStream(input));
            OutputStream fos = closer.register(new BufferedOutputStream(new FileOutputStream(output)));
            processStream(fis, fos);

        } finally {
            closer.close();
        }
    }

    public void processStream(InputStream inputStream, OutputStream outputStream) throws IOException {

        IOUtils.copy(new PropertyReplacingInputStream(new BufferedInputStream(inputStream), properties, tokenStart, tokenEnd), outputStream);
    }
}
