package net.minecraft.world.level.block.entity;

import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.ContainerUtil;
import net.minecraft.world.IInventory;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerChest;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockBarrel;
import net.minecraft.world.level.block.state.IBlockData;

public class TileEntityBarrel extends TileEntityLootable {

    private NonNullList<ItemStack> items;
    public ContainerOpenersCounter openersCounter;

    public TileEntityBarrel(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.BARREL, blockposition, iblockdata);
        this.items = NonNullList.a(27, ItemStack.EMPTY);
        this.openersCounter = new ContainerOpenersCounter() {
            @Override
            protected void a(World world, BlockPosition blockposition1, IBlockData iblockdata1) {
                TileEntityBarrel.this.playOpenSound(iblockdata1, SoundEffects.BARREL_OPEN);
                TileEntityBarrel.this.setOpenFlag(iblockdata1, true);
            }

            @Override
            protected void b(World world, BlockPosition blockposition1, IBlockData iblockdata1) {
                TileEntityBarrel.this.playOpenSound(iblockdata1, SoundEffects.BARREL_CLOSE);
                TileEntityBarrel.this.setOpenFlag(iblockdata1, false);
            }

            @Override
            protected void a(World world, BlockPosition blockposition1, IBlockData iblockdata1, int i, int j) {}

            @Override
            protected boolean a(EntityHuman entityhuman) {
                if (entityhuman.containerMenu instanceof ContainerChest) {
                    IInventory iinventory = ((ContainerChest) entityhuman.containerMenu).l();

                    return iinventory == TileEntityBarrel.this;
                } else {
                    return false;
                }
            }
        };
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        if (!this.d(nbttagcompound)) {
            ContainerUtil.a(nbttagcompound, this.items);
        }

        return nbttagcompound;
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.items = NonNullList.a(this.getSize(), ItemStack.EMPTY);
        if (!this.c(nbttagcompound)) {
            ContainerUtil.b(nbttagcompound, this.items);
        }

    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    protected NonNullList<ItemStack> f() {
        return this.items;
    }

    @Override
    protected void a(NonNullList<ItemStack> nonnulllist) {
        this.items = nonnulllist;
    }

    @Override
    protected IChatBaseComponent getContainerName() {
        return new ChatMessage("container.barrel");
    }

    @Override
    protected Container createContainer(int i, PlayerInventory playerinventory) {
        return ContainerChest.a(i, playerinventory, (IInventory) this);
    }

    @Override
    public void startOpen(EntityHuman entityhuman) {
        if (!this.remove && !entityhuman.isSpectator()) {
            this.openersCounter.a(entityhuman, this.getWorld(), this.getPosition(), this.getBlock());
        }

    }

    @Override
    public void closeContainer(EntityHuman entityhuman) {
        if (!this.remove && !entityhuman.isSpectator()) {
            this.openersCounter.b(entityhuman, this.getWorld(), this.getPosition(), this.getBlock());
        }

    }

    public void h() {
        if (!this.remove) {
            this.openersCounter.c(this.getWorld(), this.getPosition(), this.getBlock());
        }

    }

    public void setOpenFlag(IBlockData iblockdata, boolean flag) {
        this.level.setTypeAndData(this.getPosition(), (IBlockData) iblockdata.set(BlockBarrel.OPEN, flag), 3);
    }

    public void playOpenSound(IBlockData iblockdata, SoundEffect soundeffect) {
        BaseBlockPosition baseblockposition = ((EnumDirection) iblockdata.get(BlockBarrel.FACING)).p();
        double d0 = (double) this.worldPosition.getX() + 0.5D + (double) baseblockposition.getX() / 2.0D;
        double d1 = (double) this.worldPosition.getY() + 0.5D + (double) baseblockposition.getY() / 2.0D;
        double d2 = (double) this.worldPosition.getZ() + 0.5D + (double) baseblockposition.getZ() / 2.0D;

        this.level.playSound((EntityHuman) null, d0, d1, d2, soundeffect, SoundCategory.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
    }
}
