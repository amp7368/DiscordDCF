package discord.util.dcf.gui.base.page;

import discord.util.dcf.gui.base.GuiEventHandler;
import discord.util.dcf.gui.base.gui.IDCFGui;
import discord.util.dcf.util.IMessageBuilder;
import discord.util.dcf.util.IPageButtonBuilder;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

public interface IDCFGuiPage<Parent extends IDCFGui> extends GuiEventHandler, IMessageBuilder, IPageButtonBuilder {

    MessageCreateData makeMessage();

    default MessageEditData makeEditMessage() {
        return MessageEditData.fromCreateData(makeMessage());
    }

    void remove();


    default void editMessage(MessageEditData data) {
        getParent().editMessage(data);
    }

    default void editMessage(IMessageEditCallback data) {
        getParent().editMessage(data);
    }

    default void editMessage() {
        getParent().editMessage();
    }

    Parent getParent();

    default int getPageNum() {
        return getParent().getPageNum();
    }
    default int getPageSize() {
        return getParent().getPageSize();
    }
}
