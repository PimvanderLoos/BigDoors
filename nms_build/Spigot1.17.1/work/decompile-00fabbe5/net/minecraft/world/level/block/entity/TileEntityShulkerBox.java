package net.minecraft.world.level.block.entity;

import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ContainerUtil;
import net.minecraft.world.IWorldInventory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.monster.EntityShulker;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerShulkerBox;
import net.minecraft.world.item.EnumColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockShulkerBox;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.EnumPistonReaction;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;

public class TileEntityShulkerBox extends TileEntityLootable implements IWorldInventory {

    public static final int COLUMNS = 9;
    public static final int ROWS = 3;
    public static final int CONTAINER_SIZE = 27;
    public static final int EVENT_SET_OPEN_COUNT = 1;
    public static final int OPENING_TICK_LENGTH = 10;
    public static final float MAX_LID_HEIGHT = 0.5F;
    public static final float MAX_LID_ROTATION = 270.0F;
    public static final String ITEMS_TAG = "Items";
    private static final int[] SLOTS = IntStream.range(0, 27).toArray();
    private NonNullList<ItemStack> itemStacks;
    public int openCount;
    private TileEntityShulkerBox.AnimationPhase animationStatus;
    private float progress;
    private float progressOld;
    @Nullable
    private final EnumColor color;

