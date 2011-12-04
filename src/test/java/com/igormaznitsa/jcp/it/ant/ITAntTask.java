package com.igormaznitsa.jcp.it.ant;

import java.io.File;
import org.apache.tools.ant.BuildFileTest;
import org.apache.tools.ant.types.LogLevel;
import org.junit.Before;
import org.junit.Test;

public class ITAntTask extends BuildFileTest {

    @Before
    public void setUp() throws Exception {
        final File file = new File(this.getClass().getResource("build.xml").toURI());
        configureProject(file.getCanonicalPath(),LogLevel.DEBUG.getLevel());
        project.setBaseDir(file.getParentFile().getCanonicalFile());
    }
    
    @Test
    public void testPreprocess() {
        
        executeTarget("preprocess");
    }
}
