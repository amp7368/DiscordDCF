package discord.util.dcf.gui.scroll.multi;

import org.jetbrains.annotations.NotNull;

public class DCFScrollCategoryBuilder {

    private int entriesPerPage = 10;

    protected <Id, T> @NotNull DCFScrollCategory<Id, T> create(DCFScrollCatId<Id, T> category) {
        return new DCFScrollCategory<>(category, this.entriesPerPage);
    }

    public DCFScrollCategoryBuilder setEntriesPerPage(int entriesPerPage) {
        this.entriesPerPage = entriesPerPage;
        return this;
    }
}
