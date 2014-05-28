/* 
 * Copyright 2014 Igor Maznitsa (http://www.igormaznitsa.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

    assertEquals("nothing", MavenPropertiesImporter.getProperty(mockProject, property));
  }
}
