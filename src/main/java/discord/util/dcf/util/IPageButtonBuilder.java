package discord.util.dcf.util;

import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

public interface IPageButtonBuilder {

    default Button btnNext() {
        boolean isLastPage = getPageNum() + 1 < getPageSize();
        return Button.of(isLastPage ? ButtonStyle.PRIMARY : ButtonStyle.SECONDARY, "next", "Next");
    }

    default Button btnPrev() {
        boolean hastPrevPage = getPageNum() > 0;
        return Button.of(hastPrevPage ? ButtonStyle.PRIMARY : ButtonStyle.SECONDARY, "prev", "Prev");
    }

    int getPageNum();

    int getPageSize();
}
