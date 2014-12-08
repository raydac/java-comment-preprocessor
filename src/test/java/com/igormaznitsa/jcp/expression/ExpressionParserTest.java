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
package com.igormaznitsa.jcp.expression;

import com.igormaznitsa.jcp.expression.functions.AbstractFunction;
import com.igormaznitsa.jcp.expression.functions.FunctionABS;
import com.igormaznitsa.jcp.expression.functions.xml.FunctionXML_ATTR;
import com.igormaznitsa.jcp.expression.operators.AbstractOperator;
import com.igormaznitsa.jcp.expression.operators.OperatorADD;
import com.igormaznitsa.jcp.expression.operators.OperatorDIV;
import com.igormaznitsa.jcp.expression.operators.OperatorEQU;
import com.igormaznitsa.jcp.expression.operators.OperatorLESS;
import com.igormaznitsa.jcp.expression.operators.OperatorMOD;
import com.igormaznitsa.jcp.expression.operators.OperatorMUL;
import com.igormaznitsa.jcp.expression.operators.OperatorSUB;
import java.io.PushbackReader;
import java.io.StringReader;
import org.junit.Test;
import static org.junit.Assert.*;

public class ExpressionParserTest {

  @Test
  public void testReplacingNegativeNumber() throws Exception {
    final ExpressionTree tree = new ExpressionTree();
    final OperatorSUB SUB = AbstractOperator.findForClass(OperatorSUB.class);

    tree.addItem(SUB);
    tree.addItem(Value.INT_ONE);
    tree.addItem(SUB);
    tree.addItem(Value.INT_TWO);

    tree.postProcess();

    final ExpressionTreeElement root = tree.getRoot();

    assertEquals("Root must be SUB", SUB, root.getItem());
    assertEquals("Left must be -1", Value.valueOf(Long.valueOf(-1L)), root.getChildForIndex(0).getItem());
    assertEquals("Right must be 2", Value.INT_TWO, root.getChildForIndex(1).getItem());
  }

  @Test
  public void testNextItem_zero() throws Exception {
    final PushbackReader reader = new PushbackReader(new StringReader("0"));
    assertEquals("Must be 0", Value.INT_ZERO, ExpressionParser.getInstance().nextItem(reader, null));
    assertNull("Must be null", ExpressionParser.getInstance().nextItem(reader, null));
  }

  @Test
  public void testNextItem_negativeNumber() throws Exception {
    final PushbackReader reader = new PushbackReader(new StringReader("-1"));
    assertEquals("Must be SUB", AbstractOperator.findForClass(OperatorSUB.class), ExpressionParser.getInstance().nextItem(reader, null));
    assertEquals("Must be 1", Value.INT_ONE, ExpressionParser.getInstance().nextItem(reader, null));
    assertNull("Must be null", ExpressionParser.getInstance().nextItem(reader, null));
  }

  @Test
  public void testNextItem_zeroLess() throws Exception {
    final PushbackReader reader = new PushbackReader(new StringReader("0<"));
    assertEquals("Must be 0", Value.INT_ZERO, ExpressionParser.getInstance().nextItem(reader, null));
    assertEquals("Must be LESS", AbstractOperator.findForClass(OperatorLESS.class), ExpressionParser.getInstance().nextItem(reader, null));
    assertNull("Must be null", ExpressionParser.getInstance().nextItem(reader, null));
  }

  @Test
  public void testNextItem_oneValue() throws Exception {
    final PushbackReader reader = new PushbackReader(new StringReader("3"));
    assertEquals("Must be 3", Value.INT_THREE, ExpressionParser.getInstance().nextItem(reader, null));
    assertNull("Must be null", ExpressionParser.getInstance().nextItem(reader, null));
  }

  @Test
  public void testNextItem_oneHexValue() throws Exception {
    final PushbackReader reader = new PushbackReader(new StringReader("0xfF"));
    assertEquals("Must be 255", Value.valueOf(Long.valueOf(255L)), ExpressionParser.getInstance().nextItem(reader, null));
    assertNull("Must be null", ExpressionParser.getInstance().nextItem(reader, null));
  }

