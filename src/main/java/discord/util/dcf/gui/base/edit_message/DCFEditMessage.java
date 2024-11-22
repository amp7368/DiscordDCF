package discord.util.dcf.gui.base.edit_message;

import discord.util.dcf.gui.base.GuiMakeFirstMessage;
import discord.util.dcf.gui.base.GuiReplyFirstMessage;
import java.util.function.Consumer;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.Nullable;

public abstract class DCFEditMessage {

    public static DCFEditMessage ofReply(GuiReplyFirstMessage createFirstMessage) {
        return new DCFEditMessageReply(createFirstMessage);
    }

    public static DCFEditMessage ofHook(InteractionHook hook) {
        return new DCFEditMessageHook(hook);
    }

    public static DCFEditMessage ofCreate(GuiMakeFirstMessage createFirstMessage) {
        return new DCFEditMessageCreate(createFirstMessage);
    }

    public void send(MessageCreateData message, @Nullable Consumer<Message> onSuccess) {
        send(message, onSuccess, null);
    }

    public abstract void send(MessageCreateData message, @Nullable Consumer<Message> onSuccess,
        @Nullable Consumer<? super Throwable> onFailure);

    public void editMessage(MessageEditData messageEditData) {
        editMessage(messageEditData, null, null);
    }

    public abstract void editMessage(MessageEditData messageEditData, @Nullable Consumer<Message> onSuccess,
        @Nullable Consumer<? super Throwable> onFailure);

}
