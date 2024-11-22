package discord.util.dcf.util.message;

import discord.util.dcf.DCF;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

public class DiscordMessageIdData extends AbstractDiscordMessageId {

    private transient DiscordMessageId dcfMessage;

    public DiscordMessageIdData() {
    }

    public DiscordMessageIdData(Message message) {
        super(message);
    }

    public final DiscordMessageId withDCF(@NotNull DCF dcf) {
        if (dcfMessage != null && dcfMessage.getDCF() == dcf)
            return dcfMessage;
        return dcfMessage = IDiscordMessageId.withDCF(this, dcf);
    }
}
