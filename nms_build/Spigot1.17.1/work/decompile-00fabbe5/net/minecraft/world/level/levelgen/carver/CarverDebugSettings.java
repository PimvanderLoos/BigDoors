package net.minecraft.world.level.levelgen.carver;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class CarverDebugSettings {

    public static final CarverDebugSettings DEFAULT = new CarverDebugSettings(false, Blocks.ACACIA_BUTTON.getBlockData(), Blocks.CANDLE.getBlockData(), Blocks.ORANGE_STAINED_GLASS.getBlockData(), Blocks.GLASS.getBlockData());
    public static final Codec<CarverDebugSettings> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.BOOL.optionalFieldOf("debug_mode", false).forGetter(CarverDebugSettings::a), IBlockData.CODEC.optionalFieldOf("air_state", CarverDebugSettings.DEFAULT.b()).forGetter(CarverDebugSettings::b), IBlockData.CODEC.optionalFieldOf("water_state", CarverDebugSettings.DEFAULT.b()).forGetter(CarverDebugSettings::c), IBlockData.CODEC.optionalFieldOf("lava_state", CarverDebugSettings.DEFAULT.b()).forGetter(CarverDebugSettings::d), IBlockData.CODEC.optionalFieldOf("barrier_state", CarverDebugSettings.DEFAULT.b()).forGetter(CarverDebugSettings::e)).apply(instance, CarverDebugSettings::new);
    });
    private boolean debugMode;
    private final IBlockData airState;
    private final IBlockData waterState;
    private final IBlockData lavaState;
    private final IBlockData barrierState;

    public static CarverDebugSettings a(boolean flag, IBlockData iblockdata, IBlockData iblockdata1, IBlockData iblockdata2, IBlockData iblockdata3) {
        return new CarverDebugSettings(flag, iblockdata, iblockdata1, iblockdata2, iblockdata3);
    }

    public static CarverDebugSettings a(IBlockData iblockdata, IBlockData iblockdata1, IBlockData iblockdata2, IBlockData iblockdata3) {
        return new CarverDebugSettings(false, iblockdata, iblockdata1, iblockdata2, iblockdata3);
    }

    public static CarverDebugSettings a(boolean flag, IBlockData iblockdata) {
        return new CarverDebugSettings(flag, iblockdata, CarverDebugSettings.DEFAULT.c(), CarverDebugSettings.DEFAULT.d(), CarverDebugSettings.DEFAULT.e());
    }

    private CarverDebugSettings(boolean flag, IBlockData iblockdata, IBlockData iblockdata1, IBlockData iblockdata2, IBlockData iblockdata3) {
        this.debugMode = flag;
        this.airState = iblockdata;
        this.waterState = iblockdata1;
        this.lavaState = iblockdata2;
        this.barrierState = iblockdata3;
    }

    public boolean a() {
        return this.debugMode;
    }

    public IBlockData b() {
        return this.airState;
    }

    public IBlockData c() {
        return this.waterState;
    }

    public IBlockData d() {
        return this.lavaState;
    }

    public IBlockData e() {
        return this.barrierState;
    }
}
