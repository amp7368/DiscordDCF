package discord.util.dcf.gui.stored;

import discord.util.dcf.util.message.IDiscordMessageId;
import net.dv8tion.jda.api.entities.Message;

public interface IDCFStoredDormantGui<Gui extends DCFStoredGui<?>> extends IDiscordMessageId {

    void setMessageId(long messageId);

    void setChannelId(long channelId);

    void setServerId(long serverId);

    default String getMessageLink() {
        return "https://discord.com/channels/%d/%d/%d".formatted(this.getServerId(), this.getChannelId(), this.getMessageId());
    }

    Gui load();

    default Gui create() {
        return load();
    }

    default void setMessage(Message message) {
        if (message.hasGuild())
            this.setServerId(message.getGuildIdLong());
        this.setChannelId(message.getChannelIdLong());
        this.setMessageId(message.getIdLong());
    }

}
