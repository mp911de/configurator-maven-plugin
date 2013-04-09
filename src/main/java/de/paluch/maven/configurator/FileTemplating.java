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

package de.paluch.maven.configurator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public class FileTemplating {

    public static Pattern FILE_TEMPLATE_PATTERN = Pattern.compile(".*(\\.template)\\..*");

    /**
     * Process files which match the template pattern. Creates a new file using the input file with property replacement.
     * Target filename is without the template name part.
     *
     * @param root
     * @param processor
     * @throws IOException
     */
    public static void processFiles(File root, TemplateProcessor processor) throws IOException {

        Iterator<File> iterator = FileUtils.iterateFiles(root, new RegexFileFilter(FILE_TEMPLATE_PATTERN), TrueFileFilter.TRUE);

        while (iterator.hasNext()) {
            File next = iterator.next();

            try {
                processor.processFile(next, getTargetFile(next));
            } catch (IOException e) {
                throw new IOException("Cannot process file " + next.toString() + ": " + e.getMessage(), e);
            }
        }
    }

    private static File getTargetFile(File next) {
        File parent = next.getParentFile();

        String filename = next.getName();
        filename = filename.replaceAll("\\.template", "");
        return new File(parent, filename);
    }

}
