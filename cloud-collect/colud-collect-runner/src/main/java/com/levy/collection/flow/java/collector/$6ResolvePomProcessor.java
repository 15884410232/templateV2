package com.levy.collection.flow.java.collector;

import com.dtsw.collection.entity.OpenSourceSoftware;
import com.dtsw.collection.enumeration.FlowChannel;
import com.dtsw.collection.flow.java.collector.entity.JavaResource;
import com.dtsw.collection.flow.java.collector.mapper.JavaResourceMapper;
import com.dtsw.collection.mapper.OpenSourceSoftwareMapper;
import com.dtsw.collection.service.store.Storage;
import com.dtsw.collection.util.HttpUtils;
import com.dtsw.integration.endpoint.MessageProcessor;
import com.dtsw.util.MD5Encryptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.apache.maven.model.Contributor;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.License;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Wangwang Tang
 * @since 2024-11-10
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class $6ResolvePomProcessor implements MessageProcessor<Dependency> {

    private static final int RETRY = 10;
    private static final Duration RETRY_INTERVAL = Duration.ofMillis(200);
    private static final String URI_DELIMITER = "/";
    private static final String GROUP_DELIMITER = "\\.";
    private static final String FULL_NAME_PREFIX = "/maven2";
    private static final String JAVA_BUCKET = "java";

    private final Storage storage;
    private final JavaResourceMapper javaResourceMapper;
    private final OpenSourceSoftwareMapper openSourceSoftwareMapper;

    @Override
    public String from() {
        return FlowChannel.JAVA_COLLECTOR_RESOLVE_POM.getChannel();
    }

    @Override
    public Object process(Dependency payload) throws Exception {
        String filename = String.format("%s-%s.pom", payload.getArtifactId(), payload.getVersion());
        String[] groupIdParts = payload.getGroupId().split(GROUP_DELIMITER);
        String fullName = Stream.of(List.of(FULL_NAME_PREFIX), List.of(groupIdParts), List.of(payload.getArtifactId(), payload.getVersion(), filename))
                .flatMap(Collection::stream)
                .collect(Collectors.joining(URI_DELIMITER));
        JavaResource resource = javaResourceMapper.findByFullName(fullName);
        if (resource == null) {
            return null;
        }

        String objectName = StringUtils.removeStart(fullName, URI_DELIMITER);
        if (!storage.existObject(JAVA_BUCKET, objectName)) {
            String response = HttpUtils.get(resource.getUrl(), HttpResponse.BodyHandlers.ofString(), RETRY, RETRY_INTERVAL);
            storage.putObject(JAVA_BUCKET, objectName, new ByteArrayInputStream(response.getBytes()));
        }
        try (InputStream is = storage.getObject(JAVA_BUCKET, objectName)) {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(is);

            String location = Strings.join(List.of("Java", payload.getGroupId(), payload.getArtifactId(), payload.getVersion()), ':');
            OpenSourceSoftware software = openSourceSoftwareMapper.selectById(MD5Encryptor.encrypt(location));

            if (software == null) {
                return null;
            }

            // license
            Optional.ofNullable(model.getLicenses()).ifPresent(licenses -> {
                String license = licenses.stream().map(License::getName)
                        .collect(Collectors.joining(","));
                software.setLicense(license);
            });

            software.setHomepageUrl(model.getUrl());
            software.setDocumentationUrl(model.getUrl());

            // url
            Optional.ofNullable(model.getScm()).ifPresent(scm -> {
                software.setSourceCodeUrl(scm.getUrl());
                if (software.getHomepageUrl() == null) {
                    software.setHomepageUrl(scm.getUrl());
                }
                if (software.getDocumentationUrl() == null) {
                    software.setDocumentationUrl(scm.getUrl());
                }
                software.setRepositoryUrl(scm.getConnection());
                software.setTags(scm.getTag());
            });

            // author
            Optional.ofNullable(model.getDevelopers()).ifPresent(developers -> {
                String author = developers.stream().map(Contributor::getName)
                        .collect(Collectors.joining(","));
                software.setAuthor(author);
            });

            if (software.getName() == null) {
                software.setName(model.getName());
            }
            if (software.getDescription() == null) {
                software.setDescription(model.getDescription());
            }

            // contributors
            Optional.ofNullable(model.getContributors()).ifPresent(contributors -> {
                String contributor = contributors.stream().map(Contributor::getName)
                        .collect(Collectors.joining(","));
                software.setContributors(contributor);
            });

            Optional.ofNullable(model.getOrganization()).ifPresent(organization -> {
                software.setOrganization(organization.getName());
            });

            if (software.getPackaging() == null) {
                software.setPackaging(model.getPackaging());
            }

            if (software.getInceptionYear() == null) {
                software.setInceptionYear(model.getInceptionYear());
            }

            Optional.ofNullable(model.getParent()).ifPresent(parent -> {
                String parentLocation = Strings.join(List.of("Java", parent.getGroupId(), parent.getArtifactId(), parent.getVersion()), ':');
                software.setParentId(MD5Encryptor.encrypt(parentLocation));
            });

            openSourceSoftwareMapper.updateById(software);
        }

        return null;
    }
}
