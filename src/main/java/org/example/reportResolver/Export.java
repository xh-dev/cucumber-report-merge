package org.example.reportResolver;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.stream.Collectors;

public class Export {

    @SneakyThrows
    public static void export(File file, File outFile) {
        if ((!file.exists()) || (!file.isFile())) {
            throw new RuntimeException("Input file is not exists or file: " + file);
        }
        if (outFile.exists() && file.isDirectory()) {
            throw new RuntimeException("Output file is directory, which is not expected: " + outFile);
        }

        final var map = Merge.load(file);
//        final var map = new HashMap<Resolver.TestElement.Tag, Resolver.FormattedResult>();
        final var listOfEntry = map.entrySet().stream()
                .filter(it -> it.getKey().getName().startsWith("@UAT-TC"))
                .map(it -> it.getValue())
                .collect(Collectors.toList());

        final var sfd = new SimpleDateFormat("dd/MMM/YYYY HH:mm");
        try (
                final var os = new FileOutputStream(outFile)
        ) {
            for(Resolver.FormattedResult entry : listOfEntry){
                final var line = String.format("%s, %s, %s\n", entry.getTag().getName(), sfd.format(entry.getDate()), entry.getResult().name());
                os.write(line.getBytes());
            }
        }
    }
}
