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

import org.apache.maven.project.MavenProject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public enum PackageType {

    EJB("ejb", "jar"), WAR("war"), EAR("ear"), RAR("rar"), JAR("jar");


    private final String packaging;
    private final String fileExtension;

    private PackageType(final String packaging) {
        this.packaging = packaging;
        this.fileExtension = packaging;
    }

    private PackageType(final String packaging, final String fileExtension) {
        this.packaging = packaging;
        this.fileExtension = fileExtension;
    }


    /**
     * Returns the raw packaging type.
     *
     * @return the packaging type
     */
    public String getPackaging() {
        return packaging;
    }

    /**
     * Returns the file extension that should be used.
     *
     * @return the file extension
     */
    public String getFileExtension() {
        return fileExtension;
    }
}
