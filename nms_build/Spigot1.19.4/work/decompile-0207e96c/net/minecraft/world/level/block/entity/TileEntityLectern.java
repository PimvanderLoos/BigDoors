package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICommandListener;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Clearable;
import net.minecraft.world.IInventory;
import net.minecraft.world.ITileInventory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerLectern;
import net.minecraft.world.inventory.IContainerProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemWrittenBook;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BlockLectern;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec2F;
import net.minecraft.world.phys.Vec3D;

public class TileEntityLectern extends TileEntity implements Clearable, ITileInventory {

    public static final int DATA_PAGE = 0;
    public static final int NUM_DATA = 1;
    public static final int SLOT_BOOK = 0;
    public static final int NUM_SLOTS = 1;
    public final IInventory bookAccess = new IInventory() {
        @Override
        public int getContainerSize() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return TileEntityLectern.this.book.isEmpty();
        }

        @Override
        public ItemStack getItem(int i) {
            return i == 0 ? TileEntityLectern.this.book : ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItem(int i, int j) {
            if (i == 0) {
                ItemStack itemstack = TileEntityLectern.this.book.split(j);

                if (TileEntityLectern.this.book.isEmpty()) {
                    TileEntityLectern.this.onBookItemRemove();
                }

                return itemstack;
            } else {
                return ItemStack.EMPTY;
            }
        }

        @Override
        public ItemStack removeItemNoUpdate(int i) {
            if (i == 0) {
                ItemStack itemstack = TileEntityLectern.this.book;

                TileEntityLectern.this.book = ItemStack.EMPTY;
                TileEntityLectern.this.onBookItemRemove();
                return itemstack;
            } else {
                return ItemStack.EMPTY;
            }
        }

        @Override
        public void setItem(int i, ItemStack itemstack) {}

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public void setChanged() {
            TileEntityLectern.this.setChanged();
        }

        @Override
        public boolean stillValid(EntityHuman entityhuman) {
            return IInventory.stillValidBlockEntity(TileEntityLectern.this, entityhuman) && TileEntityLectern.this.hasBook();
        }

        @Override
        public boolean canPlaceItem(int i, ItemStack itemstack) {
            return false;
        }

        @Override
        public void clearContent() {}
    };
    private final IContainerProperties dataAccess = new IContainerProperties() {
        @Override
        public int get(int i) {
            return i == 0 ? TileEntityLectern.this.page : 0;
        }

        @Override
        public void set(int i, int j) {
            if (i == 0) {
                TileEntityLectern.this.setPage(j);
            }

        }

        @Override
        public int getCount() {
            return 1;
        }
    };
    ItemStack book;
    int page;
    private int pageCount;

    public TileEntityLectern(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.LECTERN, blockposition, iblockdata);
        this.book = ItemStack.EMPTY;
    }

    public ItemStack getBook() {
        return this.book;
    }

    public boolean hasBook() {
        return this.book.is(Items.WRITABLE_BOOK) || this.book.is(Items.WRITTEN_BOOK);
    }

    public void setBook(ItemStack itemstack) {
        this.setBook(itemstack, (EntityHuman) null);
    }

    void onBookItemRemove() {
        this.page = 0;
        this.pageCount = 0;
        BlockLectern.resetBookState((Entity) null, this.getLevel(), this.getBlockPos(), this.getBlockState(), false);
    }

    public void setBook(ItemStack itemstack, @Nullable EntityHuman entityhuman) {
        this.book = this.resolveBook(itemstack, entityhuman);
        this.page = 0;
        this.pageCount = ItemWrittenBook.getPageCount(this.book);
        this.setChanged();
    }

    public void setPage(int i) {
        int j = MathHelper.clamp(i, 0, this.pageCount - 1);

        if (j != this.page) {
            this.page = j;
            this.setChanged();
            BlockLectern.signalPageChange(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }

    }

    public int getPage() {
        return this.page;
    }

    public int getRedstoneSignal() {
        float f = this.pageCount > 1 ? (float) this.getPage() / ((float) this.pageCount - 1.0F) : 1.0F;

        return MathHelper.floor(f * 14.0F) + (this.hasBook() ? 1 : 0);
    }

    private ItemStack resolveBook(ItemStack itemstack, @Nullable EntityHuman entityhuman) {
        if (this.level instanceof WorldServer && itemstack.is(Items.WRITTEN_BOOK)) {
            ItemWrittenBook.resolveBookComponents(itemstack, this.createCommandSourceStack(entityhuman), entityhuman);
        }

        return itemstack;
    }

    private CommandListenerWrapper createCommandSourceStack(@Nullable EntityHuman entityhuman) {
        String s;
        Object object;

        if (entityhuman == null) {
            s = "Lectern";
            object = IChatBaseComponent.literal("Lectern");
        } else {
            s = entityhuman.getName().getString();
            object = entityhuman.getDisplayName();
        }

        Vec3D vec3d = Vec3D.atCenterOf(this.worldPosition);

        return new CommandListenerWrapper(ICommandListener.NULL, vec3d, Vec2F.ZERO, (WorldServer) this.level, 2, s, (IChatBaseComponent) object, this.level.getServer(), entityhuman);
    }

    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        if (nbttagcompound.contains("Book", 10)) {
            this.book = this.resolveBook(ItemStack.of(nbttagcompound.getCompound("Book")), (EntityHuman) null);
        } else {
            this.book = ItemStack.EMPTY;
        }

        this.pageCount = ItemWrittenBook.getPageCount(this.book);
        this.page = MathHelper.clamp(nbttagcompound.getInt("Page"), 0, this.pageCount - 1);
    }

    @Override
    protected void saveAdditional(NBTTagCompound nbttagcompound) {
        super.saveAdditional(nbttagcompound);
        if (!this.getBook().isEmpty()) {
            nbttagcompound.put("Book", this.getBook().save(new NBTTagCompound()));
            nbttagcompound.putInt("Page", this.page);
        }

    }

    @Override
    public void clearContent() {
        this.setBook(ItemStack.EMPTY);
    }

    @Override
    public Container createMenu(int i, PlayerInventory playerinventory, EntityHuman entityhuman) {
        return new ContainerLectern(i, this.bookAccess, this.dataAccess);
    }

    @Override
    public IChatBaseComponent getDisplayName() {
        return IChatBaseComponent.translatable("container.lectern");
    }
}
