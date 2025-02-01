package io.github.intisy.gradle.online;

import io.github.intisy.gradle.online.utils.FileUtils;
import io.github.intisy.gradle.online.utils.GradleUtils;
import org.gradle.api.Project;
import org.gradle.internal.impldep.com.google.common.io.Files;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * @author Finn Birich
 */
@SuppressWarnings("unused")
public class Main implements org.gradle.api.Plugin<Project> {
    /**
     * Applies the plugin to the given Gradle project. This method sets up the necessary tasks and configurations
     * for processing online Gradle files and presets.
     *
     * @param project the Gradle project to which the plugin is applied. This project will be configured with
     *                tasks to list and process online Gradle files.
     */
    public void apply(Project project) {
        UsesExtension extension = project.getExtensions().create("online", UsesExtension.class);
        Logger logger = new Logger(extension, project);
        project.afterEvaluate(proj -> {
            processActions(logger, proj, extension);
            processPresets(logger, proj, extension);
        });
        project.getTasks().create("listUrls", task -> {
            task.doLast(proj -> {
                List<String> strings = extension.getActions();
                logger.log("All online files:");
                for (String string : strings) {
                    logger.log(string);
                }
            });
            task.setGroup("online");
            task.setDescription("Lists all used online gradle files.");
        });
        project.getTasks().create("process", task -> {
            task.doLast(proj -> {
                processActions(logger, project, extension);
                processPresets(logger, project, extension);
            });
            task.setGroup("online");
            task.setDescription("Download and process files");
        });
    }
    /**
     * Processes the URLs specified in the given extension and updates or downloads the corresponding files.
     *
     * @param logger    the logger used to log messages during the process
     * @param project   the Gradle project to which the plugin is applied
     * @param extension the extension containing the configuration, including the list of URLs to process
     */
    public void processActions(Logger logger, Project project, UsesExtension extension) {
        if (extension.getActions() != null && !extension.getActions().isEmpty()) {
            extension.getActions().forEach(url -> updateUrl(logger, url, project, extension));
        } else {
            logger.debug("No URLs provided.");
        }
    }
	
	/**
	 * Updates or downloads a file from the specified URL and applies it to the given Gradle project.
	 * The method checks if the file needs to be downloaded based on its existence and the update delay
	 * specified in the extension. If the file is outdated or does not exist, it downloads the file and
	 * applies it to the project.
	 *
	 * @param logger    the logger used to log messages during the process
	 * @param url       the URL of the file to be updated or downloaded
	 * @param project   the Gradle project to which the file is applied
	 * @param extension the extension containing the configuration, including auto-update settings and update delay
	 */
	private void updateUrl(Logger logger, String url, Project project, UsesExtension extension) {
		url = processActionUrl(url);
		File file = getUrlFile(url);
		if (shouldDownloadFile(file, extension, logger)) {
			downloadFile(logger, url, file);
		}
		project.apply(spec -> spec.from(file));
	}
    
