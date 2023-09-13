package net.minecraft.world.level.levelgen.feature.structures;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorList;

public abstract class WorldGenFeatureDefinedStructurePoolStructure {

    public static final Codec<WorldGenFeatureDefinedStructurePoolStructure> e = IRegistry.STRUCTURE_POOL_ELEMENT.dispatch("element_type", WorldGenFeatureDefinedStructurePoolStructure::a, WorldGenFeatureDefinedStructurePools::codec);
    @Nullable
    private volatile WorldGenFeatureDefinedStructurePoolTemplate.Matching a;

    protected static <E extends WorldGenFeatureDefinedStructurePoolStructure> RecordCodecBuilder<E, WorldGenFeatureDefinedStructurePoolTemplate.Matching> d() {
        return WorldGenFeatureDefinedStructurePoolTemplate.Matching.c.fieldOf("projection").forGetter(WorldGenFeatureDefinedStructurePoolStructure::e);
    }

    protected WorldGenFeatureDefinedStructurePoolStructure(WorldGenFeatureDefinedStructurePoolTemplate.Matching worldgenfeaturedefinedstructurepooltemplate_matching) {
        this.a = worldgenfeaturedefinedstructurepooltemplate_matching;
    }

    public abstract List<DefinedStructure.BlockInfo> a(DefinedStructureManager definedstructuremanager, BlockPosition blockposition, EnumBlockRotation enumblockrotation, Random random);

    public abstract StructureBoundingBox a(DefinedStructureManager definedstructuremanager, BlockPosition blockposition, EnumBlockRotation enumblockrotation);

    public abstract boolean a(DefinedStructureManager definedstructuremanager, GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, BlockPosition blockposition, BlockPosition blockposition1, EnumBlockRotation enumblockrotation, StructureBoundingBox structureboundingbox, Random random, boolean flag);

    public abstract WorldGenFeatureDefinedStructurePools<?> a();

    public void a(GeneratorAccess generatoraccess, DefinedStructure.BlockInfo definedstructure_blockinfo, BlockPosition blockposition, EnumBlockRotation enumblockrotation, Random random, StructureBoundingBox structureboundingbox) {}

    public WorldGenFeatureDefinedStructurePoolStructure a(WorldGenFeatureDefinedStructurePoolTemplate.Matching worldgenfeaturedefinedstructurepooltemplate_matching) {
        this.a = worldgenfeaturedefinedstructurepooltemplate_matching;
        return this;
    }

    public WorldGenFeatureDefinedStructurePoolTemplate.Matching e() {
        WorldGenFeatureDefinedStructurePoolTemplate.Matching worldgenfeaturedefinedstructurepooltemplate_matching = this.a;

        if (worldgenfeaturedefinedstructurepooltemplate_matching == null) {
            throw new IllegalStateException();
        } else {
            return worldgenfeaturedefinedstructurepooltemplate_matching;
        }
    }

    public int f() {
        return 1;
    }

    public static Function<WorldGenFeatureDefinedStructurePoolTemplate.Matching, WorldGenFeatureDefinedStructurePoolEmpty> g() {
        return (worldgenfeaturedefinedstructurepooltemplate_matching) -> {
            return WorldGenFeatureDefinedStructurePoolEmpty.b;
        };
    }

    public static Function<WorldGenFeatureDefinedStructurePoolTemplate.Matching, WorldGenFeatureDefinedStructurePoolLegacySingle> a(String s) {
        return (worldgenfeaturedefinedstructurepooltemplate_matching) -> {
            return new WorldGenFeatureDefinedStructurePoolLegacySingle(Either.left(new MinecraftKey(s)), () -> {
                return ProcessorLists.a;
            }, worldgenfeaturedefinedstructurepooltemplate_matching);
        };
    }

    public static Function<WorldGenFeatureDefinedStructurePoolTemplate.Matching, WorldGenFeatureDefinedStructurePoolLegacySingle> a(String s, ProcessorList processorlist) {
        return (worldgenfeaturedefinedstructurepooltemplate_matching) -> {
            return new WorldGenFeatureDefinedStructurePoolLegacySingle(Either.left(new MinecraftKey(s)), () -> {
                return processorlist;
            }, worldgenfeaturedefinedstructurepooltemplate_matching);
        };
    }

    public static Function<WorldGenFeatureDefinedStructurePoolTemplate.Matching, WorldGenFeatureDefinedStructurePoolSingle> b(String s) {
        return (worldgenfeaturedefinedstructurepooltemplate_matching) -> {
            return new WorldGenFeatureDefinedStructurePoolSingle(Either.left(new MinecraftKey(s)), () -> {
                return ProcessorLists.a;
            }, worldgenfeaturedefinedstructurepooltemplate_matching);
        };
    }

    public static Function<WorldGenFeatureDefinedStructurePoolTemplate.Matching, WorldGenFeatureDefinedStructurePoolSingle> b(String s, ProcessorList processorlist) {
        return (worldgenfeaturedefinedstructurepooltemplate_matching) -> {
            return new WorldGenFeatureDefinedStructurePoolSingle(Either.left(new MinecraftKey(s)), () -> {
                return processorlist;
            }, worldgenfeaturedefinedstructurepooltemplate_matching);
        };
    }

    public static Function<WorldGenFeatureDefinedStructurePoolTemplate.Matching, WorldGenFeatureDefinedStructurePoolFeature> a(WorldGenFeatureConfigured<?, ?> worldgenfeatureconfigured) {
        return (worldgenfeaturedefinedstructurepooltemplate_matching) -> {
            return new WorldGenFeatureDefinedStructurePoolFeature(() -> {
                return worldgenfeatureconfigured;
            }, worldgenfeaturedefinedstructurepooltemplate_matching);
        };
    }

    public static Function<WorldGenFeatureDefinedStructurePoolTemplate.Matching, WorldGenFeatureDefinedStructurePoolList> a(List<Function<WorldGenFeatureDefinedStructurePoolTemplate.Matching, ? extends WorldGenFeatureDefinedStructurePoolStructure>> list) {
        return (worldgenfeaturedefinedstructurepooltemplate_matching) -> {
            return new WorldGenFeatureDefinedStructurePoolList((List) list.stream().map((function) -> {
                return (WorldGenFeatureDefinedStructurePoolStructure) function.apply(worldgenfeaturedefinedstructurepooltemplate_matching);
            }).collect(Collectors.toList()), worldgenfeaturedefinedstructurepooltemplate_matching);
        };
    }
}
