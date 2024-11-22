package com.levy.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.URISyntaxException;

public class NettyHttp {


    public static Bootstrap getInstance(String host, int port, SimpleChannelInboundHandler simpleChannelInboundHandler) throws URISyntaxException {
        EventLoopGroup group = new NioEventLoopGroup();
        // 创建 Bootstrap
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new MyChannelInitializer(host,port,simpleChannelInboundHandler));
        return bootstrap;
    }

    public static Bootstrap getInstance(String host, int port, SimpleChannelInboundHandler simpleChannelInboundHandler,int maxLength) throws URISyntaxException {
        EventLoopGroup group = new NioEventLoopGroup();
        // 创建 Bootstrap
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new MyChannelInitializer(host,port,simpleChannelInboundHandler,maxLength));
        return bootstrap;
    }

}
