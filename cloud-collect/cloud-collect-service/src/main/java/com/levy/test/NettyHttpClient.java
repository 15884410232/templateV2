package com.levy.test;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;
import java.nio.charset.Charset;

public class NettyHttpClient {
    public static void main(String[] args) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new HttpClientCodec());
                            ch.pipeline().addLast(new HttpObjectAggregator(1024 * 1024));
                            ch.pipeline().addLast(new NettyHttpClientHandler());
                        }
                    });

            URI uri = new URI("https://pypi.org/pypi/dilax/json");
            String host = uri.getHost();
            int port = uri.getPort() == -1? 443 : uri.getPort();

            HttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.toASCIIString());
            request.headers().set(HttpHeaders.Names.HOST, host);
            request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);
            request.headers().set(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP);

            ChannelFuture future = b.connect(host, port).sync();
            future.channel().writeAndFlush(request).sync();
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    private static class NettyHttpClientHandler extends SimpleChannelInboundHandler<HttpResponse> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, HttpResponse response) throws Exception {
            if (response.status().code() == 200) {
                if (response instanceof HttpContent) {
                    HttpContent content = (HttpContent) response;
                    ByteBuf contents = content.content();
                    String jsonData = contents.toString(CharsetUtil.UTF_8);
                    System.out.println(jsonData);
                }


            } else {
                System.err.println("请求失败，状态码: " + response.status().code());
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }
}