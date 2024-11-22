package discord.util.dcf.util;

import net.dv8tion.jda.api.interactions.components.buttons.Button;

public interface IPageButtonBuilder {

    default Button btnFirst() {
        boolean isEnabled = getPageNum() > 0;
        return Button.secondary("first", "First").withDisabled(!isEnabled);
    }

    default Button btnNext() {
        boolean isEnabled = getPageNum() + 1 < getPageSize();
        return Button.primary("next", "Next").withDisabled(!isEnabled);
    }

    default Button btnPrev() {
        boolean isEnabled = getPageNum() > 0;
        return Button.secondary("prev", "Prev").withDisabled(!isEnabled);
    }

    default Button btnLast() {
        boolean isEnabled = getPageNum() + 1 < getPageSize();
        return Button.primary("Last", "Last").withDisabled(!isEnabled);
    }

    default Button btnReversed() {
        return Button.secondary("reverse", "Reverse");
    }

    int getPageNum();

    int getPageSize();
}
