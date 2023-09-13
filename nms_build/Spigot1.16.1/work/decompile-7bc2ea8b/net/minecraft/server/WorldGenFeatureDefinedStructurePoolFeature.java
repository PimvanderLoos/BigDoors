package net.minecraft.server;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;

public class WorldGenFeatureDefinedStructurePoolFeature extends WorldGenFeatureDefinedStructurePoolStructure {

    public static final Codec<WorldGenFeatureDefinedStructurePoolFeature> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(WorldGenFeatureConfigured.b.fieldOf("feature").forGetter((worldgenfeaturedefinedstructurepoolfeature) -> {
            return worldgenfeaturedefinedstructurepoolfeature.b;
        }), d()).apply(instance, WorldGenFeatureDefinedStructurePoolFeature::new);
    });
    private final WorldGenFeatureConfigured<?, ?> b;
    private final NBTTagCompound c;

    @Deprecated
    public WorldGenFeatureDefinedStructurePoolFeature(WorldGenFeatureConfigured<?, ?> worldgenfeatureconfigured) {
        this(worldgenfeatureconfigured, WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID);
    }

    private WorldGenFeatureDefinedStructurePoolFeature(WorldGenFeatureConfigured<?, ?> worldgenfeatureconfigured, WorldGenFeatureDefinedStructurePoolTemplate.Matching worldgenfeaturedefinedstructurepooltemplate_matching) {
        super(worldgenfeaturedefinedstructurepooltemplate_matching);
        this.b = worldgenfeatureconfigured;
        this.c = this.b();
    }

    private NBTTagCompound b() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.setString("name", "minecraft:bottom");
        nbttagcompound.setString("final_state", "minecraft:air");
        nbttagcompound.setString("pool", "minecraft:empty");
        nbttagcompound.setString("target", "minecraft:empty");
        nbttagcompound.setString("joint", TileEntityJigsaw.JointType.ROLLABLE.getName());
        return nbttagcompound;
    }

    public BlockPosition a(DefinedStructureManager definedstructuremanager, EnumBlockRotation enumblockrotation) {
        return BlockPosition.ZERO;
    }

    @Override
    public List<DefinedStructure.BlockInfo> a(DefinedStructureManager definedstructuremanager, BlockPosition blockposition, EnumBlockRotation enumblockrotation, Random random) {
        List<DefinedStructure.BlockInfo> list = Lists.newArrayList();

        list.add(new DefinedStructure.BlockInfo(blockposition, (IBlockData) Blocks.JIGSAW.getBlockData().set(BlockJigsaw.a, BlockPropertyJigsawOrientation.a(EnumDirection.DOWN, EnumDirection.SOUTH)), this.c));
        return list;
    }

    @Override
    public StructureBoundingBox a(DefinedStructureManager definedstructuremanager, BlockPosition blockposition, EnumBlockRotation enumblockrotation) {
        BlockPosition blockposition1 = this.a(definedstructuremanager, enumblockrotation);

        return new StructureBoundingBox(blockposition.getX(), blockposition.getY(), blockposition.getZ(), blockposition.getX() + blockposition1.getX(), blockposition.getY() + blockposition1.getY(), blockposition.getZ() + blockposition1.getZ());
    }

    @Override
    public boolean a(DefinedStructureManager definedstructuremanager, GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, BlockPosition blockposition, BlockPosition blockposition1, EnumBlockRotation enumblockrotation, StructureBoundingBox structureboundingbox, Random random, boolean flag) {
        return this.b.a(generatoraccessseed, structuremanager, chunkgenerator, random, blockposition);
    }

    @Override
    public WorldGenFeatureDefinedStructurePools<?> a() {
        return WorldGenFeatureDefinedStructurePools.c;
    }

    public String toString() {
        return "Feature[" + IRegistry.FEATURE.getKey(this.b.d) + "]";
    }
}
