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
 * Main class.
 */
public class Main implements org.gradle.api.Plugin<Project> {
	public static Project project;

	/**
	 * Applies all the project stuff.
	 */
    public void apply(Project project) {
		Main.project = project;
		UsesExtension extension = project.getExtensions().create("online", UsesExtension.class);
		Logger logger = new Logger(extension, project);
		project.afterEvaluate(proj -> {
			processUrls(logger, proj, extension);
			processPresets(logger, proj, extension);
		});
		project.getTasks().create("listUrls", task -> {
			task.doLast(proj -> {
                List<String> strings = extension.getUrls();
				logger.log("All online files:");
                for (String string : strings) {
                    logger.log(string);
                }
            });
			task.setGroup("online");
			task.setDescription("Lists all used online gradle files.");
		});
    }
	public void processUrls(Logger logger, Project project, UsesExtension extension) {
		if (extension.getUrls() != null && !extension.getUrls().isEmpty()) {
			extension.getUrls().forEach(url -> {
				url = processUrl(url);
				File file = getUrlFile(url);
				boolean downloadFile = !file.exists();
				if (extension.isAutoUpdate()) {
					if (extension.getUpdateDelay() > 0) {
						try {
							BasicFileAttributes attributes = java.nio.file.Files.readAttributes(file.toPath(), BasicFileAttributes.class);
							Instant creationTime = attributes.creationTime().toInstant();
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
				if (downloadFile) {
					if (file.exists())
						logger.log("Updating " + url + " at " + file);
					else
						logger.log("Downloading " + url + " to " + file);
					downloadFile(logger, url, file);
				}
				project.apply(spec -> spec.from(file));
			});
		} else {
			logger.debug("No URLs provided.");
		}
	}

	private String processUrl(String url) {
		String[] split = url.split(":");
		if (split.length == 3) {
			String version = split[split.length-1];
			url = split[0] + ":" + split[1].replace(".gradle", "_") + version.replace(".", "_") + ".gradle";
		}
		return url;
	}

	public void processPresets(Logger logger, Project project, UsesExtension extension) {
		if (extension.getPresets() != null && !extension.getPresets().isEmpty()) {
			extension.getPresets().forEach(url -> {
				try {
					File preset = downloadFile(logger, url);
					List<String> lines = Files.readLines(preset, Charset.defaultCharset());
					for (String line : lines) {
						line = processUrl(line);
						File file = downloadFile(logger, line);
						project.apply(spec -> spec.from(file));
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
		}
	}

	public File getUrlFile(String url) {
		File folder = GradleUtils.getGradleHome().toFile();
		if (!folder.exists() && !folder.mkdirs())
			throw new RuntimeException("Failed to create directory: " + folder.getAbsolutePath());
		return new File(folder, FileUtils.generateUniqueString(url) + ".gradle");
	}

	public File downloadFile(Logger logger, String fileURL) {
		File file = getUrlFile(fileURL);
		downloadFile(logger, fileURL, file);
		return file;
	}

	public void downloadFile(Logger logger, String fileURL, File file) {
		if (file.exists() && !file.delete())
			throw new RuntimeException("Failed to delete file: " + file);
		try (InputStream in = new BufferedInputStream(new URL(fileURL).openStream());
			 FileOutputStream fileOutputStream = new FileOutputStream(file)) {
			byte[] dataBuffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
				fileOutputStream.write(dataBuffer, 0, bytesRead);
			}
			logger.log("Downloaded " + fileURL + " to " + file);
		} catch (IOException e) {
			if (file.exists()) {
				throw new RuntimeException("Could not get the file, using existing one");
			} else
				throw new RuntimeException("Could not download file");
		}
	}
}