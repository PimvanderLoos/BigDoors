package net.minecraft.world.level.levelgen.feature.structures;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.BlockPropertyJigsawOrientation;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.BlockJigsaw;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.block.entity.TileEntityJigsaw;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;

public class WorldGenFeatureDefinedStructurePoolFeature extends WorldGenFeatureDefinedStructurePoolStructure {

    public static final Codec<WorldGenFeatureDefinedStructurePoolFeature> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(WorldGenFeatureConfigured.b.fieldOf("feature").forGetter((worldgenfeaturedefinedstructurepoolfeature) -> {
            return worldgenfeaturedefinedstructurepoolfeature.b;
        }), d()).apply(instance, WorldGenFeatureDefinedStructurePoolFeature::new);
    });
    private final Supplier<WorldGenFeatureConfigured<?, ?>> b;
    private final NBTTagCompound c;

    protected WorldGenFeatureDefinedStructurePoolFeature(Supplier<WorldGenFeatureConfigured<?, ?>> supplier, WorldGenFeatureDefinedStructurePoolTemplate.Matching worldgenfeaturedefinedstructurepooltemplate_matching) {
        super(worldgenfeaturedefinedstructurepooltemplate_matching);
        this.b = supplier;
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
        return ((WorldGenFeatureConfigured) this.b.get()).a(generatoraccessseed, chunkgenerator, random, blockposition);
    }

    @Override
    public WorldGenFeatureDefinedStructurePools<?> a() {
        return WorldGenFeatureDefinedStructurePools.c;
    }

    public String toString() {
        return "Feature[" + IRegistry.FEATURE.getKey(((WorldGenFeatureConfigured) this.b.get()).b()) + "]";
    }
}
