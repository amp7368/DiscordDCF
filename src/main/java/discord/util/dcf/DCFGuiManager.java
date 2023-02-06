package discord.util.dcf;

import discord.util.dcf.gui.base.GuiEventHandler;
import discord.util.dcf.util.TimeMillis;
import java.util.HashMap;
import java.util.Iterator;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

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

    public void onButtonInteraction(ButtonInteractionEvent event) {
        GuiEventHandler gui;
        synchronized (guis) {
            gui = guis.get(event.getMessageIdLong());
        }
        if (gui != null && !gui.shouldRemove()) gui.onButtonClick(event);
        trim();
    }

}
