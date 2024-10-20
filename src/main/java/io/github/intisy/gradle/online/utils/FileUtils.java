package io.github.intisy.gradle.online.utils;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class FileUtils {
    /**
     * download file from url {@param fileURL} and return the file {@return file}
     */
    public static String generateUniqueString(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException var3) {
            throw new RuntimeException("SHA-256 algorithm not found", var3);
        }
    }
    public static File downloadFile(String fileURL, String fileExtension) {
        File folder = GradleUtils.getGradleHome().toFile();
        if (!folder.exists())
            folder.mkdirs();
        File jar = new File(folder, generateUniqueString(fileURL) + fileExtension);
        try (InputStream in = new BufferedInputStream(new URL(fileURL).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(jar)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            if (jar.exists())
                jar.delete();
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
            return jar;
        } catch (IOException e) {
            if (jar.exists()) {
                System.out.println("Could not get the file, using existing one");
                return jar;
            }
            e.printStackTrace();
        }
        throw new RuntimeException("Could not download file");
    }
}
