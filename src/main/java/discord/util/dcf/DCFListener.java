package discord.util.dcf;

import discord.util.dcf.gui.util.interaction.IHasInteractionMap;
import discord.util.dcf.gui.util.interaction.OnInteractionListener;
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
    private final List<OnInteractionListener> listeners = new ArrayList<>();
    private final List<Consumer<ButtonInteractionEvent>> guis = new ArrayList<>();

    public DCFListener(DCF dcf) {
        this.dcf = dcf;
        listeners.add(dcf.guis());
        dcf.jda().addEventListener(this);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        dcf.commands().onSlashCommandInteraction(event);
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        listeners.forEach(l -> l.onButtonInteraction(event));
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        dcf.modals().onModalInteraction(event);
    }

    @Override
    public void onEntitySelectInteraction(@NotNull EntitySelectInteractionEvent event) {
        listeners.forEach(l -> l.onSelectEntityInteraction(event));
    }

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        listeners.forEach(l -> l.onSelectStringInteraction(event));
    }

    public void listenOnButtonInteraction(Consumer<ButtonInteractionEvent> onInteraction) {
        OnInteractionListener listener = new OnInteractionListener() {
            @Override
            public void onButtonInteraction(ButtonInteractionEvent event) {
                onInteraction.accept(event);
            }
        };
        this.listeners.add(listener);
    }

    public void listen(IHasInteractionMap onInteraction) {
        this.listeners.add(onInteraction);
    }
}
