package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.WorldGenFeaturePieces;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.WorldGenFeatureDefinedStructureJigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.WorldGenFeatureDefinedStructurePoolTemplate;

public final class JigsawStructure extends Structure {

    public static final int MAX_TOTAL_STRUCTURE_RANGE = 128;
    public static final Codec<JigsawStructure> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(settingsCodec(instance), WorldGenFeatureDefinedStructurePoolTemplate.CODEC.fieldOf("start_pool").forGetter((jigsawstructure) -> {
            return jigsawstructure.startPool;
        }), MinecraftKey.CODEC.optionalFieldOf("start_jigsaw_name").forGetter((jigsawstructure) -> {
            return jigsawstructure.startJigsawName;
        }), Codec.intRange(0, 7).fieldOf("size").forGetter((jigsawstructure) -> {
            return jigsawstructure.maxDepth;
        }), HeightProvider.CODEC.fieldOf("start_height").forGetter((jigsawstructure) -> {
            return jigsawstructure.startHeight;
        }), Codec.BOOL.fieldOf("use_expansion_hack").forGetter((jigsawstructure) -> {
            return jigsawstructure.useExpansionHack;
        }), HeightMap.Type.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter((jigsawstructure) -> {
            return jigsawstructure.projectStartToHeightmap;
        }), Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter((jigsawstructure) -> {
            return jigsawstructure.maxDistanceFromCenter;
        })).apply(instance, JigsawStructure::new);
    }).flatXmap(verifyRange(), verifyRange()).codec();
    private final Holder<WorldGenFeatureDefinedStructurePoolTemplate> startPool;
    private final Optional<MinecraftKey> startJigsawName;
    private final int maxDepth;
    private final HeightProvider startHeight;
    private final boolean useExpansionHack;
    private final Optional<HeightMap.Type> projectStartToHeightmap;
    private final int maxDistanceFromCenter;

    private static Function<JigsawStructure, DataResult<JigsawStructure>> verifyRange() {
        return (jigsawstructure) -> {
            byte b0;

            switch (jigsawstructure.terrainAdaptation()) {
                case NONE:
                    b0 = 0;
                    break;
                case BURY:
                case BEARD_THIN:
                case BEARD_BOX:
                    b0 = 12;
                    break;
                default:
                    throw new IncompatibleClassChangeError();
            }

            byte b1 = b0;

            return jigsawstructure.maxDistanceFromCenter + b1 > 128 ? DataResult.error("Structure size including terrain adaptation must not exceed 128") : DataResult.success(jigsawstructure);
        };
    }

    public JigsawStructure(Structure.c structure_c, Holder<WorldGenFeatureDefinedStructurePoolTemplate> holder, Optional<MinecraftKey> optional, int i, HeightProvider heightprovider, boolean flag, Optional<HeightMap.Type> optional1, int j) {
        super(structure_c);
        this.startPool = holder;
        this.startJigsawName = optional;
        this.maxDepth = i;
        this.startHeight = heightprovider;
        this.useExpansionHack = flag;
        this.projectStartToHeightmap = optional1;
        this.maxDistanceFromCenter = j;
    }

    public JigsawStructure(Structure.c structure_c, Holder<WorldGenFeatureDefinedStructurePoolTemplate> holder, int i, HeightProvider heightprovider, boolean flag, HeightMap.Type heightmap_type) {
        this(structure_c, holder, Optional.empty(), i, heightprovider, flag, Optional.of(heightmap_type), 80);
    }

    public JigsawStructure(Structure.c structure_c, Holder<WorldGenFeatureDefinedStructurePoolTemplate> holder, int i, HeightProvider heightprovider, boolean flag) {
        this(structure_c, holder, Optional.empty(), i, heightprovider, flag, Optional.empty(), 80);
    }

    @Override
    public Optional<Structure.b> findGenerationPoint(Structure.a structure_a) {
        ChunkCoordIntPair chunkcoordintpair = structure_a.chunkPos();
        int i = this.startHeight.sample(structure_a.random(), new WorldGenerationContext(structure_a.chunkGenerator(), structure_a.heightAccessor()));
        BlockPosition blockposition = new BlockPosition(chunkcoordintpair.getMinBlockX(), i, chunkcoordintpair.getMinBlockZ());

        WorldGenFeaturePieces.forceBootstrap();
        return WorldGenFeatureDefinedStructureJigsawPlacement.addPieces(structure_a, this.startPool, this.startJigsawName, this.maxDepth, blockposition, this.useExpansionHack, this.projectStartToHeightmap, this.maxDistanceFromCenter);
    }

    @Override
    public StructureType<?> type() {
        return StructureType.JIGSAW;
    }
}
