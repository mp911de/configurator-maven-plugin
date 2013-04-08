package de.paluch.maven.configurator;

import com.sun.tools.javac.resources.version;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.factory.DefaultArtifactFactory;
import org.apache.maven.artifact.resolver.AbstractArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.artifact.ProjectArtifactFactory;

import java.io.File;
import java.util.List;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 * @since 08.04.13 10:10
 */
@Mojo(name = "configure-artifact", defaultPhase = LifecyclePhase.PACKAGE)
@Execute(phase = LifecyclePhase.PACKAGE)
public class ConfigureArtifactMojo extends AbstractMojo {

    @Parameter
    private MavenProject project;

    @Component
    private ArtifactFactory factory;

    @Component
    private ArtifactResolver artifactResolver;

    @Parameter(defaultValue = "${localRepository}")
    private org.apache.maven.artifact.repository.ArtifactRepository localRepository;

    @Parameter(defaultValue = "${project.remoteArtifactRepositories}")
    private java.util.List remoteRepositories;

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
            artifactResolver.resolve(artifact, remoteRepositories, localRepository);
        } catch (AbstractArtifactResolutionException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }

        File artifactFile = artifact.getFile();


    }
}
