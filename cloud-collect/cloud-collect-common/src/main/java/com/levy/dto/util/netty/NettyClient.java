package com.levy.dto.util.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class NettyClient {
    private static final ConcurrentHashMap<String,Bootstrap> bootstrapMap=new ConcurrentHashMap <>();
    URI uri;
    private String host;
    private Integer port;
    private SimpleChannelInboundHandler simpleChannelInboundHandler;
    private Bootstrap bootstrap;
    private static EventLoopGroup group = new NioEventLoopGroup();
    private NettyClient(){

    }

    public static NettyClientBuilder newBuilder(){
        return new NettyClientBuilder();
    }

    public static class NettyClientBuilder {
        private final NettyClient nettyClient;
        private NettyClientBuilder(){
            nettyClient=new NettyClient();
        }
        /**
         * 设置请求地址
         * @param url
         * @return
         * @throws URISyntaxException
         */
        public NettyClientBuilder setUrl(String url) throws URISyntaxException {
            Assert.notNull(url, "url can not be null");
            nettyClient.uri=new URI(url);
            nettyClient.host = this.nettyClient.uri.getHost();
            nettyClient.port = this.nettyClient.uri.getPort() == -1 ? ("https".equals(nettyClient.uri.getScheme()) ? 443 : 80) : nettyClient.uri.getPort();
            return this;
        }
        /**
         * 设置Handler
         * @param simpleChannelInboundHandler
         * @return
         */
        public NettyClientBuilder setSimpleChannelInboundHandler(SimpleChannelInboundHandler simpleChannelInboundHandler) {
            Assert.notNull(nettyClient.uri, "url can not be null");
            Assert.notNull(simpleChannelInboundHandler, "simpleChannelInboundHandler can not be null");
            nettyClient.simpleChannelInboundHandler=simpleChannelInboundHandler;
            return this;
        }
        /**
         * 创建Bootstrap
         * @return
         */
        public NettyClient buildBootstrap(){
            Assert.notNull(nettyClient.uri, "url can not be null");
            Assert.notNull(nettyClient.simpleChannelInboundHandler, "simpleChannelInboundHandler can not be null");
            Bootstrap exsitBootstrap = nettyClient.bootstrapMap.get(nettyClient.host + nettyClient.port);
            if(exsitBootstrap==null){
                synchronized (nettyClient.bootstrapMap) {
                    exsitBootstrap = nettyClient.bootstrapMap.get(nettyClient.host + nettyClient.port);
                    if(exsitBootstrap==null) {
                        // 创建 Bootstrap
                        exsitBootstrap = new Bootstrap();
                        exsitBootstrap.group(group)
                                .channel(NioSocketChannel.class)
                                .handler(new MyChannelInitializer(nettyClient.host, nettyClient.port, nettyClient.simpleChannelInboundHandler));
                        nettyClient.bootstrapMap.put(nettyClient.host + nettyClient.port, exsitBootstrap);
                    }
                }
            }
            nettyClient.bootstrap=exsitBootstrap;
            return nettyClient;
        }

    }

    /**
     * 建立连接并发起请求
     */
    public void connectAndSend(){
        ChannelFuture channelFuture = this.bootstrap.connect(host, port);
        channelFuture.addListener((ChannelFutureListener) future1 -> {
            if (future1.isSuccess()) {
                // 连接成功，发送消息
                // 构建 HTTP 请求
                FullHttpRequest request = new DefaultFullHttpRequest(
                        HttpVersion.HTTP_1_1, HttpMethod.GET, this.uri.toASCIIString(),
                        Unpooled.EMPTY_BUFFER);
                request.headers().set(HttpHeaderNames.HOST, this.host);
                request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
                request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
                // 发送请求
                future1.channel().writeAndFlush(request);
            } else {
                // 连接失败，处理错误
                log.info("Failed to connect to server.");
                future1.cause().printStackTrace();
            }
        });
    }

}