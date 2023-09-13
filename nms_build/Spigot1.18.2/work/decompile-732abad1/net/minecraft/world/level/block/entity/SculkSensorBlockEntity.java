package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.SculkSensorBlock;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.vibrations.VibrationListener;

public class SculkSensorBlockEntity extends TileEntity implements VibrationListener.a {

    private final VibrationListener listener;
    public int lastVibrationFrequency;

    public SculkSensorBlockEntity(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.SCULK_SENSOR, blockposition, iblockdata);
        this.listener = new VibrationListener(new BlockPositionSource(this.worldPosition), ((SculkSensorBlock) iblockdata.getBlock()).getListenerRange(), this);
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.lastVibrationFrequency = nbttagcompound.getInt("last_vibration_frequency");
    }

    @Override
    protected void saveAdditional(NBTTagCompound nbttagcompound) {
        super.saveAdditional(nbttagcompound);
        nbttagcompound.putInt("last_vibration_frequency", this.lastVibrationFrequency);
    }

    public VibrationListener getListener() {
        return this.listener;
    }

    public int getLastVibrationFrequency() {
        return this.lastVibrationFrequency;
    }

    @Override
    public boolean shouldListen(World world, GameEventListener gameeventlistener, BlockPosition blockposition, GameEvent gameevent, @Nullable Entity entity) {
        boolean flag = gameevent == GameEvent.BLOCK_DESTROY && blockposition.equals(this.getBlockPos());
        boolean flag1 = gameevent == GameEvent.BLOCK_PLACE && blockposition.equals(this.getBlockPos());

        return !flag && !flag1 && SculkSensorBlock.canActivate(this.getBlockState());
    }

    @Override
    public void onSignalReceive(World world, GameEventListener gameeventlistener, GameEvent gameevent, int i) {
        IBlockData iblockdata = this.getBlockState();

        if (!world.isClientSide() && SculkSensorBlock.canActivate(iblockdata)) {
            this.lastVibrationFrequency = SculkSensorBlock.VIBRATION_STRENGTH_FOR_EVENT.getInt(gameevent);
            SculkSensorBlock.activate(world, this.worldPosition, iblockdata, getRedstoneStrengthForDistance(i, gameeventlistener.getListenerRadius()));
        }

    }

    public static int getRedstoneStrengthForDistance(int i, int j) {
        double d0 = (double) i / (double) j;

        return Math.max(1, 15 - MathHelper.floor(d0 * 15.0D));
    }
}
