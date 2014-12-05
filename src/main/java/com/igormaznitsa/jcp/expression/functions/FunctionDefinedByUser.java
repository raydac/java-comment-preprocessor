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
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.expression.ValueType;
import com.igormaznitsa.jcp.utils.PreprocessorUtils;

/**
 * The class implements the user defined function handler (a function which name
 * begins with $)
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public final class FunctionDefinedByUser extends AbstractFunction {

  private final String name;
  private final int argsNumber;
  private final ValueType[][] argTypes;

  public FunctionDefinedByUser(final String name, final int argsNumber, final PreprocessorContext context) {
    super();
    PreprocessorUtils.assertNotNull("Name is null", name);
    PreprocessorUtils.assertNotNull("Context is null", context);

    if (argsNumber < 0) {
      throw context.makeException("Unexpected argument number ["+argsNumber+']',null);
    }

    this.name = name;
    this.argsNumber = argsNumber;

    final ValueType[] types = new ValueType[argsNumber];

    for (int li = 0; li < argsNumber; li++) {
      types[li] = ValueType.ANY;
    }
    this.argTypes = new ValueType[][]{types};
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public int getArity() {
    return argsNumber;
  }

  public Value execute(final PreprocessorContext context, final Value[] values) {
    return context.getPreprocessorExtension().processUserFunction(name, values);
  }

  @Override
  public ValueType[][] getAllowedArgumentTypes() {
    return argTypes;
  }

  @Override
  public String getReference() {
    return "it's a user defined function";
  }

  @Override
  public ValueType getResultType() {
    return ValueType.ANY;
  }

}
