package net.minecraft.server;

import javax.annotation.Nullable;

public class WorldProviderHell extends WorldProvider {

    private static final Vec3D f = new Vec3D(0.20000000298023224D, 0.029999999329447746D, 0.029999999329447746D);

    public WorldProviderHell(World world, DimensionManager dimensionmanager) {
        super(world, dimensionmanager, 0.1F);
        this.c = true;
        this.d = true;
    }

    @Override
    public ChunkGenerator<?> getChunkGenerator() {
        GeneratorSettingsNether generatorsettingsnether = (GeneratorSettingsNether) ChunkGeneratorType.b.a();

        generatorsettingsnether.a(Blocks.NETHERRACK.getBlockData());
        generatorsettingsnether.b(Blocks.LAVA.getBlockData());
        return ChunkGeneratorType.b.create(this.b, BiomeLayout.b.a((BiomeLayoutConfiguration) ((BiomeLayoutFixedConfiguration) BiomeLayout.b.a(this.b.getWorldData())).a(Biomes.NETHER)), generatorsettingsnether);
    }

    @Override
    public boolean isOverworld() {
        return false;
    }

    @Nullable
    @Override
    public BlockPosition a(ChunkCoordIntPair chunkcoordintpair, boolean flag) {
        return null;
    }

    @Nullable
    @Override
    public BlockPosition a(int i, int j, boolean flag) {
        return null;
    }

    @Override
    public float a(long i, float f) {
        return 0.5F;
    }

    @Override
    public boolean canRespawn() {
        return false;
    }

    @Override
    public WorldBorder getWorldBorder() {
        return new WorldBorder() {
            @Override
            public double getCenterX() {
                return super.getCenterX() / 8.0D;
            }

            @Override
            public double getCenterZ() {
                return super.getCenterZ() / 8.0D;
            }
        };
    }

    @Override
    public DimensionManager getDimensionManager() {
        return DimensionManager.NETHER;
    }
}
