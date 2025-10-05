/*
 * Copyright 2002-2019 Igor Maznitsa (http://www.igormaznitsa.com)
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.igormaznitsa.jcp.expression.functions;

import static com.igormaznitsa.jcp.expression.ExpressionTreeElement.ANY_ARITY;
import static java.util.Objects.requireNonNull;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.expression.ValueType;
import com.igormaznitsa.jcp.extension.PreprocessorExtension;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The class implements the user defined function handler (a function which name
 * starts with $)
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public final class FunctionDefinedByUser extends AbstractFunction {

  private static final List<ValueType> ARGS_0 = List.of();
  private static final List<ValueType> ARGS_1 = List.of(ValueType.ANY);
  private static final List<ValueType> ARGS_2 = List.of(ValueType.ANY, ValueType.ANY);
  private static final List<ValueType> ARGS_3 =
      List.of(ValueType.ANY, ValueType.ANY, ValueType.ANY);
  private static final List<ValueType> ARGS_4 =
      List.of(ValueType.ANY, ValueType.ANY, ValueType.ANY, ValueType.ANY);
  private static final List<ValueType> ARGS_5 =
      List.of(ValueType.ANY, ValueType.ANY, ValueType.ANY, ValueType.ANY, ValueType.ANY);

  private final String name;
  private final Set<Integer> allowedArities;
  private final List<List<ValueType>> argVariants;

  public FunctionDefinedByUser(final String name, final Set<Integer> allowedArities,
                               final PreprocessorContext context) {
    super();
    requireNonNull(name, "Name is null");
    requireNonNull(context, "Context is null");

    this.name = name;
    this.allowedArities = Set.copyOf(allowedArities);
    if (this.allowedArities.contains(ANY_ARITY)) {
      this.argVariants = List.of();
    } else {
      this.argVariants = new ArrayList<>();
      for (final int arity : this.allowedArities) {
        final List<ValueType> record;
        switch (arity) {
          case 0:
            record = ARGS_0;
            break;
          case 1:
            record = ARGS_1;
            break;
          case 2:
            record = ARGS_2;
            break;
          case 3:
            record = ARGS_3;
            break;
          case 4:
            record = ARGS_4;
            break;
          case 5:
            record = ARGS_5;
            break;
          default: {
            record = IntStream.of(arity).mapToObj(x -> ValueType.ANY).collect(Collectors.toList());
          }
          break;
        }
        this.argVariants.add(record);
      }
    }
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Set<Integer> getArity() {
    return this.allowedArities;
  }


  public Value execute(final PreprocessorContext context, final List<Value> values) {
    final List<PreprocessorExtension> extensionList = context.getPreprocessorExtensions();
    if (extensionList.isEmpty()) {
      throw context
          .makeException(
              "Found user defined function, but there is not any preprocessor extension to process it",
              null);
    }

    final Set<Integer> expectedArity = Set.of(values.size());

    final PreprocessorExtension extension =
        extensionList.stream().filter(x -> x.hasUserFunction(this.name, expectedArity))
            .findFirst().orElseThrow(() -> context
                .makeException(
                    "Can't find any preprocessor extension to process function " + this.name + " for " +
                        values.size() + " argument(s)", null));
    context.logDebug("Processing " + this.name + '/' + values.size() + " by " +
        extension.getClass().getCanonicalName());
    return extension.processUserFunction(context, name, values);
  }

  @Override
  public List<List<ValueType>> getAllowedArgumentTypes() {
    return this.argVariants;
  }

  @Override
  public String getReference() {
    return "user defined function";
  }

  @Override
  public ValueType getResultType() {
    return ValueType.ANY;
  }

}
