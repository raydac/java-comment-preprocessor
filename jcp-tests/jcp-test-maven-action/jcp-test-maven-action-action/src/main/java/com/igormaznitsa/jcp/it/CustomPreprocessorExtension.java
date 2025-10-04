package com.igormaznitsa.jcp.it;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.extension.PreprocessorExtension;
import java.util.Arrays;
import java.util.Set;
import java.util.List;

public class CustomPreprocessorExtension implements PreprocessorExtension {

  private static final Set<Integer> ARITY = Set.of(1);

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
  public Set<Integer> getUserFunctionArity(String functionName) {
    if (functionName.equals("hellofunc")) {
      return ARITY;
    } else {
      throw new IllegalArgumentException("Unexpected user function: " + functionName);
    }
  }

  @Override
  public Value processUserFunction(
      PreprocessorContext context,
      String functionName,
      List<Value> arguments) {
    if (functionName.equals("hellofunc")) {
      return Value.valueOf("Hello " + arguments.get(0).toString() + "!");
    } else {
      throw new IllegalArgumentException("Unexpected user function call: " + functionName);
    }
  }
}
