package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class WorldGenGroundBush extends WorldGenTreeAbstract<WorldGenFeatureTreeConfiguration> {

    public WorldGenGroundBush(Function<Dynamic<?>, ? extends WorldGenFeatureTreeConfiguration> function) {
        super(function);
    }

    @Override
    public boolean a(VirtualLevelWritable virtuallevelwritable, Random random, BlockPosition blockposition, Set<BlockPosition> set, Set<BlockPosition> set1, StructureBoundingBox structureboundingbox, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        blockposition = virtuallevelwritable.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, blockposition).down();
        if (g(virtuallevelwritable, blockposition)) {
            blockposition = blockposition.up();
            this.a(virtuallevelwritable, random, blockposition, set, structureboundingbox, worldgenfeaturetreeconfiguration);

            for (int i = 0; i <= 2; ++i) {
                int j = 2 - i;

                for (int k = -j; k <= j; ++k) {
                    for (int l = -j; l <= j; ++l) {
                        if (Math.abs(k) != j || Math.abs(l) != j || random.nextInt(2) != 0) {
                            this.b(virtuallevelwritable, random, new BlockPosition(k + blockposition.getX(), i + blockposition.getY(), l + blockposition.getZ()), set1, structureboundingbox, worldgenfeaturetreeconfiguration);
                        }
                    }
                }
            }
        }

        return true;
    }
}
