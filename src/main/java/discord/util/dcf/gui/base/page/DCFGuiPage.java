package discord.util.dcf.gui.base.page;

import discord.util.dcf.gui.base.gui.IDCFGui;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

public abstract class DCFGuiPage<Parent extends IDCFGui> implements IDCFGuiPage<Parent> {

    private final OnInteractionMap onInteractionMap = new OnInteractionMap();
    protected Parent parent;

    public DCFGuiPage(Parent parent) {
        this.parent = parent;
    }

    @Override
    public final void onButtonClick(ButtonInteractionEvent event) {
        onInteractionMap.onInteraction(event.getButton().getId(), event);
        if (editOnInteraction())
            this.editMessage(event);
    }

    protected boolean editOnInteraction() {
        return true;
    }


    public final void registerButton(String key, OnInteraction<ButtonInteractionEvent> onInteraction) {
        this.onInteractionMap.put(ButtonInteractionEvent.class, key, onInteraction);
    }

    public final Parent getParent() {
        return this.parent;
    }

    @Override
    public void remove() {
        this.editMessage(new MessageEditBuilder().setComponents().build());
    }

}
