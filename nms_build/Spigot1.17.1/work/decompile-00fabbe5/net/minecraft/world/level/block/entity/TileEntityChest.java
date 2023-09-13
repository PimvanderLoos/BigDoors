package net.minecraft.world.level.block.entity;

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
        this.items = NonNullList.a(27, ItemStack.EMPTY);
        this.openersCounter = new ContainerOpenersCounter() {
            @Override
            protected void a(World world, BlockPosition blockposition1, IBlockData iblockdata1) {
                TileEntityChest.playOpenSound(world, blockposition1, iblockdata1, SoundEffects.CHEST_OPEN);
            }

            @Override
            protected void b(World world, BlockPosition blockposition1, IBlockData iblockdata1) {
                TileEntityChest.playOpenSound(world, blockposition1, iblockdata1, SoundEffects.CHEST_CLOSE);
            }

            @Override
            protected void a(World world, BlockPosition blockposition1, IBlockData iblockdata1, int i, int j) {
                TileEntityChest.this.a(world, blockposition1, iblockdata1, i, j);
            }

            @Override
            protected boolean a(EntityHuman entityhuman) {
                if (!(entityhuman.containerMenu instanceof ContainerChest)) {
                    return false;
                } else {
                    IInventory iinventory = ((ContainerChest) entityhuman.containerMenu).l();

                    return iinventory == TileEntityChest.this || iinventory instanceof InventoryLargeChest && ((InventoryLargeChest) iinventory).a((IInventory) TileEntityChest.this);
                }
            }
        };
        this.chestLidController = new ChestLidController();
    }

    public TileEntityChest(BlockPosition blockposition, IBlockData iblockdata) {
        this(TileEntityTypes.CHEST, blockposition, iblockdata);
    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    protected IChatBaseComponent getContainerName() {
        return new ChatMessage("container.chest");
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
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        if (!this.d(nbttagcompound)) {
            ContainerUtil.a(nbttagcompound, this.items);
        }

        return nbttagcompound;
    }

    public static void a(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityChest tileentitychest) {
        tileentitychest.chestLidController.a();
    }

    public static void playOpenSound(World world, BlockPosition blockposition, IBlockData iblockdata, SoundEffect soundeffect) {
        BlockPropertyChestType blockpropertychesttype = (BlockPropertyChestType) iblockdata.get(BlockChest.TYPE);

        if (blockpropertychesttype != BlockPropertyChestType.LEFT) {
            double d0 = (double) blockposition.getX() + 0.5D;
            double d1 = (double) blockposition.getY() + 0.5D;
            double d2 = (double) blockposition.getZ() + 0.5D;

            if (blockpropertychesttype == BlockPropertyChestType.RIGHT) {
                EnumDirection enumdirection = BlockChest.h(iblockdata);

                d0 += (double) enumdirection.getAdjacentX() * 0.5D;
                d2 += (double) enumdirection.getAdjacentZ() * 0.5D;
            }

            world.playSound((EntityHuman) null, d0, d1, d2, soundeffect, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
        }
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

    @Override
    protected NonNullList<ItemStack> f() {
        return this.items;
    }

    @Override
    protected void a(NonNullList<ItemStack> nonnulllist) {
        this.items = nonnulllist;
    }

    @Override
    public float a(float f) {
        return this.chestLidController.a(f);
    }

    public static int a(IBlockAccess iblockaccess, BlockPosition blockposition) {
        IBlockData iblockdata = iblockaccess.getType(blockposition);

        if (iblockdata.isTileEntity()) {
            TileEntity tileentity = iblockaccess.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityChest) {
                return ((TileEntityChest) tileentity).openersCounter.getOpenerCount();
            }
        }

        return 0;
    }

    public static void a(TileEntityChest tileentitychest, TileEntityChest tileentitychest1) {
        NonNullList<ItemStack> nonnulllist = tileentitychest.f();

        tileentitychest.a(tileentitychest1.f());
        tileentitychest1.a(nonnulllist);
    }

    @Override
    protected Container createContainer(int i, PlayerInventory playerinventory) {
        return ContainerChest.a(i, playerinventory, (IInventory) this);
    }

    public void h() {
        if (!this.remove) {
            this.openersCounter.c(this.getWorld(), this.getPosition(), this.getBlock());
        }

    }

    protected void a(World world, BlockPosition blockposition, IBlockData iblockdata, int i, int j) {
        Block block = iblockdata.getBlock();

        world.playBlockAction(blockposition, block, 1, j);
    }
}
