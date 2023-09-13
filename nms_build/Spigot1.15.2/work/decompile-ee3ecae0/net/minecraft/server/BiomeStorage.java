package net.minecraft.server;

import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BiomeStorage implements BiomeManager.Provider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final int e = (int) Math.round(Math.log(16.0D) / Math.log(2.0D)) - 2;
    private static final int f = (int) Math.round(Math.log(256.0D) / Math.log(2.0D)) - 2;
    public static final int a = 1 << BiomeStorage.e + BiomeStorage.e + BiomeStorage.f;
    public static final int b = (1 << BiomeStorage.e) - 1;
    public static final int c = (1 << BiomeStorage.f) - 1;
    private final BiomeBase[] g;

    public BiomeStorage(BiomeBase[] abiomebase) {
        this.g = abiomebase;
    }

    private BiomeStorage() {
        this(new BiomeBase[BiomeStorage.a]);
    }

    public BiomeStorage(PacketDataSerializer packetdataserializer) {
        this();

        for (int i = 0; i < this.g.length; ++i) {
            int j = packetdataserializer.readInt();
            BiomeBase biomebase = (BiomeBase) IRegistry.BIOME.fromId(j);

            if (biomebase == null) {
                BiomeStorage.LOGGER.warn("Received invalid biome id: " + j);
                this.g[i] = Biomes.PLAINS;
            } else {
                this.g[i] = biomebase;
            }
        }

    }

    public BiomeStorage(ChunkCoordIntPair chunkcoordintpair, WorldChunkManager worldchunkmanager) {
        this();
        int i = chunkcoordintpair.d() >> 2;
        int j = chunkcoordintpair.e() >> 2;

        for (int k = 0; k < this.g.length; ++k) {
            int l = k & BiomeStorage.b;
            int i1 = k >> BiomeStorage.e + BiomeStorage.e & BiomeStorage.c;
            int j1 = k >> BiomeStorage.e & BiomeStorage.b;

            this.g[k] = worldchunkmanager.getBiome(i + l, i1, j + j1);
        }

    }

    public BiomeStorage(ChunkCoordIntPair chunkcoordintpair, WorldChunkManager worldchunkmanager, @Nullable int[] aint) {
        this();
        int i = chunkcoordintpair.d() >> 2;
        int j = chunkcoordintpair.e() >> 2;
        int k;
        int l;
        int i1;
        int j1;

        if (aint != null) {
            for (k = 0; k < aint.length; ++k) {
                this.g[k] = (BiomeBase) IRegistry.BIOME.fromId(aint[k]);
                if (this.g[k] == null) {
                    l = k & BiomeStorage.b;
                    i1 = k >> BiomeStorage.e + BiomeStorage.e & BiomeStorage.c;
                    j1 = k >> BiomeStorage.e & BiomeStorage.b;
                    this.g[k] = worldchunkmanager.getBiome(i + l, i1, j + j1);
                }
            }
        } else {
            for (k = 0; k < this.g.length; ++k) {
                l = k & BiomeStorage.b;
                i1 = k >> BiomeStorage.e + BiomeStorage.e & BiomeStorage.c;
                j1 = k >> BiomeStorage.e & BiomeStorage.b;
                this.g[k] = worldchunkmanager.getBiome(i + l, i1, j + j1);
            }
        }

    }

    public int[] a() {
        int[] aint = new int[this.g.length];

        for (int i = 0; i < this.g.length; ++i) {
            aint[i] = IRegistry.BIOME.a((Object) this.g[i]);
        }

        return aint;
    }

    public void a(PacketDataSerializer packetdataserializer) {
        BiomeBase[] abiomebase = this.g;
        int i = abiomebase.length;

        for (int j = 0; j < i; ++j) {
            BiomeBase biomebase = abiomebase[j];

            packetdataserializer.writeInt(IRegistry.BIOME.a((Object) biomebase));
        }

    }

    public BiomeStorage b() {
        return new BiomeStorage((BiomeBase[]) this.g.clone());
    }

    @Override
    public BiomeBase getBiome(int i, int j, int k) {
        int l = i & BiomeStorage.b;
        int i1 = MathHelper.clamp(j, 0, BiomeStorage.c);
        int j1 = k & BiomeStorage.b;

        return this.g[i1 << BiomeStorage.e + BiomeStorage.e | j1 << BiomeStorage.e | l];
    }
}
