package de.paluch.maven.configurator;

import com.google.common.base.Preconditions;
import com.google.common.io.Closer;
import de.paluch.maven.configurator.model.Container;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Abstract base to perform configuration.
 */
public abstract class AbstractConfigureMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;

    /**
     * Property-Source, optional List of File-Names or URL's or property files.
     */
    @Parameter
    protected List<String> propertySources;

    /**
     * Token start for identifying the start of the variable declaration. Can be something like @ or ${.
     */
    @Parameter
    protected String tokenStart = "@";

    /**
     * Token end for identifying the end of the variable declaration. Can be something like @ or }.
     */
    @Parameter
    protected String tokenEnd = "@";

    /**
     * The target directory the application to be deployed is located.
     */
    @Parameter(defaultValue = "${project.build.directory}/")
    protected File targetDir;


    /**
     * Perform Configuration.
     * @param artifactFile
     * @throws MojoExecutionException
     */
    protected void configure(File artifactFile) throws MojoExecutionException {

        Preconditions.checkArgument(tokenStart != null && !tokenStart.isEmpty(), "tokenStart must not be empty");
        Preconditions.checkArgument(tokenEnd != null && !tokenEnd.isEmpty(), "tokenEnd must not be empty");

        File targetConfigurator = new File(targetDir, "configurator");
        File targetWork = new File(targetConfigurator, "work");
        File finalFile = new File(targetConfigurator, artifactFile.getName());

        if (!targetWork.exists()) {
            targetWork.mkdirs();
        }

        getLog().debug("Resolved target artifact to " + artifactFile.toString());

        Closer closer = Closer.create();

        Container container = new Container(artifactFile.getName());

        try {
            ZipInputStream zis = closer.register(new ZipInputStream(new BufferedInputStream(new FileInputStream(artifactFile))));


            getLog().info("Extracting " + artifactFile + " to " + targetWork);
            ZipFileIteratorAndExtractor iterator = new ZipFileIteratorAndExtractor(container, zis, targetWork);
            iterator.extract();

            getLog().info("Retrieving Properties");
            Properties properties = getProperties();

            getLog().info("Processing Files");
            TemplateProcessor processor = new TemplateProcessor(properties, tokenStart, tokenEnd);
            FileTemplating.processFiles(targetWork, processor);

            getLog().info("Compressing to " + finalFile);
            ZipOutputStream zos = closer.register(new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(finalFile))));
            ZipFileCompressor compressor = new ZipFileCompressor(container, zos, targetWork);
            compressor.compress();

            getLog().info("Done.");
        } catch (IOException e) {
            getLog().error(e);
            throw new MojoExecutionException(e.getMessage(), e);
        } finally {
            try {
                closer.close();
            } catch (IOException e) {
                throw new MojoExecutionException(e.getMessage(), e);
            }
        }
    }


    /**
     *
     * @return Project properties and propertySources.
     * @throws IOException
     */
    private Properties getProperties() throws IOException {

        Properties properties = new Properties();
        properties.putAll(project.getProperties());

        if (propertySources != null) {

            Closer closer = Closer.create();
            try {
                for (String propertySource : propertySources) {
                    loadPropertySource(properties, closer, propertySource);
                }
            } finally {
                closer.close();
            }
        }
        return properties;
    }

    private void loadPropertySource(Properties properties, Closer closer, String propertySource) throws IOException {
        try {
            File file = new File(propertySource);
            InputStream is = null;
            if (file.exists()) {
                is = closer.register(new BufferedInputStream(new FileInputStream(file)));

            } else {
                URL url = new URL(propertySource);
                URLConnection connection = url.openConnection();
                is = closer.register(connection.getInputStream());
            }

            properties.load(is);
        } catch (IOException e) {
            throw new IOException("Cannot load property from source " + propertySource, e);
        }
    }
}