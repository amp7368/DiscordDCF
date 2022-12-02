package discord.util.dcf.slash;

import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public abstract class DCFSlashSubCommand extends DCFAbstractCommand {

    public abstract SubcommandData getData();
}
