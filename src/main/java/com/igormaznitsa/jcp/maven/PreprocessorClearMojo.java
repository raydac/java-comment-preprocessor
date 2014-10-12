/*
 * Copyright 2014 Igor Maznitsa.
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

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.*;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * The Mojo allows to delete preprocessed folders.
 *
 * @author Igor Maznitsa (http://www.igormaznitsa.com)
 */
@Mojo(name = "clear", defaultPhase = LifecyclePhase.PACKAGE, threadSafe = true, requiresProject = true)
public class PreprocessorClearMojo extends AbstractMojo {

  /**
   * The Destination folder where generated sources can be placed in non-test
   * mode and which will be removed.
   */
  @Parameter(name = "destination", defaultValue = "${project.build.directory}/generated-sources/preprocessed")
  private File destination;

  /**
   * Destination folder where generated sources can be placed in test-mode and
   * which will be removed.
   */
  @Parameter(name = "testDestination", defaultValue = "${project.build.directory}/generated-test-sources/preprocessed")
  private File testDestination;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    final Log log = getLog();

    log.info("Cleaning preprocessing folders");
    if (this.destination != null) {
      final String path = destination.getAbsolutePath();
      log.info("Removing preprocessed source folder '" + path + '\'');
      if (this.destination.isDirectory()) {
        try {
          FileUtils.deleteDirectory(this.destination);
        }
        catch (IOException ex) {
          throw new MojoFailureException("Can't delete preprocessed source folder", ex);
        }
      }
      else {
        log.info("Preprocessed Source folder '" + path + "' doesn't exist");
      }
    }

    if (this.testDestination != null) {
      final String path = testDestination.getAbsolutePath();
      log.info("Removing preprocessed test source folder '" + path + '\'');
      if (this.testDestination.isDirectory()) {
        try {
          FileUtils.deleteDirectory(this.testDestination);
        }
        catch (IOException ex) {
          throw new MojoFailureException("Can't delete preprocessed test source folder", ex);
        }
      }
      else {
        log.info("Preprocessed Test Source folder '" + path + "' doesn't exist");
      }
    }
    log.info("Deleting of preprocessed folders has been completed");
  }
}
