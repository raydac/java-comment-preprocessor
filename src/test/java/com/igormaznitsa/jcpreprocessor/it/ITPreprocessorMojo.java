package com.igormaznitsa.jcpreprocessor.it;

import java.util.List;
import java.io.File;
import java.util.Arrays;
import java.util.jar.JarEntry;
import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;
import org.apache.maven.shared.jar.JarAnalyzer;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class ITPreprocessorMojo  {
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        assertNotNull("The test desires the ${maven.home} system property", System.getProperty("maven.home"));
        
        final String jarFile = System.getProperty("project.build.directory")+File.separatorChar+System.getProperty("project.build.finalName")+".jar";
        final String pomFile = System.getProperty("project.pom");
        
        final File testDir = ResourceExtractor.simpleExtractResources( ITPreprocessorMojo.class, "dummy_maven_project" );
        final Verifier verifier = new Verifier(testDir.getAbsolutePath());

        verifier.assertFilePresent(jarFile);
        verifier.assertFilePresent(pomFile);
        
        verifier.setCliOptions(Arrays.asList("-Dfile="+jarFile, "-DpomFile="+pomFile));
        verifier.executeGoal("install:install-file");
        
        verifier.verifyErrorFreeLog();
    }
    
    @Test
    public void testPreprocessorUsage() throws Exception {
        final File testDir = ResourceExtractor.simpleExtractResources( this.getClass(), "./dummy_maven_project" );
        
        final Verifier verifier = new Verifier(testDir.getAbsolutePath());
        
        verifier.deleteArtifact( "com.igormaznitsa", "DummyMavenProjectToTestJCP", "1.0-SNAPSHOT", "jar");
        verifier.executeGoal("package");

        final File resultJar = ResourceExtractor.simpleExtractResources( this.getClass(), "./dummy_maven_project/target/DummyMavenProjectToTestJCP-1.0-SNAPSHOT.jar" );
        
        verifier.verifyErrorFreeLog();

        final JarAnalyzer jarAnalyzer = new JarAnalyzer(resultJar);
        List classEntries = null;
        try {
            classEntries = jarAnalyzer.getClassEntries();
        }finally{
            jarAnalyzer.closeQuietly();
        }
        
        assertEquals("Must have only class", 1, classEntries.size());
        final JarEntry classEntry = (JarEntry)classEntries.get(0);
        assertEquals("Class must be placed in the path", "com/igormaznitsa/dummyproject/testmain2.class", classEntry.getName());
    }
}
