package com.levy.collection.flow.java.collector;

import com.dtsw.collection.enumeration.FlowChannel;
import com.dtsw.collection.flow.java.collector.entity.JavaIndexRecord;
import com.dtsw.collection.util.HttpUtils;
import com.dtsw.integration.endpoint.MessageSplitter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.index.reader.ChunkReader;
import org.apache.maven.index.reader.RecordExpander;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.apache.maven.index.reader.Record.Type.ARTIFACT_ADD;
import static org.apache.maven.index.reader.Record.Type.ARTIFACT_REMOVE;

/**
 * 解析Index数据，按类型和 (GroupId:ArtifactId:Version) 分组
 *
 * @author Wangwang Tang
 * @since 2024-11-07
 */
@Slf4j
@Component
public class $2ParseIndexSplitter implements MessageSplitter<String> {
    @Override
    public String from() {
        return FlowChannel.JAVA_COLLECTOR_PARSE_INDEX.getChannel();
    }

    @Override
    public String to() {
        return FlowChannel.JAVA_COLLECTOR_PERSIST_INDEX.getChannel();
    }

    @Override
    public Collection<?> split(String url) throws Exception {
        String name = FilenameUtils.getName(url);
        final RecordExpander recordExpander = new RecordExpander();
        try (InputStream is = HttpUtils.get(url); ChunkReader reader = new ChunkReader(name, is)) {
            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(reader.iterator(), 0), false).parallel()
                    .map(recordExpander)
                    .map(JavaIndexRecord::new)
                    .collect(Collectors.groupingByConcurrent(JavaIndexRecord::getType))
                    .entrySet()
                    .parallelStream()
                    .map(entry -> {
                        if (List.of(ARTIFACT_ADD, ARTIFACT_REMOVE).contains(entry.getKey())) {
                            return entry.getValue().stream()
                                    .collect(Collectors.groupingByConcurrent(r ->
                                            String.join(":" + r.getGroupId(), r.getArtifactId(), r.getVersion()))
                                    ).values();
                        } else {
                            return List.of(entry.getValue());
                        }
                    })
                    .flatMap(Collection::stream)
                    .toList();
        }
    }
}
