package com.levy.test;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObjectAggregator;

public class MyHttpObjectAggregator extends HttpObjectAggregator {
    public MyHttpObjectAggregator(int maxContentLength) {
        super(maxContentLength);
    }

    public MyHttpObjectAggregator(int maxContentLength, boolean closeOnExpectationFailed) {
        super(maxContentLength, closeOnExpectationFailed);
    }

    @Override
    protected void handleOversizedMessage(ChannelHandlerContext ctx, HttpMessage oversized) throws Exception {
            MaxUtil.outMaxSize.put(MaxUtil.urlList.get());
        super.handleOversizedMessage(ctx, oversized);
    }
}
