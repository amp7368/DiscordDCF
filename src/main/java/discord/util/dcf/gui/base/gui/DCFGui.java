package discord.util.dcf.gui.base.gui;

import discord.util.dcf.DCF;
import discord.util.dcf.gui.base.GuiMakeFirstMessage;
import discord.util.dcf.gui.base.page.IDCFGuiPage;
import discord.util.dcf.util.TimeMillis;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

public class DCFGui implements IDCFGui {

    protected final DCF dcf;
    private final GuiMakeFirstMessage createFirstMessage;

    protected InteractionHook hook;

    protected final List<IDCFGuiPage<?>> pageMap = new ArrayList<>();
    protected final List<IDCFGuiPage<?>> subPages = new ArrayList<>();
    protected int page = 0;

    private long lastUpdated = System.currentTimeMillis();
    private long messageId;


    public DCFGui(DCF dcf, GuiMakeFirstMessage createFirstMessage) {
        this.dcf = dcf;
        this.createFirstMessage = createFirstMessage;
    }

    public IDCFGuiPage<?> getPage() {
        return this.subPages.isEmpty() ? pageMap.get(page) : subPages.get(0);
    }

    public void send() {
        createFirstMessage.create(getPage().makeMessage()).queue((hook) -> {
            this.hook = hook;
            this.hook.retrieveOriginal().queue((original) -> {
                this.messageId = original.getIdLong();
                this.dcf.guis().addGui(this);
            });
        });
    }

    public DCFGui addPage(IDCFGuiPage<?>... pageGuis) {
        pageMap.addAll(Arrays.asList(pageGuis));
        return this;
    }

    public DCFGui addSubPage(IDCFGuiPage<?>... subPages) {
        this.subPages.addAll(Arrays.asList(subPages));
        return this;
    }

    public void editMessage() {
        if (hookIsValid()) hook.editOriginal(getPage().makeEditMessage()).queue();
    }

    public void editMessage(MessageEditData data) {
        if (hookIsValid()) hook.editOriginal(data).queue();
    }

    public void editMessage(IMessageEditCallback callback) {
        if (hookIsValid()) callback.editMessage(getPage().makeEditMessage()).queue();
    }

    private boolean hookIsValid() {
        return hook != null && !hook.isExpired();
    }

    public void onButtonClick(ButtonInteractionEvent event) {
        getPage().onButtonClick(event);
        resetLastUpdatedTimer();
    }


    public void resetLastUpdatedTimer() {
        this.lastUpdated = System.currentTimeMillis();
    }

    public long getMillisToOld() {
        return TimeMillis.MINUTE_15;
    }


    public boolean shouldRemove() {
        return System.currentTimeMillis() - this.lastUpdated > getMillisToOld();
    }

    public void remove() {
        getPage().remove();
    }


    public long getId() {
        return this.messageId;
    }
}
