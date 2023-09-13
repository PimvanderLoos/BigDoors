package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
    public void place(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, Random random, List<BlockPosition> list, List<BlockPosition> list1) {
        if (random.nextFloat() < this.probability) {
            int i = !list1.isEmpty() ? Math.max(((BlockPosition) list1.get(0)).getY() - 1, ((BlockPosition) list.get(0)).getY() + 1) : Math.min(((BlockPosition) list.get(0)).getY() + 1 + random.nextInt(3), ((BlockPosition) list.get(list.size() - 1)).getY());
            List<BlockPosition> list2 = (List) list.stream().filter((blockposition) -> {
                return blockposition.getY() == i;
            }).flatMap((blockposition) -> {
                Stream stream = Stream.of(WorldGenFeatureTreeBeehive.SPAWN_DIRECTIONS);

                Objects.requireNonNull(blockposition);
                return stream.map(blockposition::relative);
            }).collect(Collectors.toList());

            if (!list2.isEmpty()) {
                Collections.shuffle(list2);
                Optional<BlockPosition> optional = list2.stream().filter((blockposition) -> {
                    return WorldGenerator.isAir(virtuallevelreadable, blockposition) && WorldGenerator.isAir(virtuallevelreadable, blockposition.relative(WorldGenFeatureTreeBeehive.WORLDGEN_FACING));
                }).findFirst();

                if (!optional.isEmpty()) {
                    biconsumer.accept((BlockPosition) optional.get(), (IBlockData) Blocks.BEE_NEST.defaultBlockState().setValue(BlockBeehive.FACING, WorldGenFeatureTreeBeehive.WORLDGEN_FACING));
                    virtuallevelreadable.getBlockEntity((BlockPosition) optional.get(), TileEntityTypes.BEEHIVE).ifPresent((tileentitybeehive) -> {
                        int j = 2 + random.nextInt(2);

                        for (int k = 0; k < j; ++k) {
                            NBTTagCompound nbttagcompound = new NBTTagCompound();

                            nbttagcompound.putString("id", IRegistry.ENTITY_TYPE.getKey(EntityTypes.BEE).toString());
                            tileentitybeehive.storeBee(nbttagcompound, random.nextInt(599), false);
                        }

                    });
                }
            }
        }
    }
}
