package discord.util.dcf.gui.stored;

public abstract class DCFStoredDormantGui<Gui extends DCFStoredGui<?>> implements IDCFStoredDormantGui<Gui> {

    public long messageId = -1;
    public long channelId = -1;
    public long serverId = -1;

    @Override
    public long getMessageId() {
        return this.messageId;
    }

    @Override
    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    @Override
    public long getChannelId() {
        return channelId;
    }

    @Override
    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    @Override
    public long getServerId() {
        return serverId;
    }

    @Override
    public void setServerId(long serverId) {
        this.serverId = serverId;
    }
}
