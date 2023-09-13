package net.minecraft.server;

public class WorldGenSurfaceConfigurationBase implements WorldGenSurfaceConfiguration {

    private final IBlockData a;
    private final IBlockData b;
    private final IBlockData c;

    public WorldGenSurfaceConfigurationBase(IBlockData iblockdata, IBlockData iblockdata1, IBlockData iblockdata2) {
        this.a = iblockdata;
        this.b = iblockdata1;
        this.c = iblockdata2;
    }

    public IBlockData a() {
        return this.a;
    }

    public IBlockData b() {
        return this.b;
    }

    public IBlockData c() {
        return this.c;
    }
}
