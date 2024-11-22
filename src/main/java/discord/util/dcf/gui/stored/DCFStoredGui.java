package discord.util.dcf.gui.stored;

import discord.util.dcf.DCF;
import discord.util.dcf.gui.base.GuiMakeFirstMessage;
import discord.util.dcf.gui.base.GuiReplyFirstMessage;
import discord.util.dcf.gui.base.edit_message.DCFEditMessage;
import discord.util.dcf.gui.util.interaction.IHasInteractionMap;
import discord.util.dcf.gui.util.interaction.OnInteractionMap;
import java.util.function.Consumer;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

public abstract class DCFStoredGui<Data extends IDCFStoredDormantGui<?>> implements IHasInteractionMap {

    private final OnInteractionMap onInteractionMap = new OnInteractionMap();
    protected long messageId;
    protected Data data;
    protected DCF dcf;

    public DCFStoredGui(long messageId, Data data) {
        this.messageId = messageId;
        this.data = data;
    }

    @Override
    public OnInteractionMap onInteractionMap() {
        return this.onInteractionMap;
    }

    public void send(GuiReplyFirstMessage makeFirstMessage) {
        makeFirstMessage.create(makeMessage()).queue(hook -> {
            hook.retrieveOriginal().queue((original) -> {
                this.messageId = original.getIdLong();
                this.data.setMessage(original);
                this.save();
            });
        });
    }

    public void send(GuiMakeFirstMessage makeFirstMessage) {
        makeFirstMessage.create(makeMessage()).queue(message -> {
            this.messageId = message.getIdLong();
            this.data.setMessage(message);
            this.save();
        });
    }

    public void editMessage() {
        retrieveMessage(msg -> {
            if (msg == null) return;
            msg.editMessage(this.makeEditMessage()).queue();
        });
    }

    public void retrieveMessage(Consumer<Message> consumer) {
        TextChannel channel = dcf.jda().getTextChannelById(getData().getChannelId());
        if (channel == null) {
            consumer.accept(null);
            return;
        }
        channel.retrieveMessageById(this.messageId).queue(consumer);
    }

    @Override
    public void editMessage(IMessageEditCallback event) {
        event.editMessage(makeEditMessage()).queue();
    }

    public void editMessage(DCFEditMessage edit) {
        edit.editMessage(makeEditMessage());
    }

    protected MessageEditData makeEditMessage() {
        return MessageEditData.fromCreateData(makeMessage());
    }

    protected abstract MessageCreateData makeMessage();

    public abstract void save();

    public abstract void remove();

    public Data getData() {
        return this.data;
    }

    @Deprecated
    public Data serialize() {
        return this.getData();
    }

    public void setDCF(DCF dcf) {
        this.dcf = dcf;
    }

    public long getMessageId() {
        return this.messageId;
    }
}
