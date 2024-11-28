package com.levy.collection.util;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.util.AttributeKey;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class NettyDownloader {

    private static final int MAX_CONCURRENT_DOWNLOADS = 100; // 最大并发下载数
    private static final int THREADS = 2 * Runtime.getRuntime().availableProcessors(); // 线程数
    private static final String DOWNLOAD_DIR = "D:\\pythonSourcePackage"; // 下载目录

    public static void main(String[] args) throws Exception {
        List<String> urls = getUrls(); // 获取所有文件的URL列表
        int batchSize = MAX_CONCURRENT_DOWNLOADS;

        // 创建下载目录
        File downloadDir = new File(DOWNLOAD_DIR);
        if (!downloadDir.exists()) {
            downloadDir.mkdirs();
        }

        // 创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(THREADS);

        // 创建 EventLoopGroup
        EventLoopGroup group = new NioEventLoopGroup(THREADS);

        try {
            // 创建 Bootstrap
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            SslContext sslCtx = SslContextBuilder.forClient()
                                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                                    .build();
                            p.addLast(sslCtx.newHandler(ch.alloc()));
                            p.addLast(new HttpClientCodec());
                            p.addLast(new HttpObjectAggregator(65536));
                            p.addLast(new DownloadHandler());
                        }
                    });

            // 分批次下载文件
            AtomicInteger counter = new AtomicInteger(0);
            for (int i = 0; i < urls.size(); i += batchSize) {
                int end = Math.min(i + batchSize, urls.size());
                List<String> batch = urls.subList(i, end);
                for (String url : batch) {
                    CompletableFuture.runAsync(() -> downloadFile(bootstrap, url), executorService);
                }
            }
        } finally {
            // 关闭线程池和 EventLoopGroup
            executorService.shutdown();
            group.shutdownGracefully();
        }
    }

    private static void downloadFile(Bootstrap bootstrap, String url) {
        URI uri = URI.create(url);
        String host = uri.getHost();
        int port = uri.getPort() == -1 ? (uri.getScheme().equals("https") ? 443 : 80) : uri.getPort();
        String path = uri.getRawPath();

        ChannelFuture future = bootstrap.connect(host, port);
        future.addListener((ChannelFutureListener) f -> {
            if (f.isSuccess()) {
                Channel channel = f.channel();
                FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, path);
                request.headers().set(HttpHeaderNames.HOST, host);
                request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
                channel.writeAndFlush(request);
            } else {
                System.err.println("Failed to connect to " + url);
            }
        });
    }

    private static List<String> getUrls() {
        // 示例 URL 列表
        List<String> urls = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            urls.add("http://example.com/file" + i + ".txt");
        }
        return urls;
    }

    private static class DownloadHandler extends SimpleChannelInboundHandler<HttpObject> {
        private FileOutputStream fos;

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            // 保存文件路径
            String url = (String) ctx.channel().attr(AttributeKey.valueOf("url")).get();
            File file = new File(DOWNLOAD_DIR, new URI(url).getPath().substring(1));
            fos = new FileOutputStream(file);
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
            if (msg instanceof HttpContent) {
                HttpContent content = (HttpContent) msg;
                byte[] bytes = content.content().array();
                fos.write(bytes);
            }
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            fos.close();
            System.out.println("Downloaded file from " + ctx.channel().attr(AttributeKey.valueOf("url")).get());
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }
}