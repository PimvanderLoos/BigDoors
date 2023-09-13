package net.minecraft.server;

import java.util.Random;

public class WorldGenFossils extends WorldGenerator {

    private static final MinecraftKey a = new MinecraftKey("fossils/fossil_spine_01");
    private static final MinecraftKey b = new MinecraftKey("fossils/fossil_spine_02");
    private static final MinecraftKey c = new MinecraftKey("fossils/fossil_spine_03");
    private static final MinecraftKey d = new MinecraftKey("fossils/fossil_spine_04");
    private static final MinecraftKey e = new MinecraftKey("fossils/fossil_spine_01_coal");
    private static final MinecraftKey f = new MinecraftKey("fossils/fossil_spine_02_coal");
    private static final MinecraftKey g = new MinecraftKey("fossils/fossil_spine_03_coal");
    private static final MinecraftKey h = new MinecraftKey("fossils/fossil_spine_04_coal");
    private static final MinecraftKey i = new MinecraftKey("fossils/fossil_skull_01");
    private static final MinecraftKey j = new MinecraftKey("fossils/fossil_skull_02");
    private static final MinecraftKey k = new MinecraftKey("fossils/fossil_skull_03");
    private static final MinecraftKey l = new MinecraftKey("fossils/fossil_skull_04");
    private static final MinecraftKey m = new MinecraftKey("fossils/fossil_skull_01_coal");
    private static final MinecraftKey n = new MinecraftKey("fossils/fossil_skull_02_coal");
    private static final MinecraftKey o = new MinecraftKey("fossils/fossil_skull_03_coal");
    private static final MinecraftKey p = new MinecraftKey("fossils/fossil_skull_04_coal");
    private static final MinecraftKey[] q = new MinecraftKey[] { WorldGenFossils.a, WorldGenFossils.b, WorldGenFossils.c, WorldGenFossils.d, WorldGenFossils.i, WorldGenFossils.j, WorldGenFossils.k, WorldGenFossils.l};
    private static final MinecraftKey[] r = new MinecraftKey[] { WorldGenFossils.e, WorldGenFossils.f, WorldGenFossils.g, WorldGenFossils.h, WorldGenFossils.m, WorldGenFossils.n, WorldGenFossils.o, WorldGenFossils.p};

    public WorldGenFossils() {}

    public boolean generate(World world, Random random, BlockPosition blockposition) {
        Random random1 = world.getChunkAtWorldCoords(blockposition).a(987234911L);
        MinecraftServer minecraftserver = world.getMinecraftServer();
        EnumBlockRotation[] aenumblockrotation = EnumBlockRotation.values();
        EnumBlockRotation enumblockrotation = aenumblockrotation[random1.nextInt(aenumblockrotation.length)];
        int i = random1.nextInt(WorldGenFossils.q.length);
        DefinedStructureManager definedstructuremanager = world.getDataManager().h();
        DefinedStructure definedstructure = definedstructuremanager.a(minecraftserver, WorldGenFossils.q[i]);
        DefinedStructure definedstructure1 = definedstructuremanager.a(minecraftserver, WorldGenFossils.r[i]);
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(blockposition);
        StructureBoundingBox structureboundingbox = new StructureBoundingBox(chunkcoordintpair.c(), 0, chunkcoordintpair.d(), chunkcoordintpair.e(), 256, chunkcoordintpair.f());
        DefinedStructureInfo definedstructureinfo = (new DefinedStructureInfo()).a(enumblockrotation).a(structureboundingbox).a(random1);
        BlockPosition blockposition1 = definedstructure.a(enumblockrotation);
        int j = random1.nextInt(16 - blockposition1.getX());
        int k = random1.nextInt(16 - blockposition1.getZ());
        int l = 256;

        int i1;

        for (i1 = 0; i1 < blockposition1.getX(); ++i1) {
            for (int j1 = 0; j1 < blockposition1.getX(); ++j1) {
                l = Math.min(l, world.c(blockposition.getX() + i1 + j, blockposition.getZ() + j1 + k));
            }
        }

        i1 = Math.max(l - 15 - random1.nextInt(10), 10);
        BlockPosition blockposition2 = definedstructure.a(blockposition.a(j, i1, k), EnumBlockMirror.NONE, enumblockrotation);

        definedstructureinfo.a(0.9F);
        definedstructure.a(world, blockposition2, definedstructureinfo, 20);
        definedstructureinfo.a(0.1F);
        definedstructure1.a(world, blockposition2, definedstructureinfo, 20);
        return true;
    }
}
