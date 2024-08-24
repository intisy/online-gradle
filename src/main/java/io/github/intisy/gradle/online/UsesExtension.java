package io.github.intisy.gradle.online;

import org.gradle.api.logging.Logger;
import org.gradle.api.model.ObjectFactory;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class UsesExtension {
    private List<String> urls;

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public void processUrls(Logger logger) {
        if (urls != null && !urls.isEmpty()) {
            urls.forEach(url -> logger.lifecycle("Processing URL: " + url));
        } else {
            logger.lifecycle("No URLs provided.");
        }
    }
}