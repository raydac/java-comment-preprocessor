package com.igormaznitsa.jcp.it;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.extension.PreprocessorExtension;
import java.util.Arrays;

public class CustomPreprocessorExtension implements PreprocessorExtension {

  @Override
  public boolean hasAction(int arity) {
    return true;
  }

  @Override
  public boolean hasUserFunction(String name, int arity) {
    if ("hellofunc".equals(name)) {
      return arity == ANY_ARITY || arity == 1;
    }
    return false;
  }

  @Override
  public boolean processAction(PreprocessorContext context, Value[] parameters) {
    System.out.println("Called action for parameters: " + Arrays.toString(parameters));
    return true;
  }

  @Override
  public int getUserFunctionArity(String functionName) {
    if (functionName.equals("hellofunc")) {
      return 1;
    } else {
      throw new IllegalArgumentException("Unexpected user function: " + functionName);
    }
  }

  @Override
  public Value processUserFunction(
      PreprocessorContext context,
      String functionName,
      Value[] arguments) {
    if (functionName.equals("hellofunc")) {
      return Value.valueOf("Hello " + arguments[0].toString() + "!");
    } else {
      throw new IllegalArgumentException("Unexpected user function call: " + functionName);
    }
  }
}
