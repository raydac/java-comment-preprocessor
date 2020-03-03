package com.igormaznitsa.jcp.gradle;

import com.igormaznitsa.meta.annotation.MustNotContainNull;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.gradle.api.Project;
import org.gradle.execution.commandline.TaskConfigurationException;

public class JcpPreprocessExtension {

  public static final String ID = "preprocessSettings";
  private static final long SETTER_SOURCES = 1L;
  private static final long SETTER_EOL = 1L << 1;
  private static final long SETTER_KEEPATTRIBUTES = 1L << 2;
  private static final long SETTER_TARGET = 1L << 3;
  private static final long SETTER_SOURCEENCODING = 1L << 4;
  private static final long SETTER_TARGETENCODING = 1L << 5;
  private static final long SETTER_IGNOREMIDSINGSOURCES = 1L << 6;
  private static final long SETTER_EXCLUDEEXTENSIONS = 1L << 7;
  private static final long SETTER_EXTENSIONS = 1L << 8;
  private static final long SETTER_UNKNOWNVARASFALSE = 1L << 9;
  private static final long SETTER_DRYRUN = 1L << 10;
  private static final long SETTER_VERBOSE = 1L << 11;
  private static final long SETTER_CLEARTARGET = 1L << 12;
  private static final long SETTER_BASEDIR = 1L << 13;
  private static final long SETTER_CAREFORLASTEOL = 1L << 14;
  private static final long SETTER_KEEPCOMMENTS = 1L << 15;
  private static final long SETTER_VARS = 1L << 16;
  private static final long SETTER_EXCLUDEFOLDERS = 1L << 17;
  private static final long SETTER_CONFIGFILES = 1L << 18;
  private static final long SETTER_KEEPLINES = 1L << 19;
  private static final long SETTER_ALLOWWHITESPACES = 1L << 20;
  private static final long SETTER_PRESERVEINDENTS = 1L << 21;
  private static final long SETTER_SKIP = 1L << 22;
  private static final long SETTER_DONTOVERWRITESAMECONTENT = 1L << 23;
  private long calledSetters = 0L;

  private boolean isSetterCalled(final long setterId) {
    return (this.calledSetters & setterId) != 0;
  }

  private void onSetter(final long setterId) {
    this.calledSetters |= setterId;
  }

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

  public JcpPreprocessExtension() {
    this.keepLines = false;
    this.keepComments = false;
  }

  public JcpPreprocessExtension(final JcpPreprocessExtension preferred, final JcpPreprocessExtension dflt) {
    this.allowWhitespaces = merge(preferred.isAllowWhitespaces(), dflt.isAllowWhitespaces(), preferred, SETTER_ALLOWWHITESPACES);
    this.careForLastEol = merge(preferred.isCareForLastEol(), dflt.isCareForLastEol(), preferred, SETTER_CAREFORLASTEOL);
    this.clearTarget = merge(preferred.isClearTarget(), dflt.isClearTarget(), preferred, SETTER_CLEARTARGET);
    this.dontOverwriteSameContent = merge(preferred.isDontOverwriteSameContent(), dflt.isDontOverwriteSameContent(), preferred, SETTER_DONTOVERWRITESAMECONTENT);
    this.dryRun = merge(preferred.isDryRun(), dflt.isDryRun(), preferred, SETTER_DRYRUN);
    this.ignoreMissingSources = merge(preferred.isIgnoreMissingSources(), dflt.isIgnoreMissingSources(), preferred, SETTER_IGNOREMIDSINGSOURCES);
    this.keepAttributes = merge(preferred.isKeepAttributes(), dflt.isKeepAttributes(), preferred, SETTER_KEEPATTRIBUTES);
    this.keepComments = merge(preferred.isKeepComments(), dflt.isKeepComments(), preferred, SETTER_KEEPCOMMENTS);
    this.keepLines = merge(preferred.isKeepLines(), dflt.isKeepLines(), preferred, SETTER_KEEPLINES);
    this.unknownVarAsFalse = merge(preferred.isUnknownVarAsFalse(), dflt.isUnknownVarAsFalse(), preferred, SETTER_UNKNOWNVARASFALSE);
    this.preserveIndents = merge(preferred.isPreserveIndents(), dflt.isPreserveIndents(), preferred, SETTER_PRESERVEINDENTS);
    this.skip = merge(preferred.isSkip(), dflt.isSkip(), preferred, SETTER_SKIP);
    this.verbose = merge(preferred.isVerbose(), dflt.isVerbose(), preferred, SETTER_VERBOSE);
    this.configFiles = merge(preferred.getConfigFiles(), dflt.getConfigFiles(), preferred, SETTER_CONFIGFILES);
    this.excludeExtensions = merge(preferred.getExcludeExtensions(), dflt.getExcludeExtensions(), preferred, SETTER_EXCLUDEEXTENSIONS);
    this.excludeFolders = merge(preferred.getExcludeFolders(), dflt.getExcludeFolders(), preferred, SETTER_EXCLUDEFOLDERS);
    this.extensions = merge(preferred.getExtensions(), dflt.getExtensions(), preferred, SETTER_EXTENSIONS);
    this.sources = merge(preferred.getSources(), dflt.getSources(), preferred, SETTER_SOURCES);
    this.baseDir = merge(preferred.getBaseDir(), dflt.getBaseDir(), preferred, SETTER_BASEDIR);
    this.eol = merge(preferred.getEol(), dflt.getEol(), preferred, SETTER_EOL);
    this.sourceEncoding = merge(preferred.getSourceEncoding(), dflt.getSourceEncoding(), preferred, SETTER_SOURCEENCODING);
    this.targetEncoding = merge(preferred.getTargetEncoding(), dflt.getTargetEncoding(), preferred, SETTER_TARGETENCODING);
    this.vars = merge(preferred.getVars(), dflt.getVars(), preferred, SETTER_VARS);
    this.target = merge(preferred.getTarget(), dflt.getTarget(), preferred, SETTER_TARGET);
  }

