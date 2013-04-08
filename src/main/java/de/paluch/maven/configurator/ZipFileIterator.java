package de.paluch.maven.configurator;

import de.paluch.maven.configurator.model.Container;
import de.paluch.maven.configurator.model.PackagingType;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.io.RawInputStreamFacade;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 * @since 08.04.13 11:44
 */
public class ZipFileIterator {

    private Container container;
    private ZipInputStream zis;
    private File extractionBase;


    public ZipFileIterator(Container container, ZipInputStream zis, File extractionBase) {
        this.container = container;
        this.zis = zis;
        this.extractionBase = extractionBase;
    }

    public void iterate() throws IOException {


        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            {

                File extractTarget = new File(extractionBase, entry.getName());

                if (entry.isDirectory()) {
                    extractTarget.mkdirs();
                    continue;
                }


                LimitedInputStream lis = new LimitedInputStream(entry.getSize(), zis);

                if (isPackagedFile(entry)) {
                    extractTarget.mkdirs();
                    ZipInputStream childZipStream = new ZipInputStream(lis);
                    Container childContainer = new Container(entry.getName());
                    container.getEntries().add(childContainer);
                    new ZipFileIterator(childContainer, childZipStream, extractTarget).iterate();
                    continue;
                }

                FileUtils.copyStreamToFile(new RawInputStreamFacade(lis), extractTarget);
                zis.closeEntry();
            }
        }

        zis.close();
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
