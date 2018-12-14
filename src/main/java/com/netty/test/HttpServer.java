package com.netty.test;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;


import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class HttpServer {
    private final int port;

    public HttpServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("start");
        Socket socket = new Socket();
        socket.setKeepAlive(true);
        SocketAddress remoteAddr = new InetSocketAddress("localhost",8086);
        socket.connect(remoteAddr);
        byte[] output = new byte[]{(byte) 1, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
        socket.getOutputStream().write(output);
        byte[] input = new byte[64];
        int readByte = socket.getInputStream().read(input);
        System.out.println("readByte " + readByte);
        for (int i = 0; i < readByte; i++) {
            System.out.println("read [" + i + "]:" + input[i]);
        }
        socket.close();

       /* int port = 8080;
        new HttpServer(port).start();*/
    }

    public void start() throws Exception {
        ServerBootstrap b = new ServerBootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        b.group(group)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch)
                            throws Exception {
                        System.out.println("initChannel ch:" + ch);
                        ch.pipeline()/*.addLast(
                                new ToIntegerDecoder(),
                                new ShortToByteEncoder())*/
                                //增加自定义类型编码解码器
                                .addLast("decoder", new HttpRequestDecoder())   // 1
                                .addLast("encoder", new HttpResponseEncoder())  // 2
                                .addLast("aggregator", new HttpObjectAggregator(1024 * 256))    // 3
                                .addLast("handler", new HttpHandler());


                    }
    })
            .option(ChannelOption.SO_BACKLOG, 128) // deterining the number of connections queued
                .childOption(ChannelOption.AUTO_CLOSE, Boolean.TRUE);

        b.bind(port).sync();
    }


    }

