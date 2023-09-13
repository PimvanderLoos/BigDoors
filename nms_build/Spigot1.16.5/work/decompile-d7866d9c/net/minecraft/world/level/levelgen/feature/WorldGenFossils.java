package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.EnumBlockMirror;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureInfo;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessorBlockIgnore;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessorRotation;

public class WorldGenFossils extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    private static final MinecraftKey a = new MinecraftKey("fossil/spine_1");
    private static final MinecraftKey ab = new MinecraftKey("fossil/spine_2");
    private static final MinecraftKey ac = new MinecraftKey("fossil/spine_3");
    private static final MinecraftKey ad = new MinecraftKey("fossil/spine_4");
    private static final MinecraftKey ae = new MinecraftKey("fossil/spine_1_coal");
    private static final MinecraftKey af = new MinecraftKey("fossil/spine_2_coal");
    private static final MinecraftKey ag = new MinecraftKey("fossil/spine_3_coal");
    private static final MinecraftKey ah = new MinecraftKey("fossil/spine_4_coal");
    private static final MinecraftKey ai = new MinecraftKey("fossil/skull_1");
    private static final MinecraftKey aj = new MinecraftKey("fossil/skull_2");
    private static final MinecraftKey ak = new MinecraftKey("fossil/skull_3");
    private static final MinecraftKey al = new MinecraftKey("fossil/skull_4");
    private static final MinecraftKey am = new MinecraftKey("fossil/skull_1_coal");
    private static final MinecraftKey an = new MinecraftKey("fossil/skull_2_coal");
    private static final MinecraftKey ao = new MinecraftKey("fossil/skull_3_coal");
    private static final MinecraftKey ap = new MinecraftKey("fossil/skull_4_coal");
    private static final MinecraftKey[] aq = new MinecraftKey[]{WorldGenFossils.a, WorldGenFossils.ab, WorldGenFossils.ac, WorldGenFossils.ad, WorldGenFossils.ai, WorldGenFossils.aj, WorldGenFossils.ak, WorldGenFossils.al};
    private static final MinecraftKey[] ar = new MinecraftKey[]{WorldGenFossils.ae, WorldGenFossils.af, WorldGenFossils.ag, WorldGenFossils.ah, WorldGenFossils.am, WorldGenFossils.an, WorldGenFossils.ao, WorldGenFossils.ap};

    public WorldGenFossils(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        EnumBlockRotation enumblockrotation = EnumBlockRotation.a(random);
        int i = random.nextInt(WorldGenFossils.aq.length);
        DefinedStructureManager definedstructuremanager = generatoraccessseed.getMinecraftWorld().getMinecraftServer().getDefinedStructureManager();
        DefinedStructure definedstructure = definedstructuremanager.a(WorldGenFossils.aq[i]);
        DefinedStructure definedstructure1 = definedstructuremanager.a(WorldGenFossils.ar[i]);
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
