package com.igormaznitsa.jcp.gradle;

import static java.util.Collections.emptyMap;
import static org.gradle.api.tasks.SourceSet.MAIN_SOURCE_SET_NAME;


import com.igormaznitsa.jcp.JcpPreprocessor;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.logger.PreprocessorLogger;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.apache.commons.io.FilenameUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;
import org.gradle.execution.commandline.TaskConfigurationException;

public class JcpPreprocessTask extends DefaultTask {

  public static final String ID = "preprocess";

  /**
   * Source root folders for preprocessing, if it is empty then project provided
   * folders will be used.
   */
  private final ListProperty<File> sources;
  /**
   * End of line string to be used in reprocessed results. It supports java
   * escaping chars.
   */
  private final Property<String> eol;
  /**
   * Keep attributes for preprocessing file and copy them to result one.
   */
  private final Property<Boolean> keepAttributes;
  /**
   * Target folder to place preprocessing result in regular source processing
   * phase.
   */
  private final Property<File> target;
  /**
   * Encoding for text read operations.
   */
  private final Property<String> sourceEncoding;
  /**
   * Encoding for text write operations.
   */
  private final Property<String> targetEncoding;
  /**
   * Flag to ignore missing source folders, if false then mojo fail for any
   * missing source folder, if true then missing folder will be ignored.
   */
  private final Property<Boolean> ignoreMissingSources;
  /**
   * List of file extensions to be excluded from preprocessing. By default
   * excluded xml.
   */
  private final ListProperty<String> excludeExtensions;
  /**
   * List of file extensions to be included into preprocessing. By default
   * java,txt,htm,html
   */
  private final ListProperty<String> fileExtensions;
  /**
   * Interpretate unknown variable as containing boolean false flag.
   */
  private final Property<Boolean> unknownVarAsFalse;
  /**
   * Dry run, making pre-processing but without output
   */
  private final Property<Boolean> dryRun;
  /**
   * Verbose mode.
   */
  private final Property<Boolean> verbose;
  /**
   * Clear target folder if it exists.
   */
  private final Property<Boolean> clearTarget;
  /**
   * Set base directory which will be used for relative source paths.
   * By default it is '$projectDir'.
   */
  private final Property<File> baseDir;
  /**
   * Carefully reproduce last EOL in result files.
   */
  private final Property<Boolean> careForLastEol;
  /**
   * Keep comments in result files.
   */
  private final Property<Boolean> keepComments;
  /**
   * List of variables to be registered in preprocessor as global ones.
   */
  private final MapProperty<String, String> vars;
  /**
   * List of patterns of folder paths to be excluded from preprocessing, It uses
   * ANT path pattern format.
   */
  private final ListProperty<String> excludeFolders;
  /**
   * List of external files containing variable definitions.
   */
  private final ListProperty<String> configFiles;
  /**
   * Keep preprocessing directives in result files as commented ones, it is
   * useful to not break line numeration in result files.
   */
  private final Property<Boolean> keepLines;
  /**
   * Turn on support of white spaces in preprocessor directives between '//' and
   * the '#'.
   */
  private final Property<Boolean> allowWhitespaces;
  /**
   * Preserve indents in lines marked by '//$' and '//$$' directives. Directives
   * will be replaced by white spaces chars.
   */
  private final Property<Boolean> preserveIndents;
  /**
   * Turn on check of content body compare with existing result file to prevent
   * overwriting, if content is the same then preprocessor will not be writing
   * new result content.
   */
  private final Property<Boolean> dontOverwriteSameContent;

