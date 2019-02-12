package com.igormaznitsa.jcp.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class JcpPreprocessTask extends DefaultTask {
  public static final String NAME = "preprocess";

  public JcpPreprocessTask(){
    super();
  }

  @TaskAction
  public void doAction() {

  }
}