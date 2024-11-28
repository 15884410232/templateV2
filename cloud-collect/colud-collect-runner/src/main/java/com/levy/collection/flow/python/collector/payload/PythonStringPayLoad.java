package com.levy.collection.flow.python.collector.payload;

import com.levy.dto.util.netty.BasePayload;
import lombok.Data;

@Data
public class PythonStringPayLoad extends BasePayload {


    public PythonStringPayLoad(String url) {
        this.url = url;
    }

    private String url;


    @Override
    public String getDownloadUrl() {
        return this.url;
    }
}
