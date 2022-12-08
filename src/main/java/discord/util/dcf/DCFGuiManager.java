package discord.util.dcf;

import discord.util.dcf.gui.base.gui.IDCFGui;
import discord.util.dcf.util.TimeMillis;
import java.util.HashMap;
import java.util.Iterator;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DCFGuiManager extends ListenerAdapter {

    private final HashMap<Long, IDCFGui> guis = new HashMap<>();
    private final DCF dcf;
    private long nextTrimTime = 0;

    public DCFGuiManager(DCF dcf) {
        this.dcf = dcf;
        this.dcf.jda().addEventListener(this);
    }

    private void trim() {
        if (System.currentTimeMillis() < nextTrimTime) return;
        nextTrimTime = System.currentTimeMillis() + TimeMillis.minToMillis(1);
        synchronized (guis) {
            Iterator<IDCFGui> iterator = guis.values().iterator();
            while (iterator.hasNext()) {
                IDCFGui gui = iterator.next();
                if (gui.shouldRemove()) {
                    iterator.remove();
                    gui.remove();
                }
            }
        }
    }

    public void addGui(IDCFGui gui) {
        synchronized (guis) {
            guis.put(gui.getId(), gui);
        }
    }

    public void remove(long id) {
        synchronized (guis) {
            guis.remove(id);
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        IDCFGui gui;
        synchronized (guis) {
            gui = guis.get(event.getMessageIdLong());
        }
        if (gui != null && !gui.shouldRemove()) gui.onButtonClick(event);
        trim();
    }

}
