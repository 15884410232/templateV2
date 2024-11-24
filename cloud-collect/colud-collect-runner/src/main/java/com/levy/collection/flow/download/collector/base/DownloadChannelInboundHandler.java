package com.levy.collection.flow.download.collector.base;

import com.dtsw.collection.flow.download.collector.provider.PythonDownloadProvider;
import com.dtsw.collection.flow.dto.MinioSaveObject;
import com.dtsw.collection.service.store.Storage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

@Slf4j
@Component
public class DownloadChannelInboundHandler extends SimpleChannelInboundHandler<HttpObject> {
    private volatile static DownloadChannelInboundHandler downloadChannelInboundHandler;

    ThreadLocal<MinioSaveObject> minioSaveObjectThreadLocal = new ThreadLocal<>();

    @Resource
    private Storage storage;

    private Charset charset = CharsetUtil.UTF_8;

    private DownloadChannelInboundHandler() {

    }

    public static DownloadChannelInboundHandler getInstance() {
        if (downloadChannelInboundHandler == null) {
            synchronized (DownloadChannelInboundHandler.class) {
                downloadChannelInboundHandler = new DownloadChannelInboundHandler();
            }
        }
        return downloadChannelInboundHandler;

    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if (msg instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) msg;
            log.info("STATUS: " + response.status());
        }
        if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;
            ByteBuf buf = content.content();
            MinioSaveObject minioSaveObject = minioSaveObjectThreadLocal.get();
            try (InputStream inputStream = convertByteBufToInputStream(buf);
                 BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)) {
                storage.putObject(PythonDownloadProvider.bucketName, minioSaveObject.getObjectName(), bufferedInputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (content instanceof LastHttpContent) {
                ctx.close();
            }
        }
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

        // 重置 ByteBuf 的读取索引
        byteBuf.readerIndex(0);
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


}
