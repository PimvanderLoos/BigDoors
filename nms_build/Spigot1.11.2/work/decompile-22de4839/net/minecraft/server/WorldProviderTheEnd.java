package net.minecraft.server;

import javax.annotation.Nullable;

public class WorldProviderTheEnd extends WorldProvider {

    private EnderDragonBattle h;

    public WorldProviderTheEnd() {}

    public void b() {
        this.c = new WorldChunkManagerHell(Biomes.k);
        NBTTagCompound nbttagcompound = this.b.getWorldData().a(DimensionManager.THE_END);

        this.h = this.b instanceof WorldServer ? new EnderDragonBattle((WorldServer) this.b, nbttagcompound.getCompound("DragonFight")) : null;
    }

    public ChunkGenerator getChunkGenerator() {
        return new ChunkProviderTheEnd(this.b, this.b.getWorldData().shouldGenerateMapFeatures(), this.b.getSeed(), this.h());
    }

    public float a(long i, float f) {
        return 0.0F;
    }

    public boolean e() {
        return false;
    }

    public boolean d() {
        return false;
    }

    public boolean canSpawn(int i, int j) {
        return this.b.c(new BlockPosition(i, 0, j)).getMaterial().isSolid();
    }

    public BlockPosition h() {
        return new BlockPosition(100, 50, 0);
    }

    public int getSeaLevel() {
        return 50;
    }

    public DimensionManager getDimensionManager() {
        return DimensionManager.THE_END;
    }

    public void r() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        if (this.h != null) {
            nbttagcompound.set("DragonFight", this.h.a());
        }

        this.b.getWorldData().a(DimensionManager.THE_END, nbttagcompound);
    }

    public void s() {
        if (this.h != null) {
            this.h.b();
        }

    }

    @Nullable
    public EnderDragonBattle t() {
        return this.h;
    }
}
