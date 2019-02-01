/*
 * Copyright 2016 Igor Maznitsa.
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

package com.igormaznitsa.jcp;

import com.igormaznitsa.jcp.containers.FileInfoContainer;
import com.igormaznitsa.jcp.context.PreprocessingState;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.exceptions.FilePositionInfo;
import com.igormaznitsa.jcp.exceptions.PreprocessorException;
import com.igormaznitsa.jcp.utils.ResetablePrinter;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {PreprocessorContext.class, PreprocessingState.class})
public abstract class AbstractMockPreprocessorContextTest {

  protected PreprocessorContext prepareMockContext() throws Exception {
    final PreprocessorContext pcContext = mock(PreprocessorContext.class);
    final PreprocessingState pcState = mock(PreprocessingState.class);

    doReturn(new PreprocessorException("mock_msg","",new FilePositionInfo[0],null))
        .when(pcContext)
        .makeException(any(), any());

    final FileInfoContainer container = new FileInfoContainer(
        new File("src/fake.java"),
        "fake.java",
        false
    );

    when(pcState.getRootFileInfo()).thenReturn(container);

    final ResetablePrinter printer = new ResetablePrinter(10);
    when(pcState.getPrinter()).thenReturn(printer);
    when(pcContext.getPreprocessingState()).thenReturn(pcState);

    return pcContext;
  }


}
