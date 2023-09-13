package net.minecraft.util.random;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.SystemUtils;
import net.minecraft.util.RandomSource;

public class WeightedRandom2 {

    private WeightedRandom2() {}

    public static int getTotalWeight(List<? extends WeightedEntry> list) {
        long i = 0L;

        WeightedEntry weightedentry;

        for (Iterator iterator = list.iterator(); iterator.hasNext(); i += (long) weightedentry.getWeight().asInt()) {
            weightedentry = (WeightedEntry) iterator.next();
        }

        if (i > 2147483647L) {
            throw new IllegalArgumentException("Sum of weights must be <= 2147483647");
        } else {
            return (int) i;
        }
    }

    public static <T extends WeightedEntry> Optional<T> getRandomItem(RandomSource randomsource, List<T> list, int i) {
        if (i < 0) {
            throw (IllegalArgumentException) SystemUtils.pauseInIde(new IllegalArgumentException("Negative total weight in getRandomItem"));
        } else if (i == 0) {
            return Optional.empty();
        } else {
            int j = randomsource.nextInt(i);

            return getWeightedItem(list, j);
        }
    }

    public static <T extends WeightedEntry> Optional<T> getWeightedItem(List<T> list, int i) {
        Iterator iterator = list.iterator();

        WeightedEntry weightedentry;

        do {
            if (!iterator.hasNext()) {
                return Optional.empty();
            }

            weightedentry = (WeightedEntry) iterator.next();
            i -= weightedentry.getWeight().asInt();
        } while (i >= 0);

        return Optional.of(weightedentry);
    }

    public static <T extends WeightedEntry> Optional<T> getRandomItem(RandomSource randomsource, List<T> list) {
        return getRandomItem(randomsource, list, getTotalWeight(list));
    }
}
