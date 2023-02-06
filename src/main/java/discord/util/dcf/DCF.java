package discord.util.dcf;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DCF {

    private final JDA jda;
    private final DCFCommandManager commands;
    private final DCFGuiManager guis;
    private final DCFModalManager modals;
    private final DCFListener listener;

    public DCF(JDA jda) {
        this.jda = jda;
        this.commands = new DCFCommandManager(this);
        this.guis = new DCFGuiManager(this);
        this.listener = new DCFListener(this);
        this.modals = new DCFModalManager(this);
    }

    public JDA jda() {
        return this.jda;
    }

    public DCFCommandManager commands() {
        return commands;
    }

    public DCFGuiManager guis() {
        return this.guis;
    }

    public DCFListener listener() {
        return listener;
    }

    public DCFModalManager modals() {
        return modals;
    }
}
