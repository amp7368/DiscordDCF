package discord.util.dcf;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class DCFListener extends ListenerAdapter {

    private final DCF dcf;
    private List<Consumer<ButtonInteractionEvent>> guis = new ArrayList<>();

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
    public void onModalInteraction(ModalInteractionEvent event) {
        dcf.modals().onModalInteraction(event);
    }

    public void listenOnButtonInteraction(Consumer<ButtonInteractionEvent> onInteraction) {
        this.guis.add(onInteraction);
    }
}