  @Test
  public void testNextItem_oneBooleanTrueValue() throws Exception {
    final PushbackReader reader = new PushbackReader(new StringReader("true"));
    assertEquals("Must be TRUE", Value.BOOLEAN_TRUE, ExpressionParser.getInstance().nextItem(reader, null));
    assertNull("Must be null", ExpressionParser.getInstance().nextItem(reader, null));
  }

  @Test
  public void testNextItem_oneBooleanFalseValue() throws Exception {
    final PushbackReader reader = new PushbackReader(new StringReader("false"));
    assertEquals("Must be FALSE", Value.BOOLEAN_FALSE, ExpressionParser.getInstance().nextItem(reader, null));
    assertNull("Must be null", ExpressionParser.getInstance().nextItem(reader, null));
  }

  @Test
  public void testNextItem_oneOperator() throws Exception {
    final PushbackReader reader = new PushbackReader(new StringReader("/"));
    assertEquals("Must be DIV", AbstractOperator.findForClass(OperatorDIV.class), ExpressionParser.getInstance().nextItem(reader, null));
    assertNull("Must be null", ExpressionParser.getInstance().nextItem(reader, null));
  }

  @Test
  public void testNextItem_complexExpression() throws Exception {

    final PushbackReader reader = new PushbackReader(new StringReader("xml_attr(1.3%abs(1+2)*3/4,\"hello\"==\"\nworld\t\")"));

    final ExpressionItem[] items = new ExpressionItem[]{
      AbstractFunction.findForClass(FunctionXML_ATTR.class),
      ExpressionParser.SpecialItem.BRACKET_OPENING,
      Value.valueOf(Float.valueOf(1.3f)),
      AbstractOperator.findForClass(OperatorMOD.class),
      AbstractFunction.findForClass(FunctionABS.class),
      ExpressionParser.SpecialItem.BRACKET_OPENING,
      Value.INT_ONE,
      AbstractOperator.findForClass(OperatorADD.class),
      Value.INT_TWO,
      ExpressionParser.SpecialItem.BRACKET_CLOSING,
      AbstractOperator.findForClass(OperatorMUL.class),
      Value.INT_THREE,
      AbstractOperator.findForClass(OperatorDIV.class),
      Value.INT_FOUR,
      ExpressionParser.SpecialItem.COMMA,
      Value.valueOf("hello"),
      AbstractOperator.findForClass(OperatorEQU.class),
      Value.valueOf("\nworld\t"),
      ExpressionParser.SpecialItem.BRACKET_CLOSING
    };

    int index = 0;
    for (final ExpressionItem item : items) {
      assertEquals("Position " + index + " must be equals", item, ExpressionParser.getInstance().nextItem(reader, null));
      index++;
    }
    assertNull(ExpressionParser.getInstance().nextItem(reader, null));
  }

  @Test
  public void testParsing_oneValue() throws Exception {
    final ExpressionParser parser = ExpressionParser.getInstance();
    final ExpressionTree tree = parser.parse("3", null);

    final ExpressionTreeElement root = tree.getRoot();
    assertEquals("Root is 3", Value.INT_THREE, root.getItem());
  }

  @Test
  public void testParsing_negativeNumber() throws Exception {
    final ExpressionParser parser = ExpressionParser.getInstance();
    final ExpressionTree tree = parser.parse(Long.toString(Long.MIN_VALUE + 1), null);

    final ExpressionTreeElement root = tree.getRoot();
    assertEquals("Root is Long.MIN_VALUE+1", Value.valueOf(Long.valueOf(Long.MIN_VALUE + 1)), root.getItem());
  }

  @Test
  public void testParsing_easyExpression() throws Exception {
    final ExpressionParser parser = ExpressionParser.getInstance();
    final ExpressionTree tree = parser.parse("3*4/8", null);

    final ExpressionTreeElement root = tree.getRoot();
    assertEquals("Root is DIV", AbstractOperator.findForClass(OperatorDIV.class), root.getItem());
    assertEquals("Right is 8", Value.valueOf(Long.valueOf(8L)), root.getChildForIndex(1).getItem());

    final ExpressionTreeElement left = root.getChildForIndex(0);
    assertEquals("Left is MUL", AbstractOperator.findForClass(OperatorMUL.class), left.getItem());
    assertEquals("Left-left is 3", Value.INT_THREE, left.getChildForIndex(0).getItem());
    assertEquals("Left-right is 4", Value.INT_FOUR, left.getChildForIndex(1).getItem());
  }

