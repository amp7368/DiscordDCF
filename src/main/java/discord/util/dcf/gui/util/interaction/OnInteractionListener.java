package discord.util.dcf.gui.util.interaction;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

public interface OnInteractionListener {

    default void onButtonInteraction(ButtonInteractionEvent event) {
    }

    default void onSelectStringInteraction(StringSelectInteractionEvent event) {
    }

    default void onSelectEntityInteraction(EntitySelectInteractionEvent event) {
    }
}
