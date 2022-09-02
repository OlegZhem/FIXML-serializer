package com.oz.fixmlconv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FileHelper {

    private static final Logger LOG = LoggerFactory.getLogger(FileHelper.class);

    public static String readFile(String fileName) {
        String path = null;
        try {
            URL fileUrl = FileHelper.class.getResource("/" + fileName);
            assertNotNull(fileUrl, "File: " + fileName + " absent");
            path = Paths.get(fileUrl.toURI()).toString();
        } catch (URISyntaxException e) {
            LOG.error("Could not find path of " + fileName, e);
            return null;
        }

        try {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return new String(encoded, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOG.error("Could not load " + fileName + " from " + path, e);
            return null;
        }
    }

}
