package net.minecraft.world.entity.vehicle;

import net.minecraft.core.EnumDirection;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerChest;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockChest;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class EntityMinecartChest extends EntityMinecartContainer {

    public EntityMinecartChest(EntityTypes<? extends EntityMinecartChest> entitytypes, World world) {
        super(entitytypes, world);
    }

    public EntityMinecartChest(World world, double d0, double d1, double d2) {
        super(EntityTypes.CHEST_MINECART, d0, d1, d2, world);
    }

    @Override
    public void a(DamageSource damagesource) {
        super.a(damagesource);
        if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            this.a((IMaterial) Blocks.CHEST);
        }

    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    public EntityMinecartAbstract.EnumMinecartType getMinecartType() {
        return EntityMinecartAbstract.EnumMinecartType.CHEST;
    }

    @Override
    public IBlockData q() {
        return (IBlockData) Blocks.CHEST.getBlockData().set(BlockChest.FACING, EnumDirection.NORTH);
    }

    @Override
    public int s() {
        return 8;
    }

    @Override
    public Container a(int i, PlayerInventory playerinventory) {
        return ContainerChest.a(i, playerinventory, this);
    }
}
