package net.xcodersteam.lemoprint.api.web;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.*;

/**
 * Created by semoro on 30.01.15.
 */
public class RequestHandler extends SimpleChannelInboundHandler<Object> {
    public static Map<String,Class<?>> handlerClasses=new HashMap<>();
    IPageHandler handler;
    Map<String,IPageHandler> handlersCache=new HashMap<>();

    public RequestHandler() {
        super(false);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpObject) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if(  handler instanceof IWebSocketHandler){
            ((IWebSocketHandler) handler).onWebSocketFrame(frame,ctx);
        }
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, HttpObject msg) throws Exception{
        if(!msg.decoderResult().isSuccess()){
            ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.BAD_REQUEST));
            return;
        }
        if(msg instanceof HttpRequest) {
            HttpRequest request= (HttpRequest) msg;
            handler=newOrCached(request.uri());
            if(handler==null)
                handler=newOrCached("/404.html");
            if(!handler.check(request.method(), request.uri()))
                ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.FORBIDDEN));
        }
        if(handler!=null ) {
            if( msg instanceof FullHttpRequest ){
                handler.onRequest((FullHttpRequest) msg, ctx);
            }
            if( handler instanceof IPageHandlerPartialRequest ) {
                if (msg instanceof HttpRequest)
                    ((IPageHandlerPartialRequest) handler).onRequest((HttpRequest) msg, ctx);
                if (msg instanceof HttpContent) {
                    boolean last = (msg instanceof LastHttpContent);
                    ((IPageHandlerPartialRequest) handler).onContent((HttpContent) msg, ctx, last);
                    if (last)
                        handler = null;
                }
            }

        }else
            ctx.fireChannelRead(msg);
    }

    private IPageHandler newOrCached(String url){
        try {
            IPageHandler handler = handlersCache.get(handlersCache.keySet().stream().filter(pattern -> url.matches(pattern)).findFirst().orElse(null));
            if (handler == null) {
                handler = (IPageHandler) Arrays.stream(handlerClasses.get(handlerClasses.keySet().stream().filter(pattern -> url.matches(pattern)).findFirst().get()).getConstructors()).filter(con -> con.getParameterCount() == 0).findFirst().get().newInstance();
                handlersCache.put(url, handler);
            }
            return handler;
        }catch (NoSuchElementException ex){
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        if(handler!=null)
            handler.onChannelInactive(ctx);
    }



    @Override
    protected void finalize() throws Throwable {
        handler=null;
        handlersCache.clear();
        handlersCache=null;
        super.finalize();
    }
}
