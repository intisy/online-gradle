package io.github.intisy.gradle.online;

import org.gradle.api.Project;
import org.gradle.api.logging.LogLevel;

/**
 * @author Finn Birich
 */
@SuppressWarnings("unused")
public class Logger {
    private final UsesExtension extension;
    private final org.gradle.api.logging.Logger logger;
    private final Project project;
    /**
     * Constructs a new instance of Logger using the provided {@link Project} instance.
     *
     * @param project The {@link Project} instance associated with this Logger.
     *
     * @throws NullPointerException If the {@code project} is null.
     *
     * @see UsesExtension
     */
    public Logger(Project project) {
        this(project.getExtensions().getByType(UsesExtension.class), project);
    }
    /**
     * Constructs a new instance of Logger.
     *
     * @param extension The {@link UsesExtension} instance to be used for logging configuration.
     * @param project The {@link Project} instance associated with this Logger.
     *
     * @throws NullPointerException If either {@code extension} or {@code project} is null.
     */
    public Logger(UsesExtension extension, Project project) {
        if (extension == null || project == null) {
            throw new NullPointerException("extension and project cannot be null");
        }
        this.extension = extension;
        this.project = project;
        this.logger = project.getLogger();
    }

    /**
     * Logs a standard lifecycle message, visible in the default Gradle output.
     * @param message The message to log.
     */
    public void log(String message) {
        logger.lifecycle(message);
    }

    /**
     * Logs an error message.
     * @param message The message to log.
     */
    public void error(String message) {
        logger.error(message);
    }

    /**
     * Logs an error message along with an exception's stack trace.
     * @param message The message to log.
     * @param throwable The exception to log.
     */
    public void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }

    /**
     * Logs a debug message.
     * <p>
     * This message will be shown at the LIFECYCLE level (visible by default) only if
     * the user sets `github.debug = true` in their build script, providing an easy
     * way to enable verbose logging for this plugin specifically.
     * @param message The message to log.
     */
    public void debug(String message) {
        LogLevel logLevel;
        if (extension.isDebug() || project != null && ((logLevel = project.getGradle().getStartParameter().getLogLevel()).equals(LogLevel.INFO) || logLevel.equals(LogLevel.DEBUG))) {
            logger.lifecycle(message);
        }
    }

    /**
     * Logs a warning message.
     * @param message The message to log.
     */
    public void warn(String message) {
        logger.warn(message);
    }
}
