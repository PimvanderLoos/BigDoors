package net.minecraft.world.level;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.IBlockData;

public interface IWorldWriter {

    boolean setBlock(BlockPosition blockposition, IBlockData iblockdata, int i, int j);

    default boolean setBlock(BlockPosition blockposition, IBlockData iblockdata, int i) {
        return this.setBlock(blockposition, iblockdata, i, 512);
    }

    boolean removeBlock(BlockPosition blockposition, boolean flag);

    default boolean destroyBlock(BlockPosition blockposition, boolean flag) {
        return this.destroyBlock(blockposition, flag, (Entity) null);
    }

    default boolean destroyBlock(BlockPosition blockposition, boolean flag, @Nullable Entity entity) {
        return this.destroyBlock(blockposition, flag, entity, 512);
    }

    boolean destroyBlock(BlockPosition blockposition, boolean flag, @Nullable Entity entity, int i);

    default boolean addFreshEntity(Entity entity) {
        return false;
    }
}
