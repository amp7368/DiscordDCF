package discord.util.dcf.gui.base.edit_message;

import java.util.function.Consumer;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DCFEditMessageHook extends DCFEditMessage {

    @NotNull
    private final InteractionHook hook;

    public DCFEditMessageHook(@NotNull InteractionHook hook) {
        this.hook = hook;
    }

    @Override
    public void send(MessageCreateData message, @Nullable Consumer<Message> onSuccess,
        @Nullable Consumer<? super Throwable> onFailure) {
        editMessage(MessageEditData.fromCreateData(message), onSuccess, onFailure);
    }

    @Override
    public void editMessage(MessageEditData messageEditData, @Nullable Consumer<Message> onSuccess,
        @Nullable Consumer<? super Throwable> onFailure) {
        if (!hookIsValid()) return;
        this.hook.editOriginal(messageEditData).queue(onSuccess, onFailure);
    }

    private boolean hookIsValid() {
        return !hook.isExpired();
    }
}
