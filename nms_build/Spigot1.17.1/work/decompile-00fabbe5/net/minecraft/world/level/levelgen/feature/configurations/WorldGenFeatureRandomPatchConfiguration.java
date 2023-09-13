package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.blockplacers.WorldGenBlockPlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProvider;

public class WorldGenFeatureRandomPatchConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureRandomPatchConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(WorldGenFeatureStateProvider.CODEC.fieldOf("state_provider").forGetter((worldgenfeaturerandompatchconfiguration) -> {
            return worldgenfeaturerandompatchconfiguration.stateProvider;
        }), WorldGenBlockPlacer.CODEC.fieldOf("block_placer").forGetter((worldgenfeaturerandompatchconfiguration) -> {
            return worldgenfeaturerandompatchconfiguration.blockPlacer;
        }), IBlockData.CODEC.listOf().fieldOf("whitelist").forGetter((worldgenfeaturerandompatchconfiguration) -> {
            return (List) worldgenfeaturerandompatchconfiguration.whitelist.stream().map(Block::getBlockData).collect(Collectors.toList());
        }), IBlockData.CODEC.listOf().fieldOf("blacklist").forGetter((worldgenfeaturerandompatchconfiguration) -> {
            return ImmutableList.copyOf(worldgenfeaturerandompatchconfiguration.blacklist);
        }), ExtraCodecs.POSITIVE_INT.fieldOf("tries").orElse(128).forGetter((worldgenfeaturerandompatchconfiguration) -> {
            return worldgenfeaturerandompatchconfiguration.tries;
        }), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("xspread").orElse(7).forGetter((worldgenfeaturerandompatchconfiguration) -> {
            return worldgenfeaturerandompatchconfiguration.xspread;
        }), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("yspread").orElse(3).forGetter((worldgenfeaturerandompatchconfiguration) -> {
            return worldgenfeaturerandompatchconfiguration.yspread;
        }), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("zspread").orElse(7).forGetter((worldgenfeaturerandompatchconfiguration) -> {
            return worldgenfeaturerandompatchconfiguration.zspread;
        }), Codec.BOOL.fieldOf("can_replace").orElse(false).forGetter((worldgenfeaturerandompatchconfiguration) -> {
            return worldgenfeaturerandompatchconfiguration.canReplace;
        }), Codec.BOOL.fieldOf("project").orElse(true).forGetter((worldgenfeaturerandompatchconfiguration) -> {
            return worldgenfeaturerandompatchconfiguration.project;
        }), Codec.BOOL.fieldOf("need_water").orElse(false).forGetter((worldgenfeaturerandompatchconfiguration) -> {
            return worldgenfeaturerandompatchconfiguration.needWater;
        })).apply(instance, WorldGenFeatureRandomPatchConfiguration::new);
    });
    public final WorldGenFeatureStateProvider stateProvider;
    public final WorldGenBlockPlacer blockPlacer;
    public final Set<Block> whitelist;
    public final Set<IBlockData> blacklist;
    public final int tries;
    public final int xspread;
    public final int yspread;
    public final int zspread;
    public final boolean canReplace;
    public final boolean project;
    public final boolean needWater;

    private WorldGenFeatureRandomPatchConfiguration(WorldGenFeatureStateProvider worldgenfeaturestateprovider, WorldGenBlockPlacer worldgenblockplacer, List<IBlockData> list, List<IBlockData> list1, int i, int j, int k, int l, boolean flag, boolean flag1, boolean flag2) {
        this(worldgenfeaturestateprovider, worldgenblockplacer, (Set) list.stream().map(BlockBase.BlockData::getBlock).collect(Collectors.toSet()), (Set) ImmutableSet.copyOf(list1), i, j, k, l, flag, flag1, flag2);
    }

    WorldGenFeatureRandomPatchConfiguration(WorldGenFeatureStateProvider worldgenfeaturestateprovider, WorldGenBlockPlacer worldgenblockplacer, Set<Block> set, Set<IBlockData> set1, int i, int j, int k, int l, boolean flag, boolean flag1, boolean flag2) {
        this.stateProvider = worldgenfeaturestateprovider;
        this.blockPlacer = worldgenblockplacer;
        this.whitelist = set;
        this.blacklist = set1;
        this.tries = i;
        this.xspread = j;
        this.yspread = k;
        this.zspread = l;
        this.canReplace = flag;
        this.project = flag1;
        this.needWater = flag2;
    }

    public static class a {

        private final WorldGenFeatureStateProvider stateProvider;
        private final WorldGenBlockPlacer blockPlacer;
        private Set<Block> whitelist = ImmutableSet.of();
        private Set<IBlockData> blacklist = ImmutableSet.of();
        private int tries = 64;
        private int xspread = 7;
        private int yspread = 3;
        private int zspread = 7;
        private boolean canReplace;
        private boolean project = true;
        private boolean needWater;

        public a(WorldGenFeatureStateProvider worldgenfeaturestateprovider, WorldGenBlockPlacer worldgenblockplacer) {
            this.stateProvider = worldgenfeaturestateprovider;
            this.blockPlacer = worldgenblockplacer;
        }

        public WorldGenFeatureRandomPatchConfiguration.a a(Set<Block> set) {
            this.whitelist = set;
            return this;
        }

        public WorldGenFeatureRandomPatchConfiguration.a b(Set<IBlockData> set) {
            this.blacklist = set;
            return this;
        }

        public WorldGenFeatureRandomPatchConfiguration.a a(int i) {
            this.tries = i;
            return this;
        }

        public WorldGenFeatureRandomPatchConfiguration.a b(int i) {
            this.xspread = i;
            return this;
        }

        public WorldGenFeatureRandomPatchConfiguration.a c(int i) {
            this.yspread = i;
            return this;
        }

        public WorldGenFeatureRandomPatchConfiguration.a d(int i) {
            this.zspread = i;
            return this;
        }

        public WorldGenFeatureRandomPatchConfiguration.a a() {
            this.canReplace = true;
            return this;
        }

        public WorldGenFeatureRandomPatchConfiguration.a b() {
            this.project = false;
            return this;
        }

        public WorldGenFeatureRandomPatchConfiguration.a c() {
            this.needWater = true;
            return this;
        }

        public WorldGenFeatureRandomPatchConfiguration d() {
            return new WorldGenFeatureRandomPatchConfiguration(this.stateProvider, this.blockPlacer, this.whitelist, this.blacklist, this.tries, this.xspread, this.yspread, this.zspread, this.canReplace, this.project, this.needWater);
        }
    }
}
