package org.example.reportResolver;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Merge {
    @SneakyThrows
    private static void write(Map<Resolver.TestElement.Tag, Resolver.FormattedResult> data, File file){
        try (
                final var fos = new FileOutputStream(file);
        ) {
            final var mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            fos.write(mapper.writeValueAsBytes(data.values()));
        }
    }
    @SneakyThrows
    public static Map<Resolver.TestElement.Tag, Resolver.FormattedResult> load(File file){
        try (
                final var fis = new FileInputStream(file);
        ) {
            final var mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            final var map = new HashMap<Resolver.TestElement.Tag, Resolver.FormattedResult>();
            mapper.readValue(fis.readAllBytes(), new TypeReference<List<Resolver.FormattedResult>>() {
            }).forEach(it->{
                map.put(it.getTag(), it);
            });
            return map;
        }
    }

    @SneakyThrows
    private static void createEmptyFile(File file){
        try (
                final var fos = new FileOutputStream(file);
        ) {
            final var mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            fos.write("[]".getBytes());
        }
    }
    @SneakyThrows
    public static void merge(File source, File target, File outDir) {
        if (!source.exists()) {
            createEmptyFile(source);
        }
        if ((!source.exists()) || (!source.isFile())){
            throw new RuntimeException("Source file is not exists or not a file: "+source);
        }
        if ((!target.exists()) || (!target.isFile())){
            throw new RuntimeException("Target file is not exists or not a file: "+target);
        }
        if((!outDir.exists()) || (!outDir.isDirectory())){
            throw new RuntimeException("Out directory is not exists or not a direct: "+outDir);
        }

        final var sourceMap = load(source);
        final var targetData = Resolver.resolve(target);
        for(Resolver.FormattedResult data : targetData){
            sourceMap.put(data.getTag(), data);
        }
        write(sourceMap, source);
        final var copyResult = target.renameTo(outDir.toPath().resolve(target.getName()).toFile());
        System.out.println("Copy result: "+copyResult);
    }
}
