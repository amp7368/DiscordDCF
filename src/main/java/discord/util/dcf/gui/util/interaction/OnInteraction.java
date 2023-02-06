package discord.util.dcf.gui.util.interaction;

public interface OnInteraction<Consumed> {

    void onInteraction(Consumed event);
}
