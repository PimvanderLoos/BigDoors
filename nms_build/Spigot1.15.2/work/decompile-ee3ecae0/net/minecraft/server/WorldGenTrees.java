package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class WorldGenTrees extends WorldGenFeatureSmallTree<WorldGenFeatureSmallTreeConfigurationConfiguration> {

    public WorldGenTrees(Function<Dynamic<?>, ? extends WorldGenFeatureSmallTreeConfigurationConfiguration> function) {
        super(function);
    }

    public boolean a(VirtualLevelWritable virtuallevelwritable, Random random, BlockPosition blockposition, Set<BlockPosition> set, Set<BlockPosition> set1, StructureBoundingBox structureboundingbox, WorldGenFeatureSmallTreeConfigurationConfiguration worldgenfeaturesmalltreeconfigurationconfiguration) {
        int i = worldgenfeaturesmalltreeconfigurationconfiguration.p + random.nextInt(worldgenfeaturesmalltreeconfigurationconfiguration.b + 1) + random.nextInt(worldgenfeaturesmalltreeconfigurationconfiguration.c + 1);
        int j = worldgenfeaturesmalltreeconfigurationconfiguration.d >= 0 ? worldgenfeaturesmalltreeconfigurationconfiguration.d + random.nextInt(worldgenfeaturesmalltreeconfigurationconfiguration.f + 1) : i - (worldgenfeaturesmalltreeconfigurationconfiguration.i + random.nextInt(worldgenfeaturesmalltreeconfigurationconfiguration.j + 1));
        int k = worldgenfeaturesmalltreeconfigurationconfiguration.a.a(random, j, i, worldgenfeaturesmalltreeconfigurationconfiguration);
        Optional<BlockPosition> optional = this.a(virtuallevelwritable, i, j, k, blockposition, worldgenfeaturesmalltreeconfigurationconfiguration);

        if (!optional.isPresent()) {
            return false;
        } else {
            BlockPosition blockposition1 = (BlockPosition) optional.get();

            this.a(virtuallevelwritable, blockposition1.down());
            worldgenfeaturesmalltreeconfigurationconfiguration.a.a(virtuallevelwritable, random, worldgenfeaturesmalltreeconfigurationconfiguration, i, j, k, blockposition1, set1);
            this.a(virtuallevelwritable, random, i, blockposition1, worldgenfeaturesmalltreeconfigurationconfiguration.g + random.nextInt(worldgenfeaturesmalltreeconfigurationconfiguration.h + 1), set, structureboundingbox, worldgenfeaturesmalltreeconfigurationconfiguration);
            return true;
        }
    }
}
