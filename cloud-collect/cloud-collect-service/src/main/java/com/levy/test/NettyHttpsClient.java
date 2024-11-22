package com.levy.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.util.CharsetUtil;

import java.net.URI;
import java.nio.charset.Charset;

public class NettyHttpsClient {

    public static void main(String[] args) throws Exception {
        // 解析 URL
        URI uri = new URI("https://pypi.org/pypi/dilax/json");
        String scheme = uri.getScheme();
        String host = uri.getHost();
        int port = uri.getPort() == -1 ? ("https".equals(scheme) ? 443 : 80) : uri.getPort();

        // 创建 EventLoopGroup
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            // 创建 Bootstrap
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            if ("https".equals(scheme)) {
                                SslContext sslCtx = SslContextBuilder.forClient()
                                        .trustManager(InsecureTrustManagerFactory.INSTANCE)
                                        .build();
                                p.addLast(sslCtx.newHandler(ch.alloc(), host, port));
                            }
                            p.addLast(new HttpClientCodec());
                            p.addLast(new HttpContentDecompressor()); // 添加 GZIP 解压处理器
                            p.addLast(new HttpObjectAggregator(65536));
                            p.addLast(new SimpleChannelInboundHandler<HttpObject>() {
                                private Charset charset = CharsetUtil.UTF_8; // 默认使用 UTF-8

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
                                            System.out.println("CONTENT: " + buf.toString(charset));
                                        } finally {
//                                            buf.release();
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
                            });
                        }
                    });

            // 连接到服务器
            ChannelFuture f = b.connect(host, port).sync();

            // 构建 HTTP 请求
            FullHttpRequest request = new DefaultFullHttpRequest(
                    HttpVersion.HTTP_1_1, HttpMethod.GET, uri.toASCIIString(),
                    Unpooled.EMPTY_BUFFER);
            request.headers().set(HttpHeaderNames.HOST, host);
            request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);

            // 发送请求
            f.channel().writeAndFlush(request);

            // 等待连接关闭
            f.channel().closeFuture().sync();
        } finally {
            // 关闭 EventLoopGroup
            group.shutdownGracefully();
        }
    }
}