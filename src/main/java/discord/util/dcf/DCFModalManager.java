package discord.util.dcf;

import discord.util.dcf.modal.DCFModalType;
import java.util.HashMap;
import java.util.Map;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

public class DCFModalManager {

    private final Map<String, DCFModalType> modals = new HashMap<>();

    public DCFModalManager(DCF dcf) {
    }

    public void add(DCFModalType modal) {
        modal.init();
        modals.put(modal.getId(), modal);
    }

    public void onModalInteraction(ModalInteractionEvent event) {
        DCFModalType modal = modals.get(event.getModalId());
        if (modal != null) modal.toImplAndInit(event);
    }
}
