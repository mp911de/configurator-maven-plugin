package de.paluch.maven.configurator;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;

/**
 * Perform configuration for the project artifact.
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
@Mojo(name = "configure", defaultPhase = LifecyclePhase.PACKAGE)
@Execute(phase = LifecyclePhase.PACKAGE)
public class ConfigureMojo extends AbstractConfigureMojo {

    /**
     * Perform configuration for the project artifact.
     *
     * @throws org.apache.maven.plugin.MojoExecutionException
     * @throws org.apache.maven.plugin.MojoFailureException
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        String fileName = project.getBuild().getFinalName() + "." + project.getPackaging();
        configure(new File(targetDir, fileName));
    }

}
