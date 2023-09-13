package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.VirtualLevelWritable;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.WorldGenFoilagePlacer;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;

public class TrunkPlacerGiant extends TrunkPlacer {

    public static final Codec<TrunkPlacerGiant> a = RecordCodecBuilder.create((instance) -> {
        return a(instance).apply(instance, TrunkPlacerGiant::new);
    });

    public TrunkPlacerGiant(int i, int j, int k) {
        super(i, j, k);
    }

    @Override
    protected TrunkPlacers<?> a() {
        return TrunkPlacers.c;
    }

    @Override
    public List<WorldGenFoilagePlacer.b> a(VirtualLevelWritable virtuallevelwritable, Random random, int i, BlockPosition blockposition, Set<BlockPosition> set, StructureBoundingBox structureboundingbox, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        BlockPosition blockposition1 = blockposition.down();

        a(virtuallevelwritable, blockposition1);
        a(virtuallevelwritable, blockposition1.east());
        a(virtuallevelwritable, blockposition1.south());
        a(virtuallevelwritable, blockposition1.south().east());
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int j = 0; j < i; ++j) {
            a(virtuallevelwritable, random, blockposition_mutableblockposition, set, structureboundingbox, worldgenfeaturetreeconfiguration, blockposition, 0, j, 0);
            if (j < i - 1) {
                a(virtuallevelwritable, random, blockposition_mutableblockposition, set, structureboundingbox, worldgenfeaturetreeconfiguration, blockposition, 1, j, 0);
                a(virtuallevelwritable, random, blockposition_mutableblockposition, set, structureboundingbox, worldgenfeaturetreeconfiguration, blockposition, 1, j, 1);
                a(virtuallevelwritable, random, blockposition_mutableblockposition, set, structureboundingbox, worldgenfeaturetreeconfiguration, blockposition, 0, j, 1);
            }
        }

        return ImmutableList.of(new WorldGenFoilagePlacer.b(blockposition.up(i), 0, true));
    }

    private static void a(VirtualLevelWritable virtuallevelwritable, Random random, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, Set<BlockPosition> set, StructureBoundingBox structureboundingbox, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, BlockPosition blockposition, int i, int j, int k) {
        blockposition_mutableblockposition.a((BaseBlockPosition) blockposition, i, j, k);
        a(virtuallevelwritable, random, blockposition_mutableblockposition, set, structureboundingbox, worldgenfeaturetreeconfiguration);
    }
}
