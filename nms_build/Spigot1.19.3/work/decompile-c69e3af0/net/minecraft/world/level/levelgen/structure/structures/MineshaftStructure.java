package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.IntFunction;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.INamable;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class MineshaftStructure extends Structure {

    public static final Codec<MineshaftStructure> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(settingsCodec(instance), MineshaftStructure.a.CODEC.fieldOf("mineshaft_type").forGetter((mineshaftstructure) -> {
            return mineshaftstructure.type;
        })).apply(instance, MineshaftStructure::new);
    });
    private final MineshaftStructure.a type;

    public MineshaftStructure(Structure.c structure_c, MineshaftStructure.a mineshaftstructure_a) {
        super(structure_c);
        this.type = mineshaftstructure_a;
    }

    @Override
    public Optional<Structure.b> findGenerationPoint(Structure.a structure_a) {
        structure_a.random().nextDouble();
        ChunkCoordIntPair chunkcoordintpair = structure_a.chunkPos();
        BlockPosition blockposition = new BlockPosition(chunkcoordintpair.getMiddleBlockX(), 50, chunkcoordintpair.getMinBlockZ());
        StructurePiecesBuilder structurepiecesbuilder = new StructurePiecesBuilder();
        int i = this.generatePiecesAndAdjust(structurepiecesbuilder, structure_a);

        return Optional.of(new Structure.b(blockposition.offset(0, i, 0), Either.right(structurepiecesbuilder)));
    }

    private int generatePiecesAndAdjust(StructurePiecesBuilder structurepiecesbuilder, Structure.a structure_a) {
        ChunkCoordIntPair chunkcoordintpair = structure_a.chunkPos();
        SeededRandom seededrandom = structure_a.random();
        ChunkGenerator chunkgenerator = structure_a.chunkGenerator();
        MineshaftPieces.d mineshaftpieces_d = new MineshaftPieces.d(0, seededrandom, chunkcoordintpair.getBlockX(2), chunkcoordintpair.getBlockZ(2), this.type);

        structurepiecesbuilder.addPiece(mineshaftpieces_d);
        mineshaftpieces_d.addChildren(mineshaftpieces_d, structurepiecesbuilder, seededrandom);
        int i = chunkgenerator.getSeaLevel();

        if (this.type == MineshaftStructure.a.MESA) {
            BlockPosition blockposition = structurepiecesbuilder.getBoundingBox().getCenter();
            int j = chunkgenerator.getBaseHeight(blockposition.getX(), blockposition.getZ(), HeightMap.Type.WORLD_SURFACE_WG, structure_a.heightAccessor(), structure_a.randomState());
            int k = j <= i ? i : MathHelper.randomBetweenInclusive(seededrandom, i, j);
            int l = k - blockposition.getY();

            structurepiecesbuilder.offsetPiecesVertically(l);
            return l;
        } else {
            return structurepiecesbuilder.moveBelowSeaLevel(i, chunkgenerator.getMinY(), seededrandom, 10);
        }
    }

    @Override
    public StructureType<?> type() {
        return StructureType.MINESHAFT;
    }

    public static enum a implements INamable {

        NORMAL("normal", Blocks.OAK_LOG, Blocks.OAK_PLANKS, Blocks.OAK_FENCE), MESA("mesa", Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_FENCE);

        public static final Codec<MineshaftStructure.a> CODEC = INamable.fromEnum(MineshaftStructure.a::values);
        private static final IntFunction<MineshaftStructure.a> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.a.ZERO);
        private final String name;
        private final IBlockData woodState;
        private final IBlockData planksState;
        private final IBlockData fenceState;

        private a(String s, Block block, Block block1, Block block2) {
            this.name = s;
            this.woodState = block.defaultBlockState();
            this.planksState = block1.defaultBlockState();
            this.fenceState = block2.defaultBlockState();
        }

        public String getName() {
            return this.name;
        }

        public static MineshaftStructure.a byId(int i) {
            return (MineshaftStructure.a) MineshaftStructure.a.BY_ID.apply(i);
        }

        public IBlockData getWoodState() {
            return this.woodState;
        }

        public IBlockData getPlanksState() {
            return this.planksState;
        }

        public IBlockData getFenceState() {
            return this.fenceState;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
