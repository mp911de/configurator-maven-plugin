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

import com.google.common.io.Files;
import biz.paluch.maven.configurator.model.CompressedContainer;
import biz.paluch.maven.configurator.model.Container;
import biz.paluch.maven.configurator.model.Entry;
import biz.paluch.maven.configurator.model.PackagingType;

import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Create a Zip-File using a given Container model. Acts recursively in order to create zip-in-zip-in-zip.
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 * @since 08.04.13 11:44
 */
public class ZipFileCompressor {

    private Container container;
    private ZipOutputStream zos;
    private File compressionBase;


    /**
     * @param container
     * @param zos
     * @param compressionBase base-path for compression.
     */
    public ZipFileCompressor(Container container, ZipOutputStream zos, File compressionBase) {
        this.container = container;
        this.zos = zos;
        this.compressionBase = compressionBase;
    }

    /**
     * Compress the files.
     * @throws IOException
     */
    public void compress() throws IOException {


        for (Entry entry : container.getEntries()) {


            File source = new File(compressionBase, entry.getName());
            ZipEntry ze = getZipEntry(entry.getName(), source);

            if (entry instanceof CompressedContainer && source.isDirectory()) {
                compress((CompressedContainer) entry, source, ze);
                continue;
            }

            if (source.isDirectory()) {
                continue;
            }

            zos.putNextEntry(ze);
            Files.copy(source, zos);
            zos.closeEntry();
        }

        zos.close();
    }

    /**
     * Create a nested Zip-Entry (zip-in-zip).
     * @param entry
     * @param source
     * @param ze
     * @throws IOException
     */
    private void compress(CompressedContainer entry, File source, ZipEntry ze) throws IOException {
        CompressedContainer cc = (CompressedContainer) entry;
        zos.putNextEntry(ze);

        ZipOutputStream subZipStream = new ZipOutputStream(new FilterOutputStream(zos) {
            @Override
            public void close() throws IOException {

            }
        });

        ZipFileCompressor compressor = new ZipFileCompressor(cc, subZipStream, source);
        compressor.compress();
        zos.closeEntry();
    }

    private ZipEntry getZipEntry(String name, File source) {
        ZipEntry ze = new ZipEntry( name);
        ze.setTime(source.lastModified());
        ze.setSize(source.length());
        return ze;
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
