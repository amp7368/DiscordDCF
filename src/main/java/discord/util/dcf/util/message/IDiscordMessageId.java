package discord.util.dcf.util.message;

import discord.util.dcf.DCF;

public interface IDiscordMessageId {

    static DiscordMessageId withDCF(IDiscordMessageId message, DCF dcf) {
        DiscordMessageId dcfMessage = new DiscordMessageId().setDCF(dcf);
        dcfMessage.setMessage(message);
        return dcfMessage;
    }

    long getServerId();

    long getChannelId();

    long getMessageId();
}
