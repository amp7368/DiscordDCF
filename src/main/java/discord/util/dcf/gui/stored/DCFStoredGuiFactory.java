package discord.util.dcf.gui.stored;

import discord.util.dcf.DCF;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.Nullable;

public class DCFStoredGuiFactory<Gui extends IDCFStoredDormantGui<?>> {

    protected final Map<Long, Gui> messages = new HashMap<>();
    private transient final Map<Long, DCFStoredGui<?>> cache = new HashMap<>();
    private transient final DCF dcf;

    public DCFStoredGuiFactory(DCF dcf) {
        this.dcf = dcf;
        dcf.listener().listenOnButtonInteraction(this::onButtonInteraction);
    }

    private void onButtonInteraction(ButtonInteractionEvent event) {
        DCFStoredGui<?> gui = fetchGui(event.getMessageIdLong());
        if (gui == null) return;
        gui.onButtonInteraction(event);
    }

    @Nullable
    public DCFStoredGui<?> fetchGui(long messageId) {
        DCFStoredGui<?> gui;
        synchronized (cache) {
            gui = cache.get(messageId);
        }
        if (gui == null)
            gui = loadDormant(messageId);
        return gui;
    }

    @Nullable
    private DCFStoredGui<?> loadDormant(long messageId) {
        DCFStoredGui<?> gui;
        IDCFStoredDormantGui<?> dormant;
        synchronized (messages) {
            dormant = messages.get(messageId);
        }
        if (dormant == null) return null;
        gui = dormant.load();
        gui.setDCF(this.dcf);
        synchronized (cache) {
            cache.put(gui.messageId, gui);
        }
        return gui;
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
