package net.minecraft.world.level.chunk;

import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.WorldChunkManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BiomeStorage implements BiomeManager.Provider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final int e = (int) Math.round(Math.log(16.0D) / Math.log(2.0D)) - 2;
    private static final int f = (int) Math.round(Math.log(256.0D) / Math.log(2.0D)) - 2;
    public static final int a = 1 << BiomeStorage.e + BiomeStorage.e + BiomeStorage.f;
    public static final int b = (1 << BiomeStorage.e) - 1;
    public static final int c = (1 << BiomeStorage.f) - 1;
    public final Registry<BiomeBase> registry;
    private final BiomeBase[] h;

    public BiomeStorage(Registry<BiomeBase> registry, BiomeBase[] abiomebase) {
        this.registry = registry;
        this.h = abiomebase;
    }

    private BiomeStorage(Registry<BiomeBase> registry) {
        this(registry, new BiomeBase[BiomeStorage.a]);
    }

    public BiomeStorage(Registry<BiomeBase> registry, ChunkCoordIntPair chunkcoordintpair, WorldChunkManager worldchunkmanager) {
        this(registry);
        int i = chunkcoordintpair.d() >> 2;
        int j = chunkcoordintpair.e() >> 2;

        for (int k = 0; k < this.h.length; ++k) {
            int l = k & BiomeStorage.b;
            int i1 = k >> BiomeStorage.e + BiomeStorage.e & BiomeStorage.c;
            int j1 = k >> BiomeStorage.e & BiomeStorage.b;

            this.h[k] = worldchunkmanager.getBiome(i + l, i1, j + j1);
        }

    }

    public BiomeStorage(Registry<BiomeBase> registry, ChunkCoordIntPair chunkcoordintpair, WorldChunkManager worldchunkmanager, @Nullable int[] aint) {
        this(registry);
        int i = chunkcoordintpair.d() >> 2;
        int j = chunkcoordintpair.e() >> 2;
        int k;
        int l;
        int i1;
        int j1;

        if (aint != null) {
            for (k = 0; k < aint.length; ++k) {
                this.h[k] = (BiomeBase) registry.fromId(aint[k]);
                if (this.h[k] == null) {
                    l = k & BiomeStorage.b;
                    i1 = k >> BiomeStorage.e + BiomeStorage.e & BiomeStorage.c;
                    j1 = k >> BiomeStorage.e & BiomeStorage.b;
                    this.h[k] = worldchunkmanager.getBiome(i + l, i1, j + j1);
                }
            }
        } else {
            for (k = 0; k < this.h.length; ++k) {
                l = k & BiomeStorage.b;
                i1 = k >> BiomeStorage.e + BiomeStorage.e & BiomeStorage.c;
                j1 = k >> BiomeStorage.e & BiomeStorage.b;
                this.h[k] = worldchunkmanager.getBiome(i + l, i1, j + j1);
            }
        }

    }

    public int[] a() {
        int[] aint = new int[this.h.length];

        for (int i = 0; i < this.h.length; ++i) {
            aint[i] = this.registry.a(this.h[i]);
        }

        return aint;
    }

    @Override
    public BiomeBase getBiome(int i, int j, int k) {
        int l = i & BiomeStorage.b;
        int i1 = MathHelper.clamp(j, 0, BiomeStorage.c);
        int j1 = k & BiomeStorage.b;

        return this.h[i1 << BiomeStorage.e + BiomeStorage.e | j1 << BiomeStorage.e | l];
    }
}
