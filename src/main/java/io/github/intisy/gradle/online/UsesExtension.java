package io.github.intisy.gradle.online;

import io.github.intisy.gradle.online.utils.FileUtils;
import org.gradle.api.Project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UsesExtension {
    private Project project;

    public UsesExtension() {
        this.project = null;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void uses(String scriptPath) {
        File file = FileUtils.downloadFile(scriptPath);
        project.apply(spec -> spec.from(file));
    }
}