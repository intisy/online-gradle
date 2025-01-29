package io.github.intisy.gradle.online.utils;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Finn Birich
 */
public class GradleUtils {
    /**
     * Retrieves the path to the Gradle home directory, specifically the "online" cache directory.
     *
     * @return The path to the "online" cache directory within the Gradle home directory.
     */
    public static Path getGradleHome() {
        String userHome = System.getProperty("user.home");
        return Paths.get(userHome, ".gradle", "caches", "online");
    }
}
