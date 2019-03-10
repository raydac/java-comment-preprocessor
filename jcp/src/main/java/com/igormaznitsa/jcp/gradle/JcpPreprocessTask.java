package com.igormaznitsa.jcp.gradle;

import com.igormaznitsa.jcp.JcpPreprocessor;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.exceptions.PreprocessorException;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.logger.PreprocessorLogger;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskExecutionException;

public class JcpPreprocessTask extends DefaultTask {

  public static final String ID = "preprocess";

  @TaskAction
  public void preprocessTask() throws IOException {
    final Logger logger = getProject().getLogger();

    JcpPreprocessExtension preprocessExtension = getProject().getExtensions().findByType(JcpPreprocessExtension.class);
    if (preprocessExtension == null) {
      preprocessExtension = new JcpPreprocessExtension(getProject());
    }
    preprocessExtension.validate(getProject());

    final PreprocessorContext preprocessorContext = new PreprocessorContext(getProject().getProjectDir());
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

    preprocessorContext.setTarget(preprocessExtension.getTarget());
    preprocessorContext.setSources(preprocessExtension.getSources());

    if (preprocessExtension.getEol() == null) {
      logger.debug("Using default EOL");
    } else {
      preprocessorContext.setEol(preprocessExtension.getEol());
    }

    if (preprocessExtension.getExcludeFolders() != null) {
      preprocessorContext.setExcludeFolders(preprocessExtension.getExcludeFolders());
    }

    preprocessorContext.setDontOverwriteSameContent(preprocessExtension.isDontOverwriteSameContent());
    preprocessorContext.setClearTarget(preprocessExtension.isClearTarget());
    preprocessorContext.setCareForLastEol(preprocessExtension.isCareForLastEol());
    preprocessorContext.setKeepComments(preprocessExtension.isKeepComments());
    preprocessorContext.setDryRun(preprocessExtension.isDryRun());
    preprocessorContext.setKeepAttributes(preprocessExtension.isKeepAttributes());
    preprocessorContext.setKeepLines(preprocessExtension.isKeepLines());
    preprocessorContext.setAllowWhitespaces(preprocessExtension.isAllowWhitespaces());

    if (preprocessExtension.getExcludeExtensions() != null) {
      logger.debug("Excluding extensions: " + preprocessExtension.getExcludeExtensions());
      preprocessorContext.setExcludeExtensions(preprocessExtension.getExcludeExtensions());
    }

    if (preprocessExtension.getExtensions() != null) {
      logger.debug("Extensions: " + preprocessExtension.getExtensions());
      preprocessorContext.setExtensions(preprocessExtension.getExtensions());
    }

    if (preprocessExtension.getVars() != null) {
      for (final Map.Entry<String, String> var : preprocessExtension.getVars().entrySet()) {
        logger.debug(String.format("Registering global variable: %s=%s", var.getKey(), var.getValue()));
        preprocessorContext.setGlobalVariable(var.getKey(), Value.recognizeRawString(var.getValue()));
      }
    }

    preprocessorContext.setPreserveIndents(preprocessExtension.isPreserveIndents());
    preprocessorContext.setSourceEncoding(Charset.forName(preprocessExtension.getSourceEncoding()));
    preprocessorContext.setTargetEncoding(Charset.forName(preprocessExtension.getTargetEncoding()));
    preprocessorContext.setUnknownVariableAsFalse(preprocessExtension.isUnknownVarAsFalse());
    preprocessorContext.setVerbose(preprocessExtension.isVerbose());

    final JcpPreprocessor preprocessor = new JcpPreprocessor(preprocessorContext);
    logger.debug("Start preprocessing...");

    try {
      preprocessor.execute();
    } catch (final PreprocessorException ex) {
      throw new TaskExecutionException(this, ex);
    }
  }
}
