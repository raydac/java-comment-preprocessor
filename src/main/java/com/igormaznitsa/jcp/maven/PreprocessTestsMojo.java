/*
 * Copyright 2017 Igor Maznitsa.
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
package com.igormaznitsa.jcp.maven;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * The Mojo is auxiliary wrapper over the standard preprocess mojo to automate providing of TRUE as the 'useTestSources' flag.
 * 
 * @since 6.1.1
 * @see PreprocessorMojo
 */
@Mojo(name = "preprocessTests", defaultPhase = LifecyclePhase.GENERATE_TEST_SOURCES, threadSafe = true, requiresProject = true)
public class PreprocessTestsMojo extends PreprocessorMojo {
  
  @Override
  public void setUseTestSources(final boolean flag) {
    super.setUseTestSources(true);
  }
  
  @Override
  public boolean getUseTestSources(){
    return true;
  }
}
