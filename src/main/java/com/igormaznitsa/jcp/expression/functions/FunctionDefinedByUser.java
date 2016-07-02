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

import javax.annotation.Nonnull;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.expression.ValueType;
import com.igormaznitsa.meta.annotation.MustNotContainNull;
import static com.igormaznitsa.meta.common.utils.Assertions.assertNotNull;
import static com.igormaznitsa.meta.common.utils.Assertions.assertNotNull;
import static com.igormaznitsa.meta.common.utils.Assertions.assertNotNull;
import static com.igormaznitsa.meta.common.utils.Assertions.assertNotNull;

/**
 * The class implements the user defined function handler (a function which name
 * starts with $)
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public final class FunctionDefinedByUser extends AbstractFunction {

  private final String name;
  private final int argsNumber;
  private final ValueType[][] argTypes;

  public FunctionDefinedByUser(@Nonnull final String name, final int argsNumber, @Nonnull final PreprocessorContext context) {
    super();
    assertNotNull("Name is null", name);
    assertNotNull("Context is null", context);

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
  @Nonnull
  public String getName() {
    return name;
  }

  @Override
  public int getArity() {
    return argsNumber;
  }

  @Nonnull
  public Value execute(@Nonnull final PreprocessorContext context, @Nonnull @MustNotContainNull final Value[] values) {
    return assertNotNull("Preprocessor extension must not be null", context.getPreprocessorExtension()).processUserFunction(name, values);
  }

  @Override
  @Nonnull
  @MustNotContainNull
  public ValueType[][] getAllowedArgumentTypes() {
    return argTypes;
  }

  @Override
  @Nonnull
  public String getReference() {
    return "it's a user defined function";
  }

  @Override
  @Nonnull
  public ValueType getResultType() {
    return ValueType.ANY;
  }

}
