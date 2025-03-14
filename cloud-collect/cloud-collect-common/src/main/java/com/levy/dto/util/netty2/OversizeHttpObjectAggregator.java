//package com.levy.dto.util.netty2;
//
//import com.dtsw.util.netty.util.LimitTokenUtil;
//import com.levy.dto.util.netty.BasePayload;
//import com.levy.dto.util.netty.BaseSimpleChannelInboundHandler;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.handler.codec.http.HttpMessage;
//import io.netty.handler.codec.http.HttpObjectAggregator;
//import io.netty.handler.codec.http.TooLongHttpContentException;
//import lombok.extern.slf4j.Slf4j;
//
//import java.util.concurrent.ArrayBlockingQueue;
//
//@Slf4j
//public class OversizeHttpObjectAggregator extends HttpObjectAggregator {
//
//    public static ArrayBlockingQueue<BasePayload> overSizereqUrl=new ArrayBlockingQueue<>(1024*1024);
//
//
//    private BaseSimpleChannelInboundHandler baseSimpleChannelInboundHandler;
//
//    public OversizeHttpObjectAggregator(int maxContentLength,BaseSimpleChannelInboundHandler baseSimpleChannelInboundHandler) {
//        super(maxContentLength);
//        this.baseSimpleChannelInboundHandler=baseSimpleChannelInboundHandler;
//    }
//
//    /**
//     * 如果响应内容超限，则将该请求放入阻塞队列中，等待处理
//     * @param ctx
//     * @param oversized
//     * @throws Exception
//     */
//    @Override
//    protected void handleOversizedMessage(ChannelHandlerContext ctx, HttpMessage oversized) throws Exception {
//        BasePayload basePayload = baseSimpleChannelInboundHandler.getThreadLocal().get();
//        basePayload.setSimpleChannelInboundHandler(baseSimpleChannelInboundHandler);
//        overSizereqUrl.put(basePayload);
//        try {
//            super.handleOversizedMessage(ctx, oversized);
//        }catch (TooLongHttpContentException tooLongHttpContentException){
//
//            log.info("响应内容过长，加入重试队列basePayload:{}",basePayload);
//            LimitTokenUtil.mapQueue.get(baseSimpleChannelInboundHandler.getReqKey()).put(0);
//            log.info(baseSimpleChannelInboundHandler.getReqKey()+"放入令牌"+LimitTokenUtil.mapQueue.get(baseSimpleChannelInboundHandler.getReqKey()).size());
//        }
//    }
//}
