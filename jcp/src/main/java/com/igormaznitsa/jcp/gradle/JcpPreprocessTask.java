package com.igormaznitsa.jcp.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class JcpPreprocessTask extends DefaultTask {

  public JcpPreprocessTask() {
    super();
  }

  @TaskAction
  public final void doAction() {
    JcpExtension ext = getProject().getExtensions().findByType(JcpExtension.class);
    if (ext == null) {
      ext = new JcpExtension(getProject());
    }
  }

}
