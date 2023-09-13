package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.ContainerUtil;
import net.minecraft.world.IInventory;
import net.minecraft.world.InventoryLargeChest;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerChest;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockChest;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockPropertyChestType;

public class TileEntityChest extends TileEntityLootable implements LidBlockEntity {

    private static final int EVENT_SET_OPEN_COUNT = 1;
    private NonNullList<ItemStack> items;
    public final ContainerOpenersCounter openersCounter;
    private final ChestLidController chestLidController;

    protected TileEntityChest(TileEntityTypes<?> tileentitytypes, BlockPosition blockposition, IBlockData iblockdata) {
        super(tileentitytypes, blockposition, iblockdata);
        this.items = NonNullList.withSize(27, ItemStack.EMPTY);
        this.openersCounter = new ContainerOpenersCounter() {
            @Override
            protected void onOpen(World world, BlockPosition blockposition1, IBlockData iblockdata1) {
                TileEntityChest.playSound(world, blockposition1, iblockdata1, SoundEffects.CHEST_OPEN);
            }

            @Override
            protected void onClose(World world, BlockPosition blockposition1, IBlockData iblockdata1) {
                TileEntityChest.playSound(world, blockposition1, iblockdata1, SoundEffects.CHEST_CLOSE);
            }

            @Override
            protected void openerCountChanged(World world, BlockPosition blockposition1, IBlockData iblockdata1, int i, int j) {
                TileEntityChest.this.signalOpenCount(world, blockposition1, iblockdata1, i, j);
            }

            @Override
            protected boolean isOwnContainer(EntityHuman entityhuman) {
                if (!(entityhuman.containerMenu instanceof ContainerChest)) {
                    return false;
                } else {
                    IInventory iinventory = ((ContainerChest) entityhuman.containerMenu).getContainer();

                    return iinventory == TileEntityChest.this || iinventory instanceof InventoryLargeChest && ((InventoryLargeChest) iinventory).contains(TileEntityChest.this);
                }
            }
        };
        this.chestLidController = new ChestLidController();
    }

    public TileEntityChest(BlockPosition blockposition, IBlockData iblockdata) {
        this(TileEntityTypes.CHEST, blockposition, iblockdata);
    }

    @Override
    public int getContainerSize() {
        return 27;
    }

    @Override
    protected IChatBaseComponent getDefaultName() {
        return IChatBaseComponent.translatable("container.chest");
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(nbttagcompound)) {
            ContainerUtil.loadAllItems(nbttagcompound, this.items);
        }

    }

    @Override
    protected void saveAdditional(NBTTagCompound nbttagcompound) {
        super.saveAdditional(nbttagcompound);
        if (!this.trySaveLootTable(nbttagcompound)) {
            ContainerUtil.saveAllItems(nbttagcompound, this.items);
        }

    }

    public static void lidAnimateTick(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityChest tileentitychest) {
        tileentitychest.chestLidController.tickLid();
    }

    public static void playSound(World world, BlockPosition blockposition, IBlockData iblockdata, SoundEffect soundeffect) {
        BlockPropertyChestType blockpropertychesttype = (BlockPropertyChestType) iblockdata.getValue(BlockChest.TYPE);

        if (blockpropertychesttype != BlockPropertyChestType.LEFT) {
            double d0 = (double) blockposition.getX() + 0.5D;
            double d1 = (double) blockposition.getY() + 0.5D;
            double d2 = (double) blockposition.getZ() + 0.5D;

            if (blockpropertychesttype == BlockPropertyChestType.RIGHT) {
                EnumDirection enumdirection = BlockChest.getConnectedDirection(iblockdata);

                d0 += (double) enumdirection.getStepX() * 0.5D;
                d2 += (double) enumdirection.getStepZ() * 0.5D;
            }

            world.playSound((EntityHuman) null, d0, d1, d2, soundeffect, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
        }
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

    @Override
    public void startOpen(EntityHuman entityhuman) {
        if (!this.remove && !entityhuman.isSpectator()) {
            this.openersCounter.incrementOpeners(entityhuman, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }

    }

    @Override
    public void stopOpen(EntityHuman entityhuman) {
        if (!this.remove && !entityhuman.isSpectator()) {
            this.openersCounter.decrementOpeners(entityhuman, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }

    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> nonnulllist) {
        this.items = nonnulllist;
    }

    @Override
    public float getOpenNess(float f) {
        return this.chestLidController.getOpenness(f);
    }

    public static int getOpenCount(IBlockAccess iblockaccess, BlockPosition blockposition) {
        IBlockData iblockdata = iblockaccess.getBlockState(blockposition);

        if (iblockdata.hasBlockEntity()) {
            TileEntity tileentity = iblockaccess.getBlockEntity(blockposition);

            if (tileentity instanceof TileEntityChest) {
                return ((TileEntityChest) tileentity).openersCounter.getOpenerCount();
            }
        }

        return 0;
    }

    public static void swapContents(TileEntityChest tileentitychest, TileEntityChest tileentitychest1) {
        NonNullList<ItemStack> nonnulllist = tileentitychest.getItems();

        tileentitychest.setItems(tileentitychest1.getItems());
        tileentitychest1.setItems(nonnulllist);
    }

    @Override
    protected Container createMenu(int i, PlayerInventory playerinventory) {
        return ContainerChest.threeRows(i, playerinventory, this);
    }

    public void recheckOpen() {
        if (!this.remove) {
            this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }

    }

    protected void signalOpenCount(World world, BlockPosition blockposition, IBlockData iblockdata, int i, int j) {
        Block block = iblockdata.getBlock();

        world.blockEvent(blockposition, block, 1, j);
    }
}
