package com.levy.dto.util.netty2;

import com.levy.dto.util.netty.BaseSimpleChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.URL;

public class MyChannelInitializer2 extends ChannelInitializer<SocketChannel> {
    private volatile static MyChannelInitializer2 myChannelInitializer2;

    private BaseSimpleChannelInboundHandler simpleChannelInboundHandler;

    private String url;

    private MyChannelInitializer2(BaseSimpleChannelInboundHandler simpleChannelInboundHandler){
        this.simpleChannelInboundHandler=simpleChannelInboundHandler;
    }

    public static MyChannelInitializer2 getMyChannelInitializer2(BaseSimpleChannelInboundHandler baseSimpleChannelInboundHandler){
        return getMyChannelInitializer2(null,baseSimpleChannelInboundHandler);
    }

    public static MyChannelInitializer2 getMyChannelInitializer2(String url,BaseSimpleChannelInboundHandler baseSimpleChannelInboundHandler){
        if(myChannelInitializer2==null){
            synchronized (MyChannelInitializer2.class){
                if(myChannelInitializer2==null){
                    myChannelInitializer2=new MyChannelInitializer2(baseSimpleChannelInboundHandler);
                }
            }
        }
        myChannelInitializer2.url=url;
        return myChannelInitializer2;
    }


    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();

        if(url!=null) {
            URL url1=new URL(url);
            SslContext sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
            p.addLast(sslCtx.newHandler(ch.alloc(), url1.getHost(), 443));
        }
        // 添加 LoggingHandler 用于调试目的，LogLevel 可根据需要调整
        p.addLast(new LoggingHandler(LogLevel.TRACE));

        // Add IdleStateHandler to the pipeline.
        // 设置读空闲时间为10秒，写空闲时间禁用（0秒），全部空闲时间为20秒。
        p.addLast(new IdleStateHandler(10, 0, 20));

        // 添加 HTTP 编解码器
        p.addLast(new HttpClientCodec());

        // 添加 GZIP 解压处理器
        p.addLast(new HttpContentDecompressor());

        // 添加 HttpObjectAggregator 以聚合 HTTP 消息
        p.addLast(new HttpObjectAggregator(1024 * 1024 * 100)); // 100MB

        // 添加自定义业务逻辑处理器
        p.addLast(this.simpleChannelInboundHandler);

        //增加读取超时处理器
        p.addLast(new ReadTimeHandler());

        // 添加全局异常处理器
        p.addLast(new GlobalDuplexExceptionHandler());
    }
}