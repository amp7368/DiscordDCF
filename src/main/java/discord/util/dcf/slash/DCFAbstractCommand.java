package discord.util.dcf.slash;

import discord.util.dcf.DCF;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public abstract class DCFAbstractCommand<Data> {

    protected DCF dcf;
    private Data data;

    public void init(DCF dcf) {
        this.dcf = dcf;
    }

    public abstract Data getData();

    public final Data getFullData() {
        if (this.data != null) return data;
        Data workingData = modifyInitialData(getData());
        this.data = finalizeCommandData(workingData);
        return this.data;
    }

    protected Data finalizeCommandData(Data data) {
        return data;
    }

    protected abstract Data modifyInitialData(Data data);

    public abstract void onCommand(SlashCommandInteractionEvent event);

    public boolean isGlobal() {
        return true;
    }

    public boolean isGuildAllowed(Guild guild) {
        return true;
    }
}
