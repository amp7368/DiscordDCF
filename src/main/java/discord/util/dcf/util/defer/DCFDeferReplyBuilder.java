package discord.util.dcf.util.defer;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import org.jetbrains.annotations.NotNull;

public class DCFDeferReplyBuilder<T> implements DCFUtilsDeferReply.IDCFDeferReply {

    @NotNull
    private final IReplyCallback event;
    @NotNull
    private final BiConsumer<InteractionHook, T> consumer;
    @NotNull
    private final Supplier<T> supplier;
    private Executor executor;
    private Consumer<InteractionHook> onDefer;
    private Consumer<? super Throwable> onFailure;

    DCFDeferReplyBuilder(@NotNull IReplyCallback event,
        @NotNull BiConsumer<InteractionHook, T> consumer,
        @NotNull Supplier<T> supplier) {
        this.event = event;
        this.consumer = consumer;
        this.supplier = supplier;
    }

    @Override
    public void startDefer() {
        CompletableFuture<T> task = Optional.ofNullable(executor)
            .map((ex) -> CompletableFuture.supplyAsync(supplier, ex))
            .orElse(CompletableFuture.supplyAsync(supplier));

        event.deferReply().queue(
            hook -> {
                if (onDefer != null)
                    onDefer.accept(hook);

                Consumer<T> tConsumer = obj -> consumer.accept(hook, obj);
                if (executor == null) task.thenAcceptAsync(tConsumer);
                else task.thenAcceptAsync(tConsumer, executor);
            },
            onFailure
        );
    }

    public discord.util.dcf.util.defer.DCFDeferReplyBuilder<T> withOnFailure(Consumer<? super Throwable> onFailure) {
        this.onFailure = onFailure;
        return this;
    }

    public discord.util.dcf.util.defer.DCFDeferReplyBuilder<T> withExecutor(Executor executor) {
        this.executor = executor;
        return this;
    }

    public discord.util.dcf.util.defer.DCFDeferReplyBuilder<T> withOnDefer(Consumer<InteractionHook> onDefer) {
        this.onDefer = onDefer;
        return this;
    }
}
