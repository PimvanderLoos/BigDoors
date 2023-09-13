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

    boolean a(BlockPosition blockposition, Predicate<IBlockData> predicate);

    boolean b(BlockPosition blockposition, Predicate<Fluid> predicate);

    <T extends TileEntity> Optional<T> a(BlockPosition blockposition, TileEntityTypes<T> tileentitytypes);

    BlockPosition getHighestBlockYAt(HeightMap.Type heightmap_type, BlockPosition blockposition);
}
