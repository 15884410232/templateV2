package com.levy.collection.flow.download.collector.base;


import com.levy.collection.flow.download.collector.payload.MinioSaveObject;
import com.levy.dto.util.netty.BasePayload;
import com.levy.dto.util.netty.BaseSimpleChannelInboundHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

@Slf4j
@ChannelHandler.Sharable
@Component
public class DownloadChannelInboundHandler extends BaseSimpleChannelInboundHandler {
    private volatile static DownloadChannelInboundHandler downloadChannelInboundHandler;

    private static ThreadLocal<BasePayload> minioSaveObjectThreadLocal = new ThreadLocal<>();

//    @Resource
//    private Storage storage;

    private Charset charset = CharsetUtil.UTF_8;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if (msg instanceof HttpResponse) {
                HttpResponse response = (HttpResponse) msg;
                if (response.status().code()!=200) {
                    log.error("Non-200 status code received: " + response.status().code());
                    ctx.close();
                    return;
                }
        }
            if (msg instanceof HttpContent) {
                HttpContent content = (HttpContent) msg;
                ByteBuf buf = content.content();
                try {
                    //将buf保存为文件到本地文件夹中
                    MinioSaveObject minioSaveObject = (MinioSaveObject) DownloadChannelInboundHandler.minioSaveObjectThreadLocal.get();
                    if (minioSaveObject == null) {
                        ctx.close();
                        return;
                    }
                    String downloadUrl = minioSaveObject.getDownloadUrl();
                    //截取文件名
                    String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
                    FileOutputStream fileOutputStream = new FileOutputStream("D:\\pythonSourcePackage\\" + fileName);
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

        // 重置 ByteBuf 的读取索引
        byteBuf.readerIndex(0);
    }

    @Override
    public ThreadLocal<BasePayload> getThreadLocal() {
        return DownloadChannelInboundHandler.minioSaveObjectThreadLocal;
    }
}
