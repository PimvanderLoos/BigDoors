package net.minecraft.network.chat;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;

public class ChatPreviewThrottler {

    private final AtomicReference<ChatPreviewThrottler.a> scheduledRequest = new AtomicReference();
    @Nullable
    private CompletableFuture<?> runningRequest;

    public ChatPreviewThrottler() {}

    public void tick() {
        if (this.runningRequest != null && this.runningRequest.isDone()) {
            this.runningRequest = null;
        }

        if (this.runningRequest == null) {
            this.tickIdle();
        }

    }

    private void tickIdle() {
        ChatPreviewThrottler.a chatpreviewthrottler_a = (ChatPreviewThrottler.a) this.scheduledRequest.getAndSet((Object) null);

        if (chatpreviewthrottler_a != null) {
            this.runningRequest = chatpreviewthrottler_a.run();
        }

    }

    public void schedule(ChatPreviewThrottler.a chatpreviewthrottler_a) {
        this.scheduledRequest.set(chatpreviewthrottler_a);
    }

    @FunctionalInterface
    public interface a {

        CompletableFuture<?> run();
    }
}
