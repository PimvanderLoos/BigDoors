package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class WorldGenFeatureTreeBeehive extends WorldGenFeatureTree {

    private final float b;

    public WorldGenFeatureTreeBeehive(float f) {
        super(WorldGenFeatureTrees.d);
        this.b = f;
    }

    public <T> WorldGenFeatureTreeBeehive(Dynamic<T> dynamic) {
        this(dynamic.get("probability").asFloat(0.0F));
    }

    @Override
    public void a(GeneratorAccess generatoraccess, Random random, List<BlockPosition> list, List<BlockPosition> list1, Set<BlockPosition> set, StructureBoundingBox structureboundingbox) {
        if (random.nextFloat() < this.b) {
            EnumDirection enumdirection = BlockBeehive.a[random.nextInt(BlockBeehive.a.length)];
            int i = !list1.isEmpty() ? Math.max(((BlockPosition) list1.get(0)).getY() - 1, ((BlockPosition) list.get(0)).getY()) : Math.min(((BlockPosition) list.get(0)).getY() + 1 + random.nextInt(3), ((BlockPosition) list.get(list.size() - 1)).getY());
            List<BlockPosition> list2 = (List) list.stream().filter((blockposition) -> {
                return blockposition.getY() == i;
            }).collect(Collectors.toList());

            if (!list2.isEmpty()) {
                BlockPosition blockposition = (BlockPosition) list2.get(random.nextInt(list2.size()));
                BlockPosition blockposition1 = blockposition.shift(enumdirection);

                if (WorldGenTreeAbstract.b(generatoraccess, blockposition1) && WorldGenTreeAbstract.b(generatoraccess, blockposition1.shift(EnumDirection.SOUTH))) {
                    IBlockData iblockdata = (IBlockData) Blocks.BEE_NEST.getBlockData().set(BlockBeehive.b, EnumDirection.SOUTH);

                    this.a(generatoraccess, blockposition1, iblockdata, set, structureboundingbox);
                    TileEntity tileentity = generatoraccess.getTileEntity(blockposition1);

                    if (tileentity instanceof TileEntityBeehive) {
                        TileEntityBeehive tileentitybeehive = (TileEntityBeehive) tileentity;
                        int j = 2 + random.nextInt(2);

                        for (int k = 0; k < j; ++k) {
                            EntityBee entitybee = new EntityBee(EntityTypes.BEE, generatoraccess.getMinecraftWorld());

                            tileentitybeehive.a(entitybee, false, random.nextInt(599));
                        }
                    }

                }
            }
        }
    }

    @Override
    public <T> T a(DynamicOps<T> dynamicops) {
        return (new Dynamic(dynamicops, dynamicops.createMap(ImmutableMap.of(dynamicops.createString("type"), dynamicops.createString(IRegistry.w.getKey(this.a).toString()), dynamicops.createString("probability"), dynamicops.createFloat(this.b))))).getValue();
    }
}
