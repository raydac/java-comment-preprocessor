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

public class FunctionXML_GETTest extends AbstractFunctionXMLTest {

  private static final FunctionXML_GET HANDLER = new FunctionXML_GET();
  private static final FunctionXML_TEXT GETTEXT = new FunctionXML_TEXT();

  @Test(expected = PreprocessorException.class)
  public void testExecution_WrongElementId() throws Exception {
    HANDLER.executeStrInt(SPY_CONTEXT, Value.valueOf("12qwewqe"), Value.INT_ZERO);
  }

  @Test(expected = PreprocessorException.class)
  public void testExecution_WrongIndex() throws Exception {
    final Value elementList = new FunctionXML_LIST().executeStrStr(SPY_CONTEXT, OPENED_DOCUMENT_ROOT, Value.valueOf("element"));
    HANDLER.executeStrInt(SPY_CONTEXT, elementList, Value.valueOf(Long.valueOf(-1)));
  }

  @Test
  public void testExecution() throws Exception {
    final Value elementList = new FunctionXML_LIST().executeStrStr(SPY_CONTEXT, OPENED_DOCUMENT_ROOT, Value.valueOf("element"));
    assertEquals("elem1", GETTEXT.executeStr(SPY_CONTEXT, HANDLER.executeStrInt(SPY_CONTEXT, elementList, Value.INT_ZERO)).asString());
    assertEquals("elem2", GETTEXT.executeStr(SPY_CONTEXT, HANDLER.executeStrInt(SPY_CONTEXT, elementList, Value.INT_ONE)).asString());
    assertEquals("elem3", GETTEXT.executeStr(SPY_CONTEXT, HANDLER.executeStrInt(SPY_CONTEXT, elementList, Value.INT_TWO)).asString());
    assertEquals("<test>", GETTEXT.executeStr(SPY_CONTEXT, HANDLER.executeStrInt(SPY_CONTEXT, elementList, Value.INT_THREE)).asString());
    final Value elementList2 = new FunctionXML_LIST().executeStrStr(SPY_CONTEXT, OPENED_DOCUMENT_ROOT, Value.valueOf("element"));
    assertEquals("elem1", GETTEXT.executeStr(SPY_CONTEXT, HANDLER.executeStrInt(SPY_CONTEXT, elementList2, Value.INT_ZERO)).asString());
    assertEquals("elem2", GETTEXT.executeStr(SPY_CONTEXT, HANDLER.executeStrInt(SPY_CONTEXT, elementList2, Value.INT_ONE)).asString());
    assertEquals("elem3", GETTEXT.executeStr(SPY_CONTEXT, HANDLER.executeStrInt(SPY_CONTEXT, elementList2, Value.INT_TWO)).asString());
    assertEquals("<test>", GETTEXT.executeStr(SPY_CONTEXT, HANDLER.executeStrInt(SPY_CONTEXT, elementList2, Value.INT_THREE)).asString());
}

  @Override
  public void testName() {
    assertEquals("xml_get", HANDLER.getName());
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
    assertAllowedArguments(HANDLER, new ValueType[][]{{ValueType.STRING, ValueType.INT}});
  }

  @Override
  public void testResultType() {
    assertEquals(ValueType.STRING, HANDLER.getResultType());
  }

}
