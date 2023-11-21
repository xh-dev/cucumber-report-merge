package org.example.reportResolver;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Resolver {
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FormattedResult {
        private TestElement.Tag tag;
        private Date date;
        private TestElement.Status result;
    }

    @Data
    public static class TestResult {
        private String line;
        private List<TestElement> elements;
    }

    @Data
    public static class TestElement {
        public static class StatusSerializer extends StdSerializer<Status> {
            public StatusSerializer(){
                super(Status.class);
            }
            protected StatusSerializer(Class<Status> t) {
                super(t);
            }

            @Override
            public void serialize(Status value, JsonGenerator gen, SerializerProvider provider) throws IOException {
                gen.writeString(value.name());
            }
        }
        @JsonSerialize(using = StatusSerializer.class)
        @JsonFormat(shape = JsonFormat.Shape.OBJECT)
        public enum Status {
            passed, failed, skipped;

            @JsonValue()
            public String asJson(){
                return this.name();
            }

        }

        @Data
        public static class Result {
            public long duration;
            public Status status;
        }

        @Data
        public static class Before {
            private Result result;
        }

        @Data
        public static class After {
            private Result result;
        }

        @Data
        public static class Step {
            private Result result;
        }

        @Data
        public static class Tag {
            private String name;
        }

        @JsonProperty("start_timestamp")
        private String startTimestamp;
        private String name;
        private List<Before> before;
        private List<After> after;
        private List<Step> steps;
        private List<Tag> tags;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Before> getBefore() {
            return before;
        }

        public void setBefore(List<Before> before) {
            this.before = before;
        }

        public List<After> getAfter() {
            return after;
        }

        public void setAfter(List<After> after) {
            this.after = after;
        }

        public List<Step> getSteps() {
            return steps;
        }

        public void setSteps(List<Step> steps) {
            this.steps = steps;
        }

        public List<Tag> getTags() {
            return tags;
        }

        public void setTags(List<Tag> tags) {
            this.tags = tags;
        }
    }


    public static List<FormattedResult> resolve(File file) throws IOException {
        final var fileIn = new FileInputStream(file);
        final var mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        final var listData = mapper.readValue(fileIn.readAllBytes(), new TypeReference<List<TestResult>>() {
        });

        final var elementTag = "elements";

        final var sdf = new SimpleDateFormat(StdDateFormat.DATE_FORMAT_STR_ISO8601);
        final var getDate = (Function<String, Date>) (String dateStr) -> {
            try {
                return sdf.parse(dateStr);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        };
        final var result = listData.stream()
                .flatMap(it -> it.elements.stream())
                .flatMap(it -> {
                    final var date = getDate.apply(it.startTimestamp);
                    return it.getTags().stream().flatMap(tag -> {
                        return Stream.concat(
                                it.getBefore().stream().map(TestElement.Before::getResult),
                                Stream.concat(
                                        it.getSteps().stream().map(TestElement.Step::getResult),
                                        it.getAfter().stream().map(TestElement.After::getResult)
                                )
                        ).map(x -> {
                            return FormattedResult.builder().tag(tag).date(date).result(x.getStatus()).build();
                        });
                    });
                })
                .reduce(
                        new HashMap<TestElement.Tag, FormattedResult>(),
                        (map, entry) -> {
                            if (map.containsKey(entry.tag)) {
                                final var cur = map.get(entry.tag);
                                switch (cur.getResult()) {
                                    case failed:
                                        break;
                                    case skipped:
                                        if (entry.getResult() == TestElement.Status.failed)
                                            map.put(entry.getTag(), entry);
                                        break;
                                    case passed:
                                        if (entry.getResult() != TestElement.Status.passed)
                                            map.put(entry.tag, entry);
                                        break;
                                }
                            } else {
                                map.put(entry.tag, entry);
                            }
                            return map;
                        },
                        (map, entry) -> map
                )
                .values().stream().sorted(Comparator.comparing(a -> a.tag.name)).collect(Collectors.toList());
        ;
        return result;
//        result.forEach(System.out::println);
    }
}
