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
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.*;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;

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
  @Parameter(name = "preprocessedSources", readonly = true, defaultValue = "${project.build.directory}/generated-sources/preprocessed")
  private File preprocessedSources;

  /**
   * Destination folder where generated sources can be placed in test-mode and
   * which will be removed.
   */
  @Parameter(name = "preprocessedTestSources", readonly = true, defaultValue = "${project.build.directory}/generated-test-sources/preprocessed")
  private File preprocessedTestSources;

  /**
   * List of folders and files to be removed, every folder defined as a FileSet and can contain exclude and include lists.
   * <pre>
   *  &lt;fileSets&gt;
   *    &lt;fileSet&gt;
   *      &lt;directory>${basedir}/someFolder&lt;/directory&gt;
   *      &lt;includes>
   *        &lt;include&gt;*.txt&lt;/include&gt;
   *      &lt;/includes&gt;
   *    &lt;/fileSet&gt;
   *  &lt;/fileSets&gt;
   * </pre>
   * @see <a href="http://maven.apache.org/shared/file-management/apidocs/org/apache/maven/shared/model/fileset/FileSet.html">FileSet javadoc</a>
   */
  @Parameter(name = "fileSets", required = false)
  private List<FileSet> fileSets;

  private void processPredefinedFolders(final Log log) throws MojoFailureException {
    if (this.preprocessedSources != null) {
      final String path = preprocessedSources.getAbsolutePath();
      log.info("Removing preprocessed source folder '" + path + '\'');
      if (this.preprocessedSources.isDirectory()) {
        try {
          FileUtils.deleteDirectory(this.preprocessedSources);
        }
        catch (IOException ex) {
          throw new MojoFailureException("Can't delete preprocessed source folder", ex);
        }
      }
      else {
        log.info("Preprocessed Source folder '" + path + "' doesn't exist");
      }
    }

    if (this.preprocessedTestSources != null) {
      final String path = preprocessedTestSources.getAbsolutePath();
      log.info("Removing preprocessed test source folder '" + path + '\'');
      if (this.preprocessedTestSources.isDirectory()) {
        try {
          FileUtils.deleteDirectory(this.preprocessedTestSources);
        }
        catch (IOException ex) {
          throw new MojoFailureException("Can't delete preprocessed test source folder", ex);
        }
      }
      else {
        log.info("Preprocessed Test Source folder '" + path + "' doesn't exist");
      }
    }
  }

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    final Log log = getLog();

    log.info("Cleaning has been started");
    if (this.fileSets == null) {
      processPredefinedFolders(log);
    }
    else {
      processFileSet(this.fileSets, log);
    }
    log.info("Cleaning has been completed");
  }

  private void processFileSet(final List<FileSet> fileSets, final Log log) throws MojoExecutionException {
    final FileSetManager manager = new FileSetManager(log,true);
    for (final FileSet fs : fileSets) {
      try {
        manager.delete(fs, true);
      }
      catch (IOException ex) {
        throw new MojoExecutionException("Exception during cleaning", ex);
      }
    }
  }
}
