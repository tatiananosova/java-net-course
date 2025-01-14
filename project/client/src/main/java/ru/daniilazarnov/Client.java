package ru.daniilazarnov;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.log4j.Logger;

import static ru.daniilazarnov.Constants.LOCALHOST;
import static ru.daniilazarnov.Constants.PORT;

public class Client {
    private static final Logger LOGGER = Logger.getLogger(Client.class);

    public static void main(String[] args) throws Exception {
        LOGGER.info("Client starting!");

        String host = System.getProperty("m_host", LOCALHOST);
        int port = Integer.getInteger("m_port", PORT);

        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {

                @Override
                public void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast("Decoder", new EncoderDecoder.Decoder());
                    ch.pipeline().addLast("Encoder", new EncoderDecoder.Encoder());
                    ch.pipeline().addLast("Client Handler", new ClientHandler());
                }
            });

            ChannelFuture f = b.connect(host, port).sync();

            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
