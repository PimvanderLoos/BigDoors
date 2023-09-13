package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class WorldGenSurfaceConfigurationBase implements WorldGenSurfaceConfiguration {

    public static final Codec<WorldGenSurfaceConfigurationBase> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(IBlockData.b.fieldOf("top_material").forGetter((worldgensurfaceconfigurationbase) -> {
            return worldgensurfaceconfigurationbase.b;
        }), IBlockData.b.fieldOf("under_material").forGetter((worldgensurfaceconfigurationbase) -> {
            return worldgensurfaceconfigurationbase.c;
        }), IBlockData.b.fieldOf("underwater_material").forGetter((worldgensurfaceconfigurationbase) -> {
            return worldgensurfaceconfigurationbase.d;
        })).apply(instance, WorldGenSurfaceConfigurationBase::new);
    });
    private final IBlockData b;
    private final IBlockData c;
    private final IBlockData d;

    public WorldGenSurfaceConfigurationBase(IBlockData iblockdata, IBlockData iblockdata1, IBlockData iblockdata2) {
        this.b = iblockdata;
        this.c = iblockdata1;
        this.d = iblockdata2;
    }

    @Override
    public IBlockData a() {
        return this.b;
    }

    @Override
    public IBlockData b() {
        return this.c;
    }

    public IBlockData c() {
        return this.d;
    }
}
