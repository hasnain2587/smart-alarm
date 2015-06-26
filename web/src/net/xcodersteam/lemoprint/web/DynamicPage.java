package net.xcodersteam.lemoprint.web;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import net.xcodersteam.lemoprint.api.Globals;
import net.xcodersteam.lemoprint.api.web.IPageHandler;
import net.xcodersteam.lemoprint.api.web.PageGenProcessor;
import net.xcodersteam.lemoprint.api.web.RequestHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;

/**
 * Created by semoro on 10.05.15.
 */
public class DynamicPage implements IPageHandler {

    private final static Log log = LogFactory.getLog(DynamicPage.class);

    public DynamicPage() {
    }

    /** Adds pattern for dynamic page handling
     * @param uri - Pattern using for check uri
     */
    public static void addDynamicPage(String uri){
        System.out.println("Adding dynamic page "+uri);
        RequestHandler.handlerClasses.put(uri, DynamicPage.class);
    }

    public static void initialize(){

        pgp=new PageGenProcessor(new Class[]{FullHttpRequest.class},new String[]{"request"});
        pgp.on=new File(Globals.mainDirectory,"/static/404.html");
        pgp.compile();
        pgp.gen(new Object[1]);
    }

    public static PageGenProcessor pgp;

    @Override
    public boolean check(HttpMethod method, String uri) throws Exception {
        return true;
    }

    public String gen(FullHttpRequest request, ChannelHandlerContext ctx,File f){
        synchronized (pgp) {
            pgp.on = f;
            pgp.compile();
            return pgp.gen(request);
        }
    }

    @Override
    public void onRequest(FullHttpRequest request, ChannelHandlerContext ctx) throws Exception {
        long t=System.currentTimeMillis();
        HttpResponseStatus status = HttpResponseStatus.OK;
        String urlM=request.uri();
        File f=new File(Globals.mainDirectory,"/static"+urlM);
        if(f.isDirectory())
            f=new File(f,"index.html");
        if(!f.exists()) {
            new StaticPage().onRequest(request,ctx);
            return;
        }

        ByteBuf bb = io.netty.buffer.Unpooled.copiedBuffer(gen(request,ctx,f), CharsetUtil.UTF_8);

        FullHttpResponse response=new DefaultFullHttpResponse( HttpVersion.HTTP_1_1, status,bb);
        HttpHeaderUtil.setContentLength(response,bb.readableBytes());
        ctx.write(response);

        log.info(ctx.channel().remoteAddress() + " " + request.method() + " Dynamic " + urlM + " result " + status + " time: " +(System.currentTimeMillis()-t)+"ms");

    }

    @Override
    public void onChannelInactive(ChannelHandlerContext ctx) throws Exception {

    }
}
