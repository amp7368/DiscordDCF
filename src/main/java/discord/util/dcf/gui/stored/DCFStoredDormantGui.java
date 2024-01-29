package discord.util.dcf.gui.stored;

public abstract class DCFStoredDormantGui<Gui extends DCFStoredGui<?>> implements IDCFStoredDormantGui<Gui> {

    public long messageId = -1;
    public long channelId = -1;

    @Override
    public long getId() {
        return this.messageId;
    }

    @Override
    public void setId(long messageId) {
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
}
