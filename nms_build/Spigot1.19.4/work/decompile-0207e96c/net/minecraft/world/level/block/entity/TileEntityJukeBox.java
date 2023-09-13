package net.minecraft.world.level.block.entity;

import com.google.common.annotations.VisibleForTesting;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.TagsItem;
import net.minecraft.world.Clearable;
import net.minecraft.world.IInventory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemRecord;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockJukeBox;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.ticks.ContainerSingleItem;

public class TileEntityJukeBox extends TileEntity implements Clearable, ContainerSingleItem {

    private static final int SONG_END_PADDING = 20;
    private final NonNullList<ItemStack> items;
    private int ticksSinceLastEvent;
    private long tickCount;
    private long recordStartedTick;
    private boolean isPlaying;

    public TileEntityJukeBox(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.JUKEBOX, blockposition, iblockdata);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        if (nbttagcompound.contains("RecordItem", 10)) {
            this.items.set(0, ItemStack.of(nbttagcompound.getCompound("RecordItem")));
        }

        this.isPlaying = nbttagcompound.getBoolean("IsPlaying");
        this.recordStartedTick = nbttagcompound.getLong("RecordStartTick");
        this.tickCount = nbttagcompound.getLong("TickCount");
    }

    @Override
    protected void saveAdditional(NBTTagCompound nbttagcompound) {
        super.saveAdditional(nbttagcompound);
        if (!this.getFirstItem().isEmpty()) {
            nbttagcompound.put("RecordItem", this.getFirstItem().save(new NBTTagCompound()));
        }

        nbttagcompound.putBoolean("IsPlaying", this.isPlaying);
        nbttagcompound.putLong("RecordStartTick", this.recordStartedTick);
        nbttagcompound.putLong("TickCount", this.tickCount);
    }

    public boolean isRecordPlaying() {
        return !this.getFirstItem().isEmpty() && this.isPlaying;
    }

    private void setHasRecordBlockState(@Nullable Entity entity, boolean flag) {
        if (this.level.getBlockState(this.getBlockPos()) == this.getBlockState()) {
            this.level.setBlock(this.getBlockPos(), (IBlockData) this.getBlockState().setValue(BlockJukeBox.HAS_RECORD, flag), 2);
            this.level.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(), GameEvent.a.of(entity, this.getBlockState()));
        }

    }

    @VisibleForTesting
    public void startPlaying() {
        this.recordStartedTick = this.tickCount;
        this.isPlaying = true;
        this.level.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
        this.level.levelEvent((EntityHuman) null, 1010, this.getBlockPos(), Item.getId(this.getFirstItem().getItem()));
        this.setChanged();
    }

    private void stopPlaying() {
        this.isPlaying = false;
        this.level.gameEvent(GameEvent.JUKEBOX_STOP_PLAY, this.getBlockPos(), GameEvent.a.of(this.getBlockState()));
        this.level.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
        this.level.levelEvent(1011, this.getBlockPos(), 0);
        this.setChanged();
    }

    private void tick(World world, BlockPosition blockposition, IBlockData iblockdata) {
        ++this.ticksSinceLastEvent;
        if (this.isRecordPlaying()) {
            Item item = this.getFirstItem().getItem();

            if (item instanceof ItemRecord) {
                ItemRecord itemrecord = (ItemRecord) item;

                if (this.shouldRecordStopPlaying(itemrecord)) {
                    this.stopPlaying();
                } else if (this.shouldSendJukeboxPlayingEvent()) {
                    this.ticksSinceLastEvent = 0;
                    world.gameEvent(GameEvent.JUKEBOX_PLAY, blockposition, GameEvent.a.of(iblockdata));
                    this.spawnMusicParticles(world, blockposition);
                }
            }
        }

        ++this.tickCount;
    }

    private boolean shouldRecordStopPlaying(ItemRecord itemrecord) {
        return this.tickCount >= this.recordStartedTick + (long) itemrecord.getLengthInTicks() + 20L;
    }

    private boolean shouldSendJukeboxPlayingEvent() {
        return this.ticksSinceLastEvent >= 20;
    }

    @Override
    public ItemStack getItem(int i) {
        return (ItemStack) this.items.get(i);
    }

    @Override
    public ItemStack removeItem(int i, int j) {
        ItemStack itemstack = (ItemStack) Objects.requireNonNullElse((ItemStack) this.items.get(i), ItemStack.EMPTY);

        this.items.set(i, ItemStack.EMPTY);
        if (!itemstack.isEmpty()) {
            this.setHasRecordBlockState((Entity) null, false);
            this.stopPlaying();
        }

        return itemstack;
    }

    @Override
    public void setItem(int i, ItemStack itemstack) {
        if (itemstack.is(TagsItem.MUSIC_DISCS) && this.level != null) {
            this.items.set(i, itemstack);
            this.setHasRecordBlockState((Entity) null, true);
            this.startPlaying();
        }

    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean stillValid(EntityHuman entityhuman) {
        return IInventory.stillValidBlockEntity(this, entityhuman);
    }

    @Override
    public boolean canPlaceItem(int i, ItemStack itemstack) {
        return itemstack.is(TagsItem.MUSIC_DISCS) && this.getItem(i).isEmpty();
    }

    @Override
    public boolean canTakeItem(IInventory iinventory, int i, ItemStack itemstack) {
        return iinventory.hasAnyMatching(ItemStack::isEmpty);
    }

    private void spawnMusicParticles(World world, BlockPosition blockposition) {
        if (world instanceof WorldServer) {
            WorldServer worldserver = (WorldServer) world;
            Vec3D vec3d = Vec3D.atBottomCenterOf(blockposition).add(0.0D, 1.2000000476837158D, 0.0D);
            float f = (float) world.getRandom().nextInt(4) / 24.0F;

            worldserver.sendParticles(Particles.NOTE, vec3d.x(), vec3d.y(), vec3d.z(), 0, (double) f, 0.0D, 0.0D, 1.0D);
        }

    }

    public void popOutRecord() {
        if (this.level != null && !this.level.isClientSide) {
            BlockPosition blockposition = this.getBlockPos();
            ItemStack itemstack = this.getFirstItem();

            if (!itemstack.isEmpty()) {
                this.removeFirstItem();
                Vec3D vec3d = Vec3D.atLowerCornerWithOffset(blockposition, 0.5D, 1.01D, 0.5D).offsetRandom(this.level.random, 0.7F);
                ItemStack itemstack1 = itemstack.copy();
                EntityItem entityitem = new EntityItem(this.level, vec3d.x(), vec3d.y(), vec3d.z(), itemstack1);

                entityitem.setDefaultPickUpDelay();
                this.level.addFreshEntity(entityitem);
            }
        }
    }

    public static void playRecordTick(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityJukeBox tileentityjukebox) {
        tileentityjukebox.tick(world, blockposition, iblockdata);
    }

    @VisibleForTesting
    public void setRecordWithoutPlaying(ItemStack itemstack) {
        this.items.set(0, itemstack);
        this.level.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
        this.setChanged();
    }
}
