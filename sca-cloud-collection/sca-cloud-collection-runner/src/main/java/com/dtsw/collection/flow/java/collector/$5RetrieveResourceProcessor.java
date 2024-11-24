package com.dtsw.collection.flow.java.collector;

import com.dtsw.collection.enumeration.FlowChannel;
import com.dtsw.collection.flow.java.collector.entity.JavaResource;
import com.dtsw.collection.flow.java.collector.mapper.JavaResourceMapper;
import com.dtsw.collection.util.HttpUtils;
import com.dtsw.collection.util.MessageUtils;
import com.dtsw.integration.endpoint.MessageProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.model.Dependency;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.dtsw.collection.support.BackupPreparedStatementSetter.TASK_ID;

/**
 * @author Wangwang Tang
 * @since 2024-11-10
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class $5RetrieveResourceProcessor implements MessageProcessor<Dependency> {

    private static final int RETRY = 10;
    private static final Duration RETRY_INTERVAL = Duration.ofMillis(200);
    private static final String URI_DELIMITER = "/";
    private static final String GROUP_DELIMITER = "\\.";
    private static final String MAVEN_URL = "https://repo1.maven.org";
    private static final List<String> START_NAMES = List.of("/", "maven2/");
    private static final String PRE_ELEMENT = "pre";
    private static final String HREF_ELEMENT = "href";
    private static final String PARENT_HREF = "../";
    private static final String SPLIT_REGEX = "\\s{2,}";
    private static final DateTimeFormatter PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String UNKNOWN = "-";

    private final JavaResourceMapper javaResourceMapper;

    @Override
    public String from() {
        return FlowChannel.JAVA_COLLECTOR_RETRIEVE_RESOURCE.getChannel();
    }

    @Override
    public String to() {
        return FlowChannel.JAVA_COLLECTOR_RESOLVE_POM.getChannel();
    }

    @Override
    public Object process(Dependency payload, MessageHeaders headers) throws Exception {
        Object taskId = MessageUtils.getParameter(headers, TASK_ID);

        String[] groupIdParts = payload.getGroupId().split(GROUP_DELIMITER);
        List<String> names = Stream.of(List.of(groupIdParts), List.of(payload.getArtifactId(), payload.getVersion()))
                .flatMap(Collection::stream)
                .map(n -> n + URI_DELIMITER)
                .toList();

        List<String> allNames = Stream.concat(START_NAMES.stream(), names.stream()).toList();
        loadResource(taskId.toString(), allNames);

        return payload;
    }

    private void loadResource(String taskId, List<String> names) throws IOException {
        StringBuilder urlPrefix = new StringBuilder(MAVEN_URL);
        StringBuilder fullNamePrefix = new StringBuilder();
        for (String name : names) {
            urlPrefix.append(name);
            fullNamePrefix.append(name);
            String response = HttpUtils.get(urlPrefix.toString(), HttpResponse.BodyHandlers.ofString(), RETRY, RETRY_INTERVAL);

            JavaResource parent = Optional.ofNullable(javaResourceMapper.findByFullName(fullNamePrefix.toString()))
                    .orElseGet(() -> {
                        JavaResource resource = new JavaResource();
                        resource.setTaskId(taskId);
                        resource.setName(name);
                        resource.setFullName(fullNamePrefix.toString());
                        resource.setUrl(urlPrefix.toString());
                        resource.setHasChild(name.endsWith(URI_DELIMITER));
                        resource.setCreatedTime(LocalDateTime.now());
                        resource.setUpdatedTime(LocalDateTime.now());
                        javaResourceMapper.insert(resource);
                        return resource;
                    });

            if (!parent.getHasChild()) {
                return;
            }

            Document document = Jsoup.parse(response);
            Element pre = document.selectFirst(PRE_ELEMENT);
            if (pre == null) {
                return;
            }
            List<Node> nodes = pre.childNodes();
            int child = (int) IntStream.range(0, nodes.size() / 2)
                    .mapToObj(i -> nodes.subList(i * 2, Math.min(i * 2 + 2, nodes.size())))
                    .filter(r -> r.size() == 2)
                    .map(resources -> {
                        String href = resources.get(0).attr(HREF_ELEMENT);
                        if (Objects.equals(href, PARENT_HREF) || !StringUtils.hasText(href)) {
                            return null;
                        }
                        String[] split = resources.get(1).outerHtml().trim().split(SPLIT_REGEX);
                        LocalDateTime timestamp = split.length > 0 ? resolveTimestamp(split[0]) : null;
                        Long size = split.length > 1 ? resolveSize(split[1]) : null;

                        String fullName = fullNamePrefix + href;
                        JavaResource resource = Optional.ofNullable(javaResourceMapper.findByFullName(fullName)).orElseGet(JavaResource::new);
                        if (resource.getTimestamp() != null && Objects.equals(resource.getTimestamp(), timestamp)) {
                            return resource;
                        }
                        resource.setTaskId(taskId);
                        resource.setName(href);
                        resource.setFullName(fullName);
                        resource.setParentId(parent.getId());
                        resource.setUrl(parent.getUrl() + href);
                        resource.setTimestamp(timestamp);
                        resource.setSize(size);
                        resource.setHasChild(href.endsWith(URI_DELIMITER));
                        resource.setUpdatedTime(LocalDateTime.now());
                        if (resource.getId() == null) {
                            resource.setCreatedTime(LocalDateTime.now());
                            javaResourceMapper.insert(resource);
                        } else {
                            javaResourceMapper.updateById(resource);
                        }
                        return resource;
                    })
                    .filter(Objects::nonNull)
                    .count();

            if (!Objects.equals(parent.getChild(), child)) {
                parent.setChild(child);
                parent.setTaskId(taskId);
                parent.setUpdatedTime(LocalDateTime.now());
                javaResourceMapper.updateById(parent);
            }
        }
    }

    private static LocalDateTime resolveTimestamp(String timestamp) {
        if (!StringUtils.hasText(timestamp) || Objects.equals(timestamp, UNKNOWN)) {
            return null;
        }
        try {
            return LocalDateTime.parse(timestamp, PATTERN);
        } catch (Exception e) {
            return null;
        }
    }

    private static Long resolveSize(String size) {
        if (!StringUtils.hasText(size) || Objects.equals(size, UNKNOWN)) {
            return null;
        }
        try {
            return Long.parseLong(size);
        } catch (Exception e) {
            return null;
        }
    }
}