  public JcpPreprocessTask() {
    super();
    final ObjectFactory factory = this.getProject().getObjects();

    this.allowWhitespaces = factory.property(Boolean.class).convention(false);
    this.careForLastEol = factory.property(Boolean.class).convention(false);
    this.clearTarget = factory.property(Boolean.class).convention(false);
    this.dontOverwriteSameContent = factory.property(Boolean.class).convention(false);
    this.dryRun = factory.property(Boolean.class).convention(false);
    this.ignoreMissingSources = factory.property(Boolean.class).convention(false);
    this.keepAttributes = factory.property(Boolean.class).convention(false);
    this.keepComments = factory.property(Boolean.class).convention(true);
    this.keepLines = factory.property(Boolean.class).convention(true);
    this.preserveIndents = factory.property(Boolean.class).convention(false);
    this.unknownVarAsFalse = factory.property(Boolean.class).convention(false);
    this.verbose = factory.property(Boolean.class).convention(false);

    this.targetEncoding = factory.property(String.class).convention(StandardCharsets.UTF_8.name());
    this.sourceEncoding = factory.property(String.class).convention(StandardCharsets.UTF_8.name());
    this.eol = factory.property(String.class).convention(System.lineSeparator());

    this.vars = factory.mapProperty(String.class, String.class);

    final JavaPluginConvention javaPluginConvention = this.getProject().getConvention().findPlugin(JavaPluginConvention.class);
    if (javaPluginConvention == null) {
      this.sources = factory.listProperty(File.class);
    } else {
      final SourceSetContainer srcSetContainer = javaPluginConvention.getSourceSets();
      srcSetContainer.getByName(MAIN_SOURCE_SET_NAME);
      this.sources = factory.listProperty(File.class).convention(
          srcSetContainer
              .stream()
              .filter(x -> MAIN_SOURCE_SET_NAME.equals(x.getName()))
              .flatMap(x -> x.getJava().getSrcDirs().stream())
              .collect(Collectors.toList())
      );
    }
    this.configFiles = factory.listProperty(String.class);
    this.excludeExtensions = factory.listProperty(String.class).convention(Collections.singletonList("xml"));
    this.excludeFolders = factory.listProperty(String.class);
    this.fileExtensions = factory.listProperty(String.class).convention(new ArrayList<>(Arrays.asList("java", "txt", "htm", "html")));

    this.baseDir = factory.property(File.class).convention(this.getProject().getProjectDir());
    this.target = factory.property(File.class).convention(new File(this.getProject().getBuildDir(), "java-comment-preprocessor" + File.separatorChar + this.getTaskIdentity().name));
  }

  @InputFiles
  public ListProperty<File> getSources() {
    return this.sources;
  }

  @Input
  public Property<String> getEol() {
    return this.eol;
  }

  @Input
  public Property<Boolean> getKeepAttributes() {
    return this.keepAttributes;
  }

  @Input
  @OutputFile
  public Property<File> getTarget() {
    return this.target;
  }

  @Input
  public Property<String> getSourceEncoding() {
    return this.sourceEncoding;
  }

  @Input
  public Property<String> getTargetEncoding() {
    return this.targetEncoding;
  }

  @Input
  public Property<Boolean> getIgnoreMissingSources() {
    return ignoreMissingSources;
  }

  @Input
  public ListProperty<String> getExcludeExtensions() {
    return excludeExtensions;
  }

  @Input
  public ListProperty<String> getFileExtensions() {
    return fileExtensions;
  }

  @Input
  public Property<Boolean> getUnknownVarAsFalse() {
    return unknownVarAsFalse;
  }

  @Input
  public Property<Boolean> getDryRun() {
    return dryRun;
  }

  @Input
  public Property<Boolean> getVerbose() {
    return verbose;
  }

  @Input
  public Property<Boolean> getClearTarget() {
    return clearTarget;
  }

  @Input
  public Property<File> getBaseDir() {
    return baseDir;
  }

  @Input
  public Property<Boolean> getCareForLastEol() {
    return careForLastEol;
  }

  @Input
  public Property<Boolean> getKeepComments() {
    return keepComments;
  }

  @Input
  public MapProperty<String, String> getVars() {
    return vars;
  }

  @Input
  public ListProperty<String> getExcludeFolders() {
    return excludeFolders;
  }

  @Input
  public ListProperty<String> getConfigFiles() {
    return configFiles;
  }

  @Input
  public Property<Boolean> getKeepLines() {
    return keepLines;
  }

  @Input
  public Property<Boolean> getAllowWhitespaces() {
    return allowWhitespaces;
  }

  @Input
  public Property<Boolean> getPreserveIndents() {
    return preserveIndents;
  }

  @Input
  public Property<Boolean> getDontOverwriteSameContent() {
    return dontOverwriteSameContent;
  }

