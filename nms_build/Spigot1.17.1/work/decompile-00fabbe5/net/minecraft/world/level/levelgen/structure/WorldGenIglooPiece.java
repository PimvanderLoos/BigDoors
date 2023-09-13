package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
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
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureInfo;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessor;
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

    public static void a(DefinedStructureManager definedstructuremanager, BlockPosition blockposition, EnumBlockRotation enumblockrotation, StructurePieceAccessor structurepieceaccessor, Random random) {
        if (random.nextDouble() < 0.5D) {
            int i = random.nextInt(8) + 4;

            structurepieceaccessor.a((StructurePiece) (new WorldGenIglooPiece.a(definedstructuremanager, WorldGenIglooPiece.STRUCTURE_LOCATION_LABORATORY, blockposition, enumblockrotation, i * 3)));

            for (int j = 0; j < i - 1; ++j) {
                structurepieceaccessor.a((StructurePiece) (new WorldGenIglooPiece.a(definedstructuremanager, WorldGenIglooPiece.STRUCTURE_LOCATION_LADDER, blockposition, enumblockrotation, j * 3)));
            }
        }

        structurepieceaccessor.a((StructurePiece) (new WorldGenIglooPiece.a(definedstructuremanager, WorldGenIglooPiece.STRUCTURE_LOCATION_IGLOO, blockposition, enumblockrotation, 0)));
    }

    public static class a extends DefinedStructurePiece {

        public a(DefinedStructureManager definedstructuremanager, MinecraftKey minecraftkey, BlockPosition blockposition, EnumBlockRotation enumblockrotation, int i) {
            super(WorldGenFeatureStructurePieceType.IGLOO, 0, definedstructuremanager, minecraftkey, minecraftkey.toString(), a(enumblockrotation, minecraftkey), a(minecraftkey, blockposition, i));
        }

        public a(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.IGLOO, nbttagcompound, worldserver, (minecraftkey) -> {
                return a(EnumBlockRotation.valueOf(nbttagcompound.getString("Rot")), minecraftkey);
            });
        }

        private static DefinedStructureInfo a(EnumBlockRotation enumblockrotation, MinecraftKey minecraftkey) {
            return (new DefinedStructureInfo()).a(enumblockrotation).a(EnumBlockMirror.NONE).a((BlockPosition) WorldGenIglooPiece.PIVOTS.get(minecraftkey)).a((DefinedStructureProcessor) DefinedStructureProcessorBlockIgnore.STRUCTURE_BLOCK);
        }

        private static BlockPosition a(MinecraftKey minecraftkey, BlockPosition blockposition, int i) {
            return blockposition.f((BaseBlockPosition) WorldGenIglooPiece.OFFSETS.get(minecraftkey)).down(i);
        }

        @Override
        protected void a(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super.a(worldserver, nbttagcompound);
            nbttagcompound.setString("Rot", this.placeSettings.d().name());
        }

        @Override
        protected void a(String s, BlockPosition blockposition, WorldAccess worldaccess, Random random, StructureBoundingBox structureboundingbox) {
            if ("chest".equals(s)) {
                worldaccess.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 3);
                TileEntity tileentity = worldaccess.getTileEntity(blockposition.down());

                if (tileentity instanceof TileEntityChest) {
                    ((TileEntityChest) tileentity).setLootTable(LootTables.IGLOO_CHEST, random.nextLong());
                }

            }
        }

        @Override
        public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            MinecraftKey minecraftkey = new MinecraftKey(this.templateName);
            DefinedStructureInfo definedstructureinfo = a(this.placeSettings.d(), minecraftkey);
            BlockPosition blockposition1 = (BlockPosition) WorldGenIglooPiece.OFFSETS.get(minecraftkey);
            BlockPosition blockposition2 = this.templatePosition.f(DefinedStructure.a(definedstructureinfo, new BlockPosition(3 - blockposition1.getX(), 0, -blockposition1.getZ())));
            int i = generatoraccessseed.a(HeightMap.Type.WORLD_SURFACE_WG, blockposition2.getX(), blockposition2.getZ());
            BlockPosition blockposition3 = this.templatePosition;

            this.templatePosition = this.templatePosition.c(0, i - 90 - 1, 0);
            boolean flag = super.a(generatoraccessseed, structuremanager, chunkgenerator, random, structureboundingbox, chunkcoordintpair, blockposition);

            if (minecraftkey.equals(WorldGenIglooPiece.STRUCTURE_LOCATION_IGLOO)) {
                BlockPosition blockposition4 = this.templatePosition.f(DefinedStructure.a(definedstructureinfo, new BlockPosition(3, 0, 5)));
                IBlockData iblockdata = generatoraccessseed.getType(blockposition4.down());

                if (!iblockdata.isAir() && !iblockdata.a(Blocks.LADDER)) {
                    generatoraccessseed.setTypeAndData(blockposition4, Blocks.SNOW_BLOCK.getBlockData(), 3);
                }
            }

            this.templatePosition = blockposition3;
            return flag;
        }
    }
}
