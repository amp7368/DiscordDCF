package discord.util.dcf;

import discord.util.dcf.gui.base.GuiEventHandler;
import discord.util.dcf.gui.util.interaction.OnInteractionListener;
import discord.util.dcf.util.TimeMillis;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class DCFGuiManager implements OnInteractionListener {

    private final HashMap<Long, GuiEventHandler> guis = new HashMap<>();
    private final DCF dcf;
    private final List<TrimTask> trimTasks = new ArrayList<>();
    private final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> scheduledTrimTask;
    private long nextTrimTime = 0;

    public DCFGuiManager(DCF dcf) {
        this.dcf = dcf;
    }

    public void addGui(GuiEventHandler gui) {
        synchronized (guis) {
            guis.put(gui.getMessageId(), gui);
        }
        submitTrim();
    }

    public void remove(long id) {
        synchronized (guis) {
            guis.remove(id);
        }
        synchronized (trimTasks) {
            trimTasks.removeIf(trimTask -> trimTask.isGui(id));
        }
    }

    private void removeAll(Collection<Long> ids) {
        Collection<Long> optimizedIds;
        if (ids.size() >= 10) optimizedIds = new HashSet<>(ids);
        else optimizedIds = ids;

        List<Exception> exceptions = new ArrayList<>();
        synchronized (guis) {
            try {
                optimizedIds.forEach(guis::remove);
            } catch (Exception e) {
                exceptions.add(e);
            }
        }
        for (Exception e : exceptions) {
            dcf.logger().error("Error trying to remove gui.", e);
        }
        synchronized (trimTasks) {
            trimTasks.removeIf(trimTask -> trimTask.inList(optimizedIds));
        }
    }

    private void removeOldTasks() {
        synchronized (trimTasks) {
            long now = System.currentTimeMillis();
            trimTasks.removeIf(trimTask -> trimTask.isOld(now));
        }
    }

    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        onEvent(event.getMessageIdLong(), gui -> gui.onButtonClick(event));
    }

    @Override
    public void onSelectStringInteraction(StringSelectInteractionEvent event) {
        onEvent(event.getMessageIdLong(), gui -> gui.onSelectString(event));
    }

    @Override
    public void onSelectEntityInteraction(EntitySelectInteractionEvent event) {
        onEvent(event.getMessageIdLong(), gui -> gui.onSelectEntity(event));
    }

    private void onEvent(long messageId, Consumer<GuiEventHandler> callback) {
        GuiEventHandler gui;
        synchronized (guis) {
            gui = guis.get(messageId);
        }
        if (gui != null && !gui.shouldRemove()) callback.accept(gui);
        submitTrim();
    }

    private void submitTrim() {
        synchronized (guis) {
            if (System.currentTimeMillis() < nextTrimTime) return;
            nextTrimTime = System.currentTimeMillis() + TimeMillis.minToMillis(1);
        }
        EXECUTOR.execute(this::trim);
    }

    private synchronized void trim() {
        this.removeOldTasks();
        List<GuiEventHandler> filtered;
        synchronized (guis) {
            filtered = guis.values().stream()
                .filter(GuiEventHandler::shouldRemove)
                .toList();
        }
        List<Long> removeIds = filtered.stream()
            .map(GuiEventHandler::getMessageId)
            .toList();
        filtered.forEach(GuiEventHandler::remove);
        this.removeAll(removeIds);
    }

    private void scheduleNextTrimTask() {
        long shouldBeTask;
        synchronized (trimTasks) {
            scheduledTrimTask = null;
            if (trimTasks.isEmpty()) return;
            shouldBeTask = trimTasks.get(0).trimAt();
        }
        long delay = shouldBeTask - System.currentTimeMillis();
        delay = Math.max(1000, delay);
        scheduledTrimTask = EXECUTOR.schedule(() -> {
            trim();
            scheduleNextTrimTask();
        }, delay, TimeUnit.MILLISECONDS);
    }

    public void submitScheduleTrimAt(GuiEventHandler gui, Instant trimAt) {
        EXECUTOR.execute(() -> scheduleTrimAt(gui, trimAt));
    }

    public void scheduleTrimAt(GuiEventHandler gui, Instant trimAt) {
        long guiId = gui.getMessageId();

        long shouldBeTask;
        ScheduledFuture<?> toCancel;
        synchronized (trimTasks) {
            long currentTask;
            if (trimTasks.isEmpty()) currentTask = Long.MAX_VALUE;
            else currentTask = trimTasks.get(0).trimAt();

            trimTasks.removeIf(task -> task.isGui(guiId));

            trimTasks.add(new TrimTask(guiId, trimAt.toEpochMilli()));
            trimTasks.sort(Comparator.naturalOrder());

            shouldBeTask = trimTasks.get(0).trimAt();
            if (currentTask == shouldBeTask) return;
            toCancel = scheduledTrimTask;
        }
        if (toCancel != null) {
            boolean wasCanceled = toCancel.cancel(false);
            if (!wasCanceled && !toCancel.isDone()) return;
        }
        scheduleNextTrimTask();
    }

    private record TrimTask(long guiId, long trimAt) implements Comparable<TrimTask> {

        @Override
        public int compareTo(@NotNull TrimTask o) {
            return Long.compare(trimAt, o.trimAt);
        }

        public boolean isGui(long other) {
            return guiId == other;
        }

        public boolean inList(Collection<Long> ids) {
            return ids.contains(guiId);
        }

        public boolean isOld(long now) {
            return this.trimAt <= now;
        }
    }
}
