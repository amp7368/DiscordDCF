package discord.util.dcf.gui.base.gui;

import discord.util.dcf.DCF;
import discord.util.dcf.gui.base.GuiReplyFirstMessage;
import discord.util.dcf.gui.base.edit_message.DCFEditMessage;
import discord.util.dcf.gui.base.edit_message.DCFEditMessageReply;
import discord.util.dcf.gui.base.page.IDCFGuiPage;
import discord.util.dcf.util.TimeMillis;
import discord.util.dcf.util.message.DiscordMessageId;
import discord.util.dcf.util.message.DiscordMessageIdData;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DCFGui implements IDCFGui {

    protected final DCF dcf;
    protected final List<IDCFGuiPage<?>> pageMap = new ArrayList<>();
    protected final List<IDCFGuiPage<?>> subPages = new ArrayList<>();
    private final DCFEditMessage editMessage;
    private final DiscordMessageIdData message = new DiscordMessageIdData();
    protected int page = 0;
    private Instant lastUpdated = Instant.now();
    @Nullable
    private Duration timeToOld = null;

    public DCFGui(DCF dcf, DCFEditMessage editMessage) {
        this.dcf = dcf;
        this.editMessage = editMessage;
    }

    public DCFGui(DCF dcf, GuiReplyFirstMessage createFirstMessage) {
        this(dcf, new DCFEditMessageReply(createFirstMessage));
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
        send(null, null);
    }

    public void send(@Nullable Consumer<Message> onSuccess, @Nullable Consumer<? super Throwable> onFailure) {
        this.editMessage.send(getPage().makeMessage(), (original) -> {
            this.message.setMessage(original);
            if (onSuccess != null)
                onSuccess.accept(original);
            this.dcf.guis().addGui(this);
            resetLastUpdatedTimer();
        }, onFailure);
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

    @Override
    public void editMessage() {
        editMessage.editMessage(getPage().makeEditMessage());
    }

    @Override
    public void editMessage(MessageEditData data) {
        editMessage.editMessage(data);
    }

    @Override
    public void editMessage(IMessageEditCallback callback) {
        callback.editMessage(getPage().makeEditMessage()).queue();
    }

    public void editMessage(DCFEditMessage callback) {
        callback.editMessage(getPage().makeEditMessage());
    }

    @Override
    public void onButtonClick(ButtonInteractionEvent event) {
        getPage().onButtonInteraction(event);
        resetLastUpdatedTimer();
    }

    @Override
    public void onSelectString(StringSelectInteractionEvent event) {
        getPage().onSelectStringInteraction(event);
        resetLastUpdatedTimer();
    }

    @Override
    public void onSelectEntity(EntitySelectInteractionEvent event) {
        getPage().onSelectEntityInteraction(event);
        resetLastUpdatedTimer();
    }


    public void resetLastUpdatedTimer() {
        this.lastUpdated = Instant.now();
        Instant nextUpdate = this.lastUpdated.plus(getTimeToOld());
        this.dcf.guis().submitScheduleTrimAt(this, nextUpdate);
    }

    @NotNull
    public Duration getTimeToOld() {
        if (timeToOld == null) return Duration.ofMillis(getMillisToOld());
        return timeToOld;
    }

    /**
     * @param timeToOld the duartion until this GUI becomes "old"
     * @return this
     * @apiNote passing null resets timeToOld to default
     */
    public DCFGui setTimeToOld(@Nullable Duration timeToOld) {
        this.timeToOld = timeToOld;
        return this;
    }

    @Deprecated
    public long getMillisToOld() {
        return TimeMillis.MINUTE_15;
    }

    public boolean shouldRemove() {
        Instant canUpdateAt = lastUpdated.plus(getTimeToOld());
        return !canUpdateAt.isAfter(Instant.now());
    }

    public void remove() {
        getPage().remove();
    }

    public DCF getDCF() {
        return dcf;
    }

    public DiscordMessageId getMessage() {
        return this.message.withDCF(dcf);
    }

    public long getMessageId() {
        return this.message.getMessageId();
    }
}
