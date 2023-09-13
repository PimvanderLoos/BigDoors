package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnumBlockMirror;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityChest;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureStructurePieceType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureInfo;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessorBlockIgnore;
import net.minecraft.world.level.storage.loot.LootTables;

public class WorldGenIglooPiece {

    public static final int GENERATION_HEIGHT = 90;
    static final MinecraftKey STRUCTURE_LOCATION_IGLOO = new MinecraftKey("igloo/top");
    private static final MinecraftKey STRUCTURE_LOCATION_LADDER = new MinecraftKey("igloo/middle");
    private static final MinecraftKey STRUCTURE_LOCATION_LABORATORY = new MinecraftKey("igloo/bottom");
    static final Map<MinecraftKey, BlockPosition> PIVOTS = ImmutableMap.of(WorldGenIglooPiece.STRUCTURE_LOCATION_IGLOO, new BlockPosition(3, 5, 5), WorldGenIglooPiece.STRUCTURE_LOCATION_LADDER, new BlockPosition(1, 3, 1), WorldGenIglooPiece.STRUCTURE_LOCATION_LABORATORY, new BlockPosition(3, 6, 7));
    static final Map<MinecraftKey, BlockPosition> OFFSETS = ImmutableMap.of(WorldGenIglooPiece.STRUCTURE_LOCATION_IGLOO, BlockPosition.ZERO, WorldGenIglooPiece.STRUCTURE_LOCATION_LADDER, new BlockPosition(2, -3, 4), WorldGenIglooPiece.STRUCTURE_LOCATION_LABORATORY, new BlockPosition(0, -3, -2));

    public WorldGenIglooPiece() {}

    public static void addPieces(DefinedStructureManager definedstructuremanager, BlockPosition blockposition, EnumBlockRotation enumblockrotation, StructurePieceAccessor structurepieceaccessor, Random random) {
        if (random.nextDouble() < 0.5D) {
            int i = random.nextInt(8) + 4;

            structurepieceaccessor.addPiece(new WorldGenIglooPiece.a(definedstructuremanager, WorldGenIglooPiece.STRUCTURE_LOCATION_LABORATORY, blockposition, enumblockrotation, i * 3));

            for (int j = 0; j < i - 1; ++j) {
                structurepieceaccessor.addPiece(new WorldGenIglooPiece.a(definedstructuremanager, WorldGenIglooPiece.STRUCTURE_LOCATION_LADDER, blockposition, enumblockrotation, j * 3));
            }
        }

        structurepieceaccessor.addPiece(new WorldGenIglooPiece.a(definedstructuremanager, WorldGenIglooPiece.STRUCTURE_LOCATION_IGLOO, blockposition, enumblockrotation, 0));
    }

    public static class a extends DefinedStructurePiece {

        public a(DefinedStructureManager definedstructuremanager, MinecraftKey minecraftkey, BlockPosition blockposition, EnumBlockRotation enumblockrotation, int i) {
            super(WorldGenFeatureStructurePieceType.IGLOO, 0, definedstructuremanager, minecraftkey, minecraftkey.toString(), makeSettings(enumblockrotation, minecraftkey), makePosition(minecraftkey, blockposition, i));
        }

        public a(DefinedStructureManager definedstructuremanager, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.IGLOO, nbttagcompound, definedstructuremanager, (minecraftkey) -> {
                return makeSettings(EnumBlockRotation.valueOf(nbttagcompound.getString("Rot")), minecraftkey);
            });
        }

        private static DefinedStructureInfo makeSettings(EnumBlockRotation enumblockrotation, MinecraftKey minecraftkey) {
            return (new DefinedStructureInfo()).setRotation(enumblockrotation).setMirror(EnumBlockMirror.NONE).setRotationPivot((BlockPosition) WorldGenIglooPiece.PIVOTS.get(minecraftkey)).addProcessor(DefinedStructureProcessorBlockIgnore.STRUCTURE_BLOCK);
        }

        private static BlockPosition makePosition(MinecraftKey minecraftkey, BlockPosition blockposition, int i) {
            return blockposition.offset((BaseBlockPosition) WorldGenIglooPiece.OFFSETS.get(minecraftkey)).below(i);
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext structurepieceserializationcontext, NBTTagCompound nbttagcompound) {
            super.addAdditionalSaveData(structurepieceserializationcontext, nbttagcompound);
            nbttagcompound.putString("Rot", this.placeSettings.getRotation().name());
        }

        @Override
        protected void handleDataMarker(String s, BlockPosition blockposition, WorldAccess worldaccess, Random random, StructureBoundingBox structureboundingbox) {
            if ("chest".equals(s)) {
                worldaccess.setBlock(blockposition, Blocks.AIR.defaultBlockState(), 3);
                TileEntity tileentity = worldaccess.getBlockEntity(blockposition.below());

                if (tileentity instanceof TileEntityChest) {
                    ((TileEntityChest) tileentity).setLootTable(LootTables.IGLOO_CHEST, random.nextLong());
                }

            }
        }

        @Override
        public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            MinecraftKey minecraftkey = new MinecraftKey(this.templateName);
            DefinedStructureInfo definedstructureinfo = makeSettings(this.placeSettings.getRotation(), minecraftkey);
            BlockPosition blockposition1 = (BlockPosition) WorldGenIglooPiece.OFFSETS.get(minecraftkey);
            BlockPosition blockposition2 = this.templatePosition.offset(DefinedStructure.calculateRelativePosition(definedstructureinfo, new BlockPosition(3 - blockposition1.getX(), 0, -blockposition1.getZ())));
            int i = generatoraccessseed.getHeight(HeightMap.Type.WORLD_SURFACE_WG, blockposition2.getX(), blockposition2.getZ());
            BlockPosition blockposition3 = this.templatePosition;

            this.templatePosition = this.templatePosition.offset(0, i - 90 - 1, 0);
            super.postProcess(generatoraccessseed, structuremanager, chunkgenerator, random, structureboundingbox, chunkcoordintpair, blockposition);
            if (minecraftkey.equals(WorldGenIglooPiece.STRUCTURE_LOCATION_IGLOO)) {
                BlockPosition blockposition4 = this.templatePosition.offset(DefinedStructure.calculateRelativePosition(definedstructureinfo, new BlockPosition(3, 0, 5)));
                IBlockData iblockdata = generatoraccessseed.getBlockState(blockposition4.below());

                if (!iblockdata.isAir() && !iblockdata.is(Blocks.LADDER)) {
                    generatoraccessseed.setBlock(blockposition4, Blocks.SNOW_BLOCK.defaultBlockState(), 3);
                }
            }

            this.templatePosition = blockposition3;
        }
    }
}
