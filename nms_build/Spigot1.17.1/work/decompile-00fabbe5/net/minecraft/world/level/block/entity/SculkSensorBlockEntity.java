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
        this.listener = new VibrationListener(new BlockPositionSource(this.worldPosition), ((SculkSensorBlock) iblockdata.getBlock()).e(), this);
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.lastVibrationFrequency = nbttagcompound.getInt("last_vibration_frequency");
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        nbttagcompound.setInt("last_vibration_frequency", this.lastVibrationFrequency);
        return nbttagcompound;
    }

    public VibrationListener d() {
        return this.listener;
    }

    public int getLastVibrationFrequency() {
        return this.lastVibrationFrequency;
    }

    @Override
    public boolean a(World world, GameEventListener gameeventlistener, BlockPosition blockposition, GameEvent gameevent, @Nullable Entity entity) {
        boolean flag = gameevent == GameEvent.BLOCK_DESTROY && blockposition.equals(this.getPosition());
        boolean flag1 = gameevent == GameEvent.BLOCK_PLACE && blockposition.equals(this.getPosition());

        return !flag && !flag1 && SculkSensorBlock.n(this.getBlock());
    }

    @Override
    public void a(World world, GameEventListener gameeventlistener, GameEvent gameevent, int i) {
        IBlockData iblockdata = this.getBlock();

        if (!world.isClientSide() && SculkSensorBlock.n(iblockdata)) {
            this.lastVibrationFrequency = SculkSensorBlock.VIBRATION_STRENGTH_FOR_EVENT.getInt(gameevent);
            SculkSensorBlock.a(world, this.worldPosition, iblockdata, b(i, gameeventlistener.b()));
        }

    }

    public static int b(int i, int j) {
        double d0 = (double) i / (double) j;

        return Math.max(1, 15 - MathHelper.floor(d0 * 15.0D));
    }
}
