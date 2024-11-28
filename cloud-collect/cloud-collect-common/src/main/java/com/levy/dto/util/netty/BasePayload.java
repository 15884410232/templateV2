package com.levy.dto.util.netty;

public abstract class BasePayload {
    BaseSimpleChannelInboundHandler baseSimpleChannelInboundHandler;
    private Object object;

    public abstract String getDownloadUrl();

    public BaseSimpleChannelInboundHandler getSimpleChannelInboundHandler(){
        return this.baseSimpleChannelInboundHandler;
    };

    public void setSimpleChannelInboundHandler(BaseSimpleChannelInboundHandler baseSimpleChannelInboundHandler) {
        this.baseSimpleChannelInboundHandler=baseSimpleChannelInboundHandler;
    }

}

