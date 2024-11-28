package com.levy.dto.util.netty;

import lombok.extern.slf4j.Slf4j;

import java.net.URISyntaxException;

@Slf4j
public class RetryTask implements Runnable{
    @Override
    public void run() {
            while(true) {
                try {
                BasePayload payload = OversizeHttpObjectAggregator.overSizereqUrl.take();
//                log.info("开始重试：{}",payload);
                NettyClient.newBuilder().setPayload(payload).setUrl(payload.getDownloadUrl())
                        .setSimpleChannelInboundHandler(payload.getSimpleChannelInboundHandler()).buildBootstrap(1024*1024*10).connectAndSend();
                } catch (InterruptedException | URISyntaxException e) {
                    e.printStackTrace();
//                    throw new RuntimeException(e);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }

    }
}