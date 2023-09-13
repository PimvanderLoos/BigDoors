package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class WorldGenFeatureOreConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureOreConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(WorldGenFeatureOreConfiguration.Target.d.fieldOf("target").forGetter((worldgenfeatureoreconfiguration) -> {
            return worldgenfeatureoreconfiguration.b;
        }), IBlockData.b.fieldOf("state").forGetter((worldgenfeatureoreconfiguration) -> {
            return worldgenfeatureoreconfiguration.d;
        }), Codec.INT.fieldOf("size").withDefault(0).forGetter((worldgenfeatureoreconfiguration) -> {
            return worldgenfeatureoreconfiguration.c;
        })).apply(instance, WorldGenFeatureOreConfiguration::new);
    });
    public final WorldGenFeatureOreConfiguration.Target b;
    public final int c;
    public final IBlockData d;

    public WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.Target worldgenfeatureoreconfiguration_target, IBlockData iblockdata, int i) {
        this.c = i;
        this.d = iblockdata;
        this.b = worldgenfeatureoreconfiguration_target;
    }

    public static enum Target implements INamable {

        NATURAL_STONE("natural_stone", (iblockdata) -> {
            return iblockdata == null ? false : iblockdata.a(Blocks.STONE) || iblockdata.a(Blocks.GRANITE) || iblockdata.a(Blocks.DIORITE) || iblockdata.a(Blocks.ANDESITE);
        }), NETHERRACK("netherrack", new BlockPredicate(Blocks.NETHERRACK)), NETHER_ORE_REPLACEABLES("nether_ore_replaceables", (iblockdata) -> {
            return iblockdata == null ? false : iblockdata.a(Blocks.NETHERRACK) || iblockdata.a(Blocks.BASALT) || iblockdata.a(Blocks.BLACKSTONE);
        });

        public static final Codec<WorldGenFeatureOreConfiguration.Target> d = INamable.a(WorldGenFeatureOreConfiguration.Target::values, WorldGenFeatureOreConfiguration.Target::a);
        private static final Map<String, WorldGenFeatureOreConfiguration.Target> e = (Map) Arrays.stream(values()).collect(Collectors.toMap(WorldGenFeatureOreConfiguration.Target::b, (worldgenfeatureoreconfiguration_target) -> {
            return worldgenfeatureoreconfiguration_target;
        }));
        private final String f;
        private final Predicate<IBlockData> g;

        private Target(String s, Predicate predicate) {
            this.f = s;
            this.g = predicate;
        }

        public String b() {
            return this.f;
        }

        public static WorldGenFeatureOreConfiguration.Target a(String s) {
            return (WorldGenFeatureOreConfiguration.Target) WorldGenFeatureOreConfiguration.Target.e.get(s);
        }

        public Predicate<IBlockData> c() {
            return this.g;
        }

        @Override
        public String getName() {
            return this.f;
        }
    }
}
