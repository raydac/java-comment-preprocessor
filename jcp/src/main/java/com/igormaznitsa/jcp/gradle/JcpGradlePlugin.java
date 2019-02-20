package com.igormaznitsa.jcp.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class JcpGradlePlugin implements Plugin<Project> {
  public void apply(Project project) {
    project.getTasks().create("preprocess", JcpPreprocessTask.class);
    project.getExtensions().create("preprocessSettings", JcpPreprocessExtension.class, project);
  }
}
