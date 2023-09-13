package net.minecraft.world.level.levelgen.surfacebuilders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.IBlockData;

public class WorldGenSurfaceConfigurationBase implements WorldGenSurfaceConfiguration {

    public static final Codec<WorldGenSurfaceConfigurationBase> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(IBlockData.CODEC.fieldOf("top_material").forGetter((worldgensurfaceconfigurationbase) -> {
            return worldgensurfaceconfigurationbase.topMaterial;
        }), IBlockData.CODEC.fieldOf("under_material").forGetter((worldgensurfaceconfigurationbase) -> {
            return worldgensurfaceconfigurationbase.underMaterial;
        }), IBlockData.CODEC.fieldOf("underwater_material").forGetter((worldgensurfaceconfigurationbase) -> {
            return worldgensurfaceconfigurationbase.underwaterMaterial;
        })).apply(instance, WorldGenSurfaceConfigurationBase::new);
    });
    private final IBlockData topMaterial;
    private final IBlockData underMaterial;
    private final IBlockData underwaterMaterial;

    public WorldGenSurfaceConfigurationBase(IBlockData iblockdata, IBlockData iblockdata1, IBlockData iblockdata2) {
        this.topMaterial = iblockdata;
        this.underMaterial = iblockdata1;
        this.underwaterMaterial = iblockdata2;
    }

    @Override
    public IBlockData a() {
        return this.topMaterial;
    }

    @Override
    public IBlockData b() {
        return this.underMaterial;
    }

    @Override
    public IBlockData c() {
        return this.underwaterMaterial;
    }
}
