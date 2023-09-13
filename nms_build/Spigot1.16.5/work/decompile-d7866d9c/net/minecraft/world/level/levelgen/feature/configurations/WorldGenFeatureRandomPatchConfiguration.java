package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.blockplacers.WorldGenBlockPlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProvider;

public class WorldGenFeatureRandomPatchConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureRandomPatchConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(WorldGenFeatureStateProvider.a.fieldOf("state_provider").forGetter((worldgenfeaturerandompatchconfiguration) -> {
            return worldgenfeaturerandompatchconfiguration.b;
        }), WorldGenBlockPlacer.a.fieldOf("block_placer").forGetter((worldgenfeaturerandompatchconfiguration) -> {
            return worldgenfeaturerandompatchconfiguration.c;
        }), IBlockData.b.listOf().fieldOf("whitelist").forGetter((worldgenfeaturerandompatchconfiguration) -> {
            return (List) worldgenfeaturerandompatchconfiguration.d.stream().map(Block::getBlockData).collect(Collectors.toList());
        }), IBlockData.b.listOf().fieldOf("blacklist").forGetter((worldgenfeaturerandompatchconfiguration) -> {
            return ImmutableList.copyOf(worldgenfeaturerandompatchconfiguration.e);
        }), Codec.INT.fieldOf("tries").orElse(128).forGetter((worldgenfeaturerandompatchconfiguration) -> {
            return worldgenfeaturerandompatchconfiguration.f;
        }), Codec.INT.fieldOf("xspread").orElse(7).forGetter((worldgenfeaturerandompatchconfiguration) -> {
            return worldgenfeaturerandompatchconfiguration.g;
        }), Codec.INT.fieldOf("yspread").orElse(3).forGetter((worldgenfeaturerandompatchconfiguration) -> {
            return worldgenfeaturerandompatchconfiguration.h;
        }), Codec.INT.fieldOf("zspread").orElse(7).forGetter((worldgenfeaturerandompatchconfiguration) -> {
            return worldgenfeaturerandompatchconfiguration.i;
        }), Codec.BOOL.fieldOf("can_replace").orElse(false).forGetter((worldgenfeaturerandompatchconfiguration) -> {
            return worldgenfeaturerandompatchconfiguration.j;
        }), Codec.BOOL.fieldOf("project").orElse(true).forGetter((worldgenfeaturerandompatchconfiguration) -> {
            return worldgenfeaturerandompatchconfiguration.l;
        }), Codec.BOOL.fieldOf("need_water").orElse(false).forGetter((worldgenfeaturerandompatchconfiguration) -> {
            return worldgenfeaturerandompatchconfiguration.m;
        })).apply(instance, WorldGenFeatureRandomPatchConfiguration::new);
    });
    public final WorldGenFeatureStateProvider b;
    public final WorldGenBlockPlacer c;
    public final Set<Block> d;
    public final Set<IBlockData> e;
    public final int f;
    public final int g;
    public final int h;
    public final int i;
    public final boolean j;
    public final boolean l;
    public final boolean m;

    private WorldGenFeatureRandomPatchConfiguration(WorldGenFeatureStateProvider worldgenfeaturestateprovider, WorldGenBlockPlacer worldgenblockplacer, List<IBlockData> list, List<IBlockData> list1, int i, int j, int k, int l, boolean flag, boolean flag1, boolean flag2) {
        this(worldgenfeaturestateprovider, worldgenblockplacer, (Set) list.stream().map(BlockBase.BlockData::getBlock).collect(Collectors.toSet()), (Set) ImmutableSet.copyOf(list1), i, j, k, l, flag, flag1, flag2);
    }

    private WorldGenFeatureRandomPatchConfiguration(WorldGenFeatureStateProvider worldgenfeaturestateprovider, WorldGenBlockPlacer worldgenblockplacer, Set<Block> set, Set<IBlockData> set1, int i, int j, int k, int l, boolean flag, boolean flag1, boolean flag2) {
        this.b = worldgenfeaturestateprovider;
        this.c = worldgenblockplacer;
        this.d = set;
        this.e = set1;
        this.f = i;
        this.g = j;
        this.h = k;
        this.i = l;
        this.j = flag;
        this.l = flag1;
        this.m = flag2;
    }

    public static class a {

        private final WorldGenFeatureStateProvider a;
        private final WorldGenBlockPlacer b;
        private Set<Block> c = ImmutableSet.of();
        private Set<IBlockData> d = ImmutableSet.of();
        private int e = 64;
        private int f = 7;
        private int g = 3;
        private int h = 7;
        private boolean i;
        private boolean j = true;
        private boolean k = false;

        public a(WorldGenFeatureStateProvider worldgenfeaturestateprovider, WorldGenBlockPlacer worldgenblockplacer) {
            this.a = worldgenfeaturestateprovider;
            this.b = worldgenblockplacer;
        }

        public WorldGenFeatureRandomPatchConfiguration.a a(Set<Block> set) {
            this.c = set;
            return this;
        }

        public WorldGenFeatureRandomPatchConfiguration.a b(Set<IBlockData> set) {
            this.d = set;
            return this;
        }

        public WorldGenFeatureRandomPatchConfiguration.a a(int i) {
            this.e = i;
            return this;
        }

        public WorldGenFeatureRandomPatchConfiguration.a b(int i) {
            this.f = i;
            return this;
        }

        public WorldGenFeatureRandomPatchConfiguration.a c(int i) {
            this.g = i;
            return this;
        }

        public WorldGenFeatureRandomPatchConfiguration.a d(int i) {
            this.h = i;
            return this;
        }

        public WorldGenFeatureRandomPatchConfiguration.a a() {
            this.i = true;
            return this;
        }

        public WorldGenFeatureRandomPatchConfiguration.a b() {
            this.j = false;
            return this;
        }

        public WorldGenFeatureRandomPatchConfiguration.a c() {
            this.k = true;
            return this;
        }

        public WorldGenFeatureRandomPatchConfiguration d() {
            return new WorldGenFeatureRandomPatchConfiguration(this.a, this.b, this.c, this.d, this.e, this.f, this.g, this.h, this.i, this.j, this.k);
        }
    }
}
