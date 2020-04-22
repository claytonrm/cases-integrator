package com.aurum.casesintegrator.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StreamUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUtil {

    private FileUtil() {
        throw new AssertionError("Util class should not be instantiated.");
    }


    public static String readFile(final String filePath) {
        final Resource resource = new PathMatchingResourcePatternResolver().getResource(filePath);

        try (InputStream inputStream = resource.getInputStream()) {
            return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Could not read file.", e);
        }
        return null;
    }

}
