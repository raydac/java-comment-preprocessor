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

import com.igormaznitsa.jcp.expression.operators.AbstractOperator;
import com.igormaznitsa.jcp.expression.operators.OperatorADD;
import com.igormaznitsa.jcp.expression.operators.OperatorDIV;
import com.igormaznitsa.jcp.expression.operators.OperatorEQU;
import com.igormaznitsa.jcp.expression.operators.OperatorMUL;
import com.igormaznitsa.jcp.expression.operators.OperatorNOT;
import org.junit.Test;
import static org.junit.Assert.*;

public class ExpressionTreeTest {

  @Test
  public void testAddHierarchyTree() {
    // 1+2

    final ExpressionTree tree = new ExpressionTree();
    tree.addItem(Value.INT_ONE);
    tree.addItem(AbstractOperator.findForClass(OperatorADD.class));
    tree.addItem(Value.INT_TWO);

    final ExpressionTreeElement root = tree.getRoot();

    assertNotNull("Root must not be null", root);
    assertEquals("Root must be add", AbstractOperator.findForClass(OperatorADD.class), root.getItem());
    assertEquals("Left must be 1", Value.INT_ONE, root.getChildForIndex(0).getItem());
    assertEquals("Right must be 2", Value.INT_TWO, root.getChildForIndex(1).getItem());
  }

  @Test
  public void testAddDivHierarchyTree() {
    // 1+2/3

    final ExpressionTree tree = new ExpressionTree();
    tree.addItem(Value.INT_ONE);
    tree.addItem(AbstractOperator.findForClass(OperatorADD.class));
    tree.addItem(Value.INT_TWO);
    tree.addItem(AbstractOperator.findForClass(OperatorDIV.class));
    tree.addItem(Value.INT_THREE);

    final ExpressionTreeElement root = tree.getRoot();

    assertNotNull("Root must not be null", root);
    assertEquals("Root must be add", AbstractOperator.findForClass(OperatorADD.class), root.getItem());
    assertEquals("Left must be 1", Value.INT_ONE, root.getChildForIndex(0).getItem());

    final ExpressionTreeElement right = root.getChildForIndex(1);

    assertEquals("Right must be div", AbstractOperator.findForClass(OperatorDIV.class), right.getItem());
    assertEquals("Left for div must be 2", Value.INT_TWO, right.getChildForIndex(0).getItem());
    assertEquals("Right for div must be 3", Value.INT_THREE, right.getChildForIndex(1).getItem());
  }

  @Test
  public void testDivAddHierarchyTree() {
    // 1/2+3

    final ExpressionTree tree = new ExpressionTree();
    tree.addItem(Value.INT_ONE);
    tree.addItem(AbstractOperator.findForClass(OperatorDIV.class));
    tree.addItem(Value.INT_TWO);
    tree.addItem(AbstractOperator.findForClass(OperatorADD.class));
    tree.addItem(Value.INT_THREE);

    final ExpressionTreeElement root = tree.getRoot();

    assertNotNull("Root must not be null", root);
    assertEquals("Root must be add", AbstractOperator.findForClass(OperatorADD.class), root.getItem());
    assertEquals("Left must be div", AbstractOperator.findForClass(OperatorDIV.class), root.getChildForIndex(0).getItem());

    final ExpressionTreeElement left = root.getChildForIndex(0);

    assertEquals("Left for div must be 1", Value.INT_ONE, left.getChildForIndex(0).getItem());
    assertEquals("Right for div must be 2", Value.INT_TWO, left.getChildForIndex(1).getItem());
    assertEquals("Right for add must be 3", Value.INT_THREE, root.getChildForIndex(1).getItem());
  }

  @Test
  public void testAddAddAddHierarchyTree() {
    // 1+2+3+4

    final ExpressionTree TREE = new ExpressionTree();
    final OperatorADD ADD = AbstractOperator.findForClass(OperatorADD.class);

    TREE.addItem(Value.INT_ONE);
    TREE.addItem(ADD);
    TREE.addItem(Value.INT_TWO);
    TREE.addItem(ADD);
    TREE.addItem(Value.INT_THREE);
    TREE.addItem(ADD);
    TREE.addItem(Value.INT_FOUR);

    final ExpressionTreeElement root = TREE.getRoot();

    assertEquals("Root is ADD", ADD, root.getItem());
    assertEquals("Left for root is ADD", ADD, root.getChildForIndex(0).getItem());
    assertEquals("Right for root is 4", Value.INT_FOUR, root.getChildForIndex(1).getItem());

    ExpressionTreeElement left = root.getChildForIndex(0);
    assertEquals("Left is ADD", ADD, left.getChildForIndex(0).getItem());
    assertEquals("Right is 3", Value.INT_THREE, left.getChildForIndex(1).getItem());

    left = left.getChildForIndex(0);
    assertEquals("Left is 1", Value.INT_ONE, left.getChildForIndex(0).getItem());
    assertEquals("Right is 2", Value.INT_TWO, left.getChildForIndex(1).getItem());
  }

