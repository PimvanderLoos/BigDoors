package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.SculkSensorBlock;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.vibrations.VibrationListener;
import org.slf4j.Logger;

public class SculkSensorBlockEntity extends TileEntity implements VibrationListener.b {

    private static final Logger LOGGER = LogUtils.getLogger();
    private VibrationListener listener;
    public int lastVibrationFrequency;

    public SculkSensorBlockEntity(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.SCULK_SENSOR, blockposition, iblockdata);
        this.listener = new VibrationListener(new BlockPositionSource(this.worldPosition), ((SculkSensorBlock) iblockdata.getBlock()).getListenerRange(), this, (VibrationListener.a) null, 0.0F, 0);
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.lastVibrationFrequency = nbttagcompound.getInt("last_vibration_frequency");
        if (nbttagcompound.contains("listener", 10)) {
            DataResult dataresult = VibrationListener.codec(this).parse(new Dynamic(DynamicOpsNBT.INSTANCE, nbttagcompound.getCompound("listener")));
            Logger logger = SculkSensorBlockEntity.LOGGER;

            Objects.requireNonNull(logger);
            dataresult.resultOrPartial(logger::error).ifPresent((vibrationlistener) -> {
                this.listener = vibrationlistener;
            });
        }

    }

    @Override
    protected void saveAdditional(NBTTagCompound nbttagcompound) {
        super.saveAdditional(nbttagcompound);
        nbttagcompound.putInt("last_vibration_frequency", this.lastVibrationFrequency);
        DataResult dataresult = VibrationListener.codec(this).encodeStart(DynamicOpsNBT.INSTANCE, this.listener);
        Logger logger = SculkSensorBlockEntity.LOGGER;

        Objects.requireNonNull(logger);
        dataresult.resultOrPartial(logger::error).ifPresent((nbtbase) -> {
            nbttagcompound.put("listener", nbtbase);
        });
    }

    public VibrationListener getListener() {
        return this.listener;
    }

    public int getLastVibrationFrequency() {
        return this.lastVibrationFrequency;
    }

    @Override
    public boolean canTriggerAvoidVibration() {
        return true;
    }

    @Override
    public boolean shouldListen(WorldServer worldserver, GameEventListener gameeventlistener, BlockPosition blockposition, GameEvent gameevent, @Nullable GameEvent.a gameevent_a) {
        return !this.isRemoved() && (!blockposition.equals(this.getBlockPos()) || gameevent != GameEvent.BLOCK_DESTROY && gameevent != GameEvent.BLOCK_PLACE) ? SculkSensorBlock.canActivate(this.getBlockState()) : false;
    }

    @Override
    public void onSignalReceive(WorldServer worldserver, GameEventListener gameeventlistener, BlockPosition blockposition, GameEvent gameevent, @Nullable Entity entity, @Nullable Entity entity1, float f) {
        IBlockData iblockdata = this.getBlockState();

        if (SculkSensorBlock.canActivate(iblockdata)) {
            this.lastVibrationFrequency = SculkSensorBlock.VIBRATION_FREQUENCY_FOR_EVENT.getInt(gameevent);
            SculkSensorBlock.activate(entity, worldserver, this.worldPosition, iblockdata, getRedstoneStrengthForDistance(f, gameeventlistener.getListenerRadius()));
        }

    }

    @Override
    public void onSignalSchedule() {
        this.setChanged();
    }

    public static int getRedstoneStrengthForDistance(float f, int i) {
        double d0 = (double) f / (double) i;

        return Math.max(1, 15 - MathHelper.floor(d0 * 15.0D));
    }

    public void setLastVibrationFrequency(int i) {
        this.lastVibrationFrequency = i;
    }
}
