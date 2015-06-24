package net.xcodersteam.lemoprint;

import com.github.mraa4j.GPIO;
import com.github.mraa4j.SPI;
import com.github.mraa4j.enums.GPIODirT;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import net.xcodersteam.lemoprint.api.Globals;
import net.xcodersteam.lemoprint.api.web.BlackHoleHandler;
import net.xcodersteam.lemoprint.api.web.PageHandler;
import net.xcodersteam.lemoprint.api.web.RequestHandler;
import net.xcodersteam.lemoprint.web.PageDef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;

import java.io.File;

/**
 * Created by semoro on 23.04.15.
 */
public class Main {

    public static ChannelFuture f;
    private static final Log log= LogFactory.getLog(Main.class);


    public static void main(String[] args) throws InterruptedException {
        long d=System.currentTimeMillis();
        new DreamPhaseDetector().start();
        new Thread(()->{
            try {
                GPIO gpio = new GPIO(13);
                gpio.setDirection(GPIODirT.MRAA_GPIO_OUT);
                while (true) {
                    gpio.write(1);
                    Thread.sleep(500);
                    gpio.write(0);
                    Thread.sleep(500);
                }
            }
            catch (Exception e){
                log.error(e);
            }

        }).start();

        System.out.println("Wake up, NEO!");

        Reflections reflections = new Reflections(ClasspathHelper.forPackage("net.xcodersteam.lemoprint"),
                new SubTypesScanner(),new TypeAnnotationsScanner());
        reflections.getTypesAnnotatedWith(PageHandler.class).forEach(c -> {

            for(String n:c.getAnnotation(PageHandler.class).names()){
                RequestHandler.handlerClasses.put(n,c);
                System.out.println("Page "+n+" from "+c.getCanonicalName());
            }

        });
		Globals.mainDirectory=new File("").getAbsoluteFile();
        PageDef.addStaticPages();
        System.out.println("Matrix has you.");
        System.out.println();
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();

            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)// (3)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {

                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast(new HttpServerCodec());
                            pipeline.addLast(new HttpObjectAggregator(Integer.MAX_VALUE));

                            pipeline.addLast(new WebSocketClientCompressionHandler());
                            pipeline.addLast("handler", new RequestHandler());
                            pipeline.addLast("blackhole", new BlackHoleHandler());

                            // Remove the following line if you don't want automatic content compression.
                            pipeline.addLast(new HttpContentCompressor());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            // Bind and start to accept incoming connections.
            f = b.bind(8080).sync(); // (7)

            log.info("Server started up. In "+(System.currentTimeMillis()-d)+"ms");
            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
            log.info("Socket IO shutdown");
    }

}
