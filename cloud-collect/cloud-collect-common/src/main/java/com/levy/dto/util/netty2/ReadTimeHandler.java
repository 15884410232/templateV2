package com.levy.dto.util.netty2;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReadTimeHandler extends ChannelDuplexHandler {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.info("IdleStateEvent go");
        if (evt instanceof IdleStateEvent) {
            log.info("IdleStateEvent");
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case READER_IDLE:
                    log.info("Reader has been idle for too long.");
                    // 执行相应的操作，例如关闭连接。
                    ctx.close();
                    break;
                case WRITER_IDLE:
                    log.info("Writer has been idle for too long.");
                    ctx.close();
                    break;
                case ALL_IDLE:
                    log.info("All have been idle for too long.");
                    // 执行相应操作。
                    ctx.close();
                    break;
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
