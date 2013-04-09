package de.paluch.maven.configurator;

import com.google.common.io.Files;
import de.paluch.maven.configurator.model.CompressedContainer;
import de.paluch.maven.configurator.model.Container;
import de.paluch.maven.configurator.model.Entry;
import de.paluch.maven.configurator.model.PackagingType;

import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 * @since 08.04.13 11:44
 */
public class ZipFileCompressor {

    private Container container;
    private ZipOutputStream zos;
    private File compressionBase;


    public ZipFileCompressor(Container container, ZipOutputStream zos, File compressionBase) {
        this.container = container;
        this.zos = zos;
        this.compressionBase = compressionBase;
    }

    public void compress(String prefix) throws IOException {


        for (Entry entry : container.getEntries()) {


            File source = new File(compressionBase, entry.getName());
            ZipEntry ze = getZipEntry(prefix, entry.getName(), source);

            if (entry instanceof CompressedContainer && source.isDirectory()) {
                CompressedContainer cc = (CompressedContainer) entry;
                zos.putNextEntry(ze);

                ZipOutputStream subZipStream = new ZipOutputStream(new FilterOutputStream(zos) {
                    @Override
                    public void close() throws IOException {

                    }
                });

                ZipFileCompressor compressor = new ZipFileCompressor(cc, subZipStream, source);
                compressor.compress("");
                zos.closeEntry();
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

    private ZipEntry getZipEntry(String prefix, String name, File source) {
        ZipEntry ze = new ZipEntry(prefix + name);
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
