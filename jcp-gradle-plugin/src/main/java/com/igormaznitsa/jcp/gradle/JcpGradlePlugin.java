package com.igormaznitsa.jcp.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class JcpGradlePlugin implements Plugin<Project>{

  @Override
  public void apply(final Project project) {
    project.getExtensions().create(JcpExtension.NAME, JcpExtension.class, project);
    project.getTasks().create(JcpPreprocessTask.NAME, JcpPreprocessTask.class);
  }
}