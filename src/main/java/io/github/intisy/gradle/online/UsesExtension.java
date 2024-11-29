package io.github.intisy.gradle.online;

import io.github.intisy.gradle.online.utils.FileUtils;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.internal.impldep.com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

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

    public void processUrls(Project project) {
        if (urls != null && !urls.isEmpty()) {
            urls.forEach(url -> {
                url = processUrl(url);
                File file = FileUtils.downloadFile(url, ".gradle");
                project.apply(spec -> spec.from(file));
            });
        } else if (debug) {
            project.getLogger().lifecycle("No URLs provided.");
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
                try {
                    File preset = FileUtils.downloadFile(url, ".preset");
                    List<String> lines = Files.readLines(preset, Charset.defaultCharset());
                    for (String line : lines) {
                        line = processUrl(line);
                        File file = FileUtils.downloadFile(line, ".gradle");
                        project.apply(spec -> spec.from(file));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}