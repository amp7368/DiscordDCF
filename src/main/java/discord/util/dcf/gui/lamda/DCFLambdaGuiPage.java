package discord.util.dcf.gui.lamda;

import discord.util.dcf.gui.base.gui.DCFGui;
import discord.util.dcf.gui.base.page.DCFGuiPage;
import java.util.function.Function;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class DCFLambdaGuiPage extends DCFGuiPage<DCFGui> {

    private final Function<DCFLambdaContext, MessageCreateData> createPage;

    public DCFLambdaGuiPage(DCFGui parent, Function<DCFLambdaContext, MessageCreateData> createPage) {
        super(parent);
        this.createPage = createPage;
    }

    @Override
    public MessageCreateData makeMessage() {
        return createPage.apply(createContext());
    }

    private DCFLambdaContext createContext() {
        DCFLambdaPageContext page = new DCFLambdaPageContext(this.getPageNum(), this.getPageSize());
        DCFLambdaGuiRefContext ref = new DCFLambdaGuiRefContext(this, this.getParent());
        return new DCFLambdaContext(page, ref);
    }
}
