package net.minecraft.world.level;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface ICombinedAccess extends IEntityAccess, IWorldReader, VirtualLevelWritable {

    @Override
    default <T extends TileEntity> Optional<T> getBlockEntity(BlockPosition blockposition, TileEntityTypes<T> tileentitytypes) {
        return IWorldReader.super.getBlockEntity(blockposition, tileentitytypes);
    }

    @Override
    default List<VoxelShape> getEntityCollisions(@Nullable Entity entity, AxisAlignedBB axisalignedbb) {
        return IEntityAccess.super.getEntityCollisions(entity, axisalignedbb);
    }

    @Override
    default boolean isUnobstructed(@Nullable Entity entity, VoxelShape voxelshape) {
        return IEntityAccess.super.isUnobstructed(entity, voxelshape);
    }

    @Override
    default BlockPosition getHeightmapPos(HeightMap.Type heightmap_type, BlockPosition blockposition) {
        return IWorldReader.super.getHeightmapPos(heightmap_type, blockposition);
    }
}
