package io.github.intisy.gradle.online;

import io.github.intisy.gradle.online.utils.FileUtils;
import io.github.intisy.gradle.online.utils.GradleUtils;
import org.gradle.api.Project;
import org.gradle.api.logging.LogLevel;
import org.gradle.internal.impldep.com.google.common.io.Files;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

@SuppressWarnings("unused")
public class UsesExtension {
    private List<String> urls;
    private List<String> presets;
    private boolean debug;

    public boolean getDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public List<String> getPresets() {
        return presets;
    }

    public void setPresets(List<String> presets) {
        this.presets = presets;
    }

    public void log(String message) {
        log(message, false);
    }
    public void log(String message, boolean debug) {
        LogLevel logLevel = Main.project.getGradle().getStartParameter().getLogLevel();
        if (this.debug || debug || logLevel.equals(LogLevel.INFO) || logLevel.equals(LogLevel.DEBUG))
            System.out.println(message);
    }

    public void processUrls(Project project) {
        if (urls != null && !urls.isEmpty()) {
            urls.forEach(url -> {
                url = processUrl(url);
                File file = downloadFile(url, ".gradle");
                project.apply(spec -> spec.from(file));
            });
        } else {
            log("No URLs provided.");
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

    public void processPresets(Project project) {
        if (presets != null && !presets.isEmpty()) {
            presets.forEach(url -> {
                System.out.println(1);
                File preset = downloadFile(url, ".preset");
                System.out.println(2);
                try {
                    List<String> lines = Files.readLines(preset, Charset.defaultCharset());
                    System.out.println(3);
                    for (String line : lines) {
                        line = processUrl(line);
                        File file = downloadFile(line, ".gradle");
                        project.apply(spec -> spec.from(file));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public File downloadFile(String fileURL, String fileExtension) {
        File folder = GradleUtils.getGradleHome().toFile();
        if (!folder.exists())
            folder.mkdirs();
        File file = new File(folder, FileUtils.generateUniqueString(fileURL) + fileExtension);
        boolean debug = !file.exists();
        try (InputStream in = new BufferedInputStream(new URL(fileURL).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            log("Downloading " + fileURL + " to " + file, debug);
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            if (file.exists())
                file.delete();
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
            if (debug || this.debug)
                printFileContent(file);
            log("Downloaded " + fileURL + " to " + file, debug);
            return file;
        } catch (IOException e) {
            if (file.exists()) {
                System.out.println("Could not get the file, using existing one");
                return file;
            }
            e.printStackTrace();
        }
        throw new RuntimeException("Could not download file");
    }

    public static void printFileContent(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println("An error occurred while reading the file: " + e.getMessage());
        }
    }
}