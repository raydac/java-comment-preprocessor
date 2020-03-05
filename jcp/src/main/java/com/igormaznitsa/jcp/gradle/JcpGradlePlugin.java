package com.igormaznitsa.jcp.gradle;

import javax.annotation.Nonnull;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class JcpGradlePlugin implements Plugin<Project> {

  @Override
  public void apply(@Nonnull final Project project) {
    project.getTasks().create(JcpPreprocessTask.ID, JcpPreprocessTask.class);
  }
}
