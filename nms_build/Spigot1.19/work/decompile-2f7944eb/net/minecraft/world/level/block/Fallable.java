package net.minecraft.world.level.block;

import java.util.function.Predicate;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.item.EntityFallingBlock;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;

public interface Fallable {

    default void onLand(World world, BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1, EntityFallingBlock entityfallingblock) {}

    default void onBrokenAfterFall(World world, BlockPosition blockposition, EntityFallingBlock entityfallingblock) {}

    default DamageSource getFallDamageSource() {
        return DamageSource.FALLING_BLOCK;
    }

    default Predicate<Entity> getHurtsEntitySelector() {
        return IEntitySelector.NO_SPECTATORS;
    }
}
