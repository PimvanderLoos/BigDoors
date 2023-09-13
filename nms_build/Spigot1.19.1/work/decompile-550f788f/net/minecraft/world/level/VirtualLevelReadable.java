package net.minecraft.world.level;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.material.Fluid;

public interface VirtualLevelReadable {

    boolean isStateAtPosition(BlockPosition blockposition, Predicate<IBlockData> predicate);

    boolean isFluidAtPosition(BlockPosition blockposition, Predicate<Fluid> predicate);

    <T extends TileEntity> Optional<T> getBlockEntity(BlockPosition blockposition, TileEntityTypes<T> tileentitytypes);

    BlockPosition getHeightmapPos(HeightMap.Type heightmap_type, BlockPosition blockposition);
}
