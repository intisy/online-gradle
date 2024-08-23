package io.github.intisy.gradle.online;

import io.github.intisy.gradle.online.utils.FileUtils;
import org.gradle.api.Project;

import java.io.File;

/**
 * Main class.
 */
class Main implements org.gradle.api.Plugin<Project> {
	/**
	 * Applies all the project stuff.
	 */
    public void apply(Project project) {
		project.getExtensions().getExtraProperties().set("uses", (ExternalScriptHandler) scriptPath -> {
			File file = FileUtils.downloadFile(scriptPath);
			project.apply(spec -> spec.from(file));
		});
    }

	@FunctionalInterface
	public interface ExternalScriptHandler {
		void apply(String scriptPath);
	}
}
