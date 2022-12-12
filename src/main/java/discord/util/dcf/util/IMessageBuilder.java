package discord.util.dcf.util;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

public interface IMessageBuilder {

    default MessageCreateData buildCreate(MessageEmbed embed, LayoutComponent... components) {
        return new MessageCreateBuilder().setEmbeds(embed).setComponents(components).build();
    }

    default MessageEditData buildEdit(MessageEmbed embed, LayoutComponent... components) {
        return new MessageEditBuilder().setEmbeds(embed).setComponents(components).build();
    }

    default MessageCreateData buildCreate(String content, LayoutComponent... components) {
        return new MessageCreateBuilder().setContent(content).setComponents(components).build();
    }

    default MessageEditData buildEdit(String content, LayoutComponent... components) {
        return new MessageEditBuilder().setContent(content).setComponents(components).build();
    }
}
