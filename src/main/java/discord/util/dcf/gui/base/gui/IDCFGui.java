package discord.util.dcf.gui.base.gui;

import discord.util.dcf.gui.base.GuiEventHandler;
import discord.util.dcf.util.IMessageBuilder;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

public interface IDCFGui extends GuiEventHandler, IMessageBuilder {

    long getId();

    boolean shouldRemove();

    void editMessage();

    void remove();

    void editMessage(MessageEditData data);

    void editMessage(IMessageEditCallback edit);

    int getPageNum();

    int getPageSize();
}
