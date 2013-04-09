package de.paluch.maven.configurator;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.AbstractArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;

/**
 * Perform configuration for an external artifact.
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 * @since 08.04.13 10:10
 */
@Mojo(name = "configure-artifact", defaultPhase = LifecyclePhase.PACKAGE)
public class ConfigureArtifactMojo extends AbstractConfigureMojo {


    @Component
    protected ArtifactFactory factory;

    @Component
    private ArtifactResolver artifactResolver;

    @Parameter(defaultValue = "${localRepository}", readonly = true)
    private ArtifactRepository localRepository;

    @Parameter(defaultValue = "${project.remoteArtifactRepositories}", readonly = true, required = true)
    private List<ArtifactRepository> pomRemoteRepositories;

    /**
     * Artifact-GroupId.
     */
    @Parameter(required = true)
    private String groupId;

    /**
     * Artifact-Id.
     */
    @Parameter(required = true)
    private String artifactId;

    /**
     * Version specifier for the Artifact to configure.
     */
    @Parameter(required = true)
    private String version;

    /**
     * Optional classifer.
     */
    @Parameter
    private String classifier;

    /**
     * Packaging type.
     */
    @Parameter(required = true, defaultValue = "jar")
    private String type;


    /**
     * Perform configuration for an external artifact.
     *
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {


        Artifact artifact = factory.createArtifactWithClassifier(groupId, artifactId, version, type, classifier);

        try {
            artifactResolver.resolve(artifact, pomRemoteRepositories, localRepository);
        } catch (AbstractArtifactResolutionException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }

        configure(artifact.getFile());
    }
}
