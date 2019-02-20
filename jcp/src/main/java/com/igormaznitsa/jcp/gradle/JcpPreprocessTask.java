package com.igormaznitsa.jcp.gradle;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.logger.PreprocessorLogger;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.Charset;

public class JcpPreprocessTask extends DefaultTask {

  @TaskAction
  public void preprocessTask() throws IOException {
    JcpPreprocessExtension preprocessExtension = getProject().getExtensions().findByType(JcpPreprocessExtension.class);
    if (preprocessExtension == null) {
      preprocessExtension = new JcpPreprocessExtension(getProject());
    }
    preprocessExtension.validate(getProject());

    final PreprocessorContext preprocessorContext = new PreprocessorContext(getProject().getProjectDir());
    preprocessorContext.setPreprocessorLogger(new PreprocessorLogger() {
      @Override
      public void error(@Nullable final String message) {
        getProject().getLogger().error(message);
      }

      @Override
      public void info(@Nullable final String message) {
        getProject().getLogger().info(message);
      }

      @Override
      public void debug(@Nullable final String message) {
        getProject().getLogger().debug(message);
      }

      @Override
      public void warning(@Nullable final String message) {
        getProject().getLogger().warn(message);
      }
    });

//    preprocessorContext.setTarget(preprocessExtension.getTarget());
    if (preprocessExtension.getEol() != null) {
      preprocessorContext.setEol(preprocessExtension.getEol());
    }
    preprocessorContext.setExcludeFolders(preprocessExtension.getExcludeFolders());
    preprocessorContext.setDontOverwriteSameContent(preprocessExtension.isDontOverwriteSameContent());
    preprocessorContext.setClearTarget(preprocessExtension.isClearTarget());
    preprocessorContext.setCareForLastEol(preprocessExtension.isCareForLastEol());
    preprocessorContext.setKeepComments(preprocessExtension.isKeepComments());
    preprocessorContext.setDryRun(preprocessExtension.isDryRun());
    preprocessorContext.setKeepAttributes(preprocessExtension.isKeepAttributes());
    preprocessorContext.setKeepLines(preprocessExtension.isKeepLines());
    preprocessorContext.setAllowWhitespaces(preprocessExtension.isAllowWhitespaces());
    if (preprocessExtension.getExcludeExtensions() != null) {
      preprocessorContext.setExcludeExtensions(preprocessExtension.getExcludeExtensions());
    }

    if (preprocessExtension.getExtensions() != null) {
      preprocessorContext.setExtensions(preprocessExtension.getExtensions());
    }

//    preprocessorContext.setGlobalVariable();

    preprocessorContext.setPreserveIndents(preprocessExtension.isPreserveIndents());
    preprocessorContext.setSourceEncoding(Charset.forName(preprocessExtension.getSourceEncoding()));
    preprocessorContext.setTargetEncoding(Charset.forName(preprocessExtension.getTargetEncoding()));
//    preprocessorContext.setSources(preprocessExtension.getSources());
    preprocessorContext.setUnknownVariableAsFalse(preprocessExtension.isUnknownVarAsFalse());
    preprocessorContext.setVerbose(preprocessExtension.isVerbose());


    //final JcpPreprocessor preprocessor = new JcpPreprocessor(preprocessorContext);
    //preprocessor.execute();
  }
}
