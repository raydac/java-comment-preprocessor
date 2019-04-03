package com.igormaznitsa.jcp.gradle;

import lombok.Data;
import org.gradle.api.Project;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.gradle.execution.commandline.TaskConfigurationException;

@Data
public class JcpPreprocessExtension {

  public static final String ID = "preprocessSettings";

  /**
   * Source root folders for preprocessing, if it is empty then project provided
   * folders will be used.
   */
  private List<String> sources = null;

  /**
   * End of line string to be used in reprocessed results. It supports java
   * escaping chars.
   */
  private String eol = null;

  /**
   * Keep attributes for preprocessing file and copy them to result one.
   */
  private boolean keepAttributes = false;

  /**
   * Target folder to place preprocessing result in regular source processing
   * phase.
   */
  private File target = null;

  /**
   * Encoding for text read operations.
   */
  private String sourceEncoding = StandardCharsets.UTF_8.name();

  /**
   * Encoding for text write operations.
   */
  private String targetEncoding = StandardCharsets.UTF_8.name();

  /**
   * Flag to ignore missing source folders, if false then mojo fail for any
   * missing source folder, if true then missing folder will be ignored.
   */
  private boolean ignoreMissingSources = false;

  /**
   * List of file extensions to be excluded from preprocessing. By default
   * excluded xml.
   */
  private List<String> excludeExtensions = null;

  /**
   * List of file extensions to be included into preprocessing. By default
   * java,txt,htm,html
   */
  private List<String> extensions = null;

  /**
   * Interpretate unknown variable as containing boolean false flag.
   */
  private boolean unknownVarAsFalse = false;

  /**
   * Dry run, making pre-processing but without output
   */
  private boolean dryRun = false;

  /**
   * Verbose mode.
   */
  private boolean verbose = false;

  /**
   * Clear target folder if it exists.
   */
  private boolean clearTarget = false;

  /**
   * Set base directory which will be used for relative source paths.
   * By default it is '$projectDir'.
   */
  private File baseDir = null;

  /**
   * Carefully reproduce last EOL in result files.
   */
  private boolean careForLastEol = false;

  /**
   * Keep comments in result files.
   */
  private boolean keepComments = true;

  /**
   * List of variables to be registered in preprocessor as global ones.
   */
  private Map<String, String> vars = new HashMap<>();

  /**
   * List of patterns of folder paths to be excluded from preprocessing, It uses
   * ANT path pattern format.
   */
  private List<String> excludeFolders = new ArrayList<>();

  /**
   * List of external files containing variable definitions.
   */
  private List<String> configFiles = new ArrayList<>();

  /**
   * Keep preprocessing directives in result files as commented ones, it is
   * useful to not break line numeration in result files.
   */
  private boolean keepLines = true;

  /**
   * Turn on support of white spaces in preprocessor directives between '//' and
   * the '#'.
   */
  private boolean allowWhitespaces = false;

  /**
   * Preserve indents in lines marked by '//$' and '//$$' directives. Directives
   * will be replaced by white spaces chars.
   */
  private boolean preserveIndents = false;

  /**
   * Skip preprocessing. Also can be defined by property 'jcp.preprocess.skip'
   */
  private boolean skip = false;

  /**
   * Turn on check of content body compare with existing result file to prevent
   * overwriting, if content is the same then preprocessor will not be writing
   * new result content.
   */
  private boolean dontOverwriteSameContent = false;

  public JcpPreprocessExtension(final Project project) {
    if (this.baseDir == null) {
      this.baseDir = project.getProjectDir();
    }
  }

  private void assertCharSet(@Nullable final String name) {
    if (name == null || !Charset.isSupported(name)) {
      throw new TaskConfigurationException(JcpPreprocessTask.ID, "Unsupported charset: " + name, null);
    }
  }

  public void validate(final Project project) {
    if (this.baseDir == null) {
      throw new TaskConfigurationException(JcpPreprocessTask.ID, "Basedir must be defined", null);
    }

    if (!this.baseDir.isDirectory()) {
      throw new TaskConfigurationException(JcpPreprocessTask.ID, "Basedir doesn't exist: " + this.baseDir, null);
    }

    assertCharSet(this.sourceEncoding);
    assertCharSet(this.targetEncoding);

    if (this.sources == null) {
      throw new TaskConfigurationException(JcpPreprocessTask.ID, "Source folders are not deined in 'sources'", null);
    }

    if (this.sources.isEmpty()) {
      throw new TaskConfigurationException(JcpPreprocessTask.ID, "Source folder list is empty", null);
    }

    if (this.target == null) {
      throw new TaskConfigurationException(JcpPreprocessTask.ID, "Target folder is not deined in 'target'", null);
    }
  }

}
