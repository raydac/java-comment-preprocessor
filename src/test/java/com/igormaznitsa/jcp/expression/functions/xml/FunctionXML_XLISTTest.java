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
package com.igormaznitsa.jcp.expression.functions.xml;

import com.igormaznitsa.jcp.exceptions.PreprocessorException;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.expression.ValueType;
import org.junit.Test;
import static org.junit.Assert.*;

public class FunctionXML_XLISTTest extends AbstractFunctionXMLTest {

  private static final FunctionXML_XLIST HANDLER = new FunctionXML_XLIST();

  @Test(expected = PreprocessorException.class)
  public void testExecution_ForWrongElement() throws Exception {
    assertNotNull(HANDLER.executeStrStr(SPY_CONTEXT, Value.valueOf("some wrong"), Value.valueOf("nonexist")));
  }

  @Test
  public void testExecution_NonExistElement() throws Exception {
    final Value value = HANDLER.executeStrStr(SPY_CONTEXT, OPENED_DOCUMENT_ID, Value.valueOf("/root/nonexist"));
    assertNotNull(value);
    assertEquals(0,new FunctionXML_SIZE().executeStr(SPY_CONTEXT, value).asLong().intValue());
  }

  @Test
  public void testExecution_ForExistElements() throws Exception {
    final Value value = HANDLER.executeStrStr(SPY_CONTEXT, OPENED_DOCUMENT_ID, Value.valueOf("/root/element"));
    assertNotNull(value);
    assertEquals(4, new FunctionXML_SIZE().executeStr(SPY_CONTEXT, value).asLong().intValue());
  }

  @Override
  public void testName() {
    assertEquals("xml_xlist", HANDLER.getName());
  }

  @Override
  public void testReference() {
    assertReference(HANDLER);
  }

  @Override
  public void testArity() {
    assertEquals(2, HANDLER.getArity());
  }

  @Override
  public void testAllowedArgumentTypes() {
    assertAllowedArguments(HANDLER, new ValueType[][]{{ValueType.STRING, ValueType.STRING}});
  }

  @Override
  public void testResultType() {
    assertEquals(ValueType.STRING, HANDLER.getResultType());
  }

}
