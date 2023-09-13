package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.QuartPos;
import net.minecraft.util.INamable;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenMineshaftConfiguration;
import net.minecraft.world.level.levelgen.structure.WorldGenMineshaftPieces;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class WorldGenMineshaft extends StructureGenerator<WorldGenMineshaftConfiguration> {

    public WorldGenMineshaft(Codec<WorldGenMineshaftConfiguration> codec) {
        super(codec, PieceGeneratorSupplier.simple(WorldGenMineshaft::checkLocation, WorldGenMineshaft::generatePieces));
    }

    private static boolean checkLocation(PieceGeneratorSupplier.a<WorldGenMineshaftConfiguration> piecegeneratorsupplier_a) {
        SeededRandom seededrandom = new SeededRandom(new LegacyRandomSource(0L));

        seededrandom.setLargeFeatureSeed(piecegeneratorsupplier_a.seed(), piecegeneratorsupplier_a.chunkPos().x, piecegeneratorsupplier_a.chunkPos().z);
        double d0 = (double) ((WorldGenMineshaftConfiguration) piecegeneratorsupplier_a.config()).probability;

        return seededrandom.nextDouble() >= d0 ? false : piecegeneratorsupplier_a.validBiome().test(piecegeneratorsupplier_a.chunkGenerator().getNoiseBiome(QuartPos.fromBlock(piecegeneratorsupplier_a.chunkPos().getMiddleBlockX()), QuartPos.fromBlock(50), QuartPos.fromBlock(piecegeneratorsupplier_a.chunkPos().getMiddleBlockZ())));
    }

    private static void generatePieces(StructurePiecesBuilder structurepiecesbuilder, PieceGenerator.a<WorldGenMineshaftConfiguration> piecegenerator_a) {
        WorldGenMineshaftPieces.WorldGenMineshaftRoom worldgenmineshaftpieces_worldgenmineshaftroom = new WorldGenMineshaftPieces.WorldGenMineshaftRoom(0, piecegenerator_a.random(), piecegenerator_a.chunkPos().getBlockX(2), piecegenerator_a.chunkPos().getBlockZ(2), ((WorldGenMineshaftConfiguration) piecegenerator_a.config()).type);

        structurepiecesbuilder.addPiece(worldgenmineshaftpieces_worldgenmineshaftroom);
        worldgenmineshaftpieces_worldgenmineshaftroom.addChildren(worldgenmineshaftpieces_worldgenmineshaftroom, structurepiecesbuilder, piecegenerator_a.random());
        int i = piecegenerator_a.chunkGenerator().getSeaLevel();

        if (((WorldGenMineshaftConfiguration) piecegenerator_a.config()).type == WorldGenMineshaft.Type.MESA) {
            BlockPosition blockposition = structurepiecesbuilder.getBoundingBox().getCenter();
            int j = piecegenerator_a.chunkGenerator().getBaseHeight(blockposition.getX(), blockposition.getZ(), HeightMap.Type.WORLD_SURFACE_WG, piecegenerator_a.heightAccessor());
            int k = j <= i ? i : MathHelper.randomBetweenInclusive(piecegenerator_a.random(), i, j);
            int l = k - blockposition.getY();

            structurepiecesbuilder.offsetPiecesVertically(l);
        } else {
            structurepiecesbuilder.moveBelowSeaLevel(i, piecegenerator_a.chunkGenerator().getMinY(), piecegenerator_a.random(), 10);
        }

    }

    public static enum Type implements INamable {

        NORMAL("normal", Blocks.OAK_LOG, Blocks.OAK_PLANKS, Blocks.OAK_FENCE), MESA("mesa", Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_FENCE);

        public static final Codec<WorldGenMineshaft.Type> CODEC = INamable.fromEnum(WorldGenMineshaft.Type::values, WorldGenMineshaft.Type::byName);
        private static final Map<String, WorldGenMineshaft.Type> BY_NAME = (Map) Arrays.stream(values()).collect(Collectors.toMap(WorldGenMineshaft.Type::getName, (worldgenmineshaft_type) -> {
            return worldgenmineshaft_type;
        }));
        private final String name;
        private final IBlockData woodState;
        private final IBlockData planksState;
        private final IBlockData fenceState;

        private Type(String s, Block block, Block block1, Block block2) {
            this.name = s;
            this.woodState = block.defaultBlockState();
            this.planksState = block1.defaultBlockState();
            this.fenceState = block2.defaultBlockState();
        }

        public String getName() {
            return this.name;
        }

        private static WorldGenMineshaft.Type byName(String s) {
            return (WorldGenMineshaft.Type) WorldGenMineshaft.Type.BY_NAME.get(s);
        }

        public static WorldGenMineshaft.Type byId(int i) {
            return i >= 0 && i < values().length ? values()[i] : WorldGenMineshaft.Type.NORMAL;
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
