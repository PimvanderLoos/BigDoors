package net.minecraft.world.level.block.entity;

import java.util.Iterator;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.ContainerUtil;
import net.minecraft.world.IInventory;
import net.minecraft.world.IInventoryHolder;
import net.minecraft.world.IWorldInventory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerHopper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockChest;
import net.minecraft.world.level.block.BlockHopper;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.shapes.OperatorBoolean;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class TileEntityHopper extends TileEntityLootable implements IHopper {

    public static final int MOVE_ITEM_SPEED = 8;
    public static final int HOPPER_CONTAINER_SIZE = 5;
    private NonNullList<ItemStack> items;
    private int cooldownTime;
    private long tickedGameTime;

    public TileEntityHopper(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.HOPPER, blockposition, iblockdata);
        this.items = NonNullList.withSize(5, ItemStack.EMPTY);
        this.cooldownTime = -1;
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(nbttagcompound)) {
            ContainerUtil.loadAllItems(nbttagcompound, this.items);
        }

        this.cooldownTime = nbttagcompound.getInt("TransferCooldown");
    }

    @Override
    protected void saveAdditional(NBTTagCompound nbttagcompound) {
        super.saveAdditional(nbttagcompound);
        if (!this.trySaveLootTable(nbttagcompound)) {
            ContainerUtil.saveAllItems(nbttagcompound, this.items);
        }

        nbttagcompound.putInt("TransferCooldown", this.cooldownTime);
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public ItemStack removeItem(int i, int j) {
        this.unpackLootTable((EntityHuman) null);
        return ContainerUtil.removeItem(this.getItems(), i, j);
    }

    @Override
    public void setItem(int i, ItemStack itemstack) {
        this.unpackLootTable((EntityHuman) null);
        this.getItems().set(i, itemstack);
        if (itemstack.getCount() > this.getMaxStackSize()) {
            itemstack.setCount(this.getMaxStackSize());
        }

    }

    @Override
    protected IChatBaseComponent getDefaultName() {
        return new ChatMessage("container.hopper");
    }

    public static void pushItemsTick(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityHopper tileentityhopper) {
        --tileentityhopper.cooldownTime;
        tileentityhopper.tickedGameTime = world.getGameTime();
        if (!tileentityhopper.isOnCooldown()) {
            tileentityhopper.setCooldown(0);
            tryMoveItems(world, blockposition, iblockdata, tileentityhopper, () -> {
                return suckInItems(world, tileentityhopper);
            });
        }

    }

    private static boolean tryMoveItems(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityHopper tileentityhopper, BooleanSupplier booleansupplier) {
        if (world.isClientSide) {
            return false;
        } else {
            if (!tileentityhopper.isOnCooldown() && (Boolean) iblockdata.getValue(BlockHopper.ENABLED)) {
                boolean flag = false;

                if (!tileentityhopper.isEmpty()) {
                    flag = ejectItems(world, blockposition, iblockdata, tileentityhopper);
                }

                if (!tileentityhopper.inventoryFull()) {
                    flag |= booleansupplier.getAsBoolean();
                }

                if (flag) {
                    tileentityhopper.setCooldown(8);
                    setChanged(world, blockposition, iblockdata);
                    return true;
                }
            }

            return false;
        }
    }

    private boolean inventoryFull() {
        Iterator iterator = this.items.iterator();

        ItemStack itemstack;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            itemstack = (ItemStack) iterator.next();
        } while (!itemstack.isEmpty() && itemstack.getCount() == itemstack.getMaxStackSize());

        return false;
    }

    private static boolean ejectItems(World world, BlockPosition blockposition, IBlockData iblockdata, IInventory iinventory) {
        IInventory iinventory1 = getAttachedContainer(world, blockposition, iblockdata);

        if (iinventory1 == null) {
            return false;
        } else {
            EnumDirection enumdirection = ((EnumDirection) iblockdata.getValue(BlockHopper.FACING)).getOpposite();

            if (isFullContainer(iinventory1, enumdirection)) {
                return false;
            } else {
                for (int i = 0; i < iinventory.getContainerSize(); ++i) {
                    if (!iinventory.getItem(i).isEmpty()) {
                        ItemStack itemstack = iinventory.getItem(i).copy();
                        ItemStack itemstack1 = addItem(iinventory, iinventory1, iinventory.removeItem(i, 1), enumdirection);

                        if (itemstack1.isEmpty()) {
                            iinventory1.setChanged();
                            return true;
                        }

                        iinventory.setItem(i, itemstack);
                    }
                }

                return false;
            }
        }
    }

    private static IntStream getSlots(IInventory iinventory, EnumDirection enumdirection) {
        return iinventory instanceof IWorldInventory ? IntStream.of(((IWorldInventory) iinventory).getSlotsForFace(enumdirection)) : IntStream.range(0, iinventory.getContainerSize());
    }

    private static boolean isFullContainer(IInventory iinventory, EnumDirection enumdirection) {
        return getSlots(iinventory, enumdirection).allMatch((i) -> {
            ItemStack itemstack = iinventory.getItem(i);

            return itemstack.getCount() >= itemstack.getMaxStackSize();
        });
    }

    private static boolean isEmptyContainer(IInventory iinventory, EnumDirection enumdirection) {
        return getSlots(iinventory, enumdirection).allMatch((i) -> {
            return iinventory.getItem(i).isEmpty();
        });
    }

    public static boolean suckInItems(World world, IHopper ihopper) {
        IInventory iinventory = getSourceContainer(world, ihopper);

        if (iinventory != null) {
            EnumDirection enumdirection = EnumDirection.DOWN;

            return isEmptyContainer(iinventory, enumdirection) ? false : getSlots(iinventory, enumdirection).anyMatch((i) -> {
                return tryTakeInItemFromSlot(ihopper, iinventory, i, enumdirection);
            });
        } else {
            Iterator iterator = getItemsAtAndAbove(world, ihopper).iterator();

            EntityItem entityitem;

            do {
                if (!iterator.hasNext()) {
                    return false;
                }

                entityitem = (EntityItem) iterator.next();
            } while (!addItem(ihopper, entityitem));

            return true;
        }
    }

    private static boolean tryTakeInItemFromSlot(IHopper ihopper, IInventory iinventory, int i, EnumDirection enumdirection) {
        ItemStack itemstack = iinventory.getItem(i);

        if (!itemstack.isEmpty() && canTakeItemFromContainer(iinventory, itemstack, i, enumdirection)) {
            ItemStack itemstack1 = itemstack.copy();
            ItemStack itemstack2 = addItem(iinventory, ihopper, iinventory.removeItem(i, 1), (EnumDirection) null);

            if (itemstack2.isEmpty()) {
                iinventory.setChanged();
                return true;
            }

            iinventory.setItem(i, itemstack1);
        }

        return false;
    }

    public static boolean addItem(IInventory iinventory, EntityItem entityitem) {
        boolean flag = false;
        ItemStack itemstack = entityitem.getItem().copy();
        ItemStack itemstack1 = addItem((IInventory) null, iinventory, itemstack, (EnumDirection) null);

        if (itemstack1.isEmpty()) {
            flag = true;
            entityitem.discard();
        } else {
            entityitem.setItem(itemstack1);
        }

        return flag;
    }

    public static ItemStack addItem(@Nullable IInventory iinventory, IInventory iinventory1, ItemStack itemstack, @Nullable EnumDirection enumdirection) {
        if (iinventory1 instanceof IWorldInventory && enumdirection != null) {
            IWorldInventory iworldinventory = (IWorldInventory) iinventory1;
            int[] aint = iworldinventory.getSlotsForFace(enumdirection);

            for (int i = 0; i < aint.length && !itemstack.isEmpty(); ++i) {
                itemstack = tryMoveInItem(iinventory, iinventory1, itemstack, aint[i], enumdirection);
            }
        } else {
            int j = iinventory1.getContainerSize();

            for (int k = 0; k < j && !itemstack.isEmpty(); ++k) {
                itemstack = tryMoveInItem(iinventory, iinventory1, itemstack, k, enumdirection);
            }
        }

        return itemstack;
    }

    private static boolean canPlaceItemInContainer(IInventory iinventory, ItemStack itemstack, int i, @Nullable EnumDirection enumdirection) {
        return !iinventory.canPlaceItem(i, itemstack) ? false : !(iinventory instanceof IWorldInventory) || ((IWorldInventory) iinventory).canPlaceItemThroughFace(i, itemstack, enumdirection);
    }

    private static boolean canTakeItemFromContainer(IInventory iinventory, ItemStack itemstack, int i, EnumDirection enumdirection) {
        return !(iinventory instanceof IWorldInventory) || ((IWorldInventory) iinventory).canTakeItemThroughFace(i, itemstack, enumdirection);
    }

    private static ItemStack tryMoveInItem(@Nullable IInventory iinventory, IInventory iinventory1, ItemStack itemstack, int i, @Nullable EnumDirection enumdirection) {
        ItemStack itemstack1 = iinventory1.getItem(i);

        if (canPlaceItemInContainer(iinventory1, itemstack, i, enumdirection)) {
            boolean flag = false;
            boolean flag1 = iinventory1.isEmpty();

            if (itemstack1.isEmpty()) {
                iinventory1.setItem(i, itemstack);
                itemstack = ItemStack.EMPTY;
                flag = true;
            } else if (canMergeItems(itemstack1, itemstack)) {
                int j = itemstack.getMaxStackSize() - itemstack1.getCount();
                int k = Math.min(itemstack.getCount(), j);

                itemstack.shrink(k);
                itemstack1.grow(k);
                flag = k > 0;
            }

            if (flag) {
                if (flag1 && iinventory1 instanceof TileEntityHopper) {
                    TileEntityHopper tileentityhopper = (TileEntityHopper) iinventory1;

                    if (!tileentityhopper.isOnCustomCooldown()) {
                        byte b0 = 0;

                        if (iinventory instanceof TileEntityHopper) {
                            TileEntityHopper tileentityhopper1 = (TileEntityHopper) iinventory;

                            if (tileentityhopper.tickedGameTime >= tileentityhopper1.tickedGameTime) {
                                b0 = 1;
                            }
                        }

                        tileentityhopper.setCooldown(8 - b0);
                    }
                }

                iinventory1.setChanged();
            }
        }

        return itemstack;
    }

    @Nullable
    private static IInventory getAttachedContainer(World world, BlockPosition blockposition, IBlockData iblockdata) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.getValue(BlockHopper.FACING);

        return getContainerAt(world, blockposition.relative(enumdirection));
    }

    @Nullable
    private static IInventory getSourceContainer(World world, IHopper ihopper) {
        return getContainerAt(world, ihopper.getLevelX(), ihopper.getLevelY() + 1.0D, ihopper.getLevelZ());
    }

    public static List<EntityItem> getItemsAtAndAbove(World world, IHopper ihopper) {
        return (List) ihopper.getSuckShape().toAabbs().stream().flatMap((axisalignedbb) -> {
            return world.getEntitiesOfClass(EntityItem.class, axisalignedbb.move(ihopper.getLevelX() - 0.5D, ihopper.getLevelY() - 0.5D, ihopper.getLevelZ() - 0.5D), IEntitySelector.ENTITY_STILL_ALIVE).stream();
        }).collect(Collectors.toList());
    }

    @Nullable
    public static IInventory getContainerAt(World world, BlockPosition blockposition) {
        return getContainerAt(world, (double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D);
    }

    @Nullable
    private static IInventory getContainerAt(World world, double d0, double d1, double d2) {
        Object object = null;
        BlockPosition blockposition = new BlockPosition(d0, d1, d2);
        IBlockData iblockdata = world.getBlockState(blockposition);
        Block block = iblockdata.getBlock();

        if (block instanceof IInventoryHolder) {
            object = ((IInventoryHolder) block).getContainer(iblockdata, world, blockposition);
        } else if (iblockdata.hasBlockEntity()) {
            TileEntity tileentity = world.getBlockEntity(blockposition);

            if (tileentity instanceof IInventory) {
                object = (IInventory) tileentity;
                if (object instanceof TileEntityChest && block instanceof BlockChest) {
                    object = BlockChest.getContainer((BlockChest) block, iblockdata, world, blockposition, true);
                }
            }
        }

        if (object == null) {
            List<Entity> list = world.getEntities((Entity) null, new AxisAlignedBB(d0 - 0.5D, d1 - 0.5D, d2 - 0.5D, d0 + 0.5D, d1 + 0.5D, d2 + 0.5D), IEntitySelector.CONTAINER_ENTITY_SELECTOR);

            if (!list.isEmpty()) {
                object = (IInventory) list.get(world.random.nextInt(list.size()));
            }
        }

        return (IInventory) object;
    }

    private static boolean canMergeItems(ItemStack itemstack, ItemStack itemstack1) {
        return !itemstack.is(itemstack1.getItem()) ? false : (itemstack.getDamageValue() != itemstack1.getDamageValue() ? false : (itemstack.getCount() > itemstack.getMaxStackSize() ? false : ItemStack.tagMatches(itemstack, itemstack1)));
    }

    @Override
    public double getLevelX() {
        return (double) this.worldPosition.getX() + 0.5D;
    }

    @Override
    public double getLevelY() {
        return (double) this.worldPosition.getY() + 0.5D;
    }

    @Override
    public double getLevelZ() {
        return (double) this.worldPosition.getZ() + 0.5D;
    }

    private void setCooldown(int i) {
        this.cooldownTime = i;
    }

    private boolean isOnCooldown() {
        return this.cooldownTime > 0;
    }

    private boolean isOnCustomCooldown() {
        return this.cooldownTime > 8;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> nonnulllist) {
        this.items = nonnulllist;
    }

    public static void entityInside(World world, BlockPosition blockposition, IBlockData iblockdata, Entity entity, TileEntityHopper tileentityhopper) {
        if (entity instanceof EntityItem && VoxelShapes.joinIsNotEmpty(VoxelShapes.create(entity.getBoundingBox().move((double) (-blockposition.getX()), (double) (-blockposition.getY()), (double) (-blockposition.getZ()))), tileentityhopper.getSuckShape(), OperatorBoolean.AND)) {
            tryMoveItems(world, blockposition, iblockdata, tileentityhopper, () -> {
                return addItem(tileentityhopper, (EntityItem) entity);
            });
        }

    }

    @Override
    protected Container createMenu(int i, PlayerInventory playerinventory) {
        return new ContainerHopper(i, playerinventory, this);
    }
}
