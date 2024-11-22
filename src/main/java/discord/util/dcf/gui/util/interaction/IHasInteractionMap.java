package discord.util.dcf.gui.util.interaction;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;

public interface IHasInteractionMap extends OnInteractionListener {

    OnInteractionMap onInteractionMap();

    @Override
    default void onButtonInteraction(ButtonInteractionEvent event) {
        onInteractionMap().onInteraction(event.getButton().getId(), event);
        if (shouldReply(event)) this.editMessage(event);
    }

    @Override
    default void onSelectStringInteraction(StringSelectInteractionEvent event) {
        onInteractionMap().onInteraction(event.getComponentId(), event);
        if (shouldReply(event)) this.editMessage(event);
    }

    @Override
    default void onSelectEntityInteraction(EntitySelectInteractionEvent event) {
        onInteractionMap().onInteraction(event.getComponentId(), event);
        if (shouldReply(event)) this.editMessage(event);
    }

    default void registerButton(String key, OnInteraction<ButtonInteractionEvent> onInteraction) {
        onInteractionMap().put(ButtonInteractionEvent.class, key, onInteraction);
    }

    default void registerSelectString(String key, OnInteraction<StringSelectInteractionEvent> onInteraction) {
        onInteractionMap().put(StringSelectInteractionEvent.class, key, onInteraction);
    }

    default void registerSelectEntity(String key, OnInteraction<EntitySelectInteractionEvent> onInteraction) {
        onInteractionMap().put(EntitySelectInteractionEvent.class, key, onInteraction);
    }

    default boolean shouldReply(IMessageEditCallback event) {
        return editOnInteraction() && !event.isAcknowledged();
    }

    void editMessage(IMessageEditCallback event);

    default boolean editOnInteraction() {
        return true;
    }
}
