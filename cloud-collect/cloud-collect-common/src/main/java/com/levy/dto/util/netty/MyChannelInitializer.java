package com.levy.dto.util.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {
    private String host;
    private int port;
    private SimpleChannelInboundHandler simpleChannelInboundHandler;
    //maxContentLength 默认为1M
    private int maxContentLength=1024*1024*1;

    public MyChannelInitializer(String host,int port,SimpleChannelInboundHandler simpleChannelInboundHandler){
        this.host = host;
        this.port = port;
        this.simpleChannelInboundHandler=simpleChannelInboundHandler;
    }
    public MyChannelInitializer(String host,int port,SimpleChannelInboundHandler simpleChannelInboundHandler,int maxContentLength){
        this.host = host;
        this.port = port;
        this.simpleChannelInboundHandler=simpleChannelInboundHandler;
        this.maxContentLength+=maxContentLength;
    }


    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();

        SslContext sslCtx = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();p.addLast(sslCtx.newHandler(ch.alloc(), host, port));
        p.addLast(new HttpClientCodec());
        p.addLast(new HttpContentDecompressor()); // 添加 GZIP 解压处理器
        p.addLast(new OversizeHttpObjectAggregator(this.maxContentLength));
        p.addLast(this.simpleChannelInboundHandler);
    }
}