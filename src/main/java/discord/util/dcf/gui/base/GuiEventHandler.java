package discord.util.dcf.gui.base;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

public interface GuiEventHandler {

    void onButtonClick(ButtonInteractionEvent event);

    void onSelectEntity(EntitySelectInteractionEvent event);

    void onSelectString(StringSelectInteractionEvent event);

    long getId();

    boolean shouldRemove();

    void remove();

}
