package net.minecraft.util.random;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.SystemUtils;

public class WeightedRandom2 {

    private WeightedRandom2() {}

    public static int a(List<? extends WeightedEntry> list) {
        long i = 0L;

        WeightedEntry weightedentry;

        for (Iterator iterator = list.iterator(); iterator.hasNext(); i += (long) weightedentry.a().a()) {
            weightedentry = (WeightedEntry) iterator.next();
        }

        if (i > 2147483647L) {
            throw new IllegalArgumentException("Sum of weights must be <= 2147483647");
        } else {
            return (int) i;
        }
    }

    public static <T extends WeightedEntry> Optional<T> a(Random random, List<T> list, int i) {
        if (i < 0) {
            throw (IllegalArgumentException) SystemUtils.c((Throwable) (new IllegalArgumentException("Negative total weight in getRandomItem")));
        } else if (i == 0) {
            return Optional.empty();
        } else {
            int j = random.nextInt(i);

            return a(list, j);
        }
    }

    public static <T extends WeightedEntry> Optional<T> a(List<T> list, int i) {
        Iterator iterator = list.iterator();

        WeightedEntry weightedentry;

        do {
            if (!iterator.hasNext()) {
                return Optional.empty();
            }

            weightedentry = (WeightedEntry) iterator.next();
            i -= weightedentry.a().a();
        } while (i >= 0);

        return Optional.of(weightedentry);
    }

    public static <T extends WeightedEntry> Optional<T> a(Random random, List<T> list) {
        return a(random, list, a(list));
    }
}
