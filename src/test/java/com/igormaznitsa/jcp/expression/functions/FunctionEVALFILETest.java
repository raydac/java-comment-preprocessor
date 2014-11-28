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

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.*;
import java.io.File;
import org.junit.Test;
import static org.junit.Assert.*;

public class FunctionEVALFILETest extends AbstractFunctionTest {

  private static final FunctionEVALFILE HANDLER = new FunctionEVALFILE();

  @Test
  public void testExecution_ErrorForUndefinedVariable() throws Exception {
    final File theTestPath = getCurrentTestPath();
    final PreprocessorContext context = preparePreprocessorContext(theTestPath.getParent());
    try{
      Expression.evalExpression("evalfile(\"./eval/TestEval.java\")", context);
    }catch(Exception ex){
      assertTrue(getRootCause(ex).getMessage().contains("hello_world"));
    }
    assertDestinationFolderEmpty();
  }

  @Test
  public void testExecution_VisibilityLocalVariable() throws Exception {
    final File theTestPath = getCurrentTestPath();
    final PreprocessorContext context = preparePreprocessorContext(theTestPath.getParent());
    context.setLocalVariable("hello_world", Value.valueOf("Hello World!"));
    final Value result = Expression.evalExpression("evalfile(\"./eval/TestEval.java\")", context);
    assertEquals("System.out.println(\"Hello World!\");", result.asString().trim());
    assertDestinationFolderEmpty();
  }

  @Test
  public void testExecution_IncludedEvalCall() throws Exception {
    final File theTestPath = getCurrentTestPath();
    final PreprocessorContext context = preparePreprocessorContext(theTestPath.getParent());
    context.setLocalVariable("hello_world", Value.valueOf("Hello World!"));
    final Value result = Expression.evalExpression("evalfile(\"./eval/TestEvalWithIncluded.java\")", context);
    final String resultstr = result.asString().trim();
    assertTrue(resultstr.startsWith("System.out.println(\"Hello World!\");"));
    assertTrue(resultstr.endsWith("TestEvalWithIncluded.java"));
    assertDestinationFolderEmpty();
  }

  @Test
  public void testExecution_VisibilityGlobalVariable() throws Exception {
    final File theTestPath = getCurrentTestPath();
    final PreprocessorContext context = preparePreprocessorContext(theTestPath.getParent());
    context.setGlobalVariable("hello_world", Value.valueOf("Hello World!"));
    final Value result = Expression.evalExpression("evalfile(\"./eval/TestEval.java\")", context);
    assertEquals("System.out.println(\"Hello World!\");", result.asString().trim());
    assertDestinationFolderEmpty();
  }

  @Test
  public void testExecution_ConditionsInsideFile() throws Exception {
    final File theTestPath = getCurrentTestPath();
    final PreprocessorContext context = preparePreprocessorContext(theTestPath.getParent());
    context.setGlobalVariable("includemeth", Value.valueOf(true));
    context.setGlobalVariable("hello_world", Value.valueOf("Hello World!"));
    final Value result = Expression.evalExpression("evalfile(\"./eval/TestEval.java\")", context);
    assertTrue(result.asString().contains("public void main(String ... args){"));
    assertDestinationFolderEmpty();
  }

  @Test
  public void testExecution_Str_wrongCases() throws Exception {
    assertFunctionException("evalfile()");
    assertFunctionException("evalfile(true)");
    assertFunctionException("evalfile(,)");
    assertFunctionException("evalfile(1,\"ttt\")");
    assertFunctionException("evalfile(\"d\",\"ttt\")");
    assertFunctionException("evalfile(123)");
    assertDestinationFolderEmpty();
  }

  @Override
  public void testName() {
    assertEquals("evalfile", HANDLER.getName());
  }

  @Override
  public void testReference() {
    assertReference(HANDLER);
  }

  @Override
  public void testArity() {
    assertEquals(1, HANDLER.getArity());
  }

  @Override
  public void testAllowedArgumentTypes() {
    assertAllowedArguments(HANDLER, new ValueType[][]{{ValueType.STRING}});
  }

  @Override
  public void testResultType() {
    assertEquals(ValueType.STRING, HANDLER.getResultType());
  }
}
