package com.levy.collection.flow.javascript.collector.payload;

import com.levy.dto.util.netty.BasePayload;
import lombok.Data;

@Data
public class JavaScriptStringPayLoad extends BasePayload {


    public JavaScriptStringPayLoad(String url) {
        this.url = url;
    }

    private String url;


    @Override
    public String getDownloadUrl() {
        return this.url;
    }
}
