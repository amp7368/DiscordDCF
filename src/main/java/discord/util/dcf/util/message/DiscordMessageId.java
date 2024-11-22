package discord.util.dcf.util.message;

import discord.util.dcf.DCF;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.Nullable;

/**
 * Has a DCF reference
 */
public class DiscordMessageId extends AbstractDiscordMessageId {

    protected transient DCF dcf;

    public DiscordMessageId() {
    }

    public DiscordMessageId(Message message) {
        super(message);
    }

    public DiscordMessageId(Message message, DCF dcf) {
        super(message);
        this.dcf = dcf;
    }

    public DCF getDCF() {
        return dcf;
    }

    public DiscordMessageId setDCF(DCF dcf) {
        this.dcf = dcf;
        return this;
    }

    @Nullable
    public Guild getServer() {
        return getDCF().jda().getGuildById(getServerId());
    }

    @Nullable
    public TextChannel getChannel() {
        return getDCF().jda().getTextChannelById(getChannelId());
    }

    @Nullable
    public RestAction<Message> retrieveMessage() {
        TextChannel channel = getChannel();
        if (channel == null) return null;

        return channel.retrieveMessageById(getMessageId());
    }

    public void tryEditMessage(MessageEditData editData) {
        RestAction<Message> msg = editMessage(editData);
        if (msg != null) msg.queue();
    }

    @Nullable
    public RestAction<Message> editMessage(MessageEditData editData) {
        TextChannel channel = getChannel();
        if (channel == null) return null;

        return channel.editMessageById(getMessageId(), editData);
    }

    public void tryDeleteMessage() {
        AuditableRestAction<Void> delete = deleteMessage();
        if (delete != null) delete.queue();
    }

    @Nullable
    public AuditableRestAction<Void> deleteMessage() {
        TextChannel channel = getChannel();
        if (channel == null) return null;

        return channel.deleteMessageById(getMessageId());
    }
}