  public JcpPreprocessExtension(final Project project) {
    if (this.baseDir == null) {
      this.baseDir = project.getProjectDir();
    }
  }

  @Nullable
  private static File merge(
      @Nullable final File preferredValue,
      @Nullable final File secondValue,
      @Nonnull final JcpPreprocessExtension preferred,
      final long setterId
  ) {
    if (preferred.isSetterCalled(setterId)) {
      return preferredValue;
    }
    return secondValue;
  }

  @Nullable
  private static String merge(
      @Nullable final String preferredValue,
      @Nullable final String secondValue,
      @Nonnull final JcpPreprocessExtension preferred,
      final long setterId
  ) {
    if (preferred.isSetterCalled(setterId)) {
      return preferredValue;
    }
    return secondValue;
  }

  @Nullable
  @MustNotContainNull
  private static Map<String, String> merge(
      @Nullable @MustNotContainNull final Map<String, String> preferredValue,
      @Nullable @MustNotContainNull final Map<String, String> secondValue,
      final JcpPreprocessExtension preferred,
      final long setterId) {
    if (preferredValue == null && secondValue == null) {
      return null;
    }

    if (preferred.isSetterCalled(setterId)) {
      return preferredValue;
    }
    return secondValue;
  }

  private static boolean merge(
      final boolean preferredValue,
      final boolean secondValue,
      final JcpPreprocessExtension preferred,
      final long setterId) {
    if (preferred.isSetterCalled(setterId)) {
      return preferredValue;
    }
    return secondValue;
  }

  @Nullable
  private static List<String> merge(
      @Nullable @MustNotContainNull final List<String> preferredValue,
      @Nullable @MustNotContainNull final List<String> secondValue,
      final JcpPreprocessExtension preferred,
      final long setterId) {
    if (preferredValue == null && secondValue == null) {
      return null;
    }

    if (preferred.isSetterCalled(setterId)) {
      return preferredValue;
    }
    return secondValue;
  }

  public List<String> getSources() {
    return sources;
  }

  public void setSources(List<String> sources) {
    this.sources = sources;
    onSetter(SETTER_SOURCES);
  }

  public String getEol() {
    return eol;
  }

  public void setEol(String eol) {
    this.eol = eol;
    onSetter(SETTER_EOL);
  }

  public boolean isKeepAttributes() {
    return keepAttributes;
  }

  public void setKeepAttributes(boolean keepAttributes) {
    this.keepAttributes = keepAttributes;
    onSetter(SETTER_KEEPATTRIBUTES);
  }

  public File getTarget() {
    return target;
  }

  public void setTarget(File target) {
    this.target = target;
    onSetter(SETTER_TARGET);
  }

  public String getSourceEncoding() {
    return sourceEncoding;
  }

  public void setSourceEncoding(String sourceEncoding) {
    this.sourceEncoding = sourceEncoding;
    onSetter(SETTER_SOURCEENCODING);
  }

