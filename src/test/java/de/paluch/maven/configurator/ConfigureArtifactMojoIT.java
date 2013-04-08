package de.paluch.maven.configurator;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 * @since 08.04.13 10:55
 */
@RunWith(JUnit4.class)
public class ConfigureArtifactMojoIT extends AbstractMojoTestCase {


    @Before
    public final void setUpMojoTestCase() throws Exception {
        super.setUp();
    }

    @After
    public final void tearDownMojoTestCase() throws Exception {
        super.tearDown();
    }

    @Test
    public void configure() throws Exception {
        File pluginConfig = new File(super.getBasedir(), "src/test/resources/configure-it-pom.xml");

        ConfigureArtifactMojo mojo = (ConfigureArtifactMojo) super.lookupMojo("configure-artifact", pluginConfig);
        assertNotNull(mojo);

    }
}
