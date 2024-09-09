package io.github.intisy.gradle.online;

import org.gradle.api.logging.Logger;
import org.gradle.api.Project;
import java.util.List;

/**
 * Main class.
 */
public class Main implements org.gradle.api.Plugin<Project> {
	/**
	 * Applies all the project stuff.
	 */
    public void apply(Project project) {
		UsesExtension extension = project.getExtensions().create("online", UsesExtension.class);
		project.afterEvaluate(proj -> {
			extension.processUrls(proj);
			extension.processPresets(proj);
		});
		project.getTasks().create("listUrls", task -> {
			task.doLast(proj -> {
				Logger logger = proj.getLogger();
                List<String> strings = extension.getUrls();
				logger.lifecycle("All online files:");
                for (String string : strings) {
                    logger.lifecycle(string);
                }
            });
			task.setGroup("online");
			task.setDescription("Lists all used online gradle files.");
		});
    }
}