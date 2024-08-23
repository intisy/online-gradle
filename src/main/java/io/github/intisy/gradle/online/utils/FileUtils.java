package io.github.intisy.gradle.online.utils;

import java.io.*;
import java.net.URL;

public class FileUtils {
    /**
     * download file from url {@param fileURL} and return the file {@return file}
     */
    public static File downloadFile(String fileURL) {
        File jar = new File(GradleUtils.getGradleHome().toFile(), fileURL);
        if (jar.exists())
            jar.delete();
        try (InputStream in = new BufferedInputStream(new URL(fileURL).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(jar)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
            return jar;
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Could not download file");
    }
}
