package discord.util.dcf.gui.base.page;

import discord.util.dcf.gui.base.gui.IDCFGui;
import discord.util.dcf.gui.util.interaction.OnInteractionMap;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

public abstract class DCFGuiPage<Parent extends IDCFGui> implements IDCFGuiPage<Parent> {

    private final OnInteractionMap onInteractionMap = new OnInteractionMap();
    protected Parent parent;

    public DCFGuiPage(Parent parent) {
        this.parent = parent;
        registerButton(btnFirst().getId(), (e) -> this.getParent().page(0));
        registerButton(btnPrev().getId(), (e) -> this.getParent().pagePrev());
        registerButton(btnNext().getId(), (e) -> this.getParent().pageNext());
        registerButton(btnLast().getId(), (e) -> this.getParent().page(getPageSize() - 1));
    }

    @Override
    public OnInteractionMap onInteractionMap() {
        return this.onInteractionMap;
    }

    public final Parent getParent() {
        return this.parent;
    }

    @Override
    public void remove() {
        this.editMessage(new MessageEditBuilder().setComponents().build());
    }

}
