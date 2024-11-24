package com.dtsw.collection.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author Wangwang Tang
 * @since 2024-09-04
 */
@Data
public class Metadata {

//    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSSSSS][.SS]'Z'");

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.registerModule(new JavaTimeModule());
    }

    private Info info;

    @JsonProperty("last_serial")
    private Integer lastSerial;

    private Map<String, List<Release>> releases;

    private List<Release> urls;

    private List<Vulnerability> vulnerabilities;

    @Data
    public static class Info {

        private String author;

        @JsonProperty("author_email")
        private String authorEmail;

        @JsonProperty("bugtrack_url")
        private String bugTrackUrl;

        private List<String> classifiers;

        private String description;

        @JsonProperty("description_content_type")
        private String descriptionContentType;

        @JsonProperty("docs_url")
        private String docsUrl;

        @JsonProperty("download_url")
        private String downloadUrl;

        private Downloads downloads;

        private JsonNode dynamic;

        @JsonProperty("home_page")
        private String homePage;

        private String keywords;

        private String license;

        private String maintainer;

        @JsonProperty("maintainer_email")
        private String maintainerEmail;

        private String name;

        @JsonProperty("package_url")
        private String packageUrl;

        private String platform;

        @JsonProperty("project_url")
        private String projectUrl;

        @JsonProperty("project_urls")
        private ProjectUrls projectUrls;

        @JsonProperty("provides_extra")
        private JsonNode providesExtra;

        @JsonProperty("release_url")
        private String releaseUrl;

        @JsonProperty("requires_dist")
        private List<String> requiresDist;

        @JsonProperty("requires_python")
        private String requiresPython;

        private String summary;

        private String version;

        private Boolean yanked;

        @JsonProperty("yanked_reason")
        private String yankedReason;
    }

    @Data
    public static class Downloads {

        @JsonProperty("last_month")
        private Integer lastMonth;

        @JsonProperty("last_week")
        private Integer lastWeek;

        @JsonProperty("last_day")
        private Integer lastDay;

    }

    @Data
    public static class ProjectUrls {

        @JsonProperty("Download")
        private String download;

        @JsonProperty("Homepage")
        private String homepage;

    }

    @Data
    public static class Release {

        @JsonProperty("comment_text")
        private String commentText;

        private Digests digests;

        private Integer downloads;

        private String filename;

        @JsonProperty("has_sig")
        private Boolean hasSig;

        @JsonProperty("md5_digest")
        private String md5Digest;

        @JsonProperty("packagetype")
        private String packageType;

        @JsonProperty("python_version")
        private String pythonVersion;

        @JsonProperty("requires_python")
        private String requiresPython;

        private Long size;

        @JsonProperty("upload_time")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime uploadTime;

        @JsonProperty("upload_time_iso_8601")
//        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSSSSS][.SSS]'Z'")
        private LocalDateTime uploadTimeIso8601;

        private String url;

        private Boolean yanked;

        @JsonProperty("yanked_reason")
        private String yankedReason;

    }

    @Data
    public static class Digests {

        @JsonProperty("blake2b_256")
        private String blake2b256;

        private String md5;

        private String sha256;

    }

    @Data
    public static class Vulnerability {

        private List<String> aliases;

        private String details;

        @JsonProperty("fixed_in")
        private List<String> fixedIn;

        private String id;

        private String link;

        private String source;

        private String summary;

        private JsonNode withdrawn;

    }
}
