package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPosition;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class TileEntityEnderChest extends TileEntity implements LidBlockEntity {

    private final ChestLidController chestLidController = new ChestLidController();
    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        @Override
        protected void a(World world, BlockPosition blockposition, IBlockData iblockdata) {
            world.playSound((EntityHuman) null, (double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D, SoundEffects.ENDER_CHEST_OPEN, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
        }

        @Override
        protected void b(World world, BlockPosition blockposition, IBlockData iblockdata) {
            world.playSound((EntityHuman) null, (double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D, SoundEffects.ENDER_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
        }

        @Override
        protected void a(World world, BlockPosition blockposition, IBlockData iblockdata, int i, int j) {
            world.playBlockAction(TileEntityEnderChest.this.worldPosition, Blocks.ENDER_CHEST, 1, j);
        }

        @Override
        protected boolean a(EntityHuman entityhuman) {
            return entityhuman.getEnderChest().b(TileEntityEnderChest.this);
        }
    };

    public TileEntityEnderChest(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.ENDER_CHEST, blockposition, iblockdata);
    }

    public static void a(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityEnderChest tileentityenderchest) {
        tileentityenderchest.chestLidController.a();
    }

    @Override
    public boolean setProperty(int i, int j) {
        if (i == 1) {
            this.chestLidController.a(j > 0);
            return true;
        } else {
            return super.setProperty(i, j);
        }
    }

    public void a(EntityHuman entityhuman) {
        if (!this.remove && !entityhuman.isSpectator()) {
            this.openersCounter.a(entityhuman, this.getWorld(), this.getPosition(), this.getBlock());
        }

    }

    public void b(EntityHuman entityhuman) {
        if (!this.remove && !entityhuman.isSpectator()) {
            this.openersCounter.b(entityhuman, this.getWorld(), this.getPosition(), this.getBlock());
        }

    }

    public boolean c(EntityHuman entityhuman) {
        return this.level.getTileEntity(this.worldPosition) != this ? false : entityhuman.h((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) <= 64.0D;
    }

    public void d() {
        if (!this.remove) {
            this.openersCounter.c(this.getWorld(), this.getPosition(), this.getBlock());
        }

    }

    @Override
    public float a(float f) {
        return this.chestLidController.a(f);
    }
}
