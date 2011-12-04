package com.igormaznitsa.jcp.maven;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.junit.Test;
import org.mockito.Mockito;
import static org.junit.Assert.*;

public class MavenPropertiesImporterTest {

    @Test
    public void testNormalizeGetter() {
        assertEquals("getHelloWorld", MavenPropertiesImporter.normalizeGetter("helloWorld"));
    }

    @Test
    public void testRequestProperty() throws Exception {
        final String property = "project.artifact.dependencyConflictId";

        final MavenProject mockProject = Mockito.mock(MavenProject.class);
        final Artifact mockArtifact = Mockito.mock(Artifact.class);

        Mockito.when(mockProject.getArtifact()).thenReturn(mockArtifact);
        Mockito.when(mockArtifact.getDependencyConflictId()).thenReturn("nothing");
          
        assertEquals("nothing",MavenPropertiesImporter.getProperty(mockProject, property));
    }
}
