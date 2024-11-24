package com.levy.collection.flow.java.collector;

import com.dtsw.collection.enumeration.FlowChannel;
import com.dtsw.collection.flow.java.collector.entity.JavaCollectorRecord;
import com.dtsw.collection.flow.java.collector.mapper.JavaCollectorRecordMapper;
import com.dtsw.collection.util.HttpUtils;
import com.dtsw.collection.util.MessageUtils;
import com.dtsw.integration.endpoint.MessageSplitter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.IntStream;

import static com.dtsw.collection.enumeration.FlowParameter.*;
import static com.dtsw.collection.support.BackupPreparedStatementSetter.TASK_ID;

/**
 * @author Wangwang Tang
 * @since 2024-11-06
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class $1CalculateIndexSplitter implements MessageSplitter<Object> {

    private static final String URI_DELIMITER = "/";
    private static final String PROPERTIES_NAME = "nexus-maven-repository-index.properties";
    private static final String GZ_NAME = "nexus-maven-repository-index.gz";
    private static final String GZ_INCREMENTAL_NAME = "nexus-maven-repository-index.%s.gz";
    private static final String ID = "nexus.index.id";
    private static final String LAST_INCREMENTAL = "nexus.index.last-incremental";
    private static final String INCREMENTAL_PREFIX = "nexus.index.incremental-";

    private final JavaCollectorRecordMapper javaCollectorRecordMapper;

    @Override
    public String from() {
        return FlowChannel.JAVA_COLLECTOR_GATEWAY.getChannel();
    }

    @Override
    public String to() {
        return FlowChannel.JAVA_COLLECTOR_PARSE_INDEX.getChannel();
    }

    @Override
    public Collection<?> split(MessageHeaders headers) throws Exception {
        Object taskId = MessageUtils.getParameter(headers, TASK_ID);
        String urlParameter = MessageUtils.getParameter(headers, JAVA_COLLECTOR_URL);
        String baseUrl = StringUtils.removeEnd(urlParameter, URI_DELIMITER) + URI_DELIMITER;
        String propertiesUrl = baseUrl + PROPERTIES_NAME;

        boolean isIncremental = MessageUtils.getParameter(headers, JAVA_COLLECTOR_INCREMENTAL);
        Integer firstIncremental = MessageUtils.getParameter(headers, JAVA_COLLECTOR_FIRST_INCREMENTAL);

        String id;
        int lastIncremental;
        int minIncremental;
        try (InputStream inputStream = HttpUtils.get(propertiesUrl)) {
            Properties properties = new Properties();
            properties.load(inputStream);
            id = properties.getProperty(ID);
            lastIncremental = Integer.parseInt(properties.getProperty(LAST_INCREMENTAL));
            minIncremental = lastIncremental;
            for (Object key : properties.keySet()) {
                if (key.toString().startsWith(INCREMENTAL_PREFIX)) {
                    minIncremental = Math.min(minIncremental, Integer.parseInt(properties.getProperty(key.toString())));
                }
            }
        }

        try {
            if (!isIncremental) {
                return List.of(baseUrl + GZ_NAME);
            } else {
                firstIncremental = Optional.ofNullable(firstIncremental)
                        .or(() -> Optional.ofNullable(javaCollectorRecordMapper.findMaxLastIncrementalByIndexId(id)).map(i -> ++i))
                        .orElse(minIncremental);

                return IntStream.rangeClosed(firstIncremental, lastIncremental)
                        .mapToObj(index -> baseUrl + String.format(GZ_INCREMENTAL_NAME, index))
                        .toList();
            }
        } finally {
            JavaCollectorRecord record = new JavaCollectorRecord();
            record.setTaskId(taskId.toString());
            record.setIndexId(id);
            record.setIncremental(isIncremental);
            record.setFirstIncremental(isIncremental ? firstIncremental : null);
            record.setLastIncremental(lastIncremental);
            javaCollectorRecordMapper.insert(record);
        }
    }
}
