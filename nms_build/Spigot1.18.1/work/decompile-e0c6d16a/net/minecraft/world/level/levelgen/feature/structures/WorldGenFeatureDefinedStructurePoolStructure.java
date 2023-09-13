package net.minecraft.world.level.levelgen.feature.structures;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorList;

public abstract class WorldGenFeatureDefinedStructurePoolStructure {

    public static final Codec<WorldGenFeatureDefinedStructurePoolStructure> CODEC = IRegistry.STRUCTURE_POOL_ELEMENT.byNameCodec().dispatch("element_type", WorldGenFeatureDefinedStructurePoolStructure::getType, WorldGenFeatureDefinedStructurePools::codec);
    @Nullable
    private volatile WorldGenFeatureDefinedStructurePoolTemplate.Matching projection;

    protected static <E extends WorldGenFeatureDefinedStructurePoolStructure> RecordCodecBuilder<E, WorldGenFeatureDefinedStructurePoolTemplate.Matching> projectionCodec() {
        return WorldGenFeatureDefinedStructurePoolTemplate.Matching.CODEC.fieldOf("projection").forGetter(WorldGenFeatureDefinedStructurePoolStructure::getProjection);
    }

    protected WorldGenFeatureDefinedStructurePoolStructure(WorldGenFeatureDefinedStructurePoolTemplate.Matching worldgenfeaturedefinedstructurepooltemplate_matching) {
        this.projection = worldgenfeaturedefinedstructurepooltemplate_matching;
    }

    public abstract BaseBlockPosition getSize(DefinedStructureManager definedstructuremanager, EnumBlockRotation enumblockrotation);

    public abstract List<DefinedStructure.BlockInfo> getShuffledJigsawBlocks(DefinedStructureManager definedstructuremanager, BlockPosition blockposition, EnumBlockRotation enumblockrotation, Random random);

    public abstract StructureBoundingBox getBoundingBox(DefinedStructureManager definedstructuremanager, BlockPosition blockposition, EnumBlockRotation enumblockrotation);

    public abstract boolean place(DefinedStructureManager definedstructuremanager, GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, BlockPosition blockposition, BlockPosition blockposition1, EnumBlockRotation enumblockrotation, StructureBoundingBox structureboundingbox, Random random, boolean flag);

    public abstract WorldGenFeatureDefinedStructurePools<?> getType();

    public void handleDataMarker(GeneratorAccess generatoraccess, DefinedStructure.BlockInfo definedstructure_blockinfo, BlockPosition blockposition, EnumBlockRotation enumblockrotation, Random random, StructureBoundingBox structureboundingbox) {}

    public WorldGenFeatureDefinedStructurePoolStructure setProjection(WorldGenFeatureDefinedStructurePoolTemplate.Matching worldgenfeaturedefinedstructurepooltemplate_matching) {
        this.projection = worldgenfeaturedefinedstructurepooltemplate_matching;
        return this;
    }

    public WorldGenFeatureDefinedStructurePoolTemplate.Matching getProjection() {
        WorldGenFeatureDefinedStructurePoolTemplate.Matching worldgenfeaturedefinedstructurepooltemplate_matching = this.projection;

        if (worldgenfeaturedefinedstructurepooltemplate_matching == null) {
            throw new IllegalStateException();
        } else {
            return worldgenfeaturedefinedstructurepooltemplate_matching;
        }
    }

    public int getGroundLevelDelta() {
        return 1;
    }

    public static Function<WorldGenFeatureDefinedStructurePoolTemplate.Matching, WorldGenFeatureDefinedStructurePoolEmpty> empty() {
        return (worldgenfeaturedefinedstructurepooltemplate_matching) -> {
            return WorldGenFeatureDefinedStructurePoolEmpty.INSTANCE;
        };
    }

    public static Function<WorldGenFeatureDefinedStructurePoolTemplate.Matching, WorldGenFeatureDefinedStructurePoolLegacySingle> legacy(String s) {
        return (worldgenfeaturedefinedstructurepooltemplate_matching) -> {
            return new WorldGenFeatureDefinedStructurePoolLegacySingle(Either.left(new MinecraftKey(s)), () -> {
                return ProcessorLists.EMPTY;
            }, worldgenfeaturedefinedstructurepooltemplate_matching);
        };
    }

    public static Function<WorldGenFeatureDefinedStructurePoolTemplate.Matching, WorldGenFeatureDefinedStructurePoolLegacySingle> legacy(String s, ProcessorList processorlist) {
        return (worldgenfeaturedefinedstructurepooltemplate_matching) -> {
            return new WorldGenFeatureDefinedStructurePoolLegacySingle(Either.left(new MinecraftKey(s)), () -> {
                return processorlist;
            }, worldgenfeaturedefinedstructurepooltemplate_matching);
        };
    }

    public static Function<WorldGenFeatureDefinedStructurePoolTemplate.Matching, WorldGenFeatureDefinedStructurePoolSingle> single(String s) {
        return (worldgenfeaturedefinedstructurepooltemplate_matching) -> {
            return new WorldGenFeatureDefinedStructurePoolSingle(Either.left(new MinecraftKey(s)), () -> {
                return ProcessorLists.EMPTY;
            }, worldgenfeaturedefinedstructurepooltemplate_matching);
        };
    }

    public static Function<WorldGenFeatureDefinedStructurePoolTemplate.Matching, WorldGenFeatureDefinedStructurePoolSingle> single(String s, ProcessorList processorlist) {
        return (worldgenfeaturedefinedstructurepooltemplate_matching) -> {
            return new WorldGenFeatureDefinedStructurePoolSingle(Either.left(new MinecraftKey(s)), () -> {
                return processorlist;
            }, worldgenfeaturedefinedstructurepooltemplate_matching);
        };
    }

    public static Function<WorldGenFeatureDefinedStructurePoolTemplate.Matching, WorldGenFeatureDefinedStructurePoolFeature> feature(PlacedFeature placedfeature) {
        return (worldgenfeaturedefinedstructurepooltemplate_matching) -> {
            return new WorldGenFeatureDefinedStructurePoolFeature(() -> {
                return placedfeature;
            }, worldgenfeaturedefinedstructurepooltemplate_matching);
        };
    }

    public static Function<WorldGenFeatureDefinedStructurePoolTemplate.Matching, WorldGenFeatureDefinedStructurePoolList> list(List<Function<WorldGenFeatureDefinedStructurePoolTemplate.Matching, ? extends WorldGenFeatureDefinedStructurePoolStructure>> list) {
        return (worldgenfeaturedefinedstructurepooltemplate_matching) -> {
            return new WorldGenFeatureDefinedStructurePoolList((List) list.stream().map((function) -> {
                return (WorldGenFeatureDefinedStructurePoolStructure) function.apply(worldgenfeaturedefinedstructurepooltemplate_matching);
            }).collect(Collectors.toList()), worldgenfeaturedefinedstructurepooltemplate_matching);
        };
    }
}
