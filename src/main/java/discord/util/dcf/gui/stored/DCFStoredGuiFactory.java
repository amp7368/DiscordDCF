package discord.util.dcf.gui.stored;

import discord.util.dcf.DCF;
import discord.util.dcf.gui.stored.model.DCFStoredModelManager;

@Deprecated
public class DCFStoredGuiFactory<Gui extends IDCFStoredDormantGui<?>> extends DCFStoredModelManager<Gui> {

    public DCFStoredGuiFactory(DCF dcf) {
        super(dcf);
    }
}