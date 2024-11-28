package com.levy;

import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.levy.collection.flow.download.collector.payload.MinioSaveObject;
import com.levy.collection.flow.download.collector.provider.PythonDownloadProvider;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public class NettyDownloader {
    @Resource
    private PythonDownloadProvider provider;

    private static final int MAX_CONCURRENT_DOWNLOADS = 1000; // 最大并发下载数
    private static final int THREADS = 2 * Runtime.getRuntime().availableProcessors(); // 线程数
    private static final String DOWNLOAD_DIR = "./downloads"; // 下载目录

    @Test
    public void test() throws Exception {
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
        Thread.sleep(10000000);
    }

    private  void downloadFile(Bootstrap bootstrap, String url) {
        try {

            if(StringUtils.isBlank(url)){
                return ;
            }
            URI uri = new URI(url);
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
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private  List<String> getUrls() {
        PageDTO pageDTO=new PageDTO();
        pageDTO.setCurrent(1);
        pageDTO.setSize(100000);
        List<MinioSaveObject> downloadFileList = provider.getDownloadFileList(pageDTO);
        System.out.println(downloadFileList.size());
        // 示例 URL 列表
        List<String> urls = new ArrayList<>();
        for (int i = 0; i < downloadFileList.size(); i++) {

            urls.add(downloadFileList.get(i).getDownloadUrl());
        }



        return urls;
    }

    private  class DownloadHandler extends SimpleChannelInboundHandler<HttpObject> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
            if (msg instanceof HttpContent) {
                HttpContent content = (HttpContent) msg;
                ByteBuf buf = content.content();
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream("D:\\pythonSourcePackage\\" + System.currentTimeMillis()+".zip");
                    InputStream inputStream = convertByteBufToInputStream(buf);
//                saveByteBufToFile(buf, "D:\\tahrir-api-0.2.7.tar.gz");
                    fileOutputStream.write(inputStream.readAllBytes());
                    fileOutputStream.close();
                    inputStream.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (content instanceof LastHttpContent) {
                    ctx.close();
                }
            }
        }


        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }
    public static InputStream convertByteBufToInputStream(ByteBuf byteBuf) {
        // 获取 ByteBuf 的可读字节数
        int readableBytes = byteBuf.readableBytes();

        // 创建一个新的字节数组
        byte[] bytes = new byte[readableBytes];

        // 将 ByteBuf 的内容读取到字节数组中
        byteBuf.getBytes(byteBuf.readerIndex(), bytes);

        // 创建 ByteArrayInputStream
        return new ByteArrayInputStream(bytes);
    }
    public static void saveByteBufToFile(ByteBuf byteBuf, String filePath) throws IOException {
        // 创建文件输出流
        try (FileOutputStream fos = new FileOutputStream(filePath);
             FileChannel fileChannel = fos.getChannel()) {

            // 获取 ByteBuf 的读取索引
            int readerIndex = byteBuf.readerIndex();
            // 获取 ByteBuf 的可读字节数
            int readableBytes = byteBuf.readableBytes();

            // 创建 ByteBuffer
            ByteBuffer byteBuffer = ByteBuffer.allocate(readableBytes);

            // 将 ByteBuf 的内容读取到 ByteBuffer 中
            byteBuf.getBytes(readerIndex, byteBuffer);
            byteBuffer.flip(); // 切换 ByteBuffer 到读模式

            // 将 ByteBuffer 的内容写入文件
            fileChannel.write(byteBuffer);
        }
    }
}