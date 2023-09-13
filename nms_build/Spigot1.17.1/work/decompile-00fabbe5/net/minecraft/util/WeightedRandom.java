package net.minecraft.util;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.SharedConstants;
import net.minecraft.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WeightedRandom {

    static final Logger LOGGER = LogManager.getLogger();

    public WeightedRandom() {}

    public static int a(List<? extends WeightedRandom.WeightedRandomChoice> list) {
        long i = 0L;

        WeightedRandom.WeightedRandomChoice weightedrandom_weightedrandomchoice;

        for (Iterator iterator = list.iterator(); iterator.hasNext(); i += (long) weightedrandom_weightedrandomchoice.weight) {
            weightedrandom_weightedrandomchoice = (WeightedRandom.WeightedRandomChoice) iterator.next();
        }

        if (i > 2147483647L) {
            throw new IllegalArgumentException("Sum of weights must be <= 2147483647");
        } else {
            return (int) i;
        }
    }

    public static <T extends WeightedRandom.WeightedRandomChoice> Optional<T> a(Random random, List<T> list, int i) {
        if (i < 0) {
            throw (IllegalArgumentException) SystemUtils.c((Throwable) (new IllegalArgumentException("Negative total weight in getRandomItem")));
        } else if (i == 0) {
            return Optional.empty();
        } else {
            int j = random.nextInt(i);

            return a(list, j);
        }
    }

    public static <T extends WeightedRandom.WeightedRandomChoice> Optional<T> a(List<T> list, int i) {
        Iterator iterator = list.iterator();

        WeightedRandom.WeightedRandomChoice weightedrandom_weightedrandomchoice;

        do {
            if (!iterator.hasNext()) {
                return Optional.empty();
            }

            weightedrandom_weightedrandomchoice = (WeightedRandom.WeightedRandomChoice) iterator.next();
            i -= weightedrandom_weightedrandomchoice.weight;
        } while (i >= 0);

        return Optional.of(weightedrandom_weightedrandomchoice);
    }

    public static <T extends WeightedRandom.WeightedRandomChoice> Optional<T> a(Random random, List<T> list) {
        return a(random, list, a(list));
    }

    public static class WeightedRandomChoice {

        protected final int weight;

        public WeightedRandomChoice(int i) {
            if (i < 0) {
                throw (IllegalArgumentException) SystemUtils.c((Throwable) (new IllegalArgumentException("Weight should be >= 0")));
            } else {
                if (i == 0 && SharedConstants.IS_RUNNING_IN_IDE) {
                    WeightedRandom.LOGGER.warn("Found 0 weight, make sure this is intentional!");
                }

                this.weight = i;
            }
        }
    }
}
