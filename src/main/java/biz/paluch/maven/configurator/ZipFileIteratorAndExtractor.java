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

import biz.paluch.maven.configurator.model.CompressedContainer;
import biz.paluch.maven.configurator.model.Container;
import biz.paluch.maven.configurator.model.Entry;
import biz.paluch.maven.configurator.model.PackagingType;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.io.RawInputStreamFacade;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Extracts an Zip-File (recursively)
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 * @since 08.04.13 11:44
 */
public class ZipFileIteratorAndExtractor {

    private Container container;
    private ZipInputStream zis;
    private File extractionBase;


    public ZipFileIteratorAndExtractor(Container container, ZipInputStream zis, File extractionBase) {
        this.container = container;
        this.zis = zis;
        this.extractionBase = extractionBase;
    }

    /**
     * Perform extraction and build container model.
     * @throws IOException
     */
    public void extract() throws IOException {

        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            {

                File extractTarget = new File(extractionBase, entry.getName());

                if (entry.isDirectory()) {
                    extractTarget.mkdirs();
                    continue;
                }

                LimitedNonClosingInputStream lis = new LimitedNonClosingInputStream(entry.getSize(), zis);

                if (isPackagedFile(entry)) {
                    extract(entry, extractTarget, lis);
                    continue;
                }

                FileUtils.copyStreamToFile(new RawInputStreamFacade(lis), extractTarget);
                extractTarget.setLastModified(entry.getTime());
                zis.closeEntry();

                container.getEntries().add(new Entry(entry.getName()));
            }
        }

        zis.close();
    }

    /**
     * Extract a child-entry.
     * @param entry
     * @param extractTarget
     * @param lis
     * @throws IOException
     */
    private void extract(ZipEntry entry, File extractTarget, LimitedNonClosingInputStream lis) throws IOException {
        extractTarget.mkdirs();

        ZipInputStream childZipStream = new ZipInputStream(lis);
        CompressedContainer childContainer = new CompressedContainer(entry.getName());
        childContainer.setPackagingType(getPackagingType(entry));
        container.getEntries().add(childContainer);

        new ZipFileIteratorAndExtractor(childContainer, childZipStream, extractTarget).extract();
    }

    private boolean isPackagedFile(ZipEntry entry) {
        if (getPackagingType(entry) != null) {
            return true;
        }
        return false;
    }

    private PackagingType getPackagingType(ZipEntry entry) {
        String name = entry.getName().toLowerCase();

        for (PackagingType packagingType : PackagingType.values()) {
            if (name.endsWith("." + packagingType.getType())) {
                return packagingType;
            }
        }
        return null;
    }
}
