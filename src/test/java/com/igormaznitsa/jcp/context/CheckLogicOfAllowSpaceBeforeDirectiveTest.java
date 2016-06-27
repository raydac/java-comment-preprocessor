/*
 * Copyright 2016 Igor Maznitsa.
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
package com.igormaznitsa.jcp.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import com.igormaznitsa.jcp.AbstractSpyPreprocessorContextTest;
import com.igormaznitsa.jcp.exceptions.PreprocessorException;
import com.igormaznitsa.jcp.expression.Expression;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CheckLogicOfAllowSpaceBeforeDirectiveTest extends AbstractSpyPreprocessorContextTest {
  
  @Test 
  public void testProcess_NoSpaced_SpacesNotAllowed() throws Exception {
    final PreprocessorContext context = preparePreprocessorContext(getCurrentTestFolder());
    final String text = Expression.evalExpression("evalfile(\"./standardFile.txt\")", context).asString(); 
    assertEquals(" hello\n /*$VAR$*/ Universe\n",text);
  }

  @Test 
  public void testProcess_NoSpaced_SpacesAllowed() throws Exception {
    final PreprocessorContext context = preparePreprocessorContext(getCurrentTestFolder(), new ContextDataProvider() {
      @Override
      public boolean getAllowSpaceBeforeDirectiveFlag() {
        return true;
      }
    });
    final String text = Expression.evalExpression("evalfile(\"./standardFile.txt\")", context).asString();
    assertEquals(" hello\n /*$VAR$*/ Universe\n", text);
  }

  @Test
  public void testProcess_Spaced_SpacesNotAllowed() throws Exception {
    try{
      final PreprocessorContext context = preparePreprocessorContext(getCurrentTestFolder());
      Expression.evalExpression("evalfile(\"./spacedFile.txt\")", context).asString();
      fail("Must throw preprocessor exception");
    }catch(PreprocessorException ex){
      assertTrue(ex.getMessage().contains("Unknown variable"));
    }
  }

  @Test
  public void testProcess_Spaced_SpacesAllowed() throws Exception {
    final PreprocessorContext context = preparePreprocessorContext(getCurrentTestFolder(), new ContextDataProvider() {
      @Override
      public boolean getAllowSpaceBeforeDirectiveFlag() {
        return true;
      }
    });
    final String text = Expression.evalExpression("evalfile(\"./spacedFile.txt\")", context).asString();
    assertEquals(" hello\n /*$VAR$*/ Universe\n", text);
  }



}
