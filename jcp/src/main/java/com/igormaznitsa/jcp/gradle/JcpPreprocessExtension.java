package com.igormaznitsa.jcp.gradle;

import java.io.File;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;

public class JcpPreprocessExtension {

  public static final String ID = "preprocessSettings";

  /**
   * Source root folders for preprocessing, if it is empty then project provided
   * folders will be used.
   */
  public final ListProperty<String> sources;
  /**
   * End of line string to be used in reprocessed results. It supports java
   * escaping chars.
   */
  public final Property<String> eol;
  /**
   * Keep attributes for preprocessing file and copy them to result one.
   */
  public final Property<Boolean> keepAttributes;
  /**
   * Target folder to place preprocessing result in regular source processing
   * phase.
   */
  public final Property<File> target;
  /**
   * Encoding for text read operations.
   */
  public final Property<String> sourceEncoding;
  /**
   * Encoding for text write operations.
   */
  public final Property<String> targetEncoding;
  /**
   * Flag to ignore missing source folders, if false then mojo fail for any
   * missing source folder, if true then missing folder will be ignored.
   */
  public final Property<Boolean> ignoreMissingSources;
  /**
   * List of file extensions to be excluded from preprocessing. By default
   * excluded xml.
   */
  public final ListProperty<String> excludeExtensions;
  /**
   * List of file extensions to be included into preprocessing. By default
   * java,txt,htm,html
   */
  public final ListProperty<String> extensions;
  /**
   * Interpretate unknown variable as containing boolean false flag.
   */
  public final Property<Boolean> unknownVarAsFalse;
  /**
   * Dry run, making pre-processing but without output
   */
  public final Property<Boolean> dryRun;
  /**
   * Verbose mode.
   */
  public final Property<Boolean> verbose;
  /**
   * Clear target folder if it exists.
   */
  public final Property<Boolean> clearTarget;
  /**
   * Set base directory which will be used for relative source paths.
   * By default it is '$projectDir'.
   */
  public final Property<File> baseDir;
  /**
   * Carefully reproduce last EOL in result files.
   */
  public final Property<Boolean> careForLastEol;
  /**
   * Keep comments in result files.
   */
  public final Property<Boolean> keepComments;
  /**
   * List of variables to be registered in preprocessor as global ones.
   */
  public final MapProperty<String, String> vars;
  /**
   * List of patterns of folder paths to be excluded from preprocessing, It uses
   * ANT path pattern format.
   */
  public final ListProperty<String> excludeFolders;
  /**
   * List of external files containing variable definitions.
   */
  public final ListProperty<String> configFiles;
  /**
   * Keep preprocessing directives in result files as commented ones, it is
   * useful to not break line numeration in result files.
   */
  public final Property<Boolean> keepLines;
  /**
   * Turn on support of white spaces in preprocessor directives between '//' and
   * the '#'.
   */
  public final Property<Boolean> allowWhitespaces;
  /**
   * Preserve indents in lines marked by '//$' and '//$$' directives. Directives
   * will be replaced by white spaces chars.
   */
  public final Property<Boolean> preserveIndents;
  /**
   * Skip preprocessing. Also can be defined by property 'jcp.preprocess.skip'
   */
  public final Property<Boolean> skip;
  /**
   * Turn on check of content body compare with existing result file to prevent
   * overwriting, if content is the same then preprocessor will not be writing
   * new result content.
   */
  public final Property<Boolean> dontOverwriteSameContent;

  @javax.inject.Inject
  public JcpPreprocessExtension(final ObjectFactory objectFactory) {
    this.eol = objectFactory.property(String.class);
    this.targetEncoding = objectFactory.property(String.class);
    this.sourceEncoding = objectFactory.property(String.class);

    this.sources = objectFactory.listProperty(String.class);
    this.extensions = objectFactory.listProperty(String.class);
    this.configFiles = objectFactory.listProperty(String.class);
    this.excludeFolders = objectFactory.listProperty(String.class);
    this.excludeExtensions = objectFactory.listProperty(String.class);

    this.vars = objectFactory.mapProperty(String.class, String.class);

    this.target = objectFactory.property(File.class);
    this.baseDir = objectFactory.property(File.class);

    this.keepComments = objectFactory.property(Boolean.class);
    this.clearTarget = objectFactory.property(Boolean.class);
    this.dryRun = objectFactory.property(Boolean.class);
    this.keepAttributes = objectFactory.property(Boolean.class);
    this.ignoreMissingSources = objectFactory.property(Boolean.class);
    this.unknownVarAsFalse = objectFactory.property(Boolean.class);
    this.keepLines = objectFactory.property(Boolean.class);
    this.allowWhitespaces = objectFactory.property(Boolean.class);
    this.verbose = objectFactory.property(Boolean.class);
    this.skip = objectFactory.property(Boolean.class);
    this.preserveIndents = objectFactory.property(Boolean.class);
    this.dontOverwriteSameContent = objectFactory.property(Boolean.class);
    this.careForLastEol = objectFactory.property(Boolean.class);
  }


}
