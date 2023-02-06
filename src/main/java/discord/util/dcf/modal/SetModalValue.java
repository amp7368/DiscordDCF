package discord.util.dcf.modal;

import net.dv8tion.jda.api.interactions.modals.ModalMapping;

@FunctionalInterface
public interface SetModalValue<T extends DCFModal> {

    void setValue(T modal, ModalMapping mapping);
}
