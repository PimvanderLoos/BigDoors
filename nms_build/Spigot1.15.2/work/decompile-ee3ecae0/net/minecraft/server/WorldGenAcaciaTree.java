package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class WorldGenAcaciaTree extends WorldGenFeatureSmallTree<WorldGenFeatureSmallTreeConfigurationConfiguration> {

    public WorldGenAcaciaTree(Function<Dynamic<?>, ? extends WorldGenFeatureSmallTreeConfigurationConfiguration> function) {
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
            EnumDirection enumdirection = EnumDirection.EnumDirectionLimit.HORIZONTAL.a(random);
            int l = i - random.nextInt(4) - 1;
            int i1 = 3 - random.nextInt(3);
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
            int j1 = blockposition1.getX();
            int k1 = blockposition1.getZ();
            int l1 = 0;

            int i2;

            for (int j2 = 0; j2 < i; ++j2) {
                i2 = blockposition1.getY() + j2;
                if (j2 >= l && i1 > 0) {
                    j1 += enumdirection.getAdjacentX();
                    k1 += enumdirection.getAdjacentZ();
                    --i1;
                }

                if (this.a(virtuallevelwritable, random, blockposition_mutableblockposition.d(j1, i2, k1), set, structureboundingbox, worldgenfeaturesmalltreeconfigurationconfiguration)) {
                    l1 = i2;
                }
            }

            BlockPosition blockposition2 = new BlockPosition(j1, l1, k1);

            worldgenfeaturesmalltreeconfigurationconfiguration.a.a(virtuallevelwritable, random, worldgenfeaturesmalltreeconfigurationconfiguration, i, j, k + 1, blockposition2, set1);
            j1 = blockposition1.getX();
            k1 = blockposition1.getZ();
            EnumDirection enumdirection1 = EnumDirection.EnumDirectionLimit.HORIZONTAL.a(random);

            if (enumdirection1 != enumdirection) {
                i2 = l - random.nextInt(2) - 1;
                int k2 = 1 + random.nextInt(3);

                l1 = 0;

                for (int l2 = i2; l2 < i && k2 > 0; --k2) {
                    if (l2 >= 1) {
                        int i3 = blockposition1.getY() + l2;

                        j1 += enumdirection1.getAdjacentX();
                        k1 += enumdirection1.getAdjacentZ();
                        if (this.a(virtuallevelwritable, random, blockposition_mutableblockposition.d(j1, i3, k1), set, structureboundingbox, worldgenfeaturesmalltreeconfigurationconfiguration)) {
                            l1 = i3;
                        }
                    }

                    ++l2;
                }

                if (l1 > 0) {
                    BlockPosition blockposition3 = new BlockPosition(j1, l1, k1);

                    worldgenfeaturesmalltreeconfigurationconfiguration.a.a(virtuallevelwritable, random, worldgenfeaturesmalltreeconfigurationconfiguration, i, j, k, blockposition3, set1);
                }
            }

            return true;
        }
    }
}
