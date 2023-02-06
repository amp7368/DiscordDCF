package discord.util.dcf.gui.stored;

public interface IDCFStoredDormantGui<Gui extends DCFStoredGui> {

    long getId();

    void setId(long messageId);

    Gui load();

    default Gui create() {
        return load();
    }
}
