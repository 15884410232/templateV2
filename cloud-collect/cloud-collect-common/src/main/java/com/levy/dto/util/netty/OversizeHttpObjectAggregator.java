package com.levy.dto.util.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObjectAggregator;

import java.util.concurrent.ArrayBlockingQueue;

public class OversizeHttpObjectAggregator extends HttpObjectAggregator {

    private static ArrayBlockingQueue<String> overSizereqUrl=new ArrayBlockingQueue<>(1024*1024);

    private static ThreadLocal<String> currentReqUrl=new ThreadLocal<>();

    public OversizeHttpObjectAggregator(int maxContentLength) {
        super(maxContentLength);
    }

    public OversizeHttpObjectAggregator(int maxContentLength, boolean closeOnExpectationFailed) {
        super(maxContentLength, closeOnExpectationFailed);
    }

    /**
     * 如果响应内容超限，则将该请求放入阻塞队列中，等待处理
     * @param ctx
     * @param oversized
     * @throws Exception
     */
    @Override
    protected void handleOversizedMessage(ChannelHandlerContext ctx, HttpMessage oversized) throws Exception {
        overSizereqUrl.put(currentReqUrl.get());
        super.handleOversizedMessage(ctx, oversized);
    }
}
