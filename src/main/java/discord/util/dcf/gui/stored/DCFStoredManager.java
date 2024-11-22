package discord.util.dcf.gui.stored;

import discord.util.dcf.DCF;
import discord.util.dcf.gui.stored.index.DCFStoredIndexManager;
import discord.util.dcf.gui.stored.index.DCFStoredIndexManager.DCFStoredIndexFileIO;
import discord.util.dcf.gui.stored.index.DCFStoredIndexManager.IDCFStoredIndexFileIO;
import discord.util.dcf.gui.stored.model.DCFStoredModelManager;
import discord.util.dcf.util.TimeMillis;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.Nullable;

public abstract class DCFStoredManager<Gui extends IDCFStoredDormantGui<?>> {

    private transient final Map<Long, CachedGui> cache = new HashMap<>();
    protected transient DCF dcf;

    protected DCFStoredManager() {
    }

    public DCFStoredManager(DCF dcf) {
        this.dcf = dcf;
        dcf.listener().listenOnButtonInteraction(this::onButtonInteraction);
    }

    public static <Gui extends IDCFStoredDormantGui<?>> DCFStoredModelManager<Gui> createDCFStoredModel(DCF dcf) {
        return new DCFStoredModelManager<>(dcf);
    }

    public static <Gui extends IDCFStoredDormantGui<?>> DCFStoredIndexManager<Gui> createDCFIndexFileModel(DCF dcf, File folder,
        IDCFStoredIndexFileIO<Gui> fileIO) {
        return new DCFStoredIndexManager<>(dcf, folder, fileIO);
    }

    public static <Gui extends IDCFStoredDormantGui<?>> IDCFStoredIndexFileIO<Gui> createFileIO(
        Consumer<DCFStoredIndexManager<Gui>> saveIndex,
        Consumer<Gui> save,
        Function<File, Gui> load
    ) {
        return new DCFStoredIndexFileIO<>(saveIndex, save, load);
    }

    public static <Gui extends IDCFStoredDormantGui<?>> IDCFStoredIndexFileIO<Gui> createFileIO(
        Runnable saveIndex,
        Consumer<Gui> save,
        Function<File, Gui> load
    ) {
        return new DCFStoredIndexFileIO<>(s -> saveIndex.run(), save, load);
    }

    protected void load(DCF dcf) {
        if (this.dcf != null) return;
        this.dcf = dcf;
        this.dcf.listener().listenOnButtonInteraction(this::onButtonInteraction);
    }

    @Nullable
    public DCFStoredGui<?> fetchGui(long messageId) {
        synchronized (cache) {
            CachedGui gui = cache.get(messageId);
            trimCache();
            if (gui != null) return gui.gui();
        }
        DCFStoredGui<?> loaded = loadDormant(messageId);
        if (loaded == null) return null;
        synchronized (cache) {
            long removeAt = System.currentTimeMillis() + this.cacheDuration();
            cache.put(loaded.getMessageId(), new CachedGui(loaded, removeAt));
        }
        return loaded;
    }

    @Nullable
    protected abstract DCFStoredGui<?> loadDormant(long messageId);

    private void onButtonInteraction(ButtonInteractionEvent event) {
        DCFStoredGui<?> gui = fetchGui(event.getMessageIdLong());
        if (gui == null) return;
        gui.onButtonInteraction(event);
    }

    private void trimCache() {
        cache.values().removeIf(CachedGui::isOld);
    }

    public void remove(Gui gui) {
        remove(gui.getMessageId());
    }

    public void addAll(Collection<? extends Gui> guis) {
        guis.forEach(this::add);
    }

    public abstract void add(Gui gui);

    public void remove(long messageId) {
        this.removeData(messageId);
        synchronized (cache) {
            this.cache.remove(messageId);
        }
    }

    protected long cacheDuration() {
        return TimeMillis.hourToMillis(1);
    }

    protected abstract void removeData(long messageId);


    private record CachedGui(DCFStoredGui<?> gui, long removeAtTimestamp) {

        public boolean isOld() {
            return System.currentTimeMillis() >= removeAtTimestamp;
        }
    }
}
