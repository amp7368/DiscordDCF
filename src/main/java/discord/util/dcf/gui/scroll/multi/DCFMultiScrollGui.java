package discord.util.dcf.gui.scroll.multi;

import discord.util.dcf.gui.base.gui.IDCFGui;
import discord.util.dcf.gui.base.page.DCFGuiPage;
import discord.util.dcf.gui.scroll.DCFEntry;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class DCFMultiScrollGui<Parent extends IDCFGui> extends DCFGuiPage<Parent> {

    private final Map<DCFScrollCatId<?, ?>, DCFScrollCategory<?, ?>> data = new HashMap<>();
    private final Map<Object, DCFScrollCatId<?, ?>> categoryIds = new HashMap<>();
    private DCFScrollCategoryBuilder categoryBuilder = new DCFScrollCategoryBuilder();
    private Comparator<Object> comparator;

    public DCFMultiScrollGui(Parent parent) {
        super(parent);
        registerButton(super.btnNext().getId(), event -> next());
        registerButton(super.btnPrev().getId(), event -> back());
        registerButton(super.btnFirst().getId(), event -> this.toEach(page -> page.entryPage(0)));
        registerButton(super.btnReversed().getId(), event -> this.toEach(DCFScrollCategory::reverse));
    }

    protected <Id extends Comparable<Id>> void setComparator(Class<Id> idClass) {
        this.comparator = (o1, o2) -> {
            Integer compare = compareId(idClass, o1, o2);
            if (compare != null) return compare;

            Id id1 = idClass.cast(o1);
            Id id2 = idClass.cast(o2);
            return id1.compareTo(id2);
        };
    }

    protected <Id> void setComparator(Comparator<Id> comparator, Class<Id> idClass) {
        this.comparator = (o1, o2) -> {
            Integer compare = compareId(idClass, o1, o2);
            if (compare != null) return compare;
            Id id1 = idClass.cast(o1);
            Id id2 = idClass.cast(o2);
            return comparator.compare(id1, id2);
        };
    }

    @Nullable
    private <Id> Integer compareId(Class<Id> idClass, Object o1, Object o2) {
        boolean o1IsId = idClass.isAssignableFrom(o1.getClass());
        boolean o2IsId = idClass.isAssignableFrom(o2.getClass());
        if (!o1IsId && !o2IsId) return 0;
        if (!o1IsId) return -1;
        if (!o2IsId) return 1;
        return null;
    }

    protected <Id, T> DCFScrollCatId<Id, T> getId(Id id) {
        @SuppressWarnings("unchecked")
        DCFScrollCatId<Id, T> raw = (DCFScrollCatId<Id, T>) categoryIds.get(id);
        return raw;
    }

    @NotNull
    protected final <Id, T> DCFScrollCategory<Id, T> getCategoryById(Id id) {
        return getCategory(getId(id));
    }

    public DCFScrollCategoryBuilder getCategoryBuilder() {
        return this.categoryBuilder;
    }

    public DCFMultiScrollGui<Parent> setCategoryBuilder(DCFScrollCategoryBuilder categoryBuilder) {
        this.categoryBuilder = categoryBuilder;
        return this;
    }

    @NotNull
    protected final <Id, T> DCFScrollCategory<Id, T> getCategory(DCFScrollCatId<Id, T> category) {
        @SuppressWarnings("unchecked")
        DCFScrollCategory<Id, T> raw = (DCFScrollCategory<Id, T>) this.data.get(category);
        if (raw != null) return raw;
        DCFScrollCategory<Id, T> created = getCategoryBuilder().create(category);
        this.setCategory(created);
        return created;
    }

    protected final DCFMultiScrollGui<Parent> setCategory(DCFScrollCategory<?, ?> category) {
        DCFScrollCatId<?, ?> id = category.getId();
        this.categoryIds.put(id.id(), id);
        this.data.put(id, category);
        return this;
    }

    public final List<DCFScrollCategory<?, ?>> getCategories() {
        if (this.comparator == null) return List.copyOf(this.data.values());
        return this.data.entrySet().stream()
            .sorted(Comparator.comparing(e -> e.getKey().id(), comparator))
            .<DCFScrollCategory<?, ?>>map(Entry::getValue)
            .toList();
    }

    protected final <T> List<DCFEntry<T>> getEntries(DCFScrollCatId<?, T> category) {
        return this.getCategory(category).getEntries();
    }

    protected <T> void addEntry(DCFScrollCatId<?, T> category, T add) {
        addEntries(category, Collections.singleton(add));
    }

    protected <T> void addEntries(DCFScrollCatId<?, T> category, Collection<T> add) {
        this.getCategory(category)
            .addEntries(add);
    }

    protected <T> void clearEntries(DCFScrollCatId<?, T> categoryId) {
        this.getCategory(categoryId)
            .clearEntries();
    }

    protected <T> void setEntries(DCFScrollCatId<?, T> categoryId, Collection<T> entries) {
        this.getCategory(categoryId)
            .clearEntries()
            .addEntries(entries);
    }

    private void next() {
        if (!isOnLastPage())
            this.toEach(DCFScrollCategory::forward);
    }

    @Override
    public Button btnNext() {
        if (this.data == null) return super.btnNext();
        return super.btnNext().withDisabled(isOnLastPage());
    }

    private void back() {
        if (!isOnFirstPage())
            this.toEach(DCFScrollCategory::back);
    }

    @Override
    public Button btnPrev() {
        if (this.data == null) return super.btnPrev();
        return super.btnPrev().withDisabled(isOnFirstPage());
    }

    @Override
    public Button btnFirst() {
        if (this.data == null) return super.btnFirst();
        return super.btnFirst().withDisabled(this.isOnFirstPage());
    }

    private void toEach(Consumer<DCFScrollCategory<?, ?>> fn) {
        this.getCategories().forEach(fn);
    }


    public boolean isOnFirstPage() {
        return getCategories().stream()
            .allMatch(cat -> cat.effectivePage() == 0);
    }

    public boolean isOnLastPage() {
        return getCategories().stream()
            .allMatch(cat -> cat.effectivePage() == cat.getMaxPage());
    }

    public int getCurrentPage() {
        return getCategories().stream()
            .mapToInt(DCFScrollCategory::effectivePage)
            .max().orElse(0);
    }

    public int getMaxPage() {
        return getCategories().stream()
            .mapToInt(DCFScrollCategory::getMaxPage)
            .max().orElse(0);
    }
}
