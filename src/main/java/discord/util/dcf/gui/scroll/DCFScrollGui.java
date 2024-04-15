package discord.util.dcf.gui.scroll;

import discord.util.dcf.gui.base.gui.IDCFGui;
import discord.util.dcf.gui.base.page.DCFGuiPage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public abstract class DCFScrollGui<Parent extends IDCFGui, Entry> extends DCFGuiPage<Parent> {

    protected int entryPage = 0;
    private final List<Entry> entries = new ArrayList<>();

    public DCFScrollGui(Parent parent) {
        super(parent);
        registerButton(this.btnNext().getId(), event -> this.forward());
        registerButton(this.btnPrev().getId(), event -> this.back());
        registerButton(this.btnFirst().getId(), event -> this.entryPage = 0);
    }

    protected abstract Comparator<? super Entry> entriesComparator();

    protected abstract int entriesPerPage();

    protected void addEntry(Entry entries) {
        addEntries(Collections.singleton(entries));
    }

    protected void addEntries(Collection<Entry> entries) {
        this.entries.addAll(entries);
    }

    protected void setEntries() {
        this.setEntries(new ArrayList<>(0));
    }

    protected void setEntries(Collection<Entry> entries) {
        this.entries.clear();
        this.addEntries(entries);
    }

    protected void sort() {
        this.entries.sort(entriesComparator());
    }

    protected void back() {
        this.entryPage--;
        verifyPageNumber();
    }

    protected void forward() {
        this.entryPage++;
        verifyPageNumber();
    }

    protected void verifyPageNumber() {
        this.entryPage = Math.max(0, Math.min(this.getMaxPage(), this.entryPage));
    }

    public int getMaxPage() {
        return (entries.size() - 1) / this.entriesPerPage();
    }

    protected List<DCFEntry<Entry>> getCurrentPageEntries() {
        if (this.entries.isEmpty()) return Collections.emptyList();
        int startIndex = this.entryPage * this.entriesPerPage();
        List<DCFEntry<Entry>> numberedEntries = new ArrayList<>(this.entriesPerPage());
        int endIndex = startIndex + this.entriesPerPage();
        endIndex = Math.min(this.getEntriesSize(), endIndex);
        List<Entry> entriesThisPage = this.entries.subList(startIndex, endIndex);
        for (int i = 0, size = entriesThisPage.size(); i < size; i++) {
            Entry entry = entriesThisPage.get(i);
            numberedEntries.add(new DCFEntry<>(entry, startIndex + i, i));
        }
        return numberedEntries;
    }

    protected final List<Entry> getEntriesCopy() {
        return List.copyOf(this.entries);
    }

    protected final int getEntriesSize() {
        return this.entries.size();
    }

    @Override
    public Button btnNext() {
        if (entries == null) return super.btnNext();
        boolean isDisabled = entryPage >= getMaxPage();
        return super.btnNext().withDisabled(isDisabled);
    }

    @Override
    public Button btnPrev() {
        if (entries == null) return super.btnPrev();
        boolean isDisabled = this.entryPage == 0;
        return super.btnPrev().withDisabled(isDisabled);
    }

    @Override
    public Button btnFirst() {
        if (entries == null) return super.btnFirst();
        boolean isDisabled = this.entryPage == 0;
        return super.btnFirst().withDisabled(isDisabled);
    }
}
