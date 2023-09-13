package net.minecraft.world.level.levelgen.structure;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import net.minecraft.SystemUtils;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.EnumBlockMirror;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.block.entity.TileEntityLootable;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureStructurePieceType;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureShipwreckConfiguration;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureInfo;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessorBlockIgnore;
import net.minecraft.world.level.storage.loot.LootTables;

public class WorldGenShipwreck {

    static final BlockPosition PIVOT = new BlockPosition(4, 0, 15);
    private static final MinecraftKey[] STRUCTURE_LOCATION_BEACHED = new MinecraftKey[]{new MinecraftKey("shipwreck/with_mast"), new MinecraftKey("shipwreck/sideways_full"), new MinecraftKey("shipwreck/sideways_fronthalf"), new MinecraftKey("shipwreck/sideways_backhalf"), new MinecraftKey("shipwreck/rightsideup_full"), new MinecraftKey("shipwreck/rightsideup_fronthalf"), new MinecraftKey("shipwreck/rightsideup_backhalf"), new MinecraftKey("shipwreck/with_mast_degraded"), new MinecraftKey("shipwreck/rightsideup_full_degraded"), new MinecraftKey("shipwreck/rightsideup_fronthalf_degraded"), new MinecraftKey("shipwreck/rightsideup_backhalf_degraded")};
    private static final MinecraftKey[] STRUCTURE_LOCATION_OCEAN = new MinecraftKey[]{new MinecraftKey("shipwreck/with_mast"), new MinecraftKey("shipwreck/upsidedown_full"), new MinecraftKey("shipwreck/upsidedown_fronthalf"), new MinecraftKey("shipwreck/upsidedown_backhalf"), new MinecraftKey("shipwreck/sideways_full"), new MinecraftKey("shipwreck/sideways_fronthalf"), new MinecraftKey("shipwreck/sideways_backhalf"), new MinecraftKey("shipwreck/rightsideup_full"), new MinecraftKey("shipwreck/rightsideup_fronthalf"), new MinecraftKey("shipwreck/rightsideup_backhalf"), new MinecraftKey("shipwreck/with_mast_degraded"), new MinecraftKey("shipwreck/upsidedown_full_degraded"), new MinecraftKey("shipwreck/upsidedown_fronthalf_degraded"), new MinecraftKey("shipwreck/upsidedown_backhalf_degraded"), new MinecraftKey("shipwreck/sideways_full_degraded"), new MinecraftKey("shipwreck/sideways_fronthalf_degraded"), new MinecraftKey("shipwreck/sideways_backhalf_degraded"), new MinecraftKey("shipwreck/rightsideup_full_degraded"), new MinecraftKey("shipwreck/rightsideup_fronthalf_degraded"), new MinecraftKey("shipwreck/rightsideup_backhalf_degraded")};
    static final Map<String, MinecraftKey> MARKERS_TO_LOOT = Map.of("map_chest", LootTables.SHIPWRECK_MAP, "treasure_chest", LootTables.SHIPWRECK_TREASURE, "supply_chest", LootTables.SHIPWRECK_SUPPLY);

    public WorldGenShipwreck() {}

    public static void addPieces(DefinedStructureManager definedstructuremanager, BlockPosition blockposition, EnumBlockRotation enumblockrotation, StructurePieceAccessor structurepieceaccessor, Random random, WorldGenFeatureShipwreckConfiguration worldgenfeatureshipwreckconfiguration) {
        MinecraftKey minecraftkey = (MinecraftKey) SystemUtils.getRandom((Object[]) (worldgenfeatureshipwreckconfiguration.isBeached ? WorldGenShipwreck.STRUCTURE_LOCATION_BEACHED : WorldGenShipwreck.STRUCTURE_LOCATION_OCEAN), random);

        structurepieceaccessor.addPiece(new WorldGenShipwreck.a(definedstructuremanager, minecraftkey, blockposition, enumblockrotation, worldgenfeatureshipwreckconfiguration.isBeached));
    }

    public static class a extends DefinedStructurePiece {

        private final boolean isBeached;

        public a(DefinedStructureManager definedstructuremanager, MinecraftKey minecraftkey, BlockPosition blockposition, EnumBlockRotation enumblockrotation, boolean flag) {
            super(WorldGenFeatureStructurePieceType.SHIPWRECK_PIECE, 0, definedstructuremanager, minecraftkey, minecraftkey.toString(), makeSettings(enumblockrotation), blockposition);
            this.isBeached = flag;
        }

        public a(DefinedStructureManager definedstructuremanager, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.SHIPWRECK_PIECE, nbttagcompound, definedstructuremanager, (minecraftkey) -> {
                return makeSettings(EnumBlockRotation.valueOf(nbttagcompound.getString("Rot")));
            });
            this.isBeached = nbttagcompound.getBoolean("isBeached");
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext structurepieceserializationcontext, NBTTagCompound nbttagcompound) {
            super.addAdditionalSaveData(structurepieceserializationcontext, nbttagcompound);
            nbttagcompound.putBoolean("isBeached", this.isBeached);
            nbttagcompound.putString("Rot", this.placeSettings.getRotation().name());
        }

        private static DefinedStructureInfo makeSettings(EnumBlockRotation enumblockrotation) {
            return (new DefinedStructureInfo()).setRotation(enumblockrotation).setMirror(EnumBlockMirror.NONE).setRotationPivot(WorldGenShipwreck.PIVOT).addProcessor(DefinedStructureProcessorBlockIgnore.STRUCTURE_AND_AIR);
        }

        @Override
        protected void handleDataMarker(String s, BlockPosition blockposition, WorldAccess worldaccess, Random random, StructureBoundingBox structureboundingbox) {
            MinecraftKey minecraftkey = (MinecraftKey) WorldGenShipwreck.MARKERS_TO_LOOT.get(s);

            if (minecraftkey != null) {
                TileEntityLootable.setLootTable(worldaccess, random, blockposition.below(), minecraftkey);
            }

        }

        @Override
        public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            int i = generatoraccessseed.getMaxBuildHeight();
            int j = 0;
            BaseBlockPosition baseblockposition = this.template.getSize();
            HeightMap.Type heightmap_type = this.isBeached ? HeightMap.Type.WORLD_SURFACE_WG : HeightMap.Type.OCEAN_FLOOR_WG;
            int k = baseblockposition.getX() * baseblockposition.getZ();

            if (k == 0) {
                j = generatoraccessseed.getHeight(heightmap_type, this.templatePosition.getX(), this.templatePosition.getZ());
            } else {
                BlockPosition blockposition1 = this.templatePosition.offset(baseblockposition.getX() - 1, 0, baseblockposition.getZ() - 1);

                int l;

                for (Iterator iterator = BlockPosition.betweenClosed(this.templatePosition, blockposition1).iterator(); iterator.hasNext(); i = Math.min(i, l)) {
                    BlockPosition blockposition2 = (BlockPosition) iterator.next();

                    l = generatoraccessseed.getHeight(heightmap_type, blockposition2.getX(), blockposition2.getZ());
                    j += l;
                }

                j /= k;
            }

            int i1 = this.isBeached ? i - baseblockposition.getY() / 2 - random.nextInt(3) : j;

            this.templatePosition = new BlockPosition(this.templatePosition.getX(), i1, this.templatePosition.getZ());
            super.postProcess(generatoraccessseed, structuremanager, chunkgenerator, random, structureboundingbox, chunkcoordintpair, blockposition);
        }
    }
}
