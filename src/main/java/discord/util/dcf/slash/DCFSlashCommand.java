package discord.util.dcf.slash;

import java.util.Collections;
import java.util.List;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public abstract class DCFSlashCommand extends DCFAbstractCommand<SlashCommandData> {

    public abstract SlashCommandData getData();

    @Override
    protected final SlashCommandData modifyInitialData(SlashCommandData data) {
        List<SubcommandData> subCommands = getSubCommands().stream()
            .map(DCFSlashSubCommand::getFullData)
            .toList();
        return data.addSubcommands(subCommands);
    }

    public List<DCFSlashSubCommand> getSubCommands() {
        return Collections.emptyList();
    }
}
