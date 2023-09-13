package net.minecraft.world.flag;

import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.resources.MinecraftKey;
import org.slf4j.Logger;

public class FeatureFlagRegistry {

    private static final Logger LOGGER = LogUtils.getLogger();
    private final FeatureFlagUniverse universe;
    private final Map<MinecraftKey, FeatureFlag> names;
    private final FeatureFlagSet allFlags;

    FeatureFlagRegistry(FeatureFlagUniverse featureflaguniverse, FeatureFlagSet featureflagset, Map<MinecraftKey, FeatureFlag> map) {
        this.universe = featureflaguniverse;
        this.names = map;
        this.allFlags = featureflagset;
    }

    public boolean isSubset(FeatureFlagSet featureflagset) {
        return featureflagset.isSubsetOf(this.allFlags);
    }

    public FeatureFlagSet allFlags() {
        return this.allFlags;
    }

    public FeatureFlagSet fromNames(Iterable<MinecraftKey> iterable) {
        return this.fromNames(iterable, (minecraftkey) -> {
            FeatureFlagRegistry.LOGGER.warn("Unknown feature flag: {}", minecraftkey);
        });
    }

    public FeatureFlagSet subset(FeatureFlag... afeatureflag) {
        return FeatureFlagSet.create(this.universe, Arrays.asList(afeatureflag));
    }

    public FeatureFlagSet fromNames(Iterable<MinecraftKey> iterable, Consumer<MinecraftKey> consumer) {
        Set<FeatureFlag> set = Sets.newIdentityHashSet();
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            MinecraftKey minecraftkey = (MinecraftKey) iterator.next();
            FeatureFlag featureflag = (FeatureFlag) this.names.get(minecraftkey);

            if (featureflag == null) {
                consumer.accept(minecraftkey);
            } else {
                set.add(featureflag);
            }
        }

        return FeatureFlagSet.create(this.universe, set);
    }

    public Set<MinecraftKey> toNames(FeatureFlagSet featureflagset) {
        Set<MinecraftKey> set = new HashSet();

        this.names.forEach((minecraftkey, featureflag) -> {
            if (featureflagset.contains(featureflag)) {
                set.add(minecraftkey);
            }

        });
        return set;
    }

    public Codec<FeatureFlagSet> codec() {
        return MinecraftKey.CODEC.listOf().comapFlatMap((list) -> {
            Set<MinecraftKey> set = new HashSet();

            Objects.requireNonNull(set);
            FeatureFlagSet featureflagset = this.fromNames(list, set::add);

            return !set.isEmpty() ? DataResult.error("Unknown feature ids: " + set, featureflagset) : DataResult.success(featureflagset);
        }, (featureflagset) -> {
            return List.copyOf(this.toNames(featureflagset));
        });
    }

    public static class a {

        private final FeatureFlagUniverse universe;
        private int id;
        private final Map<MinecraftKey, FeatureFlag> flags = new LinkedHashMap();

        public a(String s) {
            this.universe = new FeatureFlagUniverse(s);
        }

        public FeatureFlag createVanilla(String s) {
            return this.create(new MinecraftKey("minecraft", s));
        }

        public FeatureFlag create(MinecraftKey minecraftkey) {
            if (this.id >= 64) {
                throw new IllegalStateException("Too many feature flags");
            } else {
                FeatureFlag featureflag = new FeatureFlag(this.universe, this.id++);
                FeatureFlag featureflag1 = (FeatureFlag) this.flags.put(minecraftkey, featureflag);

                if (featureflag1 != null) {
                    throw new IllegalStateException("Duplicate feature flag " + minecraftkey);
                } else {
                    return featureflag;
                }
            }
        }

        public FeatureFlagRegistry build() {
            FeatureFlagSet featureflagset = FeatureFlagSet.create(this.universe, this.flags.values());

            return new FeatureFlagRegistry(this.universe, featureflagset, Map.copyOf(this.flags));
        }
    }
}