  @Test
  public void testMulAddMulHierarchyTree() {
    // 1*2+3*4

    final ExpressionTree TREE = new ExpressionTree();
    final OperatorADD ADD = AbstractOperator.findForClass(OperatorADD.class);
    final OperatorMUL MUL = AbstractOperator.findForClass(OperatorMUL.class);

    TREE.addItem(Value.INT_ONE);
    TREE.addItem(MUL);
    TREE.addItem(Value.INT_TWO);
    TREE.addItem(ADD);
    TREE.addItem(Value.INT_THREE);
    TREE.addItem(MUL);
    TREE.addItem(Value.INT_FOUR);

    final ExpressionTreeElement root = TREE.getRoot();

    assertEquals("Root is ADD", ADD, root.getItem());

    final ExpressionTreeElement left = root.getChildForIndex(0);
    final ExpressionTreeElement right = root.getChildForIndex(1);

    assertEquals("Left is MUL", MUL, left.getItem());
    assertEquals("Right is MUL", MUL, right.getItem());

    assertEquals("Left-Left is 1", Value.INT_ONE, left.getChildForIndex(0).getItem());
    assertEquals("Left-Right is 2", Value.INT_TWO, left.getChildForIndex(1).getItem());
    assertEquals("Right-Left is 3", Value.INT_THREE, right.getChildForIndex(0).getItem());
    assertEquals("Right-Right is 4", Value.INT_FOUR, right.getChildForIndex(1).getItem());
  }

  @Test
  public void testAddInBrakesMulHierarchyTree() {
    // (1+2)*3 

    final ExpressionTree MAIN_TREE = new ExpressionTree();
    final ExpressionTree BRAKE_TREE = new ExpressionTree();

    final OperatorADD ADD = AbstractOperator.findForClass(OperatorADD.class);
    final OperatorMUL MUL = AbstractOperator.findForClass(OperatorMUL.class);

    BRAKE_TREE.addItem(Value.INT_ONE);
    BRAKE_TREE.addItem(ADD);
    BRAKE_TREE.addItem(Value.INT_TWO);

    MAIN_TREE.addTree(BRAKE_TREE);
    MAIN_TREE.addItem(MUL);
    MAIN_TREE.addItem(Value.INT_THREE);

    final ExpressionTreeElement root = MAIN_TREE.getRoot();

    assertEquals("Root must be MUL", MUL, root.getItem());
    assertEquals("Right must be 3", Value.INT_THREE, root.getChildForIndex(1).getItem());

    final ExpressionTreeElement left = root.getChildForIndex(0);
    assertEquals("Must be ADD", ADD, left.getItem());
    assertEquals("Must be 1", Value.INT_ONE, left.getChildForIndex(0).getItem());
    assertEquals("Must be 2", Value.INT_TWO, left.getChildForIndex(1).getItem());
  }

  @Test
  public void testMulAddInBrakesMulHierarchyTree() {
    // 1*(2+3)*4

    final ExpressionTree MAIN_TREE = new ExpressionTree();
    final ExpressionTree BRAKE_TREE = new ExpressionTree();

    final OperatorADD ADD = AbstractOperator.findForClass(OperatorADD.class);
    final OperatorMUL MUL = AbstractOperator.findForClass(OperatorMUL.class);

    BRAKE_TREE.addItem(Value.INT_TWO);
    BRAKE_TREE.addItem(ADD);
    BRAKE_TREE.addItem(Value.INT_THREE);

    MAIN_TREE.addItem(Value.INT_ONE);
    MAIN_TREE.addItem(MUL);
    MAIN_TREE.addTree(BRAKE_TREE);
    MAIN_TREE.addItem(MUL);
    MAIN_TREE.addItem(Value.INT_FOUR);

    final ExpressionTreeElement root = MAIN_TREE.getRoot();

    assertEquals("Root must be MUL", MUL, root.getItem());
    assertEquals("Right must be 4", Value.INT_FOUR, root.getChildForIndex(1).getItem());

    final ExpressionTreeElement right = root.getChildForIndex(0);
    assertEquals("Right must be MUL", MUL, right.getItem());
    assertEquals("Right-right must be 1", Value.INT_ONE, right.getChildForIndex(0).getItem());

    final ExpressionTreeElement rightLeft = right.getChildForIndex(1);
    assertEquals("Right-left must be ADD", ADD, rightLeft.getItem());
    assertEquals("Right-left-right must be 2", Value.INT_TWO, rightLeft.getChildForIndex(0).getItem());
    assertEquals("Right-left-right must be 3", Value.INT_THREE, rightLeft.getChildForIndex(1).getItem());

  }

  @Test
  public void testNotEquHierarchy() {
    // !true==false

    final OperatorNOT NOT = AbstractOperator.findForClass(OperatorNOT.class);
    final OperatorEQU EQU = AbstractOperator.findForClass(OperatorEQU.class);

    final ExpressionTree MAIN_TREE = new ExpressionTree();

    MAIN_TREE.addItem(NOT);
    MAIN_TREE.addItem(Value.BOOLEAN_TRUE);
    MAIN_TREE.addItem(EQU);
    MAIN_TREE.addItem(Value.BOOLEAN_FALSE);

    final ExpressionTreeElement root = MAIN_TREE.getRoot();

    assertEquals("Root must be EQU", EQU, root.getItem());
    assertEquals("Root right must be FALSE", Value.BOOLEAN_FALSE, root.getChildForIndex(1).getItem());

    final ExpressionTreeElement rootLeft = root.getChildForIndex(0);

    assertEquals("Left must be NOT", NOT, rootLeft.getItem());
    assertEquals("Left-left must be TRUE", Value.BOOLEAN_TRUE, rootLeft.getChildForIndex(0).getItem());
  }
}
