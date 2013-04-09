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
