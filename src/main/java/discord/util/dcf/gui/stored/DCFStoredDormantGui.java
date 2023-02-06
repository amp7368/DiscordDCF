package discord.util.dcf.gui.stored;

public abstract class DCFStoredDormantGui<Gui extends DCFStoredGui<?>> implements IDCFStoredDormantGui<Gui> {

    public long messageId = -1;

    @Override
    public long getId() {
        return this.messageId;
    }

    @Override
    public void setId(long messageId) {
        this.messageId = messageId;
    }
}
