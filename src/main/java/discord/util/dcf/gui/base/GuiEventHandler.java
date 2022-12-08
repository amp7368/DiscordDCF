package discord.util.dcf.gui.base;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public interface GuiEventHandler {

    void onButtonClick(ButtonInteractionEvent event);
}
