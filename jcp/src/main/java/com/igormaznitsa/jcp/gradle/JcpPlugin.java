package com.igormaznitsa.jcp.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import javax.annotation.Nonnull;

public class JcpPlugin implements Plugin<Project> {

  @Override
  public void apply(@Nonnull final Project project) {
    project.getExtensions().create(JcpExtension.EXT_NAME, JcpExtension.class, project);
    project.getTasks().create("jcpPreprocess", JcpPreprocessTask.class);
  }
}