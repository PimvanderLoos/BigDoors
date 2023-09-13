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

    default void a(World world, BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1, EntityFallingBlock entityfallingblock) {}

    default void a(World world, BlockPosition blockposition, EntityFallingBlock entityfallingblock) {}

    default DamageSource b() {
        return DamageSource.FALLING_BLOCK;
    }

    default Predicate<Entity> T_() {
        return IEntitySelector.NO_SPECTATORS;
    }
}
