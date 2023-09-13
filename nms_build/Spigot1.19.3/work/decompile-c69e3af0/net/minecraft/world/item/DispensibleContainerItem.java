package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPositionBlock;

public interface DispensibleContainerItem {

    default void checkExtraContent(@Nullable EntityHuman entityhuman, World world, ItemStack itemstack, BlockPosition blockposition) {}

    boolean emptyContents(@Nullable EntityHuman entityhuman, World world, BlockPosition blockposition, @Nullable MovingObjectPositionBlock movingobjectpositionblock);
}
