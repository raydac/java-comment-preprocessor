package com.igormaznitsa.jcp.cmdline;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.extension.PreprocessorExtension;
import com.igormaznitsa.jcp.utils.PreprocessorUtils;
import java.util.Locale;

public class ActionPreprocessorExtensionHandler implements CommandLineHandler {

  private static final String ARG_NAME = "/EA:";

  @Override
  public String getDescription() {
    return "class to be used for action directives, the class must be in the classpath and contain default constructor.";
  }

  @Override
  public boolean processCommandLineKey(final String key, final PreprocessorContext context) {
    boolean result = false;

    if (!key.isEmpty() && key.toUpperCase(Locale.ROOT).startsWith(ARG_NAME)) {
      final String tail = PreprocessorUtils.extractTrimmedTail(ARG_NAME, key);

      if (tail.isEmpty()) {
        result = true;
      } else {
        final PreprocessorExtension preprocessorExtension =
            PreprocessorUtils.findAndInstantiatePreprocessorExtensionForClassName(tail);
        context.addPreprocessorExtension(preprocessorExtension);
        result = true;
      }
    }
    return result;
  }

  @Override
  public String getKeyName() {
    return ARG_NAME;
  }
}
