package discord.util.dcf;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class DCFListener extends ListenerAdapter {

    private final DCF dcf;
    private final List<Consumer<ButtonInteractionEvent>> guis = new ArrayList<>();

    public DCFListener(DCF dcf) {
        this.dcf = dcf;
        dcf.jda().addEventListener(this);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        dcf.commands().onSlashCommandInteraction(event);
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        dcf.guis().onButtonInteraction(event);
        guis.forEach(gui -> gui.accept(event));
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        dcf.modals().onModalInteraction(event);
    }

    @Override
    public void onEntitySelectInteraction(@NotNull EntitySelectInteractionEvent event) {
        dcf.guis().onEntitySelectInteraction(event);
    }

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        dcf.guis().onStringSelectInteraction(event);
    }

    public void listenOnButtonInteraction(Consumer<ButtonInteractionEvent> onInteraction) {
        this.guis.add(onInteraction);
    }
}
