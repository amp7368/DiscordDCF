package discord.util.dcf.gui.base;

import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

@FunctionalInterface
public interface GuiMakeFirstMessage {

    MessageCreateAction create(MessageCreateData message);
}
