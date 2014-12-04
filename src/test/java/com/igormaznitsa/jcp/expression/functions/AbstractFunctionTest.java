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
package com.igormaznitsa.jcp.expression.functions;

import org.junit.Test;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.exceptions.PreprocessorException;
import com.igormaznitsa.jcp.expression.Expression;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.expression.ValueType;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.apache.commons.io.FileUtils;
import org.junit.*;
import static org.junit.Assert.*;
import org.junit.rules.TemporaryFolder;

public abstract class AbstractFunctionTest {

  public static TemporaryFolder destinationFolder;

  
  @BeforeClass
  public static void prepareClassTests() throws Exception{
    destinationFolder = new TemporaryFolder(new File("./"));
    destinationFolder.create();
  }
  
  @AfterClass
  public static void doJanitor() throws Exception {
    destinationFolder.delete();
  }
  
  @Before
  public void beforeTest() throws Exception {
    FileUtils.cleanDirectory(destinationFolder.getRoot());
  }
  
  @Test
  public abstract void testName();

  @Test
  public abstract void testReference();

  @Test
  public abstract void testArity();

  @Test
  public abstract void testAllowedArgumentTypes();

  @Test
  public abstract void testResultType();

  protected void assertReference(final AbstractFunction function) {
    final String reference = function.getReference();
    assertNotNull("Reference must not be null", reference);
    assertFalse("Reference must not be empty", reference.isEmpty());
    assertTrue("Reference must not be too short", reference.length() > 10);
  }

  protected void assertAllowedArguments(final AbstractFunction function, final ValueType[][] checkingData) {
    final ValueType[][] argTypes = function.getAllowedArgumentTypes();
    for (final ValueType[] currentTypes : argTypes) {
      boolean found = false;
      for (final ValueType[] etalon : checkingData) {
        if (Arrays.deepEquals(currentTypes, etalon)) {
          found = true;
          break;
        }
      }
      if (!found) {
        fail("Found not allowed argument types " + Arrays.toString(currentTypes));
      }
    }
  }

  protected File getCurrentTestPath() throws Exception {
    final File root = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
    final String clazz = this.getClass().getCanonicalName().replace('.', File.separatorChar)+".class";
    return new File(root,clazz);
  }
  
  protected File getDestinationFolder(){
    return destinationFolder.getRoot();
  }
  
  protected void assertDestinationFolderEmpty() throws Exception {
    assertEquals("Destination folder must be enpty",0, destinationFolder.getRoot().list().length);
  }
  
  protected PreprocessorContext preparePreprocessorContext(final String sourceFolder) throws Exception {
    final PreprocessorContext preprocessorcontext = new PreprocessorContext();
    preprocessorcontext.setSourceDirectories(sourceFolder);
    preprocessorcontext.setDestinationDirectory(destinationFolder.getRoot().getAbsolutePath());
    return preprocessorcontext;
  }
  
  protected void assertFunction(final String expression, final Value expected) throws Exception {
    final PreprocessorContext context = preparePreprocessorContext("./");
    final Value result = Expression.evalExpression(expression, context);
    assertEquals("Must be equals", expected, result);
  }

  protected Throwable getRootCause(final Throwable thr){
    if (thr == null) return null;
    Throwable t = thr;
    while(t!=null){
      if (t.getCause()==null) return t;
      t = t.getCause();
    }
    return t;
  }
  
  protected void assertFunctionException(final String expression) throws IOException {
    final PreprocessorContext context = new PreprocessorContext();
    try {
      Expression.evalExpression(expression, context);
      fail("Must throw RuntimeException [" + expression + ']');
    }
    catch (RuntimeException ex) {
      final PreprocessorException cause = PreprocessorException.extractPreprocessorException(ex);
      if (cause!=null) return;
      ex.printStackTrace();
      fail("Expression must contain preprocessor exception as cause [" + expression + "] but it doesn't have ["+ex.getClass().getName()+']');
    }
  }
}
