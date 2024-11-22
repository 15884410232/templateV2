package com.levy.test;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;

@Component
public class LocalSimpleChannelInboundHandler extends SimpleChannelInboundHandler<HttpObject> {

    private Charset charset = CharsetUtil.UTF_8;

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
                Thread.sleep(3000);
                System.out.println("CONTENT: ");
                System.out.println("CONTENT: " + buf.toString(charset));
            } finally {
//                                            buf.release();
            }
            if (content instanceof LastHttpContent) {
                ctx.close();
            }
        }
    }
}
