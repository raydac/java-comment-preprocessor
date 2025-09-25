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

package com.igormaznitsa.jcp;

import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import com.igormaznitsa.jcp.containers.FileInfoContainer;
import com.igormaznitsa.jcp.context.CommentRemoverType;
import com.igormaznitsa.jcp.context.PreprocessingState;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.exceptions.FilePositionInfo;
import com.igormaznitsa.jcp.exceptions.PreprocessorException;
import com.igormaznitsa.jcp.utils.ResettablePrinter;
import java.io.File;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PreprocessorContext.class, PreprocessingState.class})
public abstract class AbstractMockPreprocessorContextTest {

  protected PreprocessorContext prepareMockContext() throws Exception {
    final PreprocessorContext preparedContext = mock(PreprocessorContext.class);
    final PreprocessingState preparedState = mock(PreprocessingState.class);

    final AtomicReference<CommentRemoverType> keepComments = new AtomicReference<>(
        CommentRemoverType.REMOVE_C_STYLE);

    doThrow(new PreprocessorException("mock_msg", "", new FilePositionInfo[0], null))
        .when(preparedContext)
        .makeException(any(String.class), any());

    final FileInfoContainer container = new FileInfoContainer(
        new File("src/fake.java"),
        "fake.java",
        false
    );

    when(preparedContext.getKeepComments()).thenAnswer(
        invocationOnMock -> keepComments.get());

    doAnswer(invocationOnMock -> {
      keepComments.set((CommentRemoverType) invocationOnMock.getArguments()[0]);
      return null;
    }).when(preparedContext).setKeepComments(any(CommentRemoverType.class));

    when(preparedState.getRootFileInfo()).thenReturn(container);

    final ResettablePrinter printer = new ResettablePrinter(10);
    when(preparedState.getSelectedPrinter()).thenReturn(printer);
    when(preparedContext.getPreprocessingState()).thenReturn(preparedState);

    return preparedContext;
  }


}
