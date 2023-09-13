package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.block.BlockBeehive;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;

public class WorldGenFeatureTreeBeehive extends WorldGenFeatureTree {

    public static final Codec<WorldGenFeatureTreeBeehive> CODEC = Codec.floatRange(0.0F, 1.0F).fieldOf("probability").xmap(WorldGenFeatureTreeBeehive::new, (worldgenfeaturetreebeehive) -> {
        return worldgenfeaturetreebeehive.probability;
    }).codec();
    private final float probability;

    public WorldGenFeatureTreeBeehive(float f) {
        this.probability = f;
    }

    @Override
    protected WorldGenFeatureTrees<?> a() {
        return WorldGenFeatureTrees.BEEHIVE;
    }

    @Override
    public void a(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, Random random, List<BlockPosition> list, List<BlockPosition> list1) {
        if (random.nextFloat() < this.probability) {
            EnumDirection enumdirection = BlockBeehive.a(random);
            int i = !list1.isEmpty() ? Math.max(((BlockPosition) list1.get(0)).getY() - 1, ((BlockPosition) list.get(0)).getY()) : Math.min(((BlockPosition) list.get(0)).getY() + 1 + random.nextInt(3), ((BlockPosition) list.get(list.size() - 1)).getY());
            List<BlockPosition> list2 = (List) list.stream().filter((blockposition) -> {
                return blockposition.getY() == i;
            }).collect(Collectors.toList());

            if (!list2.isEmpty()) {
                BlockPosition blockposition = (BlockPosition) list2.get(random.nextInt(list2.size()));
                BlockPosition blockposition1 = blockposition.shift(enumdirection);

                if (WorldGenerator.b(virtuallevelreadable, blockposition1) && WorldGenerator.b(virtuallevelreadable, blockposition1.shift(EnumDirection.SOUTH))) {
                    biconsumer.accept(blockposition1, (IBlockData) Blocks.BEE_NEST.getBlockData().set(BlockBeehive.FACING, EnumDirection.SOUTH));
                    virtuallevelreadable.a(blockposition1, TileEntityTypes.BEEHIVE).ifPresent((tileentitybeehive) -> {
                        int j = 2 + random.nextInt(2);

                        for (int k = 0; k < j; ++k) {
                            NBTTagCompound nbttagcompound = new NBTTagCompound();

                            nbttagcompound.setString("id", IRegistry.ENTITY_TYPE.getKey(EntityTypes.BEE).toString());
                            tileentitybeehive.a(nbttagcompound, random.nextInt(599), false);
                        }

                    });
                }
            }
        }
    }
}
