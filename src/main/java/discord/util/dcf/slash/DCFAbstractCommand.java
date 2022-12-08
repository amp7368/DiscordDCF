package discord.util.dcf.slash;

import discord.util.dcf.DCF;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public abstract class DCFAbstractCommand {

    protected DCF dcf;

    public abstract void onCommand(SlashCommandInteractionEvent event);

    public void init(DCF dcf) {
        this.dcf = dcf;
    }
}
