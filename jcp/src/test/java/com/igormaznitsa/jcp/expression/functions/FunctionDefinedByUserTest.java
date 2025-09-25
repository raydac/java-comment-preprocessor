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

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import com.igormaznitsa.jcp.AbstractSpyPreprocessorContextTest;
import com.igormaznitsa.jcp.containers.FileInfoContainer;
import com.igormaznitsa.jcp.context.PreprocessingState;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Expression;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.extension.PreprocessorExtension;
import java.io.File;
import java.util.List;
import org.junit.Test;
import org.mockito.AdditionalMatchers;

public class FunctionDefinedByUserTest extends AbstractSpyPreprocessorContextTest {

  @Test
  public void testExecution_withArguments() throws Exception {
    final PreprocessorExtension mock = mock(PreprocessorExtension.class);
    when(mock.hasUserFunction(anyString(), anyInt())).thenReturn(true);
    when(mock.isAllowed(any(), any(), any(), any())).thenReturn(true);

    final PreprocessorContext context = preparePreprocessorContext(getCurrentTestFolder());

    final Value testResult = Value.valueOf("result");
    context.addPreprocessorExtension(mock);

    when(mock.processUserFunction(any(), eq("test"), any(Value[].class))).thenReturn(testResult);
    when(mock.getUserFunctionArity(eq("test"))).thenReturn(5);

    assertEquals(testResult, Expression.evalExpression("$test(1,2,3,4,5+6)", context));

    verify(mock).processUserFunction(any(), eq("test"), AdditionalMatchers.aryEq(new Value[] {
        Value.valueOf(1L),
        Value.valueOf(2L),
        Value.valueOf(3L),
        Value.valueOf(4L),
        Value.valueOf(11L)}));
  }

  @Test
  public void testExecution_withoutArguments() throws Exception {
    final PreprocessorExtension mock = mock(PreprocessorExtension.class);
    when(mock.hasUserFunction(anyString(), anyInt())).thenReturn(true);
    when(mock.isAllowed(any(), any(), any(), any())).thenReturn(true);

    final PreprocessorContext context = preparePreprocessorContext(getCurrentTestFolder());
    context.addAllPreprocessedResources(List.of(new FileInfoContainer(new File(PreprocessingState.FAKE_FILE_PATH), PreprocessingState.FAKE_FILE_PATH, false)));

    final Value testResult = Value.valueOf("result");
    context.addPreprocessorExtension(mock);

    when(mock.processUserFunction(any(), eq("test"), any(Value[].class))).thenReturn(testResult);
    when(mock.getUserFunctionArity(eq("test"))).thenReturn(0);

    assertEquals(testResult, Expression.evalExpression("$test()", context));

    verify(mock).processUserFunction(any(), eq("test"), AdditionalMatchers.aryEq(new Value[0]));
  }
}
