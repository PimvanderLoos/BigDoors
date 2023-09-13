package net.minecraft.server;

import java.util.Random;

public class WorldGenEndCity extends StructureGenerator {

    private final int a = 20;
    private final int b = 11;
    private final ChunkProviderTheEnd d;

    public WorldGenEndCity(ChunkProviderTheEnd chunkprovidertheend) {
        this.d = chunkprovidertheend;
    }

    public String a() {
        return "EndCity";
    }

    protected boolean a(int i, int j) {
        int k = i;
        int l = j;

        if (i < 0) {
            i -= 19;
        }

        if (j < 0) {
            j -= 19;
        }

        int i1 = i / 20;
        int j1 = j / 20;
        Random random = this.g.a(i1, j1, 10387313);

        i1 *= 20;
        j1 *= 20;
        i1 += (random.nextInt(9) + random.nextInt(9)) / 2;
        j1 += (random.nextInt(9) + random.nextInt(9)) / 2;
        if (k == i1 && l == j1 && this.d.c(k, l)) {
            int k1 = b(k, l, this.d);

            return k1 >= 60;
        } else {
            return false;
        }
    }

    protected StructureStart b(int i, int j) {
        return new WorldGenEndCity.Start(this.g, this.d, this.f, i, j);
    }

    public BlockPosition getNearestGeneratedFeature(World world, BlockPosition blockposition, boolean flag) {
        this.g = world;
        return a(world, this, blockposition, 20, 11, 10387313, true, 100, flag);
    }

    private static int b(int i, int j, ChunkProviderTheEnd chunkprovidertheend) {
        Random random = new Random((long) (i + j * 10387313));
        EnumBlockRotation enumblockrotation = EnumBlockRotation.values()[random.nextInt(EnumBlockRotation.values().length)];
        ChunkSnapshot chunksnapshot = new ChunkSnapshot();

        chunkprovidertheend.a(i, j, chunksnapshot);
        byte b0 = 5;
        byte b1 = 5;

        if (enumblockrotation == EnumBlockRotation.CLOCKWISE_90) {
            b0 = -5;
        } else if (enumblockrotation == EnumBlockRotation.CLOCKWISE_180) {
            b0 = -5;
            b1 = -5;
        } else if (enumblockrotation == EnumBlockRotation.COUNTERCLOCKWISE_90) {
            b1 = -5;
        }

        int k = chunksnapshot.a(7, 7);
        int l = chunksnapshot.a(7, 7 + b1);
        int i1 = chunksnapshot.a(7 + b0, 7);
        int j1 = chunksnapshot.a(7 + b0, 7 + b1);
        int k1 = Math.min(Math.min(k, l), Math.min(i1, j1));

        return k1;
    }

    public static class Start extends StructureStart {

        private boolean c;

        public Start() {}

        public Start(World world, ChunkProviderTheEnd chunkprovidertheend, Random random, int i, int j) {
            super(i, j);
            this.a(world, chunkprovidertheend, random, i, j);
        }

        private void a(World world, ChunkProviderTheEnd chunkprovidertheend, Random random, int i, int j) {
            Random random1 = new Random((long) (i + j * 10387313));
            EnumBlockRotation enumblockrotation = EnumBlockRotation.values()[random1.nextInt(EnumBlockRotation.values().length)];
            int k = WorldGenEndCity.b(i, j, chunkprovidertheend);

            if (k < 60) {
                this.c = false;
            } else {
                BlockPosition blockposition = new BlockPosition(i * 16 + 8, k, j * 16 + 8);

                WorldGenEndCityPieces.a(world.getDataManager().h(), blockposition, enumblockrotation, this.a, random);
                this.d();
                this.c = true;
            }
        }

        public boolean a() {
            return this.c;
        }
    }
}
