package com.igormaznitsa.jcp.gradle;

import com.igormaznitsa.jcp.JcpPreprocessor;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.exceptions.PreprocessorException;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.logger.PreprocessorLogger;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.commons.io.FilenameUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;
import org.gradle.execution.commandline.TaskConfigurationException;

public class JcpPreprocessTask extends DefaultTask {

  public static final String ID = "preprocess";

  /**
   * Source root folders for preprocessing, if it is empty then project provided
   * folders will be used.
   */
  @Input
  public final ListProperty<String> sources = this.getProject().getObjects().listProperty(String.class);
  /**
   * End of line string to be used in reprocessed results. It supports java
   * escaping chars.
   */
  @Input
  public final Property<String> eol = this.getProject().getObjects().property(String.class);
  /**
   * Keep attributes for preprocessing file and copy them to result one.
   */
  @Input
  public final Property<Boolean> keepAttributes = this.getProject().getObjects().property(Boolean.class);
  /**
   * Target folder to place preprocessing result in regular source processing
   * phase.
   */
  @Input
  public final Property<File> target = this.getProject().getObjects().property(File.class);
  /**
   * Encoding for text read operations.
   */
  @Input
  public final Property<String> sourceEncoding = this.getProject().getObjects().property(String.class);
  /**
   * Encoding for text write operations.
   */
  @Input
  public final Property<String> targetEncoding = this.getProject().getObjects().property(String.class);
  /**
   * Flag to ignore missing source folders, if false then mojo fail for any
   * missing source folder, if true then missing folder will be ignored.
   */
  @Input
  public final Property<Boolean> ignoreMissingSources = this.getProject().getObjects().property(Boolean.class);
  /**
   * List of file extensions to be excluded from preprocessing. By default
   * excluded xml.
   */
  @Input
  public final ListProperty<String> excludeExtensions = this.getProject().getObjects().listProperty(String.class);
  /**
   * List of file extensions to be included into preprocessing. By default
   * java,txt,htm,html
   */
  @Input
  public final ListProperty<String> extensions = this.getProject().getObjects().listProperty(String.class);
  /**
   * Interpretate unknown variable as containing boolean false flag.
   */
  @Input
  public final Property<Boolean> unknownVarAsFalse = this.getProject().getObjects().property(Boolean.class);
  /**
   * Dry run, making pre-processing but without output
   */
  @Input
  public final Property<Boolean> dryRun = this.getProject().getObjects().property(Boolean.class);
  /**
   * Verbose mode.
   */
  private final Property<Boolean> verbose = this.getProject().getObjects().property(Boolean.class);

  /**
   * Clear target folder if it exists.
   */
  @Input
  public final Property<Boolean> clearTarget = this.getProject().getObjects().property(Boolean.class);
  /**
   * Set base directory which will be used for relative source paths.
   * By default it is '$projectDir'.
   */
  @Input
  public final Property<File> baseDir = this.getProject().getObjects().property(File.class);
  /**
   * Carefully reproduce last EOL in result files.
   */
  @Input
  public final Property<Boolean> careForLastEol = this.getProject().getObjects().property(Boolean.class);
  /**
   * Keep comments in result files.
   */
  @Input
  public final Property<Boolean> keepComments = this.getProject().getObjects().property(Boolean.class);
  /**
   * List of variables to be registered in preprocessor as global ones.
   */
  @Input
  public final MapProperty<String, String> vars = this.getProject().getObjects().mapProperty(String.class, String.class);
  /**
   * List of patterns of folder paths to be excluded from preprocessing, It uses
   * ANT path pattern format.
   */
  @Input
  public final ListProperty<String> excludeFolders = this.getProject().getObjects().listProperty(String.class);
  /**
   * List of external files containing variable definitions.
   */
  @Input
  public final ListProperty<String> configFiles = this.getProject().getObjects().listProperty(String.class);
  /**
   * Keep preprocessing directives in result files as commented ones, it is
   * useful to not break line numeration in result files.
   */
  @Input
  public final Property<Boolean> keepLines = this.getProject().getObjects().property(Boolean.class);
  /**
   * Turn on support of white spaces in preprocessor directives between '//' and
   * the '#'.
   */
  @Input
  public final Property<Boolean> allowWhitespaces = this.getProject().getObjects().property(Boolean.class);
  /**
   * Preserve indents in lines marked by '//$' and '//$$' directives. Directives
   * will be replaced by white spaces chars.
   */
  @Input
  public final Property<Boolean> preserveIndents = this.getProject().getObjects().property(Boolean.class);
  /**
   * Skip preprocessing. Also can be defined by property 'jcp.preprocess.skip'
   */
  @Input
  public final Property<Boolean> skip = this.getProject().getObjects().property(Boolean.class);
  /**
   * Turn on check of content body compare with existing result file to prevent
   * overwriting, if content is the same then preprocessor will not be writing
   * new result content.
   */
  @Input
  public final Property<Boolean> dontOverwriteSameContent = this.getProject().getObjects().property(Boolean.class);

