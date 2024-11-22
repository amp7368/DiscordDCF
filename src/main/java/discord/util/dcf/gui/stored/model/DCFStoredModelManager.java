package discord.util.dcf.gui.stored.model;

import discord.util.dcf.DCF;
import discord.util.dcf.gui.stored.DCFStoredGui;
import discord.util.dcf.gui.stored.DCFStoredManager;
import discord.util.dcf.gui.stored.IDCFStoredDormantGui;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public class DCFStoredModelManager<Gui extends IDCFStoredDormantGui<?>> extends DCFStoredManager<Gui> {

    protected final Map<Long, Gui> messages = new HashMap<>();

    public DCFStoredModelManager(DCF dcf) {
        super(dcf);
    }

    public List<Gui> getAllMessagesCopy() {
        synchronized (messages) {
            return List.copyOf(messages.values());
        }
    }

    @Nullable
    @Override
    protected DCFStoredGui<?> loadDormant(long messageId) {
        IDCFStoredDormantGui<?> dormant;
        synchronized (messages) {
            dormant = messages.get(messageId);
        }
        if (dormant == null) return null;
        DCFStoredGui<?> gui = dormant.load();
        gui.setDCF(this.dcf);
        return gui;
    }


    @Override
    public void add(Gui gui) {
        synchronized (messages) {
            this.messages.put(gui.getMessageId(), gui);
        }
    }

    @Override
    protected void removeData(long messageId) {
        synchronized (messages) {
            this.messages.remove(messageId);
        }
    }
}
