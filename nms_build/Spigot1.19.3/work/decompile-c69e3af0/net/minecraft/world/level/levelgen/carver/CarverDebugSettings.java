package net.minecraft.world.level.levelgen.carver;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class CarverDebugSettings {

    public static final CarverDebugSettings DEFAULT = new CarverDebugSettings(false, Blocks.ACACIA_BUTTON.defaultBlockState(), Blocks.CANDLE.defaultBlockState(), Blocks.ORANGE_STAINED_GLASS.defaultBlockState(), Blocks.GLASS.defaultBlockState());
    public static final Codec<CarverDebugSettings> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.BOOL.optionalFieldOf("debug_mode", false).forGetter(CarverDebugSettings::isDebugMode), IBlockData.CODEC.optionalFieldOf("air_state", CarverDebugSettings.DEFAULT.getAirState()).forGetter(CarverDebugSettings::getAirState), IBlockData.CODEC.optionalFieldOf("water_state", CarverDebugSettings.DEFAULT.getAirState()).forGetter(CarverDebugSettings::getWaterState), IBlockData.CODEC.optionalFieldOf("lava_state", CarverDebugSettings.DEFAULT.getAirState()).forGetter(CarverDebugSettings::getLavaState), IBlockData.CODEC.optionalFieldOf("barrier_state", CarverDebugSettings.DEFAULT.getAirState()).forGetter(CarverDebugSettings::getBarrierState)).apply(instance, CarverDebugSettings::new);
    });
    private final boolean debugMode;
    private final IBlockData airState;
    private final IBlockData waterState;
    private final IBlockData lavaState;
    private final IBlockData barrierState;

    public static CarverDebugSettings of(boolean flag, IBlockData iblockdata, IBlockData iblockdata1, IBlockData iblockdata2, IBlockData iblockdata3) {
        return new CarverDebugSettings(flag, iblockdata, iblockdata1, iblockdata2, iblockdata3);
    }

    public static CarverDebugSettings of(IBlockData iblockdata, IBlockData iblockdata1, IBlockData iblockdata2, IBlockData iblockdata3) {
        return new CarverDebugSettings(false, iblockdata, iblockdata1, iblockdata2, iblockdata3);
    }

    public static CarverDebugSettings of(boolean flag, IBlockData iblockdata) {
        return new CarverDebugSettings(flag, iblockdata, CarverDebugSettings.DEFAULT.getWaterState(), CarverDebugSettings.DEFAULT.getLavaState(), CarverDebugSettings.DEFAULT.getBarrierState());
    }

    private CarverDebugSettings(boolean flag, IBlockData iblockdata, IBlockData iblockdata1, IBlockData iblockdata2, IBlockData iblockdata3) {
        this.debugMode = flag;
        this.airState = iblockdata;
        this.waterState = iblockdata1;
        this.lavaState = iblockdata2;
        this.barrierState = iblockdata3;
    }

    public boolean isDebugMode() {
        return this.debugMode;
    }

    public IBlockData getAirState() {
        return this.airState;
    }

    public IBlockData getWaterState() {
        return this.waterState;
    }

    public IBlockData getLavaState() {
        return this.lavaState;
    }

    public IBlockData getBarrierState() {
        return this.barrierState;
    }
}
