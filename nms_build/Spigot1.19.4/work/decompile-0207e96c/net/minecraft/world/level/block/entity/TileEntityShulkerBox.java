package net.minecraft.world.level.block.entity;

import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.NBTTagCompound;
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
        this.itemStacks = NonNullList.withSize(27, ItemStack.EMPTY);
        this.animationStatus = TileEntityShulkerBox.AnimationPhase.CLOSED;
        this.color = enumcolor;
    }

    public TileEntityShulkerBox(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.SHULKER_BOX, blockposition, iblockdata);
        this.itemStacks = NonNullList.withSize(27, ItemStack.EMPTY);
        this.animationStatus = TileEntityShulkerBox.AnimationPhase.CLOSED;
        this.color = BlockShulkerBox.getColorFromBlock(iblockdata.getBlock());
    }

    public static void tick(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityShulkerBox tileentityshulkerbox) {
        tileentityshulkerbox.updateAnimation(world, blockposition, iblockdata);
    }

    private void updateAnimation(World world, BlockPosition blockposition, IBlockData iblockdata) {
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
                    doNeighborUpdates(world, blockposition, iblockdata);
                }

                this.moveCollidedEntities(world, blockposition, iblockdata);
                break;
            case CLOSING:
                this.progress -= 0.1F;
                if (this.progress <= 0.0F) {
                    this.animationStatus = TileEntityShulkerBox.AnimationPhase.CLOSED;
                    this.progress = 0.0F;
                    doNeighborUpdates(world, blockposition, iblockdata);
                }
                break;
            case OPENED:
                this.progress = 1.0F;
        }

    }

    public TileEntityShulkerBox.AnimationPhase getAnimationStatus() {
        return this.animationStatus;
    }

    public AxisAlignedBB getBoundingBox(IBlockData iblockdata) {
        return EntityShulker.getProgressAabb((EnumDirection) iblockdata.getValue(BlockShulkerBox.FACING), 0.5F * this.getProgress(1.0F));
    }

    private void moveCollidedEntities(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (iblockdata.getBlock() instanceof BlockShulkerBox) {
            EnumDirection enumdirection = (EnumDirection) iblockdata.getValue(BlockShulkerBox.FACING);
            AxisAlignedBB axisalignedbb = EntityShulker.getProgressDeltaAabb(enumdirection, this.progressOld, this.progress).move(blockposition);
            List<Entity> list = world.getEntities((Entity) null, axisalignedbb);

            if (!list.isEmpty()) {
                for (int i = 0; i < list.size(); ++i) {
                    Entity entity = (Entity) list.get(i);

                    if (entity.getPistonPushReaction() != EnumPistonReaction.IGNORE) {
                        entity.move(EnumMoveType.SHULKER_BOX, new Vec3D((axisalignedbb.getXsize() + 0.01D) * (double) enumdirection.getStepX(), (axisalignedbb.getYsize() + 0.01D) * (double) enumdirection.getStepY(), (axisalignedbb.getZsize() + 0.01D) * (double) enumdirection.getStepZ()));
                    }
                }

            }
        }
    }

    @Override
    public int getContainerSize() {
        return this.itemStacks.size();
    }

    @Override
    public boolean triggerEvent(int i, int j) {
        if (i == 1) {
            this.openCount = j;
            if (j == 0) {
                this.animationStatus = TileEntityShulkerBox.AnimationPhase.CLOSING;
                doNeighborUpdates(this.getLevel(), this.worldPosition, this.getBlockState());
            }

            if (j == 1) {
                this.animationStatus = TileEntityShulkerBox.AnimationPhase.OPENING;
                doNeighborUpdates(this.getLevel(), this.worldPosition, this.getBlockState());
            }

            return true;
        } else {
            return super.triggerEvent(i, j);
        }
    }

    private static void doNeighborUpdates(World world, BlockPosition blockposition, IBlockData iblockdata) {
        iblockdata.updateNeighbourShapes(world, blockposition, 3);
    }

    @Override
    public void startOpen(EntityHuman entityhuman) {
        if (!this.remove && !entityhuman.isSpectator()) {
            if (this.openCount < 0) {
                this.openCount = 0;
            }

            ++this.openCount;
            this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.openCount);
            if (this.openCount == 1) {
                this.level.gameEvent((Entity) entityhuman, GameEvent.CONTAINER_OPEN, this.worldPosition);
                this.level.playSound((EntityHuman) null, this.worldPosition, SoundEffects.SHULKER_BOX_OPEN, SoundCategory.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
            }
        }

    }

    @Override
    public void stopOpen(EntityHuman entityhuman) {
        if (!this.remove && !entityhuman.isSpectator()) {
            --this.openCount;
            this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.openCount);
            if (this.openCount <= 0) {
                this.level.gameEvent((Entity) entityhuman, GameEvent.CONTAINER_CLOSE, this.worldPosition);
                this.level.playSound((EntityHuman) null, this.worldPosition, SoundEffects.SHULKER_BOX_CLOSE, SoundCategory.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
            }
        }

    }

    @Override
    protected IChatBaseComponent getDefaultName() {
        return IChatBaseComponent.translatable("container.shulkerBox");
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.loadFromTag(nbttagcompound);
    }

    @Override
    protected void saveAdditional(NBTTagCompound nbttagcompound) {
        super.saveAdditional(nbttagcompound);
        if (!this.trySaveLootTable(nbttagcompound)) {
            ContainerUtil.saveAllItems(nbttagcompound, this.itemStacks, false);
        }

    }

    public void loadFromTag(NBTTagCompound nbttagcompound) {
        this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(nbttagcompound) && nbttagcompound.contains("Items", 9)) {
            ContainerUtil.loadAllItems(nbttagcompound, this.itemStacks);
        }

    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.itemStacks;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> nonnulllist) {
        this.itemStacks = nonnulllist;
    }

    @Override
    public int[] getSlotsForFace(EnumDirection enumdirection) {
        return TileEntityShulkerBox.SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int i, ItemStack itemstack, @Nullable EnumDirection enumdirection) {
        return !(Block.byItem(itemstack.getItem()) instanceof BlockShulkerBox);
    }

    @Override
    public boolean canTakeItemThroughFace(int i, ItemStack itemstack, EnumDirection enumdirection) {
        return true;
    }

    public float getProgress(float f) {
        return MathHelper.lerp(f, this.progressOld, this.progress);
    }

    @Nullable
    public EnumColor getColor() {
        return this.color;
    }

    @Override
    protected Container createMenu(int i, PlayerInventory playerinventory) {
        return new ContainerShulkerBox(i, playerinventory, this);
    }

    public boolean isClosed() {
        return this.animationStatus == TileEntityShulkerBox.AnimationPhase.CLOSED;
    }

    public static enum AnimationPhase {

        CLOSED, OPENING, OPENED, CLOSING;

        private AnimationPhase() {}
    }
}
