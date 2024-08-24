package io.github.intisy.gradle.online;

import io.github.intisy.gradle.online.utils.FileUtils;
import org.gradle.api.Action;
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
		project.getExtensions().create("myPluginExtension", UsesExtension.class);

		project.getTasks().create("listUses", task -> {
			task.doLast(p -> {
                UsesExtension extension = project.getExtensions().getByType(UsesExtension.class);
                List<String> strings = extension.getStrings();
				System.out.println("All online files:");
                for (String string : strings) {
                    System.out.println(string);
                }
            });
			task.setGroup("online");
			task.setDescription("Lists all used online gradle files.");
		});
    }
}