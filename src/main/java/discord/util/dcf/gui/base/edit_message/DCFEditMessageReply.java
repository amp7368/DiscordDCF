package discord.util.dcf.gui.base.edit_message;

import discord.util.dcf.gui.base.GuiReplyFirstMessage;
import java.util.function.Consumer;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.Nullable;

public class DCFEditMessageReply extends DCFEditMessage {

    private final GuiReplyFirstMessage createFirstMessage;
    private InteractionHook hook;

    public DCFEditMessageReply(GuiReplyFirstMessage createFirstMessage) {
        this.createFirstMessage = createFirstMessage;
    }

    @Override
    public void send(MessageCreateData message, Consumer<Message> onSuccess, Consumer<? super Throwable> onFailure) {
        createFirstMessage.create(message).queue(hook -> {
            this.hook = hook;
            hook.retrieveOriginal().queue(onSuccess);
        });
    }

    @Override
    public void editMessage(MessageEditData messageEditData, @Nullable Consumer<Message> onSuccess,
        @Nullable Consumer<? super Throwable> onFailure) {
        if (!hookIsValid()) return;
        this.hook.editOriginal(messageEditData).queue(onSuccess, onFailure);
    }

    private boolean hookIsValid() {
        return hook != null && !hook.isExpired();
    }
}
