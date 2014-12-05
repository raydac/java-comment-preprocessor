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
package com.igormaznitsa.jcp.context;

import com.igormaznitsa.jcp.InfoHelper;
import com.igormaznitsa.jcp.exceptions.PreprocessorException;
import com.igormaznitsa.jcp.expression.Value;
import org.junit.Test;
import static org.junit.Assert.*;

public class JCPSpecialVariableProcessorTest {

  @Test
  public void testReadVariable() {
    assertEquals("Must be equals", InfoHelper.getVersion(), new JCPSpecialVariableProcessor().getVariable("jcp.version", null).asString());
    assertNotNull(new JCPSpecialVariableProcessor().getVariable("__line__", null).toString());
    assertNotNull(new JCPSpecialVariableProcessor().getVariable("__date__", null).asString());
    assertNotNull(new JCPSpecialVariableProcessor().getVariable("__time__", null).asString());
    assertNotNull(new JCPSpecialVariableProcessor().getVariable("__timestamp__", null).asString());
  }

  @Test(expected = PreprocessorException.class)
  public void testReadUnknownVariable() {
    new JCPSpecialVariableProcessor().getVariable("jcp.version2", new PreprocessorContext());
  }

  @Test(expected = PreprocessorException.class)
  public void testWriteDisallowed() {
    new JCPSpecialVariableProcessor().setVariable("jcp.version", Value.INT_ONE, new PreprocessorContext());
  }
}
