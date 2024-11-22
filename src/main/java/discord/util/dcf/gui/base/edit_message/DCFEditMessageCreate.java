package discord.util.dcf.gui.base.edit_message;

import discord.util.dcf.gui.base.GuiMakeFirstMessage;
import java.util.function.Consumer;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.Nullable;

public class DCFEditMessageCreate extends DCFEditMessage {

    private final GuiMakeFirstMessage createFirstMessage;
    private Message msg;

    public DCFEditMessageCreate(GuiMakeFirstMessage createFirstMessage) {
        this.createFirstMessage = createFirstMessage;
    }

    @Override
    public void send(MessageCreateData message, Consumer<Message> onSuccess, Consumer<? super Throwable> onFailure) {
        createFirstMessage.create(message).queue(msg -> {
            this.msg = msg;
            onSuccess.accept(msg);
        }, onFailure);
    }

    @Override
    public void editMessage(MessageEditData messageEditData, @Nullable Consumer<Message> onSuccess,
        @Nullable Consumer<? super Throwable> onFailure) {
        this.msg.editMessage(messageEditData).queue(onSuccess, onFailure);
    }
}
