package discord.util.dcf.modal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.Modal.Builder;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

public abstract class DCFModalType<T extends DCFModal> {

    private final Map<String, SetModalValue<T>> mappings = new HashMap<>();

    protected void registerField(String key, SetModalValue<T> set) {
        this.mappings.put(key, set);
    }

    public final void init() {
        for (TextInput input : getInputs()) {
            String id = input.getId();
            if (mappings.containsKey(id))
                continue;
            registerField(id, getModalSetField(id));
        }
    }

    protected SetModalValue<T> getModalSetField(String id) {
        throw new IllegalStateException("Unexpected modal id: " + id);
    }

    public final void toImplAndInit(ModalInteractionEvent event) {
        T dcfModal = toImpl();
        for (Entry<String, SetModalValue<T>> entry : mappings.entrySet()) {
            SetModalValue<T> setValue = entry.getValue();
            ModalMapping value = event.getValue(entry.getKey());
            setValue.setValue(dcfModal, value);
        }
        dcfModal.onEvent(event);
    }

    public abstract T toImpl();

    public Modal buildModal() {
        Builder modal = Modal.create(getId(), getTitle());
        modal.addActionRows(getInputs().stream().map(ActionRow::of).toList());
        return modal.build();
    }

    protected abstract List<TextInput> getInputs();

    protected abstract String getTitle();

    public abstract String getId();

}
