package com.dtsw.collection.flow.java.collector;

import com.dtsw.collection.entity.OpenSourceSoftware;
import com.dtsw.collection.enumeration.FlowChannel;
import com.dtsw.collection.flow.java.collector.entity.JavaIndexRecord;
import com.dtsw.collection.flow.java.collector.mapper.JavaIndexRecordMapper;
import com.dtsw.collection.util.MessageUtils;
import com.dtsw.integration.endpoint.MessageProcessor;
import com.dtsw.util.MD5Encryptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.apache.maven.index.reader.Record;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.dtsw.collection.support.BackupPreparedStatementSetter.TASK_ID;
import static org.apache.maven.index.reader.Record.Type.ARTIFACT_ADD;
import static org.apache.maven.index.reader.Record.Type.ARTIFACT_REMOVE;

/**
 * 持久化 Index 数据，并构建开源软件数据
 *
 * @author Wangwang Tang
 * @since 2024-11-10
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class $3PersistIndexProcessor implements MessageProcessor<List<JavaIndexRecord>> {

    private final JavaIndexRecordMapper javaIndexRecordMapper;

    @Override
    public String from() {
        return FlowChannel.JAVA_COLLECTOR_PERSIST_INDEX.getChannel();
    }

    @Override
    public String to() {
        return FlowChannel.JAVA_COLLECTOR_PERSIST_SOFTWARE.getChannel();
    }

    @Override
    public Object process(List<JavaIndexRecord> payload, MessageHeaders headers) throws Exception {
        Object taskId = MessageUtils.getParameter(headers, TASK_ID);
        payload.forEach(r -> r.setTaskId(taskId.toString()));
        javaIndexRecordMapper.insert(payload);
        List<Record.Type> artifactTypes = List.of(ARTIFACT_ADD, ARTIFACT_REMOVE);
        if (payload.stream().map(JavaIndexRecord::getType).noneMatch(artifactTypes::contains)) {
            return null;
        }
        return buildOpenSourceSoftware(payload);
    }

    private Object buildOpenSourceSoftware(List<JavaIndexRecord> payload) {
        boolean remove = payload.stream().map(JavaIndexRecord::getType).anyMatch(ARTIFACT_REMOVE::equals);
        OpenSourceSoftware software = new OpenSourceSoftware();
        software.setName(getProperty(payload, JavaIndexRecord::getName));
        software.setDescription(getProperty(payload, JavaIndexRecord::getDescription));
        software.setGroupId(getProperty(payload, JavaIndexRecord::getGroupId));
        software.setArtifactId(getProperty(payload, JavaIndexRecord::getArtifactId));
        software.setVersion(getProperty(payload, JavaIndexRecord::getVersion));
        LocalDateTime fileModified = getProperty(payload, JavaIndexRecord::getFileModified);
        if (fileModified != null) {
            software.setTimeCreated(fileModified.toInstant(ZoneOffset.UTC).toEpochMilli());
        }
        LocalDateTime recordModified = getProperty(payload, JavaIndexRecord::getRecordModified);
        if (recordModified != null) {
            software.setTimeUpdated(recordModified.toInstant(ZoneOffset.UTC).toEpochMilli());
            software.setLastUpdated(recordModified.toInstant(ZoneOffset.UTC).toEpochMilli());
        }
        software.setProgrammingLanguage("JAVA");
        String location = Strings.join(List.of("Java", software.getGroupId(), software.getArtifactId(), software.getVersion()), ':');
        software.setId(MD5Encryptor.encrypt(location));
        software.setStatus(remove ? 2 : 1);
        return software;
    }

    private static  <T> T getProperty(List<JavaIndexRecord> records, Function<JavaIndexRecord, T> getter) {
        return records.stream().map(getter)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }
}