  @TaskAction
  public void preprocessTask() throws IOException {
    final Logger logger = getProject().getLogger();

    final JcpPreprocessExtension extension = this.getProject().getExtensions().findByType(JcpPreprocessExtension.class);

    final File baseDirFile;
    if (this.baseDir.isPresent()) {
      baseDirFile = this.baseDir.get();
    } else if (extension.baseDir.isPresent()) {
      baseDirFile = extension.baseDir.get();
    } else {
      baseDirFile = this.getProject().getProjectDir();
      logger.debug("Using project folder as base one: " + baseDirFile);
    }
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

    List<String> configFilesList = Collections.emptyList();
    if (this.configFiles.isPresent()) {
      configFilesList = this.configFiles.get();
    } else if (extension != null && extension.configFiles.isPresent()) {
      configFilesList = extension.configFiles.get();
    }
    configFilesList.forEach(x -> {
      final File cfgFile = new File(baseDirFile, x);
      if (cfgFile.isFile()) {
        logger.debug("Registering config file: " + cfgFile);
        preprocessorContext.registerConfigFile(cfgFile);
      } else {
        throw new TaskExecutionException(this, new IOException("Can't find config file: " + FilenameUtils.normalize(cfgFile.getAbsolutePath())));
      }
    });

    if (this.target.isPresent()) {
      preprocessorContext.setTarget(this.target.get());
    } else if (extension != null && extension.target.isPresent()) {
      preprocessorContext.setTarget(extension.target.get());
    } else {
      throw new TaskConfigurationException(JcpPreprocessTask.ID, "Target folder is not deined in 'target'", null);
    }


    final boolean ignoreMissingSourcesFlag;
    if (this.ignoreMissingSources.isPresent()) {
      ignoreMissingSourcesFlag = this.ignoreMissingSources.get();
    } else if (extension != null && extension.ignoreMissingSources.isPresent()) {
      ignoreMissingSourcesFlag = extension.ignoreMissingSources.get();
    } else {
      ignoreMissingSourcesFlag = false;
    }

    final List<String> sourcesList;
    if (this.sources.isPresent()) {
      sourcesList = this.sources.get();
    } else if (extension != null && extension.sources.isPresent()) {
      sourcesList = extension.sources.get();
    } else {
      sourcesList = null;
    }

    if (sourcesList == null || sourcesList.isEmpty()) {
      throw new TaskConfigurationException(JcpPreprocessTask.ID, "Source folder list is not defined or empty in 'sources'", null);
    }

    List<String> preparedSourcesList = new ArrayList<>();
    for (final String srcFolder : sourcesList) {
      final File srcFolderFile = new File(baseDirFile, srcFolder);
      if (!ignoreMissingSourcesFlag || srcFolderFile.isDirectory()) {
        preparedSourcesList.add(srcFolderFile.getAbsolutePath());
      }
      if (!srcFolderFile.isDirectory()) {
        logger.debug(String.format("Src.folder doesn't exist: %s", srcFolderFile));
      }
    }

    logger.debug("Soirce folders: " + preparedSourcesList);

    preprocessorContext.setSources(preparedSourcesList);

    if (this.eol.isPresent()) {
      preprocessorContext.setEol(this.eol.get());
    } else if (extension != null && extension.eol.isPresent()) {
      preprocessorContext.setEol(extension.eol.get());
    } else {
      logger.debug("Using default EOL");
    }

    if (this.excludeFolders.isPresent()) {
      preprocessorContext.setExcludeFolders(this.excludeFolders.get());
    } else if (extension != null && extension.excludeFolders.isPresent()) {
      preprocessorContext.setExcludeFolders(extension.excludeFolders.get());
    }

    if (this.dontOverwriteSameContent.isPresent()) {
      preprocessorContext.setDontOverwriteSameContent(this.dontOverwriteSameContent.get());
    } else if (extension != null && extension.dontOverwriteSameContent.isPresent()) {
      preprocessorContext.setDontOverwriteSameContent(extension.dontOverwriteSameContent.get());
    }

    if (this.clearTarget.isPresent()) {
      preprocessorContext.setClearTarget(this.clearTarget.get());
    } else if (extension != null && extension.clearTarget.isPresent()) {
      preprocessorContext.setClearTarget(extension.clearTarget.get());
    }

    if (this.careForLastEol.isPresent()) {
      preprocessorContext.setCareForLastEol(this.careForLastEol.get());
    } else if (extension != null && extension.careForLastEol.isPresent()) {
      preprocessorContext.setCareForLastEol(extension.careForLastEol.get());
    }

    if (this.keepComments.isPresent()) {
      preprocessorContext.setKeepComments(this.keepComments.get());
    } else if (extension != null && extension.keepComments.isPresent()) {
      preprocessorContext.setKeepComments(extension.keepComments.get());
    }

    if (this.dryRun.isPresent()) {
      preprocessorContext.setDryRun(this.dryRun.get());
    } else if (extension != null && extension.dryRun.isPresent()) {
      preprocessorContext.setDryRun(extension.dryRun.get());
    }

    if (this.keepAttributes.isPresent()) {
      preprocessorContext.setKeepAttributes(this.keepAttributes.get());
    } else if (extension != null && extension.keepAttributes.isPresent()) {
      preprocessorContext.setKeepAttributes(extension.keepAttributes.get());
    }

    if (this.keepLines.isPresent()) {
      preprocessorContext.setKeepLines(this.keepLines.get());
    } else if (extension != null && extension.keepLines.isPresent()) {
      preprocessorContext.setKeepLines(extension.keepLines.get());
    } else {
      preprocessorContext.setKeepLines(true);
    }

    if (this.allowWhitespaces.isPresent()) {
      preprocessorContext.setAllowWhitespaces(this.allowWhitespaces.get());
    } else if (extension != null && extension.allowWhitespaces.isPresent()) {
      preprocessorContext.setAllowWhitespaces(extension.allowWhitespaces.get());
    }

    List<String> excludeExtensionsList = null;
    if (this.excludeExtensions.isPresent()) {
      excludeExtensionsList = this.excludeExtensions.get();
    } else if (extension != null && extension.excludeExtensions.isPresent()) {
      excludeExtensionsList = this.excludeExtensions.get();
    }
    if (excludeExtensionsList != null) {
      logger.debug("Excluding extensions: " + excludeExtensionsList);
      preprocessorContext.setExcludeExtensions(excludeExtensionsList);
    }

    List<String> extensionsList = null;
    if (this.extensions.isPresent()) {
      extensionsList = this.extensions.get();
    } else if (extension != null && extension.extensions.isPresent()) {
      extensionsList = this.extensions.get();
    }
    if (extensionsList != null) {
      logger.debug("Extensions: " + extensionsList);
      preprocessorContext.setExtensions(extensionsList);
    }

    Map<String, String> mapVars = null;
    if (this.vars.isPresent()) {
      mapVars = this.vars.get();
    } else if (extension != null && extension.vars.isPresent()) {
      mapVars = extension.vars.get();
    }
    if (mapVars != null) {
      mapVars.forEach((key, value) -> {
        logger.debug(String.format("Registering global variable: %s=%s", key, value));
        preprocessorContext.setGlobalVariable(key, Value.recognizeRawString(value));
      });
    }

    if (this.preserveIndents.isPresent()) {
      preprocessorContext.setPreserveIndents(this.preserveIndents.get());
    } else if (extension != null && extension.preserveIndents.isPresent()) {
      preprocessorContext.setPreserveIndents(extension.preserveIndents.get());
    }

    if (this.sourceEncoding.isPresent()) {
      preprocessorContext.setSourceEncoding(Charset.forName(this.sourceEncoding.get()));
    } else if (extension != null && extension.sourceEncoding.isPresent()) {
      preprocessorContext.setSourceEncoding(Charset.forName(extension.sourceEncoding.get()));
    } else {
      preprocessorContext.setSourceEncoding(StandardCharsets.UTF_8);
    }

    if (this.targetEncoding.isPresent()) {
      preprocessorContext.setTargetEncoding(Charset.forName(this.targetEncoding.get()));
    } else if (extension != null && extension.targetEncoding.isPresent()) {
      preprocessorContext.setTargetEncoding(Charset.forName(extension.targetEncoding.get()));
    } else {
      preprocessorContext.setTargetEncoding(StandardCharsets.UTF_8);
    }

    if (this.unknownVarAsFalse.isPresent()) {
      preprocessorContext.setUnknownVariableAsFalse(this.unknownVarAsFalse.get());
    } else if (extension != null && extension.unknownVarAsFalse.isPresent()) {
      preprocessorContext.setUnknownVariableAsFalse(extension.unknownVarAsFalse.get());
    }

    if (this.verbose.isPresent()) {
      preprocessorContext.setVerbose(this.verbose.get());
    } else if (extension != null && extension.verbose.isPresent()) {
      preprocessorContext.setVerbose(extension.verbose.get());
    }

    final JcpPreprocessor preprocessor = new JcpPreprocessor(preprocessorContext);
    logger.debug("Start preprocessing...");

    try {
      preprocessor.execute();
    } catch (final PreprocessorException ex) {
      throw new TaskExecutionException(this, ex);
    }
  }
}
