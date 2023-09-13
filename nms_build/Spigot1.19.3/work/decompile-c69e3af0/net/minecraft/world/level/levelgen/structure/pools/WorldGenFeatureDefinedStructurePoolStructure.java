package net.minecraft.world.level.levelgen.structure.pools;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public abstract class WorldGenFeatureDefinedStructurePoolStructure {

    public static final Codec<WorldGenFeatureDefinedStructurePoolStructure> CODEC = BuiltInRegistries.STRUCTURE_POOL_ELEMENT.byNameCodec().dispatch("element_type", WorldGenFeatureDefinedStructurePoolStructure::getType, WorldGenFeatureDefinedStructurePools::codec);
    private static final Holder<ProcessorList> EMPTY = Holder.direct(new ProcessorList(List.of()));
    @Nullable
    private volatile WorldGenFeatureDefinedStructurePoolTemplate.Matching projection;

    protected static <E extends WorldGenFeatureDefinedStructurePoolStructure> RecordCodecBuilder<E, WorldGenFeatureDefinedStructurePoolTemplate.Matching> projectionCodec() {
        return WorldGenFeatureDefinedStructurePoolTemplate.Matching.CODEC.fieldOf("projection").forGetter(WorldGenFeatureDefinedStructurePoolStructure::getProjection);
    }

    protected WorldGenFeatureDefinedStructurePoolStructure(WorldGenFeatureDefinedStructurePoolTemplate.Matching worldgenfeaturedefinedstructurepooltemplate_matching) {
        this.projection = worldgenfeaturedefinedstructurepooltemplate_matching;
    }

    public abstract BaseBlockPosition getSize(StructureTemplateManager structuretemplatemanager, EnumBlockRotation enumblockrotation);

    public abstract List<DefinedStructure.BlockInfo> getShuffledJigsawBlocks(StructureTemplateManager structuretemplatemanager, BlockPosition blockposition, EnumBlockRotation enumblockrotation, RandomSource randomsource);

    public abstract StructureBoundingBox getBoundingBox(StructureTemplateManager structuretemplatemanager, BlockPosition blockposition, EnumBlockRotation enumblockrotation);

    public abstract boolean place(StructureTemplateManager structuretemplatemanager, GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, BlockPosition blockposition, BlockPosition blockposition1, EnumBlockRotation enumblockrotation, StructureBoundingBox structureboundingbox, RandomSource randomsource, boolean flag);

    public abstract WorldGenFeatureDefinedStructurePools<?> getType();

    public void handleDataMarker(GeneratorAccess generatoraccess, DefinedStructure.BlockInfo definedstructure_blockinfo, BlockPosition blockposition, EnumBlockRotation enumblockrotation, RandomSource randomsource, StructureBoundingBox structureboundingbox) {}

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
            return new WorldGenFeatureDefinedStructurePoolLegacySingle(Either.left(new MinecraftKey(s)), WorldGenFeatureDefinedStructurePoolStructure.EMPTY, worldgenfeaturedefinedstructurepooltemplate_matching);
        };
    }

    public static Function<WorldGenFeatureDefinedStructurePoolTemplate.Matching, WorldGenFeatureDefinedStructurePoolLegacySingle> legacy(String s, Holder<ProcessorList> holder) {
        return (worldgenfeaturedefinedstructurepooltemplate_matching) -> {
            return new WorldGenFeatureDefinedStructurePoolLegacySingle(Either.left(new MinecraftKey(s)), holder, worldgenfeaturedefinedstructurepooltemplate_matching);
        };
    }

    public static Function<WorldGenFeatureDefinedStructurePoolTemplate.Matching, WorldGenFeatureDefinedStructurePoolSingle> single(String s) {
        return (worldgenfeaturedefinedstructurepooltemplate_matching) -> {
            return new WorldGenFeatureDefinedStructurePoolSingle(Either.left(new MinecraftKey(s)), WorldGenFeatureDefinedStructurePoolStructure.EMPTY, worldgenfeaturedefinedstructurepooltemplate_matching);
        };
    }

    public static Function<WorldGenFeatureDefinedStructurePoolTemplate.Matching, WorldGenFeatureDefinedStructurePoolSingle> single(String s, Holder<ProcessorList> holder) {
        return (worldgenfeaturedefinedstructurepooltemplate_matching) -> {
            return new WorldGenFeatureDefinedStructurePoolSingle(Either.left(new MinecraftKey(s)), holder, worldgenfeaturedefinedstructurepooltemplate_matching);
        };
    }

    public static Function<WorldGenFeatureDefinedStructurePoolTemplate.Matching, WorldGenFeatureDefinedStructurePoolFeature> feature(Holder<PlacedFeature> holder) {
        return (worldgenfeaturedefinedstructurepooltemplate_matching) -> {
            return new WorldGenFeatureDefinedStructurePoolFeature(holder, worldgenfeaturedefinedstructurepooltemplate_matching);
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
