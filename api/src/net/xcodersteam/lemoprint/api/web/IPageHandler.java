package net.xcodersteam.lemoprint.api.web;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

/**
 * Created by semoro on 30.01.15.
 */
public interface IPageHandler {
    boolean check(HttpMethod method,String uri) throws Exception;
    void onRequest(FullHttpRequest request,ChannelHandlerContext ctx) throws Exception;
    void onChannelInactive(ChannelHandlerContext ctx) throws Exception;
}