  @Test
  public void testParsing_complexExpression() throws Exception {
    final ExpressionParser parser = ExpressionParser.getInstance();
    final ExpressionTree tree = parser.parse("(var1+1)*xml_attr(\"first\",\"hello\"+\"world\")", null);

    final ExpressionTreeElement root = tree.getRoot();

    assertEquals("Root must be MUL", AbstractOperator.findForClass(OperatorMUL.class), root.getItem());

    final ExpressionTreeElement left = root.getChildForIndex(0);
    final ExpressionTreeElement right = root.getChildForIndex(1);

    assertEquals("Left must be ADD", AbstractOperator.findForClass(OperatorADD.class), left.getItem());
    assertEquals("Right must be Function", AbstractFunction.findForClass(FunctionXML_ATTR.class), right.getItem());
  }

  @Test
  public void testParsing_deepIncludingBrackets() throws Exception {
    final ExpressionParser parser = ExpressionParser.getInstance();
    final ExpressionTree tree = parser.parse("((((((1+2))))))", null);

    final ExpressionTreeElement root = tree.getRoot();

    assertEquals("Root must be ADD", AbstractOperator.findForClass(OperatorADD.class), root.getItem());
    assertEquals("Left must be 1", Value.INT_ONE, root.getChildForIndex(0).getItem());
    assertEquals("Left must be 2", Value.INT_TWO, root.getChildForIndex(1).getItem());
  }

  @Test
  public void testParsing_insideFunctionCall() throws Exception {
    final ExpressionParser parser = ExpressionParser.getInstance();
    final ExpressionTree tree = parser.parse("xml_get(xml_get(1,2),3)", null);

    final AbstractFunction xmlElementAt = AbstractFunction.findForName("xml_get");
    assertNotNull(xmlElementAt);

    final ExpressionTreeElement root = tree.getRoot();
    assertEquals("Must be xml_get", xmlElementAt, root.getItem());

    final ExpressionTreeElement left = root.getChildForIndex(0);
    final ExpressionTreeElement right = root.getChildForIndex(1);

    assertEquals("Must be 3", Value.INT_THREE, right.getItem());
    assertEquals("Must be xml_get", xmlElementAt, left.getItem());
    assertEquals("Must be 1", Value.INT_ONE, left.getChildForIndex(0).getItem());
    assertEquals("Must be 2", Value.INT_TWO, left.getChildForIndex(1).getItem());
  }

  @Test
  public void testParsing_notEasyBrackets() throws Exception {
    final ExpressionParser parser = ExpressionParser.getInstance();
    final ExpressionTree tree = parser.parse("(1+2*(3-4))", null);

    final OperatorADD ADD = AbstractOperator.findForClass(OperatorADD.class);
    final OperatorSUB SUB = AbstractOperator.findForClass(OperatorSUB.class);
    final OperatorMUL MUL = AbstractOperator.findForClass(OperatorMUL.class);

    final ExpressionTreeElement root = tree.getRoot();

    assertEquals("Root must be ADD", ADD, root.getItem());
    assertEquals("Left must be 1", Value.INT_ONE, root.getChildForIndex(0).getItem());

    final ExpressionTreeElement right = root.getChildForIndex(1);
    assertEquals("Right must be MUL", MUL, right.getItem());
    assertEquals("Right-left must be 2", Value.INT_TWO, right.getChildForIndex(0).getItem());

    final ExpressionTreeElement rightRight = right.getChildForIndex(1);
    assertEquals("Right-right must be SUB", SUB, rightRight.getItem());
    assertEquals("Right-right-left must be 3", Value.INT_THREE, rightRight.getChildForIndex(0).getItem());
    assertEquals("Right-right-right must be 4", Value.INT_FOUR, rightRight.getChildForIndex(1).getItem());
  }

  @Test
  public void testParsing_emptyBrakes() throws Exception {
    final ExpressionParser parser = ExpressionParser.getInstance();
    final ExpressionTree tree = parser.parse("()", null);
    assertNull("Must be null", tree.getRoot());
  }
}
