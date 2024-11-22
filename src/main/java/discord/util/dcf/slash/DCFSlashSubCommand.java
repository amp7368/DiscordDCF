package discord.util.dcf.slash;

import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public abstract class DCFSlashSubCommand extends DCFAbstractCommand<SubcommandData> {

    public abstract SubcommandData getData();

    protected SubcommandData finalizeCommandData(SubcommandData data) {
        return data;
    }

    @Override
    protected final SubcommandData modifyInitialData(SubcommandData data) {
        return data;
    }
}
