package io.github.intisy.gradle.online;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A preset whose cache file is fresh must still have its snippets applied:
 * skipping the download may never skip applying the build logic.
 */
public class TestCachedPreset {

    @Test
    public void testCachedPresetStillApplies() throws Exception {
        String originalHome = System.getProperty("user.home");
        Path tempHome = Files.createTempDirectory("online-gradle-test-home");
        System.setProperty("user.home", tempHome.toString());
        try {
            Main main = new Main();
            String snippetUrl = "https://example.invalid/test-snippet.gradle";
            String presetUrl = "https://example.invalid/test.preset";

            Files.write(main.getUrlFile(snippetUrl).toPath(),
                    "version = '9.9.9-test'".getBytes(StandardCharsets.UTF_8));
            Files.write(main.getUrlFile(presetUrl).toPath(),
                    snippetUrl.getBytes(StandardCharsets.UTF_8));

            UsesExtension extension = new UsesExtension();
            extension.setAutoUpdate(true);
            extension.setUpdateDelay(3600);
            extension.setPresets(Collections.singletonList(presetUrl));

            Project project = ProjectBuilder.builder().withName("cached-preset").build();
            main.processPresets(new Logger(extension, project), project, extension);

            assertEquals("9.9.9-test", project.getVersion().toString());
        } finally {
            System.setProperty("user.home", originalHome);
        }
    }
}
