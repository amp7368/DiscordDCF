package discord.util.dcf.gui.base.gui;

import discord.util.dcf.DCF;
import discord.util.dcf.gui.base.GuiReplyFirstMessage;
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
    protected final List<IDCFGuiPage<?>> pageMap = new ArrayList<>();
    protected final List<IDCFGuiPage<?>> subPages = new ArrayList<>();
    private final GuiReplyFirstMessage createFirstMessage;
    protected InteractionHook hook;
    protected int page = 0;

    private long lastUpdated = System.currentTimeMillis();
    private long messageId;


    public DCFGui(DCF dcf, GuiReplyFirstMessage createFirstMessage) {
        this.dcf = dcf;
        this.createFirstMessage = createFirstMessage;
    }

    public IDCFGuiPage<?> getPage() {
        return this.subPages.isEmpty() ? pageMap.get(page) : subPages.get(subPages.size() - 1);
    }

    @Override
    public void pageNext() {
        this.page = this.verifyPage(this.page + 1);
    }

    @Override
    public void pagePrev() {
        this.page = this.verifyPage(this.page - 1);
    }

    @Override
    public void page(int pageNum) {
        this.page = this.verifyPage(pageNum);
    }

    private int verifyPage(int i) {
        return Math.max(0, Math.min(this.pageMap.size() - 1, i));
    }

    @Override
    public int getPageNum() {
        return page;
    }

    @Override
    public int getPageSize() {
        return pageMap.size();
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
        pageMap.addAll(List.of(pageGuis));
        Arrays.stream(pageGuis).forEach(page -> page.setParent(this));
        return this;
    }

    public DCFGui addSubPage(IDCFGuiPage<?>... subPages) {
        this.subPages.addAll(List.of(subPages));
        Arrays.stream(subPages).forEach(page -> page.setParent(this));
        return this;
    }

    public DCFGui clearSubPages() {
        this.subPages.clear();
        return this;
    }

    public DCFGui popSubPage() {
        this.subPages.remove(this.subPages.size() - 1);
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
        getPage().onButtonInteraction(event);
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