    /**
     * Determines whether a file should be downloaded based on its existence, update delay, and auto-update settings.
     *
     * @param file       The file to be checked for download.
     * @param extension  The extension containing the configuration, including auto-update settings and update delay.
     * @param logger     The logger used to log messages during the process.
     * @return {@code true} if the file should be downloaded, {@code false} otherwise.
     *         If the file does not exist, it will be downloaded.
     *         If auto-update is enabled and the update delay is greater than 0,
     *         the file will be downloaded if the difference between its creation time and the current time is greater than or equal to the update delay.
     *         If auto-update is enabled and the update delay is 0, the file will always be downloaded.
     *         If auto-update is disabled, the file will always be downloaded.
     */
    public boolean shouldDownloadFile(File file, UsesExtension extension, Logger logger) {
        boolean downloadFile = !file.exists();
        if (extension.isAutoUpdate()) {
            if (extension.getUpdateDelay() > 0) {
                try {
                    BasicFileAttributes attributes = java.nio.file.Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                    Instant creationTime = attributes.lastModifiedTime().toInstant();
                    Instant now = Instant.now();
                    long differenceInSeconds = Duration.between(creationTime, now).getSeconds();
                    logger.debug("Difference in seconds between creation time and now: " + differenceInSeconds + " seconds");
                    downloadFile = differenceInSeconds >= extension.getUpdateDelay();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                downloadFile = true;
            }
        }
        return downloadFile;
    }

    /**
     * Processes the given URL by transforming it into a specific format if it matches a certain pattern.
     * If the URL contains three parts separated by colons, it modifies the second and third parts
     * to replace certain characters and returns the modified URL.
     *
     * @param url the original URL to be processed. It is expected to be in the format of "part1:part2:part3".
     * @return the processed URL. If the original URL contains three parts, the second part will have ".gradle"
     *         replaced with "_" and the third part will have "." replaced with "_", followed by ".gradle".
     *         If the URL does not match the expected pattern, it is returned unchanged.
     */
    private String processActionUrl(String url) {
        String[] split = url.split(":");
        if (split.length == 3) {
            String version = split[split.length-1];
            url = split[0] + ":" + split[1].replace(".gradle", "_") + version.replace(".", "_") + ".gradle";
        }
        return url;
    }

    /**
     * Processes the given URL by transforming it into a specific format if it matches a certain pattern.
     * If the URL contains three parts separated by colons, it modifies the second and third parts
     * to replace certain characters and returns the modified URL.
     *
     * @param url the original URL to be processed. It is expected to be in the format of "part1:part2:part3".
     * @return the processed URL. If the original URL contains three parts, the second part will have ".gradle"
     *         replaced with "_" and the third part will have "." replaced with "_", followed by ".gradle".
     *         If the URL does not match the expected pattern, it is returned unchanged.
     */
    private String processPresetUrl(String url) {
        String[] split = url.split(":");
        if (split.length == 3) {
            String version = split[split.length-1];
            url = split[0] + ":" + split[1].replace(".preset", "_") + version.replace(".", "_") + ".preset";
        }
        return url;
    }

    /**
     * Processes the presets specified in the given extension and updates or downloads the corresponding files.
     *
     * @param logger    the logger used to log messages during the process
     * @param project   the Gradle project to which the plugin is applied
     * @param extension the extension containing the configuration, including the list of presets to process
     *
     */
    public void processPresets(Logger logger, Project project, UsesExtension extension) {
        if (extension.getPresets() != null && !extension.getPresets().isEmpty()) {
            extension.getPresets().forEach(url -> processPreset(logger, project, extension, url));
        }
    }
    
        /**
     * Processes a preset file specified by the given URL. This method checks if the preset file needs to be
     * downloaded based on the configuration in the extension. If the file is downloaded, it reads the file
     * line by line and updates each URL found within the file.
     *
     * @param logger    The logger used to log messages during the process.
     * @param project   The Gradle project to which the preset is applied.
     * @param extension The extension containing the configuration, including auto-update settings and update delay.
     * @param url       The URL of the preset file to be processed.
     */
    public void processPreset(Logger logger, Project project, UsesExtension extension, String url) {
        try {
            File preset = getUrlFile(processPresetUrl(url));
            if (shouldDownloadFile(preset, extension, logger)) {
                downloadFile(logger, url, preset);
                List<String> lines = Files.readLines(preset, Charset.defaultCharset());
                for (String line : lines) {
                    if (line.contains(".gradle"))
                        updateUrl(logger, line, project, extension);
                    else if (line.contains(".preset"))
                        processPreset(logger, project, extension, line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates a File object representing a unique file path for a given URL.
     * This method creates a file in the Gradle home directory with a unique name
     * based on the provided URL.
     *
     * @param url The URL string for which to generate a unique file path.
     * @return A File object representing the unique file path for the given URL.
     * @throws RuntimeException If the Gradle home directory cannot be created.
     */
    public File getUrlFile(String url) {
        File folder = GradleUtils.getGradleHome().toFile();
        if (!folder.exists() && !folder.mkdirs())
            throw new RuntimeException("Failed to create directory: " + folder.getAbsolutePath());
        return new File(folder, FileUtils.generateUniqueString(url) + ".gradle");
    }

    /**
     * Downloads a file from the specified URL and saves it to a unique file location.
     *
     * @param logger  The Logger object used for logging messages during the download process.
     * @param fileURL The URL of the file to be downloaded.
     * @return A File object representing the downloaded file on the local file system.
     * @throws RuntimeException If there's an error during the download process.
     */
    public File downloadFile(Logger logger, String fileURL) {
        File file = getUrlFile(fileURL);
        downloadFile(logger, fileURL, file);
        return file;
    }

    /**
     * Downloads a file from a specified URL and saves it to a local file.
     * If the target file already exists, it attempts to delete it before downloading.
     * The method uses a buffered input stream to read the file content and writes it to the local file system.
     *
     * @param logger  The Logger object used for logging the download status.
     * @param url The URL of the file to be downloaded as a String.
     * @param file    The File object representing the local file where the downloaded content will be saved.
     * @throws RuntimeException If the existing file cannot be deleted, if there's an IO error during download,
     *                          or if the file cannot be downloaded and doesn't exist locally.
     */
    public void downloadFile(Logger logger, String url, File file) {
        boolean update = file.exists();
        if (update)
            logger.log("Updating " + url + " at " + file);
        else
            logger.log("Downloading " + url + " to " + file);
        if (file.exists() && !file.delete())
            throw new RuntimeException("Failed to delete file: " + file);
        try (InputStream in = new BufferedInputStream(new URL(url).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
            if (update)
                logger.debug("Updated " + url + " at " + file);
            else
                logger.debug("Downloaded " + url + " to " + file);
        } catch (IOException e) {
            if (file.exists()) {
                throw new RuntimeException("Could not get the file, using existing one");
            } else
                throw new RuntimeException("Could not download file");
        }
    }
}