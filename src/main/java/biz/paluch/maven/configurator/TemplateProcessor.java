/*
 * The MIT License (MIT)
 * Copyright (c) 2013 Mark Paluch
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished
 * to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package biz.paluch.maven.configurator;

import com.google.common.io.Closer;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.logging.Log;

import java.io.*;
import java.util.Properties;

/**
 * Template Processor. Processing of a Template File.
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public class TemplateProcessor implements PropertyReplacingListener {
    private Properties properties;

    private String tokenStart;
    private String tokenEnd;
    private Log log;

    public TemplateProcessor(Properties properties, String tokenStart, String tokenEnd, Log log) {
        this.properties = properties;
        this.tokenStart = tokenStart;
        this.tokenEnd = tokenEnd;
        this.log = log;
    }

    public void processFile(File input, File output) throws IOException {

        Closer closer = Closer.create();
        try {

            InputStream fis = closer.register(new FileInputStream(input));
            OutputStream fos = closer.register(new BufferedOutputStream(new FileOutputStream(output)));

            log.debug("Processing file " + input);
            log.debug("Output file " + output);

            processStream(fis, fos);

            log.debug("Processing file " + input + " done");

        } finally {
            closer.close();
        }
    }

    public void processStream(InputStream inputStream, OutputStream outputStream) throws IOException {


        PropertyReplacingInputStream propertyReplacingInputStream = new PropertyReplacingInputStream(new BufferedInputStream(inputStream), properties, tokenStart, tokenEnd);
        propertyReplacingInputStream.setListener(this);
        IOUtils.copy(propertyReplacingInputStream, outputStream);
    }

    @Override
    public void notifyPropertyFound(String key) {
    }

    @Override
    public void notifyPropertyReplaced(String key, String value) {
        log.debug("Setting value " + value + " for property " + key);
    }
}
