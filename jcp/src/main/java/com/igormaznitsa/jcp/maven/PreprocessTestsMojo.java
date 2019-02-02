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

package com.igormaznitsa.jcp.maven;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * The Mojo is auxiliary wrapper over the standard preprocess mojo to automate providing of TRUE as the 'useTestSources' flag.
 *
 * @see PreprocessorMojo
 * @since 6.1.1
 */
@Mojo(name = "preprocessTests", defaultPhase = LifecyclePhase.GENERATE_TEST_SOURCES, threadSafe = true, requiresProject = true)
public class PreprocessTestsMojo extends PreprocessorMojo {

  @Override
  public boolean getUseTestSources() {
    return true;
  }

  @Override
  public void setUseTestSources(final boolean flag) {
    super.setUseTestSources(true);
  }
}
