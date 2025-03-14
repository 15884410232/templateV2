package com.levy.dto.util.netty2;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GlobalDuplexExceptionHandler extends ChannelDuplexHandler {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // Handle exceptions for both inbound and outbound operations.
        log.info("56444444444");
        cause.printStackTrace();
        ctx.close();
    }


}