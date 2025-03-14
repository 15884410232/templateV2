package com.levy.dto.util.netty2;

import com.levy.dto.util.netty.BasePayload;
import com.levy.dto.util.netty.BaseSimpleChannelInboundHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class NettyClient3 {

    private static final ConcurrentHashMap<String, Bootstrap> bootstrapMap = new ConcurrentHashMap<>();
    private static EventLoopGroup group = new NioEventLoopGroup(384);
    private static final int OversizeMaxContentLength = 1024 * 1024 * 102;
    private int defaultMaxContentLength = 1024 * 1024 * 1;

    public static Bootstrap getBootstrap(String key, BaseSimpleChannelInboundHandler baseSimpleChannelInboundHandler) {
        Bootstrap bootstrap;
        synchronized (bootstrapMap) {
            bootstrap = bootstrapMap.get(key);
            if(bootstrap==null) {
                bootstrap=new Bootstrap();
                bootstrap.group(group)
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .channel(NioSocketChannel.class)
                        .handler(MyChannelInitializer2.getMyChannelInitializer2(baseSimpleChannelInboundHandler));
                bootstrapMap.put(key, bootstrap);
            }
        }
        return bootstrap;

    }

    public static Bootstrap getBootstrap(String key, BaseSimpleChannelInboundHandler baseSimpleChannelInboundHandler,String url) {
        Bootstrap bootstrap;
        synchronized (bootstrapMap) {
            bootstrap = bootstrapMap.get(key);
            if(bootstrap==null) {
                bootstrap=new Bootstrap();
                bootstrap.group(group)
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                        .channel(NioSocketChannel.class)
                        .handler(MyChannelInitializer2.getMyChannelInitializer2(url,baseSimpleChannelInboundHandler));
                bootstrapMap.put(key, bootstrap);
            }
        }
        return bootstrap;

    }

    public static void send(String key, BaseSimpleChannelInboundHandler baseSimpleChannelInboundHandler,BasePayload basePayload) throws URISyntaxException {
        URI uri=new URI(basePayload.getDownloadUrl());
        String host = uri.getHost();
        Integer port = uri.getPort() == -1 ? ("https".equals(uri.getScheme()) ? 443 : 80) : uri.getPort();
        Bootstrap bootstrap = getBootstrap(key, baseSimpleChannelInboundHandler,basePayload.getDownloadUrl());
        ChannelFuture channelFuture = bootstrap.connect(host, port);
        channelFuture.addListener((ChannelFutureListener) future1 -> {
            if (future1.isSuccess()) {
                // 连接成功，发送消息
                // 构建 HTTP 请求
                FullHttpRequest request = new DefaultFullHttpRequest(
                        HttpVersion.HTTP_1_1, HttpMethod.GET, uri.toASCIIString(),
                        Unpooled.EMPTY_BUFFER);
                request.headers().set(HttpHeaderNames.HOST, host);
                request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
                request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
                // 发送请求
                future1.channel().writeAndFlush(request);
            } else {
                // 连接失败，处理错误
                log.info("Failed to connect to server."+future1.cause().getMessage());
//                future1.cause().printStackTrace();
            }
        });
    }



}
