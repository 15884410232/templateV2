package com.dtsw.util.netty;


import com.dtsw.util.netty.base.BasePayload;
import com.dtsw.util.netty.base.ProxyChannelInitializer;
import com.dtsw.util.netty.base.ProxyConfig;
import com.dtsw.util.netty.base.handler.BaseChannelInboundHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.NettyRuntime;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class NettyClient {

    //重试次数记录，重试不能超过5次
    private static ConcurrentHashMap<String, Integer> retryMap=new ConcurrentHashMap<>();

    //重定向队列
    public static ArrayBlockingQueue<BasePayload> redirectQueue = new ArrayBlockingQueue<>(24);

    public static Map<String, Semaphore> limitMap = new ConcurrentHashMap<>();

    public static Semaphore limit = new Semaphore(100);

    public static ConcurrentHashMap<String, AtomicInteger> pendingConnectionsMap=new ConcurrentHashMap<>();
    /**
     * 记录正在连接的请求数量
     */
    public static AtomicInteger pendingConnections = new AtomicInteger(0);
    /**
     * 获取cpu核心数量
     */
    private static final int NUM_PROCESSORS = NettyRuntime.availableProcessors();

    /**
     * 线程池数量为cpu核心*20
     */
//    private static EventLoopGroup group = new NioEventLoopGroup(NUM_PROCESSORS * 20);
    private static EventLoopGroup group = new NioEventLoopGroup(NUM_PROCESSORS * 2);
    /**
     * @param key                             标志请求的发起的key，用于做同一个key的限流
     * @param baseChannelInboundHandler
     * @param url
     * @param proxyConfig
     * @return
     */
    public static Bootstrap getBootstrap(String key, BaseChannelInboundHandler baseChannelInboundHandler, String url, ProxyConfig proxyConfig) {
        initLimit(key, baseChannelInboundHandler.getBasePayload().getLimitSize());
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
//                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .channel(NioSocketChannel.class)
                .handler(new ProxyChannelInitializer(url, baseChannelInboundHandler, key, proxyConfig));
        return bootstrap;
    }

    /**
     * 初始化限流信号量
     *
     * @param key
     * @param limitSize
     */
    public static void initLimit(String key, Integer limitSize) {
        if (NettyClient.limitMap.get(key) == null) {
            synchronized (NettyClient.limitMap) {
                if (NettyClient.limitMap.get(key) == null) {
                    Semaphore limit = new Semaphore(limitSize, true);
                    NettyClient.limitMap.put(key, limit);
                }
            }
        }
        if(pendingConnectionsMap.get(key)==null){
            synchronized (pendingConnectionsMap) {
                if(pendingConnectionsMap.get(key)==null) {
                    pendingConnectionsMap.put(key, new AtomicInteger(0));
                }
            }
        }
    }

    /**
     * 获取请求的NetAddress
     */
    @Data
    public static class NetAddress {
        private String host;

        private int port;

        private String scheme;

        private String ascllUrl;
    }

    private static NetAddress getNetAddress(String url) throws URISyntaxException {
        if (StringUtils.isEmpty(url)) {
            throw new RuntimeException("url is not be null");
        }
        URI uri = new URI(url);
        String ascllUrl = uri.toASCIIString();
        String host = uri.getHost();
        Integer port = uri.getPort() == -1 ? ("https".equals(uri.getScheme()) ? 443 : 80) : uri.getPort();
        NetAddress netAddress = new NetAddress();
        netAddress.setHost(host);
        netAddress.setPort(port);
        netAddress.setScheme(uri.getScheme());
        netAddress.setAscllUrl(ascllUrl);
        return netAddress;
    }

//    /**
//     * 建立连接发送Http请求
//     *
//     * @param requestKeyChannelInboundHandler
//     */
//    public static void connectAndsend(RequestKeyChannelInboundHandler requestKeyChannelInboundHandler) throws URISyntaxException {
//        proxyConnectAndsend(requestKeyChannelInboundHandler, null);
//    }


    /**
     * 建立代理连接发送Http请求
     *
     * @param basePayload
     * @throws URISyntaxException
     */
    public static void connectAndsend(BasePayload basePayload) {
        BaseChannelInboundHandler baseChannelInboundHandler = null;
        try {
            baseChannelInboundHandler = (BaseChannelInboundHandler) basePayload.getHandlerClass().getDeclaredConstructor(BasePayload.class)
                    .newInstance(basePayload);
        } catch (Exception e) {
            log.error("Handler 生成异常");
        }
        ProxyConfig proxyConfig=basePayload.getProxyConfig();
        NetAddress netAddress;
        try {
            netAddress = getNetAddress(basePayload.getReqUrl());
        } catch (URISyntaxException e) {
            log.error("url无效");
            return;
        }
        String key = basePayload.getKey();
        Bootstrap bootstrap = getBootstrap(key, baseChannelInboundHandler, basePayload.getReqUrl(), proxyConfig);
        ChannelFuture channelFuture;
        try {
            //如果剩余令牌剩1个，则证明程序即将过载，就将线程睡眠1S，等待程序处理一下积压的请求
            synchronized (limitMap.get(key)) {
                while (limitMap.get(key).availablePermits() < 2) {
                    limitMap.get(key).wait(1000);
                }
                try {
                    limitMap.get(key).acquire();
                }catch (InterruptedException ex){
                    log.error("获取令牌失败：{}", ex.getMessage());
                }
            }
//            try {
//                limitMap.get(key).acquire();
//            }catch (InterruptedException ex){
//                log.error("获取令牌失败：{}", ex.getMessage());
//            }
            channelFuture = bootstrap.connect(netAddress.getHost(), netAddress.getPort());
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("连接失败：{}", ex.getMessage());
            limitMap.get(key).release();
            return;
        }
        //在每次尝试建立连接之前增加计数器
        pendingConnectionsMap.get(key).incrementAndGet();
//        pendingConnections.incrementAndGet();
        channelFuture.addListener((ChannelFutureListener) future1 -> {
//            pendingConnections.decrementAndGet();
            pendingConnectionsMap.get(key).decrementAndGet();
            if (future1.isSuccess()) {
                // 连接成功，发送消息  构建 HTTP 请求
                FullHttpRequest request = new DefaultFullHttpRequest(
                        HttpVersion.HTTP_1_1, HttpMethod.GET, netAddress.getAscllUrl(),
                        Unpooled.EMPTY_BUFFER);
                request.headers().set(HttpHeaderNames.HOST, netAddress.getHost());
                //请求完成之后立刻关闭连接
                request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
                request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
                request.headers().set(HttpHeaderNames.HOST, netAddress.getHost());
                // 发送请求
                future1.channel().writeAndFlush(request);
            } else {
                // 连接失败,释放令牌
                log.info("Failed to connect to server." + future1.cause().getMessage());
                limitMap.get(key).release();
                retryMap.compute(basePayload.getReqUrl(), (keys, oldValue) -> {
                    if (oldValue == null) {
                        return 1;
                    } else {
                        return oldValue + 1;
                    }
                });
                if(retryMap.get(basePayload.getReqUrl())<5){
                    //如果重试少于5次就继续重试
                    log.info(basePayload.getReqUrl()+"  开始重试第:"+basePayload.getReqUrl()+"次");
                    NettyClient.connectAndsend(basePayload);
                }

            }
        });
    }


}
