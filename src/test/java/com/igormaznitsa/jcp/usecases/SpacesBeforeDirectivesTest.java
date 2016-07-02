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
package com.igormaznitsa.jcp.usecases;

import static org.junit.Assert.assertEquals;
import javax.annotation.Nonnull;
import com.igormaznitsa.jcp.JCPreprocessor;
import com.igormaznitsa.jcp.context.PreprocessorContext;

public class SpacesBeforeDirectivesTest extends AbstractUseCaseTest {

  @Override
  protected void tuneContext(@Nonnull final PreprocessorContext context) {
    context.setAllowWhitespace(true);
  }

  @Override
  public void check(@Nonnull final PreprocessorContext context, @Nonnull final JCPreprocessor.PreprocessingStatistics stat) throws Exception {
    assertEquals(1, stat.getNumberOfPreprocessed());
    assertEquals(0, stat.getNumberOfCopied());
  }
  
}
