package io.github.intisy.gradle.online;

import io.github.intisy.gradle.online.utils.FileUtils;
import org.gradle.api.Project;
import org.gradle.api.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Main class.
 */
public class Main implements org.gradle.api.Plugin<Project> {
	/**
	 * Applies all the project stuff.
	 */
    public void apply(Project project) {
		UsesExtension extension = project.getExtensions().create("uses", UsesExtension.class);
		project.afterEvaluate(p -> {
			Task myTask = p.getTasks().create("listUses", task -> {
				if (!extension.getMessages().isEmpty()) {
					System.out.println("All online files:");
					extension.getMessages().forEach(System.out::println);
				}
			});
			myTask.setGroup("online");
			myTask.setDescription("Lists all used online gradle files.");
		});
		extension.setCallback(scriptPath -> {
			File file = FileUtils.downloadFile(scriptPath);
			project.apply(spec -> spec.from(file));
		});
    }
}