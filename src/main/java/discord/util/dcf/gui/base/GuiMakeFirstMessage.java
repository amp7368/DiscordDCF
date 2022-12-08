package discord.util.dcf.gui.base;

import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.FluentRestAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

@FunctionalInterface
public interface GuiMakeFirstMessage {

    FluentRestAction<InteractionHook, ReplyCallbackAction> create(MessageCreateData message);
}
