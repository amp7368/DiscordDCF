package discord.util.dcf.gui.base;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

public interface GuiEventHandler {

    void onButtonClick(ButtonInteractionEvent event);

    void onSelectEntity(EntitySelectInteractionEvent event);

    void onSelectString(StringSelectInteractionEvent event);

    /**
     * Message id is a snowflake and therefore unique
     *
     * @return The id of the message behind the GUI
     */
    long getMessageId();

    @Deprecated
    default long getId() {
        return getMessageId();
    }

    boolean shouldRemove();

    void remove();

}
