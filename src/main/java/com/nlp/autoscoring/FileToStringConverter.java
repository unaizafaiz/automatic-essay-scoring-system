package com.nlp.autoscoring;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class FileToStringConverter {
    private static Charset charset = Charset.forName("UTF-8");
    public static String toStringConverter(File fileName) throws IOException {
        return Files.toString(fileName, charset);
    }
}
