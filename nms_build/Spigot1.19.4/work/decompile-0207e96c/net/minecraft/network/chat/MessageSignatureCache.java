package net.minecraft.network.chat;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

public class MessageSignatureCache {

    public static final int NOT_FOUND = -1;
    private static final int DEFAULT_CAPACITY = 128;
    private final MessageSignature[] entries;

    public MessageSignatureCache(int i) {
        this.entries = new MessageSignature[i];
    }

    public static MessageSignatureCache createDefault() {
        return new MessageSignatureCache(128);
    }

    public int pack(MessageSignature messagesignature) {
        for (int i = 0; i < this.entries.length; ++i) {
            if (messagesignature.equals(this.entries[i])) {
                return i;
            }
        }

        return -1;
    }

    @Nullable
    public MessageSignature unpack(int i) {
        return this.entries[i];
    }

    public void push(PlayerChatMessage playerchatmessage) {
        List<MessageSignature> list = playerchatmessage.signedBody().lastSeen().entries();
        ArrayDeque<MessageSignature> arraydeque = new ArrayDeque(list.size() + 1);

        arraydeque.addAll(list);
        MessageSignature messagesignature = playerchatmessage.signature();

        if (messagesignature != null) {
            arraydeque.add(messagesignature);
        }

        this.push(arraydeque);
    }

    @VisibleForTesting
    void push(List<MessageSignature> list) {
        this.push(new ArrayDeque(list));
    }

    private void push(ArrayDeque<MessageSignature> arraydeque) {
        Set<MessageSignature> set = new ObjectOpenHashSet(arraydeque);

        for (int i = 0; !arraydeque.isEmpty() && i < this.entries.length; ++i) {
            MessageSignature messagesignature = this.entries[i];

            this.entries[i] = (MessageSignature) arraydeque.removeLast();
            if (messagesignature != null && !set.contains(messagesignature)) {
                arraydeque.addFirst(messagesignature);
            }
        }

    }
}
