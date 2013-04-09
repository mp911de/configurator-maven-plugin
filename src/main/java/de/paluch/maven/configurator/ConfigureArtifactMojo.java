package de.paluch.maven.configurator;

import com.google.common.io.Closer;
import de.paluch.maven.configurator.model.Container;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.AbstractArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 * @since 08.04.13 10:10
 */
@Mojo(name = "configure-artifact", defaultPhase = LifecyclePhase.PACKAGE)
@Execute(phase = LifecyclePhase.PACKAGE)
public class ConfigureArtifactMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Component
    protected ArtifactFactory factory;

    @Component
    private ArtifactResolver artifactResolver;

    @Parameter(defaultValue = "${localRepository}", readonly = true)
    private ArtifactRepository localRepository;

    @Parameter(defaultValue = "${project.remoteArtifactRepositories}", readonly = true, required = true)
    private List<ArtifactRepository> pomRemoteRepositories;

    @Parameter(required = true)
    private String groupId;

    @Parameter(required = true)
    private String artifactId;

    @Parameter(required = true)
    private String version;

    @Parameter
    private String classifier;

    @Parameter(required = true, defaultValue = "jar")
    private String type;

    @Parameter
    private List<String> propertySources;

    @Parameter
    private String tokenStart = "@";

    @Parameter
    private String tokenEnd = "@";


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {


        Artifact artifact = factory.createArtifactWithClassifier(groupId, artifactId, version, type, classifier);

        try {
            artifactResolver.resolve(artifact, pomRemoteRepositories, localRepository);
        } catch (AbstractArtifactResolutionException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }

        File artifactFile = artifact.getFile();

        File target = new File(project.getBuild().getDirectory());
        File targetConfigurator = new File(target, "configurator");
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
            ZipFileIterator iterator = new ZipFileIterator(container, zis, targetWork);
            iterator.extract();

            getLog().info("Retrieving Properties");
            Properties properties = getProperties();

            getLog().info("Processing Files");
            TemplateProcessor processor = new TemplateProcessor(properties, tokenStart, tokenEnd);
            FileTemplating.processFiles(targetWork, processor);

            getLog().info("Compressing to " + finalFile);
            ZipOutputStream zos = closer.register(new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(finalFile))));
            ZipFileCompressor compressor = new ZipFileCompressor(container, zos, targetWork);
            compressor.compress("");

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

    public Properties getProperties() throws IOException {

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
