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
package com.igormaznitsa.jcp.it.maven;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.jar.*;
import org.apache.commons.io.IOUtils;
import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;
import org.apache.maven.shared.jar.JarAnalyzer;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

public class ITPreprocessorMojo {

  private static void assertMainClass(final String jarFile, final String mainClass) throws Exception {
    JarInputStream jarStream = null;
    try {
      jarStream = new JarInputStream(new FileInputStream(jarFile));
      final Manifest manifest = jarStream.getManifest();
      final Attributes attrs = manifest.getMainAttributes();
      assertEquals("Maven plugin must also provide and main class in manifest",mainClass, attrs.getValue("Main-Class"));
    }
    finally {
      IOUtils.closeQuietly(jarStream);
    }
  }

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    assertNotNull("The test desires the ${maven.home} system property", System.getProperty("maven.home"));

    final String jarFile = System.getProperty("plugin.jar");
    final String pomFile = System.getProperty("project.pom");

    final File testDir = ResourceExtractor.simpleExtractResources(ITPreprocessorMojo.class, "dummy_maven_project");
    final Verifier verifier = new Verifier(testDir.getAbsolutePath());

    verifier.assertFilePresent(jarFile);
    verifier.assertFilePresent(pomFile);

    final String processedJarFileName = (jarFile.indexOf(' ') >= 0 ? "\"" + jarFile + "\"" : jarFile).replace('/', File.separatorChar).replace('\\', File.separatorChar);
    final String processedPomFile = (pomFile.indexOf(' ') >= 0 ? "\"" + pomFile + "\"" : pomFile).replace('/', File.separatorChar).replace('\\', File.separatorChar);

    // check that manifest contains main class
    assertMainClass(jarFile, "com.igormaznitsa.jcp.JCPreprocessor");

    verifier.setCliOptions(Arrays.asList("-Dfile=" + processedJarFileName, "-DpomFile=" + processedPomFile));
    verifier.executeGoal("install:install-file");

    verifier.verifyErrorFreeLog();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testPreprocessorUsage() throws Exception {
    final File testDir = ResourceExtractor.simpleExtractResources(this.getClass(), "./dummy_maven_project");

    final Verifier verifier = new Verifier(testDir.getAbsolutePath());

    verifier.deleteArtifact("com.igormaznitsa", "DummyMavenProjectToTestJCP", "1.0-SNAPSHOT", "jar");
    verifier.executeGoal("package");
    assertFalse("Folder must be removed", new File("./dummy_maven_project/target").exists());

    final File resultJar = ResourceExtractor.simpleExtractResources(this.getClass(), "./dummy_maven_project/DummyMavenProjectToTestJCP-1.0-SNAPSHOT.jar");

    verifier.verifyErrorFreeLog();
    verifier.verifyTextInLog("PREPROCESSED_TESTING_COMPLETED");
    verifier.verifyTextInLog("Cleaning has been started");
    verifier.verifyTextInLog("Removing preprocessed source folder");
    verifier.verifyTextInLog("Removing preprocessed test source folder");
    verifier.verifyTextInLog("Scanning for deletable directories");
    verifier.verifyTextInLog("Deleting directory:");
    verifier.verifyTextInLog("Cleaning has been completed");

    final JarAnalyzer jarAnalyzer = new JarAnalyzer(resultJar);
    List<JarEntry> classEntries;
    try {
      classEntries = (List<JarEntry>) jarAnalyzer.getClassEntries();

      assertEquals("Must have only class", 1, classEntries.size());
      final JarEntry classEntry = classEntries.get(0);
      assertEquals("Class must be placed in the path", "com/igormaznitsa/dummyproject/testmain2.class", classEntry.getName());

      DataInputStream inStream = null;
      final byte[] buffer = new byte[(int) classEntry.getSize()];
      Class<?> instanceClass = null;
      try {
        inStream = new DataInputStream(jarAnalyzer.getEntryInputStream(classEntry));
        inStream.readFully(buffer);

        instanceClass = new ClassLoader() {

          public Class<?> loadClass(final byte[] data) throws ClassNotFoundException {
            return defineClass(null, data, 0, data.length);
          }
        }.loadClass(buffer);
      }
      finally {
        IOUtils.closeQuietly(inStream);
      }

      if (instanceClass!=null){
        final Object instance = instanceClass.newInstance();
        assertEquals("Must return the project name", "Dummy Maven Project To Test JCP", instanceClass.getMethod("test").invoke(instance));
      }else{
        fail("Unexpected state");
      }
    }
    finally {
      jarAnalyzer.closeQuietly();
    }
  }
}
