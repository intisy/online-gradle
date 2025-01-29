package io.github.intisy.gradle.online;

import org.gradle.api.Project;
import org.gradle.api.logging.LogLevel;

@SuppressWarnings("unused")
public class Logger {
    private final UsesExtension extension;
    private final Project project;
    public Logger(Project project) {
        this(project.getExtensions().getByType(UsesExtension.class), project);
    }
    public Logger(UsesExtension extension, Project project) {
        this.extension = extension;
        this.project = project;
    }
    public void log(String message) {
        project.getLogger().lifecycle(message);
    }

    public void error(String message) {
        project.getLogger().error(message);
    }

    public void debug(String message) {
        LogLevel logLevel = project.getGradle().getStartParameter().getLogLevel();
        if (extension.isDebug() || logLevel.equals(LogLevel.INFO) || logLevel.equals(LogLevel.DEBUG)) {
            project.getLogger().lifecycle(message);
        }
    }

    public void warn(String message) {
        project.getLogger().warn(message);
    }
}
