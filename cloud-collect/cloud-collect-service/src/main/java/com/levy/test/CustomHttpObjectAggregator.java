package com.levy.test;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.*;

import java.util.List;

public class CustomHttpObjectAggregator extends MessageToMessageDecoder<HttpObject> {

    private String currentUrl;
    private final int maxContentLength;

    public CustomHttpObjectAggregator(int maxContentLength) {
        this.maxContentLength = maxContentLength;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, HttpObject msg, List<Object> out) throws Exception {
        out.add(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof TooLongFrameException) {
            // 处理消息过大的情况
            MaxUtil.outMaxSize.put(MaxUtil.urlList.get());
            // 发送错误响应
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.INTERNAL_SERVER_ERROR,
                    Unpooled.copiedBuffer("Internal Server Error", HttpConstants.DEFAULT_CHARSET)
            );
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            super.exceptionCaught(ctx, cause);
        }
    }
}