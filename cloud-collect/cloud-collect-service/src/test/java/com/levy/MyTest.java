package com.levy;

import com.levy.test.DownloadChannelInboundHandler;
import com.levy.test.LocalSimpleChannelInboundHandler;
import com.levy.test.MaxUtil;
import com.levy.test.NettyHttp;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Slf4j
@SpringBootTest
public class MyTest {
    @Resource
    private LocalSimpleChannelInboundHandler localSimpleChannelInboundHandler;




    @Test
    public void test() throws URISyntaxException, InterruptedException {
        List<String> list = List.of("zdd-tools",
                "zdhr-wlcj-components",
                "zdndd-lib");

        String url="https://registry.npmmirror.com/";
        URI uri = new URI(url);
        String scheme = uri.getScheme();
        String host = uri.getHost();
        int port = uri.getPort() == -1 ? ("https".equals(scheme) ? 443 : 80) : uri.getPort();
        Bootstrap boot = NettyHttp.getInstance(host,port,new LocalSimpleChannelInboundHandler());

        list.stream().forEach(item ->{
            // 连接到服务器
            ChannelFuture channelFuture = null;
            channelFuture = boot.connect(host, port);
            URI uris;
            try {
                uris = new URI(url+"item");
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }

            // 添加连接成功的监听器
            channelFuture.addListener((ChannelFutureListener) future1 -> {
                if (future1.isSuccess()) {
                    // 连接成功，发送消息
                    // 构建 HTTP 请求
                    FullHttpRequest request = new DefaultFullHttpRequest(
                            HttpVersion.HTTP_1_1, HttpMethod.GET, uris.toASCIIString(),
                            Unpooled.EMPTY_BUFFER);
                    request.headers().set(HttpHeaderNames.HOST, host);
                    request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
                    request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
                    // 发送请求
                    future1.channel().writeAndFlush(request);
                } else {
                    // 连接失败，处理错误
                    System.err.println("Failed to connect to server.");
                    future1.cause().printStackTrace();
                }
            });


        });
        System.out.println("发送完毕====================================================================================================");
        Thread.sleep(100000);

    }

    @Test
    public void tess() throws URISyntaxException, InterruptedException {
        Thread thread=new Thread(()->{
            while (true){
                try {
                    retry();
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();

        String url="https://files.pythonhosted.org/packages/fa/08/9466b723c7709192dcd93328b028500c2871462b0ba0aee809f2d9b5844d/tahrir-api-0.2.7.tar.gz";
        List<String> list = List.of(url);
        URI uri = new URI(url);
        String scheme = uri.getScheme();
        String host = uri.getHost();
        int port = uri.getPort() == -1 ? ("https".equals(scheme) ? 443 : 80) : uri.getPort();
        Bootstrap boot = NettyHttp.getInstance(host,port, new DownloadChannelInboundHandler());

        list.stream().forEach(item ->{
            // 连接到服务器
            ChannelFuture channelFuture = null;
            channelFuture = boot.connect(host, port);
            URI uris;
            try {
                uris = new URI(url);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }

            // 添加连接成功的监听器
            channelFuture.addListener((ChannelFutureListener) future1 -> {
                if (future1.isSuccess()) {
                    MaxUtil.urlList.set(String.valueOf(uri));
                    // 连接成功，发送消息
                    // 构建 HTTP 请求
                    FullHttpRequest request = new DefaultFullHttpRequest(
                            HttpVersion.HTTP_1_1, HttpMethod.GET, uris.toASCIIString(),
                            Unpooled.EMPTY_BUFFER);
                    request.headers().set(HttpHeaderNames.HOST, host);
                    request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
                    request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
                    // 发送请求
                    future1.channel().writeAndFlush(request);
                } else {
                    // 连接失败，处理错误
                    System.err.println("Failed to connect to server.");
                    future1.cause().printStackTrace();
                }
            });


        });
        System.out.println("发送完毕====================================================================================================");
        Thread.sleep(100000);


    }
    @Test
    public void retrys() throws URISyntaxException, InterruptedException {
        Thread thread=new Thread(()->{
            while (true){
                try {
                    retry();
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        },"AAAAAAAAAAAAA");
        thread.start();
//        retry();
        Thread.sleep(3000);
        MaxUtil.outMaxSize.put("https://files.pythonhosted.org/packages/fa/08/9466b723c7709192dcd93328b028500c2871462b0ba0aee809f2d9b5844d/tahrir-api-0.2.7.tar.gz");
        log.info(String.valueOf(MaxUtil.outMaxSize.size()));
        Thread.sleep(1000000000);
    }

//    @Async
    public void retry() throws URISyntaxException, InterruptedException {
        try {
            while(true) {
                String take = MaxUtil.outMaxSize.take();
                String url = take;
                log.info(Thread.currentThread().getName());
                log.info("1111111111111====================================================");
                log.info(url);
                List<String> list = List.of(url);
                URI uri = new URI(url);
                String scheme = uri.getScheme();
                String host = uri.getHost();
                int port = uri.getPort() == -1 ? ("https".equals(scheme) ? 443 : 80) : uri.getPort();
                Bootstrap boot = NettyHttp.getInstance(host, port, new DownloadChannelInboundHandler(), 1024 * 1024 * 10);

                list.stream().forEach(item -> {
                    // 连接到服务器
                    ChannelFuture channelFuture = null;
                    channelFuture = boot.connect(host, port);
                    URI uris;
                    try {
                        uris = new URI(url);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }

                    // 添加连接成功的监听器
                    channelFuture.addListener((ChannelFutureListener) future1 -> {
                        if (future1.isSuccess()) {
                            // 连接成功，发送消息
                            // 构建 HTTP 请求
                            FullHttpRequest request = new DefaultFullHttpRequest(
                                    HttpVersion.HTTP_1_1, HttpMethod.GET, uris.toASCIIString(),
                                    Unpooled.EMPTY_BUFFER);
                            request.headers().set(HttpHeaderNames.HOST, host);
                            request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
                            request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
                            // 发送请求
                            future1.channel().writeAndFlush(request);
                        } else {
                            // 连接失败，处理错误
                            System.err.println("Failed to connect to server.");
                            future1.cause().printStackTrace();
                        }
                    });


                });
                System.out.println("发送完毕====================================================================================================");
                Thread.sleep(100000);
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }




}
