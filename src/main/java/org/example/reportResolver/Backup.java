package org.example.reportResolver;

import lombok.SneakyThrows;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Instant;

public class Backup {
    @SneakyThrows
    public static void copy(File in, File outDir) {
        if((!in.exists()) || (!in.isFile())){
            throw new RuntimeException("Input file is not exists or file: "+in);
        }
        if((! outDir.exists()) || (!outDir.isDirectory())){
            throw new RuntimeException("Output directory not exists or not directory: "+outDir);
        }
        final var now = Instant.now();
        final var newName = String.format("%s_%d", in.getName(), now.toEpochMilli());
        final var newFileName = outDir.toPath().resolve(newName).toFile();
        Files.copy(in.toPath(), newFileName.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

}
