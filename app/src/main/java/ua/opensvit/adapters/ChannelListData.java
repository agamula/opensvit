package ua.opensvit.adapters;

import java.util.List;

import ua.opensvit.data.channels.Channel;

public class ChannelListData {
    public final List<String> groups;
    public final List<List<Channel>> channels;

    public ChannelListData(List<String> groups, List<List<Channel>> channels) {
        this.groups = groups;
        this.channels = channels;
    }
}