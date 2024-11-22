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
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.Command.Subcommand;
import net.dv8tion.jda.api.interactions.commands.ICommandReference;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;

public class DCFCommandManager {

    private final DCF dcf;
    private final List<DCFSlashCommand> baseCommands = new ArrayList<>();
    private final Map<String, DCFAbstractCommand<?>> nameToCommand = new HashMap<>();
    private final Map<String, ICommandReference> nameToDiscordBaseCommands = new HashMap<>();
    // don't use. subcommands have the same id as parent. <----
    private final Map<Long, DCFAbstractCommand<?>> idToCommand = new HashMap<>();

    public DCFCommandManager(DCF dcf) {
        this.dcf = dcf;
    }

    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        DCFAbstractCommand<?> command = this.getCommand(event.getFullCommandName());
        if (command == null) return;
        command.onCommand(event);
    }

    public String getCommandAsMention(String commandName) {
        if (commandName.startsWith("/")) commandName = commandName.substring(1);
        ICommandReference command = nameToDiscordBaseCommands.get(commandName);
        if (command == null) return null;
        return command.getAsMention();
    }

    private void setCommands(List<Command> commands) {
        Map<Long, DCFAbstractCommand<?>> idToCommand = new HashMap<>();
        synchronized (this.nameToCommand) {
            for (Command command : commands) {
                idToCommand.put(command.getIdLong(), this.getCommand(command.getFullCommandName()));
                for (Subcommand subCommand : command.getSubcommands()) {
                    idToCommand.put(subCommand.getIdLong(), this.getCommand(subCommand.getFullCommandName()));
                    nameToDiscordBaseCommands.put(subCommand.getFullCommandName(), subCommand);
                }
                nameToDiscordBaseCommands.put(command.getFullCommandName(), command);
            }
        }
        synchronized (this.idToCommand) {
            this.idToCommand.putAll(idToCommand);
        }
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
                    String subCommandName = commandName + " " + subCommand.getFullData().getName();
                    this.nameToCommand.put(subCommandName, subCommand);
                }
            }
        }
    }

    public void updateCommand(Guild guild) {
        List<SlashCommandData> data = baseCommands.stream()
            .filter(command -> command.isGuildAllowed(guild))
            .map(DCFSlashCommand::getFullData)
            .toList();
        CommandListUpdateAction action = guild.updateCommands().addCommands(data);

        updateCommands(action, Function.identity(), commands -> {});
    }


    public void updateCommands() {
        updateCommands(Function.identity(), commands -> {});
    }

    public void updateCommands(Function<CommandListUpdateAction, CommandListUpdateAction> modify, Consumer<List<Command>> callback) {
        List<SlashCommandData> data = baseCommands.stream()
            .filter(DCFAbstractCommand::isGlobal)
            .map(DCFSlashCommand::getFullData)
            .toList();
        CommandListUpdateAction action = dcf.jda().updateCommands().addCommands(data);
        updateCommands(action, modify, callback);
    }

    private void updateCommands(
        CommandListUpdateAction action,
        Function<CommandListUpdateAction, CommandListUpdateAction> modify,
        Consumer<List<Command>> callback) {
        modify.apply(action).queue((commands) -> {
            this.setCommands(commands);
            callback.accept(commands);
        });
    }

    public DCFAbstractCommand<?> getCommand(String fullCommandName) {
        synchronized (this.nameToCommand) {
            return this.nameToCommand.get(fullCommandName);
        }
    }

    public DCFAbstractCommand<?> getCommand(long commandId) {
        synchronized (this.idToCommand) {
            return this.idToCommand.get(commandId);
        }
    }
}
