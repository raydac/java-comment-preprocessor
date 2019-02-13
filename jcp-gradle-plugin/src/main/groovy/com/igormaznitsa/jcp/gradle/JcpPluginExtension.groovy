package com.igormaznitsa.jcp.gradle;

import org.gradle.api.provider.Property
import org.gradle.api.Project

class JcpPluginExtension {
    final Property<String> message

    JcpPluginExtension(Project project) {
        message = project.objects.property(String)
        message.set('Hello from JCP plugin')
    }
}