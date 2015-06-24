package net.xcodersteam.lemoprint.web;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import net.xcodersteam.lemoprint.api.Globals;
import net.xcodersteam.lemoprint.api.web.IPageHandler;
import net.xcodersteam.lemoprint.api.web.RequestHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.Optional;

/** Handler of static pages and data: Images, CSS, etc...
 * Created by semoro on 23.04.15.
 */

public class StaticPage implements IPageHandler {

    private final static Log log = LogFactory.getLog(StaticPage.class);


    /** Adds pattern for static page handling
     * @param uri - Pattern using for check uri
     */
    public static void addStaticPage(String uri){
        System.out.println("Adding static page "+uri);
        RequestHandler.handlerClasses.put(uri, StaticPage.class);
    }

    @Override
    public boolean check(HttpMethod method, String uri) throws Exception {
        return method==HttpMethod.GET;
    }

    @Override
    public void onRequest(FullHttpRequest request, ChannelHandlerContext ctx) throws Exception {
        HttpResponseStatus status = HttpResponseStatus.OK;
        String urlM=request.uri();

        File f=new File(Globals.mainDirectory,"/static"+urlM);
        if(f.isDirectory())
            f=new File(f,"index.html");
        if(!f.exists()) {
            f=new File(Globals.mainDirectory,"/static/404.html");
            status=HttpResponseStatus.NOT_FOUND;
        }
        log.info(ctx.channel().remoteAddress() + " GET Static " + urlM + " result " + status);

        FileInputStream fis=new FileInputStream(f);
        ByteBuf bb=io.netty.buffer.Unpooled.buffer(fis.available());
        bb.writeBytes(fis, fis.available());

        FullHttpResponse response=new DefaultFullHttpResponse( HttpVersion.HTTP_1_1, status,bb);
        HttpHeaderUtil.setContentLength(response, bb.readableBytes());
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, Optional.ofNullable(Files.probeContentType(f.toPath())).orElse("application/octet-stream"));
        ctx.write(response);
    }

    @Override
    public void onChannelInactive(ChannelHandlerContext ctx) throws Exception {

    }
}
