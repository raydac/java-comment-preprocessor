package com.igormaznitsa.jcp.gradle;

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.provider.Property

class JcpPreprocessTask extends DefaultTask{
    final Property<String> message = project.objects.property(String)

    @TaskAction
    void doPreprocess() {
        logger.warn(message.get())
    }
}