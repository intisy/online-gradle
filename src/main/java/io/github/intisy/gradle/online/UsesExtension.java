package io.github.intisy.gradle.online;

import io.github.intisy.gradle.online.utils.FileUtils;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import java.io.File;
import java.util.List;

public class UsesExtension {
    private List<String> urls;

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public void processUrls(Logger logger, Project project) {
        if (urls != null && !urls.isEmpty()) {
            urls.forEach(url -> {
                logger.lifecycle("Applying gradle file from URL: " + url);
                File file = FileUtils.downloadFile(url);
                project.apply(spec -> spec.from(file));
            });
        } else {
            logger.lifecycle("No URLs provided.");
        }
    }
}