package net.minecraft.network.chat;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

public class LastSeenMessagesValidator {

    private static final int NOT_FOUND = Integer.MIN_VALUE;
    private LastSeenMessages lastSeenMessages;
    private final ObjectList<LastSeenMessages.a> pendingEntries;

    public LastSeenMessagesValidator() {
        this.lastSeenMessages = LastSeenMessages.EMPTY;
        this.pendingEntries = new ObjectArrayList();
    }

    public void addPending(LastSeenMessages.a lastseenmessages_a) {
        this.pendingEntries.add(lastseenmessages_a);
    }

    public int pendingMessagesCount() {
        return this.pendingEntries.size();
    }

    private boolean hasDuplicateProfiles(LastSeenMessages lastseenmessages) {
        Set<UUID> set = new HashSet(lastseenmessages.entries().size());
        Iterator iterator = lastseenmessages.entries().iterator();

        LastSeenMessages.a lastseenmessages_a;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            lastseenmessages_a = (LastSeenMessages.a) iterator.next();
        } while (set.add(lastseenmessages_a.profileId()));

        return true;
    }

    private int calculateIndices(List<LastSeenMessages.a> list, int[] aint, @Nullable LastSeenMessages.a lastseenmessages_a) {
        Arrays.fill(aint, Integer.MIN_VALUE);
        List<LastSeenMessages.a> list1 = this.lastSeenMessages.entries();
        int i = list1.size();

        int j;
        int k;

        for (k = i - 1; k >= 0; --k) {
            j = list.indexOf(list1.get(k));
            if (j != -1) {
                aint[j] = -k - 1;
            }
        }

        k = Integer.MIN_VALUE;
        j = this.pendingEntries.size();

        for (int l = 0; l < j; ++l) {
            LastSeenMessages.a lastseenmessages_a1 = (LastSeenMessages.a) this.pendingEntries.get(l);
            int i1 = list.indexOf(lastseenmessages_a1);

            if (i1 != -1) {
                aint[i1] = l;
            }

            if (lastseenmessages_a1.equals(lastseenmessages_a)) {
                k = l;
            }
        }

        return k;
    }

    public Set<LastSeenMessagesValidator.a> validateAndUpdate(LastSeenMessages.b lastseenmessages_b) {
        EnumSet<LastSeenMessagesValidator.a> enumset = EnumSet.noneOf(LastSeenMessagesValidator.a.class);
        LastSeenMessages lastseenmessages = lastseenmessages_b.lastSeen();
        LastSeenMessages.a lastseenmessages_a = (LastSeenMessages.a) lastseenmessages_b.lastReceived().orElse((Object) null);
        List<LastSeenMessages.a> list = lastseenmessages.entries();
        int i = this.lastSeenMessages.entries().size();
        int j = Integer.MIN_VALUE;
        int k = list.size();

        if (k < i) {
            enumset.add(LastSeenMessagesValidator.a.REMOVED_MESSAGES);
        }

        int[] aint = new int[k];
        int l = this.calculateIndices(list, aint, lastseenmessages_a);

        for (int i1 = k - 1; i1 >= 0; --i1) {
            int j1 = aint[i1];

            if (j1 != Integer.MIN_VALUE) {
                if (j1 < j) {
                    enumset.add(LastSeenMessagesValidator.a.OUT_OF_ORDER);
                } else {
                    j = j1;
                }
            } else {
                enumset.add(LastSeenMessagesValidator.a.UNKNOWN_MESSAGES);
            }
        }

        if (lastseenmessages_a != null) {
            if (l != Integer.MIN_VALUE && l >= j) {
                j = l;
            } else {
                enumset.add(LastSeenMessagesValidator.a.UNKNOWN_MESSAGES);
            }
        }

        if (j >= 0) {
            this.pendingEntries.removeElements(0, j + 1);
        }

        if (this.hasDuplicateProfiles(lastseenmessages)) {
            enumset.add(LastSeenMessagesValidator.a.DUPLICATED_PROFILES);
        }

        this.lastSeenMessages = lastseenmessages;
        return enumset;
    }

    public static enum a {

        OUT_OF_ORDER("messages received out of order"), DUPLICATED_PROFILES("multiple entries for single profile"), UNKNOWN_MESSAGES("unknown message"), REMOVED_MESSAGES("previously present messages removed from context");

        private final String message;

        private a(String s) {
            this.message = s;
        }

        public String message() {
            return this.message;
        }
    }
}
