package ua.opensvit.utils;

import ua.opensvit.data.channels.Channel;

public class ChannelFactory {
    private ChannelFactory() {
    }

    private static final ChannelFactory instance = new ChannelFactory();

    public static ChannelFactory getInstance() {
        return instance;
    }

    private static int sId = 0;

    public Channel createChannel() {
        Channel channel = new Channel();
        channel.setArchive("asdsa");
        channel.setName("Lala" + sId);
        channel.setId(sId++);
        channel.setFavorits(true);
        channel.setLogo("dds");
        return channel;
    }
}
