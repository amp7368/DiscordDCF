package discord.util.dcf.gui.base.page;

import discord.util.dcf.gui.base.gui.DCFGui;
import discord.util.dcf.gui.base.gui.IDCFGui;
import discord.util.dcf.gui.util.interaction.OnInteractionMap;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

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
    public void setParent(DCFGui gui) {
        @SuppressWarnings("unchecked") Parent parent = (Parent) gui;
        this.parent = parent;
    }

    @Override
    public void remove() {
        MessageCreateBuilder createBuilder = MessageCreateBuilder.from(this.makeMessage());
        List<LayoutComponent> components = createBuilder.getComponents()
            .stream()
            .map(this::disableButtons)
            .toList();

        MessageCreateData msg = createBuilder.setComponents(components).build();
        this.editMessage(MessageEditData.fromCreateData(msg));
    }

    private LayoutComponent disableButtons(LayoutComponent layout) {
        if (!(layout instanceof ActionRow row)) return layout;

        List<ActionComponent> buttons = new ArrayList<>();
        for (ActionComponent component : row.getActionComponents()) {
            boolean enabled = component instanceof Button btn &&
                btn.getStyle() == ButtonStyle.LINK;

            buttons.add(component.withDisabled(!enabled));
        }
        return ActionRow.of(buttons);
    }
}
