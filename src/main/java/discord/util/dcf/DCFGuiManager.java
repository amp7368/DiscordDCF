package discord.util.dcf;

import discord.util.dcf.gui.base.GuiEventHandler;
import discord.util.dcf.util.TimeMillis;
import java.util.HashMap;
import java.util.Iterator;
import java.util.function.Consumer;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class DCFGuiManager {

    private final HashMap<Long, GuiEventHandler> guis = new HashMap<>();
    private final DCF dcf;
    private long nextTrimTime = 0;

    public DCFGuiManager(DCF dcf) {
        this.dcf = dcf;
    }

    private void trim() {
        if (System.currentTimeMillis() < nextTrimTime) return;
        nextTrimTime = System.currentTimeMillis() + TimeMillis.minToMillis(1);
        synchronized (guis) {
            Iterator<GuiEventHandler> iterator = guis.values().iterator();
            while (iterator.hasNext()) {
                GuiEventHandler gui = iterator.next();
                if (gui.shouldRemove()) {
                    iterator.remove();
                    gui.remove();
                }
            }
        }
    }

    public void addGui(GuiEventHandler gui) {
        synchronized (guis) {
            guis.put(gui.getId(), gui);
        }
    }

    public void remove(long id) {
        synchronized (guis) {
            guis.remove(id);
        }
    }

    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        onEvent(event.getMessageIdLong(), gui -> gui.onButtonClick(event));
    }

    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        onEvent(event.getMessageIdLong(), gui -> gui.onSelectString(event));
    }

    public void onEntitySelectInteraction(@NotNull EntitySelectInteractionEvent event) {
        onEvent(event.getMessageIdLong(), gui -> gui.onSelectEntity(event));
    }

    private void onEvent(long messageId, Consumer<GuiEventHandler> callback) {
        GuiEventHandler gui;
        synchronized (guis) {
            gui = guis.get(messageId);
        }
        if (gui != null && !gui.shouldRemove()) callback.accept(gui);
        trim();
    }
}
