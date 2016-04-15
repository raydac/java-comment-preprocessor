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

import static org.mockito.Matchers.any;
import java.io.File;
import org.junit.runner.RunWith;
import static org.powermock.api.mockito.PowerMockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import com.igormaznitsa.jcp.containers.FileInfoContainer;
import com.igormaznitsa.jcp.context.PreprocessingState;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.exceptions.FilePositionInfo;
import com.igormaznitsa.jcp.exceptions.PreprocessorException;
import com.igormaznitsa.jcp.utils.ResetablePrinter;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PreprocessorContext.class, PreprocessingState.class})
public abstract class AbstractMockPreprocessorContextTest {

  protected PreprocessorContext preparePreprocessorContext() throws Exception {
    final PreprocessorContext preprocessorcontext = mock(PreprocessorContext.class);
    final PreprocessingState stateMock = mock(PreprocessingState.class);
    
    when(preprocessorcontext.makeException(any(String.class),any(Throwable.class))).thenAnswer(new Answer<PreprocessorException>() {
      @Override
      public PreprocessorException answer(final InvocationOnMock invocation) throws Throwable {
        return new PreprocessorException(invocation.getArgumentAt(0, String.class), "", new FilePositionInfo[0], invocation.getArgumentAt(1, Throwable.class));
      }
    });
    
    final FileInfoContainer container = new FileInfoContainer(new File("src/fake.java"), "fake.java", false);
    
    when(stateMock.getRootFileInfo()).thenReturn(container);

    final ResetablePrinter printer = new ResetablePrinter(10);
    when(stateMock.getPrinter()).thenReturn(printer);
    when(preprocessorcontext.getPreprocessingState()).thenReturn(stateMock);

    return preprocessorcontext;
  }


}
