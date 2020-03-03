package com.igormaznitsa.jcp.gradle;

import static com.igormaznitsa.meta.common.utils.GetUtils.ensureNonNull;


import com.igormaznitsa.jcp.JcpPreprocessor;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.exceptions.PreprocessorException;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.logger.PreprocessorLogger;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.commons.io.FilenameUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;

public class JcpPreprocessTask extends DefaultTask {

  public static final String ID = "preprocess";

  @Internal
  private JcpPreprocessExtension taskConfig = new JcpPreprocessExtension();

  /**
   * Source root folders for preprocessing, if it is empty then project provided
   * folders will be used.
   */
  @Input
  public void setSources(final List<String> values) {
    this.taskConfig.setSources(values);
  }

  /**
   * End of line string to be used in reprocessed results. It supports java
   * escaping chars.
   */
  @Input
  public void setEol(final String value) {
    this.taskConfig.setEol(value);
  }

  /**
   * Keep attributes for preprocessing file and copy them to result one.
   */
  @Input
  public void setKeepAttributes(final boolean value) {
    this.taskConfig.setKeepAttributes(value);
  }

  public boolean isKeepAttributes() {
    return this.taskConfig.isKeepAttributes();
  }

  /**
   * Target folder to place preprocessing result in regular source processing
   * phase.
   */
  @Input
  public void setTarget(final File value) {
    this.taskConfig.setTarget(value);
  }

  /**
   * Encoding for text read operations.
   */
  @Input
  public void setSourceEncoding(final String value) {
    this.taskConfig.setSourceEncoding(value);
  }

  /**
   * Encoding for text write operations.
   */
  @Input
  public void setTargetEncoding(final String value) {
    this.taskConfig.setTargetEncoding(value);
  }

  /**
   * Flag to ignore missing source folders, if false then mojo fail for any
   * missing source folder, if true then missing folder will be ignored.
   */
  @Input
  public void setIgnoreMissingSources(final boolean value) {
    this.taskConfig.setIgnoreMissingSources(value);
  }

  /**
   * List of file extensions to be excluded from preprocessing. By default
   * excluded xml.
   */
  @Input
  public void setExcludeExtensions(final List<String> value) {
    this.taskConfig.setExcludeExtensions(value);
  }

  /**
   * List of file extensions to be included into preprocessing. By default
   * java,txt,htm,html
   */
  @Input
  public void setExtensions(final List<String> value) {
    this.taskConfig.setExtensions(value);
  }

  /**
   * Interpretate unknown variable as containing boolean false flag.
   */
  @Input
  public void setUnknownVarAsFalse(final boolean value) {
    this.taskConfig.setUnknownVarAsFalse(value);
  }

  /**
   * Dry run, making pre-processing but without output
   */
  @Input
  public void setDryRun(final boolean value) {
    this.taskConfig.setDryRun(value);
  }

  /**
   * Verbose mode.
   */
  @Input
  public void setVerbose(final boolean value) {
    this.taskConfig.setVerbose(value);
  }

  /**
   * Clear target folder if it exists.
   */
  @Input
  public void setClearTarget(final boolean value) {
    this.taskConfig.setClearTarget(value);
  }

  /**
   * Set base directory which will be used for relative source paths.
   * By default it is '$projectDir'.
   */
  @Input
  public void setBaseDir(final File value) {
    this.taskConfig.setBaseDir(value);
  }

  /**
   * Carefully reproduce last EOL in result files.
   */
  @Input
  public void setCareForLastEol(final boolean value) {
    this.taskConfig.setCareForLastEol(value);
  }

  /**
   * Keep comments in result files.
   */
  @Input
  public void setKeepComments(final boolean value) {
    this.taskConfig.setKeepComments(value);
  }

  /**
   * List of variables to be registered in preprocessor as global ones.
   */
  @Input
  public void setVars(final Map<String, String> value) {
    this.taskConfig.setVars(value);
  }

  /**
   * List of patterns of folder paths to be excluded from preprocessing, It uses
   * ANT path pattern format.
   */
  @Input
  public void setExcludeFolders(final List<String> value) {
    this.taskConfig.setExcludeFolders(value);
  }

  /**
   * List of external files containing variable definitions.
   */
  @Input
  public void setConfigFiles(final List<String> value) {
    this.taskConfig.setConfigFiles(value);
  }

  /**
   * Keep preprocessing directives in result files as commented ones, it is
   * useful to not break line numeration in result files.
   */
  @Input
  public void setKeepLines(final boolean value) {
    this.taskConfig.setKeepLines(value);
  }

  /**
   * Turn on support of white spaces in preprocessor directives between '//' and
   * the '#'.
   */
  @Input
  public void setAllowWhitespaces(final boolean value) {
    this.taskConfig.setAllowWhitespaces(value);
  }

