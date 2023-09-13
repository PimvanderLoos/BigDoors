package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class WorldGenFeatureTreeVineLeaves extends WorldGenFeatureTree {

    public WorldGenFeatureTreeVineLeaves() {
        super(WorldGenFeatureTrees.b);
    }

    public <T> WorldGenFeatureTreeVineLeaves(Dynamic<T> dynamic) {
        this();
    }

    @Override
    public void a(GeneratorAccess generatoraccess, Random random, List<BlockPosition> list, List<BlockPosition> list1, Set<BlockPosition> set, StructureBoundingBox structureboundingbox) {
        list1.forEach((blockposition) -> {
            BlockPosition blockposition1;

            if (random.nextInt(4) == 0) {
                blockposition1 = blockposition.west();
                if (WorldGenTreeAbstract.b(generatoraccess, blockposition1)) {
                    this.a((VirtualLevelWritable) generatoraccess, blockposition1, BlockVine.EAST, set, structureboundingbox);
                }
            }

            if (random.nextInt(4) == 0) {
                blockposition1 = blockposition.east();
                if (WorldGenTreeAbstract.b(generatoraccess, blockposition1)) {
                    this.a((VirtualLevelWritable) generatoraccess, blockposition1, BlockVine.WEST, set, structureboundingbox);
                }
            }

            if (random.nextInt(4) == 0) {
                blockposition1 = blockposition.north();
                if (WorldGenTreeAbstract.b(generatoraccess, blockposition1)) {
                    this.a((VirtualLevelWritable) generatoraccess, blockposition1, BlockVine.SOUTH, set, structureboundingbox);
                }
            }

            if (random.nextInt(4) == 0) {
                blockposition1 = blockposition.south();
                if (WorldGenTreeAbstract.b(generatoraccess, blockposition1)) {
                    this.a((VirtualLevelWritable) generatoraccess, blockposition1, BlockVine.NORTH, set, structureboundingbox);
                }
            }

        });
    }

    private void a(VirtualLevelWritable virtuallevelwritable, BlockPosition blockposition, BlockStateBoolean blockstateboolean, Set<BlockPosition> set, StructureBoundingBox structureboundingbox) {
        this.a((IWorldWriter) virtuallevelwritable, blockposition, blockstateboolean, set, structureboundingbox);
        int i = 4;

        for (blockposition = blockposition.down(); WorldGenTreeAbstract.b(virtuallevelwritable, blockposition) && i > 0; --i) {
            this.a((IWorldWriter) virtuallevelwritable, blockposition, blockstateboolean, set, structureboundingbox);
            blockposition = blockposition.down();
        }

    }

    @Override
    public <T> T a(DynamicOps<T> dynamicops) {
        return (new Dynamic(dynamicops, dynamicops.createMap(ImmutableMap.of(dynamicops.createString("type"), dynamicops.createString(IRegistry.w.getKey(this.a).toString()))))).getValue();
    }
}
