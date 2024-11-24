package com.dtsw.mybatis;

import com.dtsw.collect.service.SourceService;
import com.dtsw.collection.entity.Source;
import com.dtsw.collection.enumeration.SourceType;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SourceTest {

    @Resource
    private SourceService sourceService;

    @Test
    public void insertSource(){
        for(int i=0;i<3;i++){
            Source source = new Source();
            source.setName("test");
            source.setRemove(false);
            source.setCron("0 ? 0");
            source.setGateway("gateway");
            source.setType(SourceType.JAVA);
            source.setPriority(1);
            sourceService.saveOrUpdate(source);
        }
    }


}
