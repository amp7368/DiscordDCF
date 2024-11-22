package discord.util.dcf.util.defer;

import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

public interface DCFUtilsDeferReply {

    default <T> DCFDeferReplyBuilder<T> builderDefer(IReplyCallback event,
        BiConsumer<InteractionHook, T> consumer,
        Supplier<T> supplier) {
        return new DCFDeferReplyBuilder<>(event, consumer, supplier);
    }

    interface IDCFDeferReply {

        void startDefer();

    }
}
