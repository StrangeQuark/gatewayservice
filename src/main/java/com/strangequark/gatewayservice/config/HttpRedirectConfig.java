package com.strangequark.gatewayservice.config;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/*
    This file is for redirecting traffic from http (port 80) to https (port 443)
    It is commented out for development but should be made active when doing a prod deployment
 */
@Configuration
public class HttpRedirectConfig {
//    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRedirectConfig.class);
//
//    @Value("${hosts}")
//    private List<String> hosts;
//
//    @PostConstruct
//    public void startRedirectServer() {
//        new Thread(() -> {
//            LOGGER.info("Start a new thread for https redirect server");
//
//            EventLoopGroup bossGroup = new NioEventLoopGroup(1);
//            EventLoopGroup workerGroup = new NioEventLoopGroup();
//            try {
//                ServerBootstrap b = new ServerBootstrap();
//                b.group(bossGroup, workerGroup)
//                        .channel(NioServerSocketChannel.class)
//                        .childHandler(new ChannelInitializer<>() {
//                            @Override
//                            protected void initChannel(Channel ch) {
//                                LOGGER.info("Begin channel initialization");
//                                ChannelPipeline p = ch.pipeline();
//                                p.addLast(new HttpServerCodec());
//                                p.addLast(new HttpObjectAggregator(65536));
//                                p.addLast(new SimpleChannelInboundHandler<FullHttpRequest>() {
//                                    @Override
//                                    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) {
//                                        LOGGER.info("Being channel read");
//                                        String host = req.headers().get(HttpHeaderNames.HOST);
//
//                                        // If the host in the request header is null or not in the valid hosts list
//                                        // then we redirect to the first host in the hosts list
//                                        if(host == null || !hosts.contains(host.toLowerCase())) {
//                                            String firstHost = hosts.getFirst().toLowerCase();
//
//                                            LOGGER.error("Host " + host + " is not a valid host, redirecting to " + firstHost);
//                                            host = firstHost;
//                                        }
//
//                                        String redirectUrl = "https://" + host + req.uri();
//
//                                        FullHttpResponse response = new DefaultFullHttpResponse(
//                                                HttpVersion.HTTP_1_1,
//                                                HttpResponseStatus.MOVED_PERMANENTLY,
//                                                Unpooled.EMPTY_BUFFER
//                                        );
//                                        response.headers().set(HttpHeaderNames.LOCATION, redirectUrl);
//                                        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
//                                    }
//                                });
//                            }
//                        });
//
//                LOGGER.info("Bind to port 8080");
//                b.bind(8080).sync().channel().closeFuture().sync();
//            } catch (InterruptedException e) {
//                LOGGER.error("Thread interrupted");
//                LOGGER.error(e.getMessage());
//                Thread.currentThread().interrupt();
//            } finally {
//                LOGGER.info("Shutdown after completion");
//                bossGroup.shutdownGracefully();
//                workerGroup.shutdownGracefully();
//            }
//        }).start();
//    }
}