package net.xcodersteam.lemoprint.api.web;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;

/**
 * Created by semoro on 26.04.15.
 */
public interface IPageHandlerPartialRequest extends IPageHandler{
    void onRequest(HttpRequest request,ChannelHandlerContext ctx) throws Exception;
    void onContent(HttpContent content,ChannelHandlerContext ctx,boolean last) throws Exception;
}
