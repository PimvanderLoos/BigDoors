package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPositionBlock;

public interface DispensibleContainerItem {

    default void a(@Nullable EntityHuman entityhuman, World world, ItemStack itemstack, BlockPosition blockposition) {}

    boolean a(@Nullable EntityHuman entityhuman, World world, BlockPosition blockposition, @Nullable MovingObjectPositionBlock movingobjectpositionblock);
}
