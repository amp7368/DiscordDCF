package discord.util.dcf.gui.scroll.multi;

import discord.util.dcf.gui.scroll.DCFEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DCFScrollCategory<Id, T> {

    private final List<T> entries = new ArrayList<>();
    private final DCFScrollCatId<?, T> categoryId;
    private final int entriesPerPage;
    protected int entryPage = 0;
    private boolean sorted = true;
    private boolean isComparatorReversed = false;

    public DCFScrollCategory(DCFScrollCatId<?, T> categoryId, int entriesPerPage) {
        this.categoryId = categoryId;
        this.entriesPerPage = entriesPerPage;
    }

    public DCFScrollCategory<Id, T> clearEntries() {
        this.entries.clear();
        this.sorted = true;
        return this;
    }

    public DCFScrollCategory<Id, T> addEntries(Collection<T> entries) {
        this.entries.addAll(entries);
        this.sorted = false;
        return this;
    }

    public DCFScrollCategory<Id, T> reverse() {
        this.entryPage = 0;
        this.isComparatorReversed = !this.isComparatorReversed;
        this.sorted = false;
        return this;
    }

    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    protected void back() {
        this.entryPage--;
    }

    protected void forward() {
        this.entryPage++;
    }

    public void entryPage(int page) {
        this.entryPage = page;
    }

    protected int effectivePage() {
        return Math.max(0, Math.min(this.getMaxPage(), this.entryPage));
    }

    public int getMaxPage() {
        return (size() - 1) / this.entriesPerPage();
    }

    private int entriesPerPage() {
        return entriesPerPage;
    }

    public List<T> getAllEntries() {
        if (!this.sorted) {
            Comparator<T> comparator = categoryId.comparator();
            if (isComparatorReversed) this.entries.sort(comparator.reversed());
            else this.entries.sort(comparator);

            this.sorted = true;
        }
        return List.copyOf(this.entries);
    }

    public List<DCFEntry<T>> getEntries() {
        List<T> sorted = getAllEntries();
        if (sorted.isEmpty()) return Collections.emptyList();
        int startIndex = effectivePage() * this.entriesPerPage();
        List<DCFEntry<T>> numberedEntries = new ArrayList<>(this.entriesPerPage());
        int endIndex = startIndex + this.entriesPerPage();
        endIndex = Math.min(this.size(), endIndex);
        List<T> entriesThisPage = sorted.subList(startIndex, endIndex);
        for (int i = 0, size = entriesThisPage.size(); i < size; i++) {
            T entry = entriesThisPage.get(i);
            numberedEntries.add(new DCFEntry<>(entry, startIndex + i, i));
        }
        return numberedEntries;
    }

    private int size() {
        return this.entries.size();
    }

    public DCFScrollCatId<?, T> getId() {
        return this.categoryId;
    }

}