  /**
   * Preserve indents in lines marked by '//$' and '//$$' directives. Directives
   * will be replaced by white spaces chars.
   */
  @Input
  public void setPreserveIndents(final boolean value) {
    this.taskConfig.setPreserveIndents(value);
  }

  /**
   * Skip preprocessing. Also can be defined by property 'jcp.preprocess.skip'
   */
  @Input
  public void setSkip(final boolean value) {
    this.taskConfig.setSkip(value);
  }

  /**
   * Turn on check of content body compare with existing result file to prevent
   * overwriting, if content is the same then preprocessor will not be writing
   * new result content.
   */
  @Input
  public void setDontOverwriteSameContent(final boolean value) {
    this.taskConfig.setDontOverwriteSameContent(value);
  }

  @TaskAction
  public void preprocessTask() throws IOException {
    final Logger logger = getProject().getLogger();

    final JcpPreprocessExtension mergedConfig = new JcpPreprocessExtension(
        this.taskConfig,
        ensureNonNull(getProject().getExtensions().findByType(JcpPreprocessExtension.class), new JcpPreprocessExtension())
    );

    if (mergedConfig.getBaseDir() == null) {
      mergedConfig.setBaseDir(this.getProject().getProjectDir());
    }

    mergedConfig.validate();

    final File baseDir = mergedConfig.getBaseDir();

    final PreprocessorContext preprocessorContext = new PreprocessorContext(baseDir);
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

    if (mergedConfig.getConfigFiles() != null) {
      for (final String configFile : mergedConfig.getConfigFiles()) {
        final File cfgFile = new File(baseDir, configFile);
        if (cfgFile.isFile()) {
          logger.debug("Registering config file: " + cfgFile);
          preprocessorContext.registerConfigFile(cfgFile);
        } else {
          throw new TaskExecutionException(this, new IOException("Can't find config file: " + FilenameUtils.normalize(cfgFile.getAbsolutePath())));
        }
      }
    }

    preprocessorContext.setTarget(mergedConfig.getTarget());
    preprocessorContext.setSources(mergedConfig.getSources());

    if (mergedConfig.getEol() == null) {
      logger.debug("Using default EOL");
    } else {
      preprocessorContext.setEol(mergedConfig.getEol());
    }

    if (mergedConfig.getExcludeFolders() != null) {
      preprocessorContext.setExcludeFolders(mergedConfig.getExcludeFolders());
    }

    preprocessorContext.setDontOverwriteSameContent(mergedConfig.isDontOverwriteSameContent());
    preprocessorContext.setClearTarget(mergedConfig.isClearTarget());
    preprocessorContext.setCareForLastEol(mergedConfig.isCareForLastEol());
    preprocessorContext.setKeepComments(mergedConfig.isKeepComments());
    preprocessorContext.setDryRun(mergedConfig.isDryRun());
    preprocessorContext.setKeepAttributes(mergedConfig.isKeepAttributes());
    preprocessorContext.setKeepLines(mergedConfig.isKeepLines());
    preprocessorContext.setAllowWhitespaces(mergedConfig.isAllowWhitespaces());

    if (mergedConfig.getExcludeExtensions() != null) {
      logger.debug("Excluding extensions: " + mergedConfig.getExcludeExtensions());
      preprocessorContext.setExcludeExtensions(mergedConfig.getExcludeExtensions());
    }

    if (mergedConfig.getExtensions() != null) {
      logger.debug("Extensions: " + mergedConfig.getExtensions());
      preprocessorContext.setExtensions(mergedConfig.getExtensions());
    }

    if (mergedConfig.getVars() != null) {
      for (final Map.Entry<String, String> var : mergedConfig.getVars().entrySet()) {
        logger.debug(String.format("Registering global variable: %s=%s", var.getKey(), var.getValue()));
        preprocessorContext.setGlobalVariable(var.getKey(), Value.recognizeRawString(var.getValue()));
      }
    }

    preprocessorContext.setPreserveIndents(mergedConfig.isPreserveIndents());
    preprocessorContext.setSourceEncoding(Charset.forName(mergedConfig.getSourceEncoding()));
    preprocessorContext.setTargetEncoding(Charset.forName(mergedConfig.getTargetEncoding()));
    preprocessorContext.setUnknownVariableAsFalse(mergedConfig.isUnknownVarAsFalse());
    preprocessorContext.setVerbose(mergedConfig.isVerbose());

    final JcpPreprocessor preprocessor = new JcpPreprocessor(preprocessorContext);
    logger.debug("Start preprocessing...");

    try {
      preprocessor.execute();
    } catch (final PreprocessorException ex) {
      throw new TaskExecutionException(this, ex);
    }
  }
}
