package discord.util.dcf.gui.base.page;

import java.util.HashMap;
import java.util.Map;

public class OnInteractionMap {

    private final Map<Class<?>, Map<Object, OnInteraction<?>>> map = new HashMap<>();

    public <Consumed, Key> void put(Class<Consumed> mapType, Key key, OnInteraction<Consumed> onInteraction) {
        Map<Object, OnInteraction<?>> onInteractionDoMap = map.computeIfAbsent(mapType, (t) -> new HashMap<>());
        onInteractionDoMap.put(key, onInteraction);
    }

    public <Consumed, Key> boolean onInteraction(Key key, Consumed consumed) {
        Map<Object, OnInteraction<?>> interactionMap = map.get(consumed.getClass());
        if (interactionMap == null) return false;
        // this cast is okay because put() enforces this
        OnInteraction<?> interactionHandler = interactionMap.get(key);
        if (interactionHandler == null) return false;
        @SuppressWarnings("unchecked") OnInteraction<Consumed> castedInteractionHandler = (OnInteraction<Consumed>) interactionHandler;
        castedInteractionHandler.onInteraction(consumed);
        return true;
    }
}
