package discord.util.dcf;

import net.dv8tion.jda.api.JDA;

public class DCF {

    private final JDA jda;
    private DCFCommandManager commands;
    private DCFGuiManager guis;

    public DCF(JDA jda) {
        this.jda = jda;
        this.commands = new DCFCommandManager(this);
        this.guis = new DCFGuiManager(this);
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
}
