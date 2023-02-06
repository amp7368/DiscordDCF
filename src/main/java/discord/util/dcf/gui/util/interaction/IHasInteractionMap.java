package discord.util.dcf.gui.util.interaction;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;

public interface IHasInteractionMap {

    OnInteractionMap onInteractionMap();

    default void onButtonInteraction(ButtonInteractionEvent event) {
        onInteractionMap().onInteraction(event.getButton().getId(), event);
        if (editOnInteraction()) this.editMessage(event);
    }

    default void registerButton(String key, OnInteraction<ButtonInteractionEvent> onInteraction) {
        onInteractionMap().put(ButtonInteractionEvent.class, key, onInteraction);
    }

    void editMessage(IMessageEditCallback event);

    default boolean editOnInteraction() {
        return true;
    }


}
