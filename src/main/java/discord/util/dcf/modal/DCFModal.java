package discord.util.dcf.modal;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

public abstract class DCFModal {

    public abstract void onEvent(ModalInteractionEvent event);
}
