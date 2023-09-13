package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPosition;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.IInventory;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class TileEntityEnderChest extends TileEntity implements LidBlockEntity {

    private final ChestLidController chestLidController = new ChestLidController();
    public final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(World world, BlockPosition blockposition, IBlockData iblockdata) {
            world.playSound((EntityHuman) null, (double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D, SoundEffects.ENDER_CHEST_OPEN, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
        }

        @Override
        protected void onClose(World world, BlockPosition blockposition, IBlockData iblockdata) {
            world.playSound((EntityHuman) null, (double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D, SoundEffects.ENDER_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
        }

        @Override
        protected void openerCountChanged(World world, BlockPosition blockposition, IBlockData iblockdata, int i, int j) {
            world.blockEvent(TileEntityEnderChest.this.worldPosition, Blocks.ENDER_CHEST, 1, j);
        }

        @Override
        protected boolean isOwnContainer(EntityHuman entityhuman) {
            return entityhuman.getEnderChestInventory().isActiveChest(TileEntityEnderChest.this);
        }
    };

    public TileEntityEnderChest(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.ENDER_CHEST, blockposition, iblockdata);
    }

    public static void lidAnimateTick(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityEnderChest tileentityenderchest) {
        tileentityenderchest.chestLidController.tickLid();
    }

    @Override
    public boolean triggerEvent(int i, int j) {
        if (i == 1) {
            this.chestLidController.shouldBeOpen(j > 0);
            return true;
        } else {
            return super.triggerEvent(i, j);
        }
    }

    public void startOpen(EntityHuman entityhuman) {
        if (!this.remove && !entityhuman.isSpectator()) {
            this.openersCounter.incrementOpeners(entityhuman, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }

    }

    public void stopOpen(EntityHuman entityhuman) {
        if (!this.remove && !entityhuman.isSpectator()) {
            this.openersCounter.decrementOpeners(entityhuman, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }

    }

    public boolean stillValid(EntityHuman entityhuman) {
        return IInventory.stillValidBlockEntity(this, entityhuman);
    }

    public void recheckOpen() {
        if (!this.remove) {
            this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }

    }

    @Override
    public float getOpenNess(float f) {
        return this.chestLidController.getOpenness(f);
    }
}
