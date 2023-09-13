package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.block.BlockBeehive;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.block.state.IBlockData;

public class WorldGenFeatureTreeBeehive extends WorldGenFeatureTree {

    public static final Codec<WorldGenFeatureTreeBeehive> CODEC = Codec.floatRange(0.0F, 1.0F).fieldOf("probability").xmap(WorldGenFeatureTreeBeehive::new, (worldgenfeaturetreebeehive) -> {
        return worldgenfeaturetreebeehive.probability;
    }).codec();
    private static final EnumDirection WORLDGEN_FACING = EnumDirection.SOUTH;
    private static final EnumDirection[] SPAWN_DIRECTIONS = (EnumDirection[]) EnumDirection.EnumDirectionLimit.HORIZONTAL.stream().filter((enumdirection) -> {
        return enumdirection != WorldGenFeatureTreeBeehive.WORLDGEN_FACING.getOpposite();
    }).toArray((i) -> {
        return new EnumDirection[i];
    });
    private final float probability;

    public WorldGenFeatureTreeBeehive(float f) {
        this.probability = f;
    }

    @Override
    protected WorldGenFeatureTrees<?> type() {
        return WorldGenFeatureTrees.BEEHIVE;
    }

    @Override
    public void place(WorldGenFeatureTree.a worldgenfeaturetree_a) {
        RandomSource randomsource = worldgenfeaturetree_a.random();

        if (randomsource.nextFloat() < this.probability) {
            List<BlockPosition> list = worldgenfeaturetree_a.leaves();
            List<BlockPosition> list1 = worldgenfeaturetree_a.logs();
            int i = !list.isEmpty() ? Math.max(((BlockPosition) list.get(0)).getY() - 1, ((BlockPosition) list1.get(0)).getY() + 1) : Math.min(((BlockPosition) list1.get(0)).getY() + 1 + randomsource.nextInt(3), ((BlockPosition) list1.get(list1.size() - 1)).getY());
            List<BlockPosition> list2 = (List) list1.stream().filter((blockposition) -> {
                return blockposition.getY() == i;
            }).flatMap((blockposition) -> {
                Stream stream = Stream.of(WorldGenFeatureTreeBeehive.SPAWN_DIRECTIONS);

                Objects.requireNonNull(blockposition);
                return stream.map(blockposition::relative);
            }).collect(Collectors.toList());

            if (!list2.isEmpty()) {
                Collections.shuffle(list2);
                Optional<BlockPosition> optional = list2.stream().filter((blockposition) -> {
                    return worldgenfeaturetree_a.isAir(blockposition) && worldgenfeaturetree_a.isAir(blockposition.relative(WorldGenFeatureTreeBeehive.WORLDGEN_FACING));
                }).findFirst();

                if (!optional.isEmpty()) {
                    worldgenfeaturetree_a.setBlock((BlockPosition) optional.get(), (IBlockData) Blocks.BEE_NEST.defaultBlockState().setValue(BlockBeehive.FACING, WorldGenFeatureTreeBeehive.WORLDGEN_FACING));
                    worldgenfeaturetree_a.level().getBlockEntity((BlockPosition) optional.get(), TileEntityTypes.BEEHIVE).ifPresent((tileentitybeehive) -> {
                        int j = 2 + randomsource.nextInt(2);

                        for (int k = 0; k < j; ++k) {
                            NBTTagCompound nbttagcompound = new NBTTagCompound();

                            nbttagcompound.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(EntityTypes.BEE).toString());
                            tileentitybeehive.storeBee(nbttagcompound, randomsource.nextInt(599), false);
                        }

                    });
                }
            }
        }
    }
}
