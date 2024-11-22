package discord.util.dcf.gui.base.page;

import discord.util.dcf.gui.base.edit_message.DCFEditMessage;
import discord.util.dcf.gui.base.gui.DCFGui;
import discord.util.dcf.gui.base.gui.IDCFGui;
import discord.util.dcf.gui.util.interaction.IHasInteractionMap;
import discord.util.dcf.util.IMessageBuilder;
import discord.util.dcf.util.IPageButtonBuilder;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

public interface IDCFGuiPage<Parent extends IDCFGui> extends IMessageBuilder, IPageButtonBuilder, IHasInteractionMap {

    MessageCreateData makeMessage();

    default MessageEditData makeEditMessage() {
        return MessageEditData.fromCreateData(makeMessage());
    }

    default Parent addPageToGui() throws IllegalArgumentException {
        if (getParent() instanceof DCFGui gui) {
            gui.addPage(this);
            return getParent();
        } else {
            String error = "%s$getParent() returned %s, which is not a DCFGui for addPage()".formatted(this, getParent());
            throw new IllegalArgumentException(error);
        }
    }

    void remove();

    default void editMessage(MessageEditData data) {
        getParent().editMessage(data);
    }

    default void editMessage(IMessageEditCallback data) {
        getParent().editMessage(data);
    }

    default void editMessage(DCFEditMessage data) {
        getParent().editMessage(data);
    }

    default void editMessage() {
        getParent().editMessage();
    }

    Parent getParent();

    void setParent(DCFGui dcfGui);

    default int getPageNum() {
        return getParent().getPageNum();
    }

    default int getPageSize() {
        return getParent().getPageSize();
    }
}
