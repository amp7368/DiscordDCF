package discord.util.dcf.gui.stored.index;

import discord.util.dcf.DCF;
import discord.util.dcf.gui.stored.DCFStoredGui;
import discord.util.dcf.gui.stored.DCFStoredManager;
import discord.util.dcf.gui.stored.IDCFStoredDormantGui;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;
import org.jetbrains.annotations.Nullable;

public class DCFStoredIndexManager<Data extends IDCFStoredDormantGui<?>> extends DCFStoredManager<Data> {

    protected final Set<Long> index = new HashSet<>();
    private transient File folder;
    private transient IDCFStoredIndexFileIO<Data> fileIO;

    public DCFStoredIndexManager() {
    }

    public DCFStoredIndexManager(DCF dcf, File folder, IDCFStoredIndexFileIO<Data> fileIO) {
        super(dcf);
        this.folder = folder;
        this.fileIO = fileIO;
        verifyFolderLength();
    }

    public void load(DCF dcf, File folder, IDCFStoredIndexFileIO<Data> fileIO) {
        super.load(dcf);
        this.folder = folder;
        this.fileIO = fileIO;
        verifyFolderLength();
    }

    private void verifyFolderLength() {
        Pattern messageIdPattern = Pattern.compile("^([0-9]+).*");

        File[] subFiles = folder.listFiles();
        if (subFiles == null) return;

        for (File subFolder : subFiles) {
            if (!subFolder.isDirectory()) continue;
            if (subFolder.getName().length() == getFolderCharacters()) continue;

            boolean anyMoved = false;
            File[] guiFiles = subFolder.listFiles();
            if (guiFiles == null) continue;
            for (File file : guiFiles) {
                String message = messageIdPattern
                    .matcher(file.getName())
                    .group(1);

                try {
                    long id = Long.parseUnsignedLong(message);
                    synchronized (index) {
                        if (!index.contains(id)) continue;
                    }
                    File moveTo = getFile(id);
                    Files.move(file.toPath(), moveTo.toPath());
                    anyMoved = true;
                } catch (NumberFormatException ignored) {
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (!anyMoved) continue;
            String[] list = subFolder.list();
            if (list != null && list.length != 0) continue;
            subFolder.delete();
        }
    }

    protected void save(Data gui) {
        this.fileIO.save(gui);
    }


    public File getFile(long messageIdLong) {
        String filename = getFilename(messageIdLong);

        if (getFolderCharacters() == 0)
            return new File(folder, filename);

        String[] subFolder = getSubFolder(messageIdLong).toArray(String[]::new);
        Path path = Path.of(this.folder.getAbsolutePath(), subFolder);
        return new File(path.toFile(), filename);
    }


    public List<String> getSubFolder(long messageIdLong) {
        String messageId = String.valueOf(messageIdLong);
        if (getFolderCharacters() == 0) return List.of();

        String suffix;
        if (messageId.length() < getFolderCharacters())
            suffix = messageId;
        else suffix = messageId.substring(0, getFolderCharacters());
        return List.of("msg_" + suffix);
    }

    public String getFilename(long messageIdLong) {
        return messageIdLong + ".json";
    }

    protected int getFolderCharacters() {
        return 1;
    }

    @Nullable
    protected Data loadFromFile(File file) {
        return this.fileIO.loadFromFile(file);
    }

    @Nullable
    @Override
    protected final DCFStoredGui<?> loadDormant(long messageId) {
        if (!index.contains(messageId)) return null;

        Data data = loadFromFile(getFile(messageId));
        if (data == null) {
            index.remove(messageId);
            saveIndex();
            return null;
        }

        return data.load();
    }

    protected void saveIndex() {
        this.fileIO.saveIndex(this);
    }

    @Override
    public void add(Data gui) {
        this.index.add(gui.getMessageId());
        this.save(gui);
        this.saveIndex();
    }

    @Override
    protected void removeData(long messageId) {
        File file = this.getFile(messageId);
        file.delete();

        synchronized (index) {
            this.index.remove(messageId);
            this.saveIndex();
        }

        File parent = file.getParentFile();
        String[] siblings = parent.list();
        if (siblings == null || siblings.length == 0)
            parent.delete();
    }

    public interface IDCFStoredIndexFileIO<Data extends IDCFStoredDormantGui<?>> {

        void save(Data data);

        Data loadFromFile(File file);

        void saveIndex(DCFStoredIndexManager<Data> index);

        default DCFStoredIndexManager<Data> buildManager(DCF dcf, File folder) {
            return new DCFStoredIndexManager<>(dcf, folder, this);
        }
    }


    public record DCFStoredIndexFileIO<Data extends IDCFStoredDormantGui<?>>(
        Consumer<DCFStoredIndexManager<Data>> saveIndex,
        Consumer<Data> save,
        Function<File, Data> loadFromFile
    ) implements IDCFStoredIndexFileIO<Data> {

        @Override
        public void save(Data data) {
            save.accept(data);
        }

        @Override
        public Data loadFromFile(File file) {
            return loadFromFile.apply(file);
        }

        @Override
        public void saveIndex(DCFStoredIndexManager<Data> index) {
            saveIndex.accept(index);
        }
    }
}
