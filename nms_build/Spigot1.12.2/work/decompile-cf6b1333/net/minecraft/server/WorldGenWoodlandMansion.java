package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class WorldGenWoodlandMansion extends StructureGenerator {

    private final int b = 80;
    private final int d = 20;
    public static final List<BiomeBase> a = Arrays.asList(new BiomeBase[] { Biomes.E, Biomes.ab});
    private final ChunkProviderGenerate h;

    public WorldGenWoodlandMansion(ChunkProviderGenerate chunkprovidergenerate) {
        this.h = chunkprovidergenerate;
    }

    public String a() {
        return "Mansion";
    }

    protected boolean a(int i, int j) {
        int k = i;
        int l = j;

        if (i < 0) {
            k = i - 79;
        }

        if (j < 0) {
            l = j - 79;
        }

        int i1 = k / 80;
        int j1 = l / 80;
        Random random = this.g.a(i1, j1, 10387319);

        i1 *= 80;
        j1 *= 80;
        i1 += (random.nextInt(60) + random.nextInt(60)) / 2;
        j1 += (random.nextInt(60) + random.nextInt(60)) / 2;
        if (i == i1 && j == j1) {
            boolean flag = this.g.getWorldChunkManager().a(i * 16 + 8, j * 16 + 8, 32, WorldGenWoodlandMansion.a);

            if (flag) {
                return true;
            }
        }

        return false;
    }

    public BlockPosition getNearestGeneratedFeature(World world, BlockPosition blockposition, boolean flag) {
        this.g = world;
        WorldChunkManager worldchunkmanager = world.getWorldChunkManager();

        return worldchunkmanager.c() && worldchunkmanager.d() != Biomes.E ? null : a(world, this, blockposition, 80, 20, 10387319, true, 100, flag);
    }

    protected StructureStart b(int i, int j) {
        return new WorldGenWoodlandMansion.a(this.g, this.h, this.f, i, j);
    }

    public static class a extends StructureStart {

        private boolean c;

        public a() {}

        public a(World world, ChunkProviderGenerate chunkprovidergenerate, Random random, int i, int j) {
            super(i, j);
            this.a(world, chunkprovidergenerate, random, i, j);
        }

        private void a(World world, ChunkProviderGenerate chunkprovidergenerate, Random random, int i, int j) {
            EnumBlockRotation enumblockrotation = EnumBlockRotation.values()[random.nextInt(EnumBlockRotation.values().length)];
            ChunkSnapshot chunksnapshot = new ChunkSnapshot();

            chunkprovidergenerate.a(i, j, chunksnapshot);
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

            if (k1 < 60) {
                this.c = false;
            } else {
                BlockPosition blockposition = new BlockPosition(i * 16 + 8, k1 + 1, j * 16 + 8);
                LinkedList linkedlist = Lists.newLinkedList();

                WorldGenWoodlandMansionPieces.a(world.getDataManager().h(), blockposition, enumblockrotation, linkedlist, random);
                this.a.addAll(linkedlist);
                this.d();
                this.c = true;
            }
        }

        public void a(World world, Random random, StructureBoundingBox structureboundingbox) {
            super.a(world, random, structureboundingbox);
            int i = this.b.b;

            for (int j = structureboundingbox.a; j <= structureboundingbox.d; ++j) {
                for (int k = structureboundingbox.c; k <= structureboundingbox.f; ++k) {
                    BlockPosition blockposition = new BlockPosition(j, i, k);

                    if (!world.isEmpty(blockposition) && this.b.b((BaseBlockPosition) blockposition)) {
                        boolean flag = false;
                        Iterator iterator = this.a.iterator();

                        while (iterator.hasNext()) {
                            StructurePiece structurepiece = (StructurePiece) iterator.next();

                            if (structurepiece.l.b((BaseBlockPosition) blockposition)) {
                                flag = true;
                                break;
                            }
                        }

                        if (flag) {
                            for (int l = i - 1; l > 1; --l) {
                                BlockPosition blockposition1 = new BlockPosition(j, l, k);

                                if (!world.isEmpty(blockposition1) && !world.getType(blockposition1).getMaterial().isLiquid()) {
                                    break;
                                }

                                world.setTypeAndData(blockposition1, Blocks.COBBLESTONE.getBlockData(), 2);
                            }
                        }
                    }
                }
            }

        }

        public boolean a() {
            return this.c;
        }
    }
}