  public String getTargetEncoding() {
    return targetEncoding;
  }

  public void setTargetEncoding(String targetEncoding) {
    this.targetEncoding = targetEncoding;
    onSetter(SETTER_TARGETENCODING);
  }

  public boolean isIgnoreMissingSources() {
    return ignoreMissingSources;
  }

  public void setIgnoreMissingSources(boolean ignoreMissingSources) {
    this.ignoreMissingSources = ignoreMissingSources;
    onSetter(SETTER_IGNOREMIDSINGSOURCES);
  }

  public List<String> getExcludeExtensions() {
    return excludeExtensions;
  }

  public void setExcludeExtensions(List<String> excludeExtensions) {
    this.excludeExtensions = excludeExtensions;
    onSetter(SETTER_EXCLUDEEXTENSIONS);
  }

  public List<String> getExtensions() {
    return extensions;
  }

  public void setExtensions(List<String> extensions) {
    this.extensions = extensions;
    onSetter(SETTER_EXTENSIONS);
  }

  public boolean isUnknownVarAsFalse() {
    return unknownVarAsFalse;
  }

  public void setUnknownVarAsFalse(boolean unknownVarAsFalse) {
    this.unknownVarAsFalse = unknownVarAsFalse;
    onSetter(SETTER_UNKNOWNVARASFALSE);
  }

  public boolean isDryRun() {
    return dryRun;
  }

  public void setDryRun(boolean dryRun) {
    this.dryRun = dryRun;
    onSetter(SETTER_DRYRUN);
  }

  public boolean isVerbose() {
    return verbose;
  }

  public void setVerbose(boolean verbose) {
    this.verbose = verbose;
    onSetter(SETTER_VERBOSE);
  }

  public boolean isClearTarget() {
    return clearTarget;
  }

  public void setClearTarget(boolean clearTarget) {
    this.clearTarget = clearTarget;
    onSetter(SETTER_CLEARTARGET);
  }

  public File getBaseDir() {
    return baseDir;
  }

  public void setBaseDir(File baseDir) {
    this.baseDir = baseDir;
    onSetter(SETTER_BASEDIR);
  }

  public boolean isCareForLastEol() {
    return careForLastEol;
  }

  public void setCareForLastEol(boolean careForLastEol) {
    this.careForLastEol = careForLastEol;
    onSetter(SETTER_CAREFORLASTEOL);
  }

  public boolean isKeepComments() {
    return keepComments;
  }

  public void setKeepComments(boolean keepComments) {
    this.keepComments = keepComments;
    onSetter(SETTER_KEEPCOMMENTS);
  }

  public Map<String, String> getVars() {
    return vars;
  }

  public void setVars(Map<String, String> vars) {
    this.vars = vars;
    onSetter(SETTER_VARS);
  }

  public List<String> getExcludeFolders() {
    return excludeFolders;
  }

  public void setExcludeFolders(List<String> excludeFolders) {
    this.excludeFolders = excludeFolders;
    onSetter(SETTER_EXCLUDEFOLDERS);
  }

  public List<String> getConfigFiles() {
    return configFiles;
  }

  public void setConfigFiles(List<String> configFiles) {
    this.configFiles = configFiles;
    onSetter(SETTER_CONFIGFILES);
  }

  public boolean isKeepLines() {
    return keepLines;
  }

  public void setKeepLines(boolean keepLines) {
    this.keepLines = keepLines;
    onSetter(SETTER_KEEPLINES);
  }

  public boolean isAllowWhitespaces() {
    return allowWhitespaces;
  }

  public void setAllowWhitespaces(boolean allowWhitespaces) {
    this.allowWhitespaces = allowWhitespaces;
    onSetter(SETTER_ALLOWWHITESPACES);
  }

  public boolean isPreserveIndents() {
    return preserveIndents;
  }

  public void setPreserveIndents(boolean preserveIndents) {
    this.preserveIndents = preserveIndents;
    onSetter(SETTER_PRESERVEINDENTS);
  }

  public boolean isSkip() {
    return skip;
  }

  public void setSkip(boolean skip) {
    this.skip = skip;
    onSetter(SETTER_SKIP);
  }

  public boolean isDontOverwriteSameContent() {
    return dontOverwriteSameContent;
  }

  public void setDontOverwriteSameContent(boolean dontOverwriteSameContent) {
    this.dontOverwriteSameContent = dontOverwriteSameContent;
    onSetter(SETTER_DONTOVERWRITESAMECONTENT);
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
