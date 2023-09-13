package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemRecord;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockJukeBox;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEvent;

public class TileEntityJukeBox extends TileEntity implements Clearable {

    private ItemStack record;
    private int ticksSinceLastEvent;
    private long tickCount;
    private long recordStartedTick;
    private boolean isPlaying;

    public TileEntityJukeBox(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.JUKEBOX, blockposition, iblockdata);
        this.record = ItemStack.EMPTY;
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        if (nbttagcompound.contains("RecordItem", 10)) {
            this.setRecord(ItemStack.of(nbttagcompound.getCompound("RecordItem")));
        }

        this.isPlaying = nbttagcompound.getBoolean("IsPlaying");
        this.recordStartedTick = nbttagcompound.getLong("RecordStartTick");
        this.tickCount = nbttagcompound.getLong("TickCount");
    }

    @Override
    protected void saveAdditional(NBTTagCompound nbttagcompound) {
        super.saveAdditional(nbttagcompound);
        if (!this.getRecord().isEmpty()) {
            nbttagcompound.put("RecordItem", this.getRecord().save(new NBTTagCompound()));
        }

        nbttagcompound.putBoolean("IsPlaying", this.isPlaying);
        nbttagcompound.putLong("RecordStartTick", this.recordStartedTick);
        nbttagcompound.putLong("TickCount", this.tickCount);
    }

    public ItemStack getRecord() {
        return this.record;
    }

    public void setRecord(ItemStack itemstack) {
        this.record = itemstack;
        this.setChanged();
    }

    public void playRecord() {
        this.recordStartedTick = this.tickCount;
        this.isPlaying = true;
    }

    @Override
    public void clearContent() {
        this.setRecord(ItemStack.EMPTY);
        this.isPlaying = false;
    }

    public static void playRecordTick(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityJukeBox tileentityjukebox) {
        ++tileentityjukebox.ticksSinceLastEvent;
        if (recordIsPlaying(iblockdata, tileentityjukebox)) {
            Item item = tileentityjukebox.getRecord().getItem();

            if (item instanceof ItemRecord) {
                ItemRecord itemrecord = (ItemRecord) item;

                if (recordShouldStopPlaying(tileentityjukebox, itemrecord)) {
                    world.gameEvent(GameEvent.JUKEBOX_STOP_PLAY, blockposition, GameEvent.a.of(iblockdata));
                    tileentityjukebox.isPlaying = false;
                } else if (shouldSendJukeboxPlayingEvent(tileentityjukebox)) {
                    tileentityjukebox.ticksSinceLastEvent = 0;
                    world.gameEvent(GameEvent.JUKEBOX_PLAY, blockposition, GameEvent.a.of(iblockdata));
                }
            }
        }

        ++tileentityjukebox.tickCount;
    }

    private static boolean recordIsPlaying(IBlockData iblockdata, TileEntityJukeBox tileentityjukebox) {
        return (Boolean) iblockdata.getValue(BlockJukeBox.HAS_RECORD) && tileentityjukebox.isPlaying;
    }

    private static boolean recordShouldStopPlaying(TileEntityJukeBox tileentityjukebox, ItemRecord itemrecord) {
        return tileentityjukebox.tickCount >= tileentityjukebox.recordStartedTick + (long) itemrecord.getLengthInTicks();
    }

    private static boolean shouldSendJukeboxPlayingEvent(TileEntityJukeBox tileentityjukebox) {
        return tileentityjukebox.ticksSinceLastEvent >= 20;
    }
}
