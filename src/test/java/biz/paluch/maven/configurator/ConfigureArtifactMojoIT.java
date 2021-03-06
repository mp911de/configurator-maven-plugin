package biz.paluch.maven.configurator;

import biz.paluch.maven.configurator.ConfigureArtifactMojo;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.ReflectionUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 * @since 08.04.13 10:55
 */
@RunWith(MockitoJUnitRunner.class)
public class ConfigureArtifactMojoIT extends AbstractMojoTestCase {

    @Mock
    protected ArtifactFactory factory;

    @Mock
    private ArtifactResolver artifactResolver;

    @Mock
    private Artifact artifact;


    @Before
    public final void setUpMojoTestCase() throws Exception {
        super.setUp();
    }

    @After
    public final void tearDownMojoTestCase() throws Exception {
        super.tearDown();
    }

    @Test
    public void testConfigure() throws Exception {
        File pluginConfig = new File(super.getBasedir(), "src/test/resources/configure-it-pom.xml");
        File artifactFile = new File(super.getBasedir(), "src/test/resources/simple.war");
        File targetFile = new File(super.getBasedir() + "/target/configurator/simple.war");


        when(factory.createArtifactWithClassifier(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(artifact);
        when(artifact.getFile()).thenReturn(artifactFile);

        ConfigureArtifactMojo mojo = (ConfigureArtifactMojo) super.lookupMojo("configure-artifact", pluginConfig);
        MavenProject project = new MavenProject();
        project.getProperties().setProperty("value", "the value after processing");

        ReflectionUtils.setVariableValueInObject(mojo, "factory", factory);
        ReflectionUtils.setVariableValueInObject(mojo, "artifactResolver", artifactResolver);
        ReflectionUtils.setVariableValueInObject(mojo, "targetDir", new File(super.getBasedir() + "/target/"));
        ReflectionUtils.setVariableValueInObject(mojo, "project", project);

        mojo.execute();


        assertTrue(targetFile.exists());

        Properties p = getProperties(targetFile);
        assertEquals("the value after processing", p.getProperty("property"));



    }

    private Properties getProperties(File targetFile) throws IOException {
        ZipFile zipFile = new ZipFile(targetFile);

        ZipEntry ze = zipFile.getEntry("WEB-INF/config.properties");


        Properties p = new Properties();
        p.load(zipFile.getInputStream(ze));
        zipFile.close();
        return p;
    }
}
