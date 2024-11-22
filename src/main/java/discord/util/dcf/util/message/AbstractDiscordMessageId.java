package discord.util.dcf.util.message;

import net.dv8tion.jda.api.entities.Message;

public abstract class AbstractDiscordMessageId implements IDiscordMessageId {

    protected long serverId = -1;
    protected long channelId = -1;
    protected long messageId = -1;

    public AbstractDiscordMessageId() {
    }

    public AbstractDiscordMessageId(Message message) {
        setMessage(message);
    }

    public void setMessage(Message message) {
        if (message.hasGuild())
            this.serverId = message.getGuild().getIdLong();
        this.channelId = message.getChannelIdLong();
        this.messageId = message.getIdLong();
    }

    public void setMessage(IDiscordMessageId message) {
        this.serverId = message.getServerId();
        this.channelId = message.getChannelId();
        this.messageId = message.getMessageId();
    }

    public long getChannelId() {
        return channelId;
    }

    public long getServerId() {
        return serverId;
    }

    public long getMessageId() {
        return messageId;
    }
}
