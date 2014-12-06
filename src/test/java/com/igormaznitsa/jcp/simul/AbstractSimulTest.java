/*
 * Copyright 2014 Igor Maznitsa.
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
package com.igormaznitsa.jcp.simul;

import com.igormaznitsa.jcp.JCPreprocessor;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

public abstract class AbstractSimulTest  {
  protected TemporaryFolder resultFolder;
  protected File sourceFolder;
  
  @Before
  public void before() throws Exception {
    resultFolder = new TemporaryFolder();
    resultFolder.create();
    sourceFolder = new File(new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getAbsoluteFile(),this.getClass().getName().replace('.', File.separatorChar)+File.separatorChar+"source");
  }
 
  @After
  public void after() throws Exception {
    try{
      FileUtils.cleanDirectory(resultFolder.getRoot());
    }finally{
      resultFolder.delete();
    }
  }
  
  public abstract void check();

  @Test
  public final void main() throws Exception {
    final PreprocessorContext context = new PreprocessorContext();
    context.setClearDestinationDirBefore(true);
    context.setSourceDirectories(sourceFolder.getAbsolutePath());
    context.setDestinationDirectory(resultFolder.getRoot().getAbsolutePath());
    
    JCPreprocessor preprocessor = new JCPreprocessor(context);
    preprocessor.execute();
    
    check();
  }
}
