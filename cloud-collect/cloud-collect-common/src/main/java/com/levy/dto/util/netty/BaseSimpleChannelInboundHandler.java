package com.levy.dto.util.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpObject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.ConnectException;

@Slf4j
public abstract class BaseSimpleChannelInboundHandler extends SimpleChannelInboundHandler<HttpObject> {

    @Getter
    @Setter
    private BasePayload basePayload;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof ConnectException) {
            log.info("连接失败：" + cause.getMessage());
            ctx.close();
        } else {
            super.exceptionCaught(ctx, cause);
        }
    }
}