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
package com.igormaznitsa.jcp.usecases;

import com.igormaznitsa.jcp.JCPreprocessor;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.extension.PreprocessorExtension;
import static org.junit.Assert.*;

public class UserFunctionTest extends AbstractUseCaseTest implements PreprocessorExtension {

  int calledfunc;
  int calledaction;
  
  @Override
  protected void tuneContext(PreprocessorContext context) {
    context.setPreprocessorExtension(this);
  }

  @Override
  public void check(PreprocessorContext context, JCPreprocessor.PreprocessingStatistics stat) throws Exception {
    assertEquals("User function must be called once", 1, calledfunc);
    assertEquals("User action must be called twice", 2, calledaction);
    assertEquals(0,stat.getNumberOfCopied());
    assertEquals(1,stat.getNumberOfPreprocessed());
  }

  @Override
  public boolean processAction(final PreprocessorContext context, final Value[] parameters) {
    calledaction ++;
    assertEquals(1000L, parameters[0].asLong().longValue());
    assertEquals("hello", parameters[1].asString());
    assertEquals(123L, parameters[2].asLong().longValue());
    return true;
  }

  @Override
  public Value processUserFunction(final String functionName, final Value[] arguments) {
    if ("testfunc".equals(functionName) && arguments.length == 3){
      calledfunc++;
      assertEquals(1L,arguments[0].asLong().longValue());
      assertEquals("hry",arguments[1].asString());
      assertEquals(3L,arguments[2].asLong().longValue());
      return Value.valueOf("yayaya");
    }else{
      fail("Unexpected function '"+functionName+'\'');
      throw new RuntimeException("Error");
    }
  }

  @Override
  public int getUserFunctionArity(final String functionName) {
    return functionName.equals("testfunc") ? 3 : -1;
  }
  
}