    public TileEntityShulkerBox(@Nullable EnumColor enumcolor, BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.SHULKER_BOX, blockposition, iblockdata);
        this.itemStacks = NonNullList.a(27, ItemStack.EMPTY);
        this.animationStatus = TileEntityShulkerBox.AnimationPhase.CLOSED;
        this.color = enumcolor;
    }

    public TileEntityShulkerBox(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.SHULKER_BOX, blockposition, iblockdata);
        this.itemStacks = NonNullList.a(27, ItemStack.EMPTY);
        this.animationStatus = TileEntityShulkerBox.AnimationPhase.CLOSED;
        this.color = BlockShulkerBox.a(iblockdata.getBlock());
    }

    public static void a(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityShulkerBox tileentityshulkerbox) {
        tileentityshulkerbox.b(world, blockposition, iblockdata);
    }

    private void b(World world, BlockPosition blockposition, IBlockData iblockdata) {
        this.progressOld = this.progress;
        switch (this.animationStatus) {
            case CLOSED:
                this.progress = 0.0F;
                break;
            case OPENING:
                this.progress += 0.1F;
                if (this.progress >= 1.0F) {
                    this.animationStatus = TileEntityShulkerBox.AnimationPhase.OPENED;
                    this.progress = 1.0F;
                    d(world, blockposition, iblockdata);
                }

                this.c(world, blockposition, iblockdata);
                break;
            case CLOSING:
                this.progress -= 0.1F;
                if (this.progress <= 0.0F) {
                    this.animationStatus = TileEntityShulkerBox.AnimationPhase.CLOSED;
                    this.progress = 0.0F;
                    d(world, blockposition, iblockdata);
                }
                break;
            case OPENED:
                this.progress = 1.0F;
        }

    }

    public TileEntityShulkerBox.AnimationPhase h() {
        return this.animationStatus;
    }

    public AxisAlignedBB a(IBlockData iblockdata) {
        return EntityShulker.a((EnumDirection) iblockdata.get(BlockShulkerBox.FACING), 0.5F * this.a(1.0F));
    }

    private void c(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (iblockdata.getBlock() instanceof BlockShulkerBox) {
            EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockShulkerBox.FACING);
            AxisAlignedBB axisalignedbb = EntityShulker.a(enumdirection, this.progressOld, this.progress).a(blockposition);
            List<Entity> list = world.getEntities((Entity) null, axisalignedbb);

            if (!list.isEmpty()) {
                for (int i = 0; i < list.size(); ++i) {
                    Entity entity = (Entity) list.get(i);

                    if (entity.getPushReaction() != EnumPistonReaction.IGNORE) {
                        entity.move(EnumMoveType.SHULKER_BOX, new Vec3D((axisalignedbb.b() + 0.01D) * (double) enumdirection.getAdjacentX(), (axisalignedbb.c() + 0.01D) * (double) enumdirection.getAdjacentY(), (axisalignedbb.d() + 0.01D) * (double) enumdirection.getAdjacentZ()));
                    }
                }

            }
        }
    }

    @Override
    public int getSize() {
        return this.itemStacks.size();
    }

    @Override
    public boolean setProperty(int i, int j) {
        if (i == 1) {
            this.openCount = j;
            if (j == 0) {
                this.animationStatus = TileEntityShulkerBox.AnimationPhase.CLOSING;
                d(this.getWorld(), this.worldPosition, this.getBlock());
            }

            if (j == 1) {
                this.animationStatus = TileEntityShulkerBox.AnimationPhase.OPENING;
                d(this.getWorld(), this.worldPosition, this.getBlock());
            }

            return true;
        } else {
            return super.setProperty(i, j);
        }
    }

    private static void d(World world, BlockPosition blockposition, IBlockData iblockdata) {
        iblockdata.a(world, blockposition, 3);
    }

    @Override
    public void startOpen(EntityHuman entityhuman) {
        if (!entityhuman.isSpectator()) {
            if (this.openCount < 0) {
                this.openCount = 0;
            }

            ++this.openCount;
            this.level.playBlockAction(this.worldPosition, this.getBlock().getBlock(), 1, this.openCount);
            if (this.openCount == 1) {
                this.level.a((Entity) entityhuman, GameEvent.CONTAINER_OPEN, this.worldPosition);
                this.level.playSound((EntityHuman) null, this.worldPosition, SoundEffects.SHULKER_BOX_OPEN, SoundCategory.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
            }
        }

    }

    @Override
    public void closeContainer(EntityHuman entityhuman) {
        if (!entityhuman.isSpectator()) {
            --this.openCount;
            this.level.playBlockAction(this.worldPosition, this.getBlock().getBlock(), 1, this.openCount);
            if (this.openCount <= 0) {
                this.level.a((Entity) entityhuman, GameEvent.CONTAINER_CLOSE, this.worldPosition);
                this.level.playSound((EntityHuman) null, this.worldPosition, SoundEffects.SHULKER_BOX_CLOSE, SoundCategory.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
            }
        }

    }

    @Override
    protected IChatBaseComponent getContainerName() {
        return new ChatMessage("container.shulkerBox");
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.e(nbttagcompound);
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        return this.f(nbttagcompound);
    }

    public void e(NBTTagCompound nbttagcompound) {
        this.itemStacks = NonNullList.a(this.getSize(), ItemStack.EMPTY);
        if (!this.c(nbttagcompound) && nbttagcompound.hasKeyOfType("Items", 9)) {
            ContainerUtil.b(nbttagcompound, this.itemStacks);
        }

    }

    public NBTTagCompound f(NBTTagCompound nbttagcompound) {
        if (!this.d(nbttagcompound)) {
            ContainerUtil.a(nbttagcompound, this.itemStacks, false);
        }

        return nbttagcompound;
    }

    @Override
    protected NonNullList<ItemStack> f() {
        return this.itemStacks;
    }

    @Override
    protected void a(NonNullList<ItemStack> nonnulllist) {
        this.itemStacks = nonnulllist;
    }

    @Override
    public int[] getSlotsForFace(EnumDirection enumdirection) {
        return TileEntityShulkerBox.SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int i, ItemStack itemstack, @Nullable EnumDirection enumdirection) {
        return !(Block.asBlock(itemstack.getItem()) instanceof BlockShulkerBox);
    }

    @Override
    public boolean canTakeItemThroughFace(int i, ItemStack itemstack, EnumDirection enumdirection) {
        return true;
    }

    public float a(float f) {
        return MathHelper.h(f, this.progressOld, this.progress);
    }

    @Nullable
    public EnumColor i() {
        return this.color;
    }

    @Override
    protected Container createContainer(int i, PlayerInventory playerinventory) {
        return new ContainerShulkerBox(i, playerinventory, this);
    }

    public boolean j() {
        return this.animationStatus == TileEntityShulkerBox.AnimationPhase.CLOSED;
    }

    public static enum AnimationPhase {

        CLOSED, OPENING, OPENED, CLOSING;

        private AnimationPhase() {}
    }
}
