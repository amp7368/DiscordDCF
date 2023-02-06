package discord.util.dcf.gui.stored;

import discord.util.dcf.DCF;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class DCFStoredGuiFactory<Gui extends IDCFStoredDormantGui<?>> {

    private transient final Map<Long, DCFStoredGui<?>> cache = new HashMap<>();
    private final Map<Long, Gui> messages = new HashMap<>();

    public DCFStoredGuiFactory(DCF dcf) {
        dcf.listener().listenOnButtonInteraction(this::onButtonInteraction);
    }

    private void onButtonInteraction(ButtonInteractionEvent event) {
        DCFStoredGui<?> gui;
        synchronized (cache) {
            gui = cache.get(event.getMessageIdLong());
        }
        if (gui != null) {
            gui.onButtonInteraction(event);
            return;
        }
        IDCFStoredDormantGui<?> dormant;
        synchronized (messages) {
            dormant = messages.get(event.getMessageIdLong());
        }
        if (dormant == null) return;
        gui = dormant.load();
        synchronized (cache) {
            cache.put(gui.messageId, gui);
        }
        gui.onButtonInteraction(event);
    }

    public void addAll(Collection<? extends Gui> guis) {
        guis.forEach(this::add);
    }

    public void add(Gui gui) {
        this.messages.put(gui.getId(), gui);
    }

    public void remove(Gui gui) {
        this.messages.remove(gui.getId());
    }

    public void remove(long id) {
        this.messages.remove(id);
    }
}
