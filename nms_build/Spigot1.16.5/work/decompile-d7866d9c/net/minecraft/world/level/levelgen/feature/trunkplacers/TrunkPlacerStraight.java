package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.VirtualLevelWritable;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.WorldGenFoilagePlacer;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;

public class TrunkPlacerStraight extends TrunkPlacer {

    public static final Codec<TrunkPlacerStraight> a = RecordCodecBuilder.create((instance) -> {
        return a(instance).apply(instance, TrunkPlacerStraight::new);
    });

    public TrunkPlacerStraight(int i, int j, int k) {
        super(i, j, k);
    }

    @Override
    protected TrunkPlacers<?> a() {
        return TrunkPlacers.a;
    }

    @Override
    public List<WorldGenFoilagePlacer.b> a(VirtualLevelWritable virtuallevelwritable, Random random, int i, BlockPosition blockposition, Set<BlockPosition> set, StructureBoundingBox structureboundingbox, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        a(virtuallevelwritable, blockposition.down());

        for (int j = 0; j < i; ++j) {
            a(virtuallevelwritable, random, blockposition.up(j), set, structureboundingbox, worldgenfeaturetreeconfiguration);
        }

        return ImmutableList.of(new WorldGenFoilagePlacer.b(blockposition.up(i), 0, false));
    }
}
