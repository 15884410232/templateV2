package com.levy.test;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

@Component
public class DownloadChannelInboundHandler extends SimpleChannelInboundHandler<HttpObject> {

    private Charset charset = CharsetUtil.UTF_8;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) msg;
            HttpResponseStatus status = response.status();
            System.out.println("Response status: " + status);

            if (status == HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE) {
                MaxUtil.outMaxSize.put(MaxUtil.urlList.get());
            } else {
                super.channelRead(ctx,msg);
            }
        }
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
            if (msg instanceof HttpResponse) {
                HttpResponse response = (HttpResponse) msg;
                System.out.println("STATUS: " + response.status());

                // 检查 Content-Type 头以确定编码
                String contentType = response.headers().get(HttpHeaderNames.CONTENT_TYPE);
                if (contentType != null && contentType.contains("charset=")) {
                    String charsetStr = contentType.split("charset=")[1];
                    charset = Charset.forName(charsetStr);
                } else {
                    // 如果 Content-Type 头中没有字符编码信息，手动指定字符编码
                    charset = CharsetUtil.UTF_8; // 假设服务器返回的是 UTF-8 编码
                }
            }
            if (msg instanceof HttpContent) {
                HttpContent content = (HttpContent) msg;
                ByteBuf buf = content.content();
                try {
                    //获取当前线程的名称
                    System.out.println("当前线程名称：" + Thread.currentThread().getName());
                    //将buf保存为文件到本地文件夹中

                    FileOutputStream fileOutputStream = new FileOutputStream("D:\\tahrir-api-0.2.7.tar.gz");
                    InputStream inputStream = convertByteBufToInputStream(buf);
//                saveByteBufToFile(buf, "D:\\tahrir-api-0.2.7.tar.gz");
                    fileOutputStream.write(inputStream.readAllBytes());
                    fileOutputStream.close();
                    inputStream.close();
                } finally {
//                                            buf.release();
                }
                if (content instanceof LastHttpContent) {
                    ctx.close();
                }
            }
    }

//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        if (cause instanceof TooLongFrameException) {
//            // 处理消息过大的情况
//            MaxUtil.outMaxSize.put(currentUrl.get());
//            // 发送错误响应
//            FullHttpResponse response = new DefaultFullHttpResponse(
//                    HttpVersion.HTTP_1_1,
//                    HttpResponseStatus.INTERNAL_SERVER_ERROR,
//                    Unpooled.copiedBuffer("Internal Server Error", HttpConstants.DEFAULT_CHARSET)
//            );
//            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
//            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
//        } else {
//            super.exceptionCaught(ctx, cause);
//        }
//    }

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
