package com.levy.collection.flow.java.collector;

import com.dtsw.collection.entity.OpenSourceSoftware;
import com.dtsw.collection.enumeration.FlowChannel;
import com.dtsw.collection.mapper.OpenSourceSoftwareMapper;
import com.dtsw.integration.endpoint.MessageProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Dependency;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author Wangwang Tang
 * @since 2024-11-10
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class $4PersistSoftwareProcessor implements MessageProcessor<OpenSourceSoftware> {

    private final OpenSourceSoftwareMapper openSourceSoftwareMapper;

    @Override
    public String from() {
        return FlowChannel.JAVA_COLLECTOR_PERSIST_SOFTWARE.getChannel();
    }

    @Override
    public String to() {
        return FlowChannel.JAVA_COLLECTOR_RETRIEVE_RESOURCE.getChannel();
    }

    @Override
    public Object process(OpenSourceSoftware payload) throws Exception {
        OpenSourceSoftware oldSoftware = openSourceSoftwareMapper.selectById(payload.getId());
        if (oldSoftware == null) {
            openSourceSoftwareMapper.insert(payload);
        } else {
            if (Objects.equals(payload.getStatus(), 2)) {
                oldSoftware.setStatus(payload.getStatus());
                oldSoftware.setLastUpdated(payload.getLastUpdated());
            }
            if (StringUtils.isNotBlank(payload.getName())) {
                oldSoftware.setName(payload.getName());
            }
            if (StringUtils.isNotBlank(payload.getDescription())) {
                oldSoftware.setDescription(payload.getDescription());
            }
            openSourceSoftwareMapper.updateById(oldSoftware);
        }
        Dependency dependency = new Dependency();
        dependency.setGroupId(payload.getGroupId());
        dependency.setArtifactId(payload.getArtifactId());
        dependency.setVersion(payload.getVersion());
        return dependency;
    }

}
