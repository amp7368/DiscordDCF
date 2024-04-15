package discord.util.dcf;

import discord.util.dcf.slash.DCFAbstractCommand;
import discord.util.dcf.slash.DCFSlashCommand;
import discord.util.dcf.slash.DCFSlashSubCommand;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.Command.Subcommand;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
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
        updateCommands(Function.identity(), commands -> {});
    }

    public void updateCommands(Function<CommandListUpdateAction, CommandListUpdateAction> modify, Consumer<List<Command>> callback) {
        synchronized (this.nameToCommand) {
            List<SlashCommandData> dataList = baseCommands.stream().map(DCFSlashCommand::getFullData).toList();
            CommandListUpdateAction action = dcf.jda().updateCommands()
                .addCommands(dataList);
            modify.apply(action).queue((commands) -> {
                this.setCommands(commands);
                callback.accept(commands);
            });
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
