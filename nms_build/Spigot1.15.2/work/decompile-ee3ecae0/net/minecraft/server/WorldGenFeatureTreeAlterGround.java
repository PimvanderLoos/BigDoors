package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class WorldGenFeatureTreeAlterGround extends WorldGenFeatureTree {

    private final WorldGenFeatureStateProvider b;

    public WorldGenFeatureTreeAlterGround(WorldGenFeatureStateProvider worldgenfeaturestateprovider) {
        super(WorldGenFeatureTrees.e);
        this.b = worldgenfeaturestateprovider;
    }

    public <T> WorldGenFeatureTreeAlterGround(Dynamic<T> dynamic) {
        this(((WorldGenFeatureStateProviders) IRegistry.t.get(new MinecraftKey((String) dynamic.get("provider").get("type").asString().orElseThrow(RuntimeException::new)))).a(dynamic.get("provider").orElseEmptyMap()));
    }

    @Override
    public void a(GeneratorAccess generatoraccess, Random random, List<BlockPosition> list, List<BlockPosition> list1, Set<BlockPosition> set, StructureBoundingBox structureboundingbox) {
        int i = ((BlockPosition) list.get(0)).getY();

        list.stream().filter((blockposition) -> {
            return blockposition.getY() == i;
        }).forEach((blockposition) -> {
            this.a((VirtualLevelWritable) generatoraccess, random, blockposition.west().north());
            this.a((VirtualLevelWritable) generatoraccess, random, blockposition.east(2).north());
            this.a((VirtualLevelWritable) generatoraccess, random, blockposition.west().south(2));
            this.a((VirtualLevelWritable) generatoraccess, random, blockposition.east(2).south(2));

            for (int j = 0; j < 5; ++j) {
                int k = random.nextInt(64);
                int l = k % 8;
                int i1 = k / 8;

                if (l == 0 || l == 7 || i1 == 0 || i1 == 7) {
                    this.a((VirtualLevelWritable) generatoraccess, random, blockposition.b(-3 + l, 0, -3 + i1));
                }
            }

        });
    }

    private void a(VirtualLevelWritable virtuallevelwritable, Random random, BlockPosition blockposition) {
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                if (Math.abs(i) != 2 || Math.abs(j) != 2) {
                    this.b(virtuallevelwritable, random, blockposition.b(i, 0, j));
                }
            }
        }

    }

    private void b(VirtualLevelWritable virtuallevelwritable, Random random, BlockPosition blockposition) {
        for (int i = 2; i >= -3; --i) {
            BlockPosition blockposition1 = blockposition.up(i);

            if (WorldGenTreeAbstract.g(virtuallevelwritable, blockposition1)) {
                virtuallevelwritable.setTypeAndData(blockposition1, this.b.a(random, blockposition), 19);
                break;
            }

            if (!WorldGenTreeAbstract.b(virtuallevelwritable, blockposition1) && i < 0) {
                break;
            }
        }

    }

    @Override
    public <T> T a(DynamicOps<T> dynamicops) {
        return (new Dynamic(dynamicops, dynamicops.createMap(ImmutableMap.of(dynamicops.createString("type"), dynamicops.createString(IRegistry.w.getKey(this.a).toString()), dynamicops.createString("provider"), this.b.a(dynamicops))))).getValue();
    }
}
