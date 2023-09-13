package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public abstract class WorldGenFeatureSmallTree<T extends WorldGenFeatureSmallTreeConfigurationConfiguration> extends WorldGenTreeAbstract<T> {

    public WorldGenFeatureSmallTree(Function<Dynamic<?>, ? extends T> function) {
        super(function);
    }

    protected void a(VirtualLevelWritable virtuallevelwritable, Random random, int i, BlockPosition blockposition, int j, Set<BlockPosition> set, StructureBoundingBox structureboundingbox, WorldGenFeatureSmallTreeConfigurationConfiguration worldgenfeaturesmalltreeconfigurationconfiguration) {
        for (int k = 0; k < i - j; ++k) {
            this.a(virtuallevelwritable, random, blockposition.up(k), set, structureboundingbox, worldgenfeaturesmalltreeconfigurationconfiguration);
        }

    }

    public Optional<BlockPosition> a(VirtualLevelWritable virtuallevelwritable, int i, int j, int k, BlockPosition blockposition, WorldGenFeatureSmallTreeConfigurationConfiguration worldgenfeaturesmalltreeconfigurationconfiguration) {
        int l;
        int i1;
        BlockPosition blockposition1;

        if (!worldgenfeaturesmalltreeconfigurationconfiguration.q) {
            l = virtuallevelwritable.getHighestBlockYAt(HeightMap.Type.OCEAN_FLOOR, blockposition).getY();
            i1 = virtuallevelwritable.getHighestBlockYAt(HeightMap.Type.WORLD_SURFACE, blockposition).getY();
            blockposition1 = new BlockPosition(blockposition.getX(), l, blockposition.getZ());
            if (i1 - l > worldgenfeaturesmalltreeconfigurationconfiguration.k) {
                return Optional.empty();
            }
        } else {
            blockposition1 = blockposition;
        }

        if (blockposition1.getY() >= 1 && blockposition1.getY() + i + 1 <= 256) {
            for (l = 0; l <= i + 1; ++l) {
                i1 = worldgenfeaturesmalltreeconfigurationconfiguration.a.a(j, i, k, l);
                BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

                for (int j1 = -i1; j1 <= i1; ++j1) {
                    int k1 = -i1;

                    while (k1 <= i1) {
                        if (l + blockposition1.getY() >= 0 && l + blockposition1.getY() < 256) {
                            blockposition_mutableblockposition.d(j1 + blockposition1.getX(), l + blockposition1.getY(), k1 + blockposition1.getZ());
                            if (a((VirtualLevelReadable) virtuallevelwritable, (BlockPosition) blockposition_mutableblockposition) && (worldgenfeaturesmalltreeconfigurationconfiguration.l || !d(virtuallevelwritable, blockposition_mutableblockposition))) {
                                ++k1;
                                continue;
                            }

                            return Optional.empty();
                        }

                        return Optional.empty();
                    }
                }
            }

            if (h(virtuallevelwritable, blockposition1.down()) && blockposition1.getY() < 256 - i - 1) {
                return Optional.of(blockposition1);
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }
}
