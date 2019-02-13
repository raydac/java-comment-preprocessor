package com.igormaznitsa.jcp.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class JcpGradlePlugin implements Plugin<Project> {

    void apply(Project project) {
        def extension = project.extensions.create('preprocessSettings', JcpPluginExtension, project)
        project.tasks.create('preprocess', JcpPreprocessTask) {
            message = extension.message
        }
    }

}