  @TaskAction
  public void preprocessTask() throws IOException {
    final Logger logger = getProject().getLogger();

    final File baseDirFile;
    if (this.baseDir.isPresent()) {
      baseDirFile = this.baseDir.get();
    } else {
      baseDirFile = this.getProject().getProjectDir();
    }
    logger.info("Base folder: " + baseDirFile);
    final PreprocessorContext preprocessorContext = new PreprocessorContext(baseDirFile);

    preprocessorContext.setPreprocessorLogger(new PreprocessorLogger() {
      @Override
      public void error(@Nullable final String message) {
        logger.error(message);
      }

      @Override
      public void info(@Nullable final String message) {
        logger.info(message);
      }

      @Override
      public void debug(@Nullable final String message) {
        logger.debug(message);
      }

      @Override
      public void warning(@Nullable final String message) {
        logger.warn(message);
      }
    });

    List<String> configFilesList = this.configFiles.get();
    configFilesList.forEach(x -> {
      final File cfgFile = new File(baseDirFile, x);
      if (cfgFile.isFile()) {
        logger.debug("Adding config file: " + cfgFile);
        preprocessorContext.registerConfigFile(cfgFile);
      } else {
        throw new TaskExecutionException(this, new IOException("Can't find config file: " + FilenameUtils.normalize(cfgFile.getAbsolutePath())));
      }
    });

    logger.info("Target folder: " + this.target.get());
    preprocessorContext.setTarget(this.target.get());

    final List<File> sourcesList = this.sources.get();
    if (sourcesList.isEmpty()) {
      throw new TaskConfigurationException(JcpPreprocessTask.ID, "Source folder list must be defined as 'sources'", null);
    }

    List<File> preparedSourcesList = new ArrayList<>();
    for (final File srcFolder : sourcesList) {
      final File srcFolderFile = srcFolder.isAbsolute() ? srcFolder : new File(baseDirFile, srcFolder.getPath());
      if (!this.ignoreMissingSources.get() || srcFolderFile.isDirectory()) {
        preparedSourcesList.add(srcFolderFile);
      }
      if (!srcFolderFile.isDirectory()) {
        logger.debug(String.format("Src.folder doesn't exist: %s", srcFolderFile));
      }
    }

    logger.info("Sources: " + preparedSourcesList);

    preprocessorContext.setSources(preparedSourcesList.stream().map(File::getAbsolutePath).collect(Collectors.toList()));
    preprocessorContext.setEol(this.eol.get());
    preprocessorContext.setExcludeFolders(this.excludeFolders.get());
    preprocessorContext.setDontOverwriteSameContent(this.dontOverwriteSameContent.get());
    preprocessorContext.setClearTarget(this.clearTarget.get());
    preprocessorContext.setCareForLastEol(this.careForLastEol.get());
    preprocessorContext.setKeepComments(this.keepComments.get());
    preprocessorContext.setDryRun(this.dryRun.get());
    preprocessorContext.setKeepAttributes(this.keepAttributes.get());
    preprocessorContext.setKeepLines(this.keepLines.get());
    preprocessorContext.setAllowWhitespaces(this.allowWhitespaces.get());
    preprocessorContext.setExcludeExtensions(this.excludeExtensions.get());
    preprocessorContext.setExtensions(this.fileExtensions.get());
    preprocessorContext.setPreserveIndents(this.preserveIndents.get());
    preprocessorContext.setSourceEncoding(Charset.forName(this.sourceEncoding.get()));
    preprocessorContext.setTargetEncoding(Charset.forName(this.targetEncoding.get()));
    preprocessorContext.setUnknownVariableAsFalse(this.unknownVarAsFalse.get());
    preprocessorContext.setVerbose(this.verbose.get());

    this.vars.getOrElse(emptyMap()).forEach((key, value) -> {
      logger.debug(String.format("Registering global variable: %s=%s", key, value));
      preprocessorContext.setGlobalVariable(key, Value.recognizeRawString(value));
    });

    final JcpPreprocessor preprocessor = new JcpPreprocessor(preprocessorContext);
    logger.debug("Start preprocessing...");

    preprocessor.execute();
  }
}
