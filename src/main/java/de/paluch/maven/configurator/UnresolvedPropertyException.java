package de.paluch.maven.configurator;

import java.io.File;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public class UnresolvedPropertyException extends Exception {

    private File file;

    public UnresolvedPropertyException(String message, File file) {
        super(message);
        this.file = file;
    }

    public File getFile()
    {
        return file;
    }
}
