package com.igormaznitsa.jcp.gradle;

import com.igormaznitsa.meta.annotation.MustNotContainNull;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import lombok.Data;
import org.gradle.api.Project;
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

  @Nullable
  private static File merge(@Nullable final File prefered, @Nullable final File second) {
    return prefered == null ? second : prefered;
  }

  @Nullable
  private static String merge(@Nullable final String prefered, @Nullable final String second) {
    return prefered == null ? second : prefered;
  }

  @Nullable
  @MustNotContainNull
  private static Map<String, String> merge(
      @Nullable @MustNotContainNull final Map<String, String> preferred,
      @Nullable @MustNotContainNull final Map<String, String> second) {
    if (preferred == null && second == null) {
      return null;
    }
    final Map<String, String> result = new HashMap<>();
    if (preferred != null) {
      result.putAll(preferred);
    }
    if (second != null) {
      second.forEach(result::putIfAbsent);
    }
    return result;
  }

  @Nullable
  private static List<String> merge(
      @Nullable @MustNotContainNull final List<String> preferred,
      @Nullable @MustNotContainNull final List<String> second
  ) {
    if (preferred == null && second == null) {
      return null;
    }
    final List<String> result = new ArrayList<>();

    if (preferred != null) {
      result.addAll(preferred);
    }

    if (second != null) {
      second.stream().filter(x -> !result.contains(x)).forEach(result::add);
    }

    return result;
  }

  public JcpPreprocessExtension() {
    this.keepLines = false;
    this.keepComments = false;
  }

  public JcpPreprocessExtension(final JcpPreprocessExtension preferred, final JcpPreprocessExtension dflt) {
    this.allowWhitespaces = preferred.isAllowWhitespaces() || dflt.isAllowWhitespaces();
    this.careForLastEol = preferred.isCareForLastEol() || dflt.isCareForLastEol();
    this.clearTarget = preferred.isClearTarget() || dflt.isClearTarget();
    this.dontOverwriteSameContent = preferred.isDontOverwriteSameContent() || dflt.isDontOverwriteSameContent();
    this.dryRun = preferred.isDryRun() || dflt.isDryRun();
    this.ignoreMissingSources = preferred.isIgnoreMissingSources() || dflt.isIgnoreMissingSources();
    this.keepAttributes = preferred.isKeepAttributes() || dflt.isKeepAttributes();
    this.keepComments = preferred.isKeepComments() || dflt.isKeepComments();
    this.keepLines = preferred.isKeepLines() || dflt.isKeepLines();
    this.unknownVarAsFalse = preferred.isUnknownVarAsFalse() || dflt.isUnknownVarAsFalse();
    this.preserveIndents = preferred.isPreserveIndents() || dflt.isPreserveIndents();
    this.skip = preferred.isSkip() || dflt.isSkip();
    this.verbose = preferred.isVerbose() || dflt.isVerbose();
    this.configFiles = merge(preferred.getConfigFiles(), dflt.getConfigFiles());
    this.excludeExtensions = merge(preferred.getExcludeExtensions(), dflt.getExcludeExtensions());
    this.excludeFolders = merge(preferred.getExcludeFolders(), dflt.getExcludeFolders());
    this.extensions = merge(preferred.getExtensions(), dflt.getExtensions());
    this.sources = merge(preferred.getSources(), dflt.getSources());
    this.baseDir = merge(preferred.getBaseDir(), dflt.getBaseDir());
    this.eol = merge(preferred.getEol(), dflt.getEol());
    this.sourceEncoding = merge(preferred.getSourceEncoding(), dflt.getSourceEncoding());
    this.targetEncoding = merge(preferred.getTargetEncoding(), dflt.getTargetEncoding());
    this.vars = merge(preferred.getVars(), dflt.getVars());
    this.target = merge(preferred.getTarget(), dflt.getTarget());
  }

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

  public void validate() {
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
