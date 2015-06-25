package net.xcodersteam.lemoprint.web;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import net.xcodersteam.lemoprint.api.web.IPageHandler;
import net.xcodersteam.lemoprint.api.web.PageHandler;

/**
 * Created by one on 25.06.15.
 */
@PageHandler(names="/setalarm.j")
public class SetAlarmPage implements IPageHandler {

    @Override
    public boolean check(HttpMethod method, String uri) throws Exception {
        return method.equals(HttpMethod.GET);
    }

    @Override
    public void onRequest(FullHttpRequest request, ChannelHandlerContext ctx) throws Exception {

        QueryStringDecoder qsd=new QueryStringDecoder(request.uri());
        String time=qsd.parameters().get("time").get(0);
    }

    @Override
    public void onChannelInactive(ChannelHandlerContext ctx) throws Exception {

    }
}
