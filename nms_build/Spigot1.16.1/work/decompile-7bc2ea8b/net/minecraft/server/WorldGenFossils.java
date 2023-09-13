package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;

public class WorldGenFossils extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    private static final MinecraftKey a = new MinecraftKey("fossil/spine_1");
    private static final MinecraftKey ac = new MinecraftKey("fossil/spine_2");
    private static final MinecraftKey ad = new MinecraftKey("fossil/spine_3");
    private static final MinecraftKey ae = new MinecraftKey("fossil/spine_4");
    private static final MinecraftKey af = new MinecraftKey("fossil/spine_1_coal");
    private static final MinecraftKey ag = new MinecraftKey("fossil/spine_2_coal");
    private static final MinecraftKey ah = new MinecraftKey("fossil/spine_3_coal");
    private static final MinecraftKey ai = new MinecraftKey("fossil/spine_4_coal");
    private static final MinecraftKey aj = new MinecraftKey("fossil/skull_1");
    private static final MinecraftKey ak = new MinecraftKey("fossil/skull_2");
    private static final MinecraftKey al = new MinecraftKey("fossil/skull_3");
    private static final MinecraftKey am = new MinecraftKey("fossil/skull_4");
    private static final MinecraftKey an = new MinecraftKey("fossil/skull_1_coal");
    private static final MinecraftKey ao = new MinecraftKey("fossil/skull_2_coal");
    private static final MinecraftKey ap = new MinecraftKey("fossil/skull_3_coal");
    private static final MinecraftKey aq = new MinecraftKey("fossil/skull_4_coal");
    private static final MinecraftKey[] ar = new MinecraftKey[]{WorldGenFossils.a, WorldGenFossils.ac, WorldGenFossils.ad, WorldGenFossils.ae, WorldGenFossils.aj, WorldGenFossils.ak, WorldGenFossils.al, WorldGenFossils.am};
    private static final MinecraftKey[] as = new MinecraftKey[]{WorldGenFossils.af, WorldGenFossils.ag, WorldGenFossils.ah, WorldGenFossils.ai, WorldGenFossils.an, WorldGenFossils.ao, WorldGenFossils.ap, WorldGenFossils.aq};

    public WorldGenFossils(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        EnumBlockRotation enumblockrotation = EnumBlockRotation.a(random);
        int i = random.nextInt(WorldGenFossils.ar.length);
        DefinedStructureManager definedstructuremanager = ((WorldServer) generatoraccessseed.getMinecraftWorld()).getMinecraftServer().getDefinedStructureManager();
        DefinedStructure definedstructure = definedstructuremanager.a(WorldGenFossils.ar[i]);
        DefinedStructure definedstructure1 = definedstructuremanager.a(WorldGenFossils.as[i]);
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(blockposition);
        StructureBoundingBox structureboundingbox = new StructureBoundingBox(chunkcoordintpair.d(), 0, chunkcoordintpair.e(), chunkcoordintpair.f(), 256, chunkcoordintpair.g());
        DefinedStructureInfo definedstructureinfo = (new DefinedStructureInfo()).a(enumblockrotation).a(structureboundingbox).a(random).a((DefinedStructureProcessor) DefinedStructureProcessorBlockIgnore.d);
        BlockPosition blockposition1 = definedstructure.a(enumblockrotation);
        int j = random.nextInt(16 - blockposition1.getX());
        int k = random.nextInt(16 - blockposition1.getZ());
        int l = 256;

        int i1;

        for (i1 = 0; i1 < blockposition1.getX(); ++i1) {
            for (int j1 = 0; j1 < blockposition1.getZ(); ++j1) {
                l = Math.min(l, generatoraccessseed.a(HeightMap.Type.OCEAN_FLOOR_WG, blockposition.getX() + i1 + j, blockposition.getZ() + j1 + k));
            }
        }

        i1 = Math.max(l - 15 - random.nextInt(10), 10);
        BlockPosition blockposition2 = definedstructure.a(blockposition.b(j, i1, k), EnumBlockMirror.NONE, enumblockrotation);
        DefinedStructureProcessorRotation definedstructureprocessorrotation = new DefinedStructureProcessorRotation(0.9F);

        definedstructureinfo.b().a((DefinedStructureProcessor) definedstructureprocessorrotation);
        definedstructure.a(generatoraccessseed, blockposition2, blockposition2, definedstructureinfo, random, 4);
        definedstructureinfo.b((DefinedStructureProcessor) definedstructureprocessorrotation);
        DefinedStructureProcessorRotation definedstructureprocessorrotation1 = new DefinedStructureProcessorRotation(0.1F);

        definedstructureinfo.b().a((DefinedStructureProcessor) definedstructureprocessorrotation1);
        definedstructure1.a(generatoraccessseed, blockposition2, blockposition2, definedstructureinfo, random, 4);
        return true;
    }
}
