package discord.util.dcf;

import discord.util.dcf.slash.DCFAbstractCommand;
import discord.util.dcf.slash.DCFSlashCommand;
import discord.util.dcf.slash.DCFSlashSubCommand;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.Command.Subcommand;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;

public class DCFCommandManager {

    private final List<DCFSlashCommand> baseCommands = new ArrayList<>();
    private final Map<String, DCFAbstractCommand> nameToCommand = new HashMap<>();
    private final DCF dcf;
    private final Map<Long, DCFAbstractCommand> idToCommand = new HashMap<>();

    public DCFCommandManager(DCF dcf) {
        this.dcf = dcf;
    }

    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        DCFAbstractCommand command = this.getCommand(event.getFullCommandName());
        if (command == null) return;
        command.onCommand(event);
    }

    public void addCommand(DCFSlashCommand... commands) {
        synchronized (this.nameToCommand) {
            for (DCFSlashCommand command : commands) {
                command.init(this.dcf);
                String commandName = command.getFullData().getName();
                this.nameToCommand.put(commandName, command);
                this.baseCommands.add(command);
                for (DCFSlashSubCommand subCommand : command.getSubCommands()) {
                    subCommand.init(this.dcf);
                    String subCommandName = commandName + " " + subCommand.getData().getName();
                    this.nameToCommand.put(subCommandName, subCommand);
                }
            }
        }
    }

    public void updateCommands() {
        synchronized (this.nameToCommand) {
            List<SlashCommandData> dataList = baseCommands.stream().map(DCFSlashCommand::getFullData).toList();
            dcf.jda().updateCommands().addCommands(dataList).queue(this::setCommands);
        }
    }

    private void setCommands(List<Command> commands) {
        synchronized (this.idToCommand) {
            for (Command command : commands) {
                this.idToCommand.put(command.getIdLong(), this.nameToCommand.get(command.getFullCommandName()));
                for (Subcommand subCommand : command.getSubcommands()) {
                    this.idToCommand.put(subCommand.getIdLong(), this.nameToCommand.get(subCommand.getFullCommandName()));
                }
            }
        }
    }

    public DCFAbstractCommand getCommand(String fullCommandName) {
        synchronized (this.nameToCommand) {
            return this.nameToCommand.get(fullCommandName);
        }
    }
}
