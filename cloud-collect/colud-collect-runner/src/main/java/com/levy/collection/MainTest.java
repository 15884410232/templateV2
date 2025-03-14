package com.levy.collection;

import com.levy.collection.flow.download.collector.base.DownloadChannelInboundHandler;
import com.levy.collection.flow.download.collector.payload.MinioSaveObject;
import com.levy.dto.util.netty2.NettyClient3;

import java.net.URISyntaxException;

public class MainTest {

    public static void main(String[] args) throws URISyntaxException {
        DownloadChannelInboundHandler downloadChannelInboundHandler=new DownloadChannelInboundHandler();
        MinioSaveObject basePayload=new MinioSaveObject();
        basePayload.setBucketName("python");
        basePayload.setDownloadUrl("https://files.pythonhosted.org/packages/37/a5/8a84ebfc61f8ddebf101bae6eab834a0f1ba482dc02aa25ace96943b3aa6/pygamepal-0.3.4.tar.gz");
        downloadChannelInboundHandler.setBasePayload(basePayload);
        NettyClient3.send("download", downloadChannelInboundHandler,basePayload);
    }

}
