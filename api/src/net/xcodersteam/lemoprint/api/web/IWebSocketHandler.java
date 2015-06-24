package net.xcodersteam.lemoprint.api.web;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

/**
 * Created by semoro on 26.04.15.
 */
public interface IWebSocketHandler {
    void onWebSocketFrame(WebSocketFrame webSocketFrame,ChannelHandlerContext ctx);
}
