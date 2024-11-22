package discord.util.dcf.gui.scroll.multi;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public interface DCFScrollCatId<Id, T> {

    static <Id, T> DCFScrollCatId<Id, T> createOne(Comparator<T> comparator, @NotNull Id id) {
        return new DCFScrollCatIdImpl<>(id, comparator);
    }

    static <Id, T> List<DCFScrollCatId<Id, T>> create(Comparator<T> comparator, @NotNull List<Id> ids) {
        return ids.stream()
            .map(id -> createOne(comparator, id))
            .toList();
    }

    static <Id, T> List<DCFScrollCatId<Id, T>> create(Comparator<T> comparator, @NotNull Id[] ids) {
        return create(comparator, List.of(ids));
    }

    Id id();

    Comparator<T> comparator();

    boolean equals(Object o);

    int hashCode();

    record DCFScrollCatIdImpl<Id, T>(@NotNull Id id, Comparator<T> comparator) implements DCFScrollCatId<Id, T> {

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof DCFScrollCatId.DCFScrollCatIdImpl<?, ?> other)
                return Objects.equals(id, other.id);
            return false;
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }
}
