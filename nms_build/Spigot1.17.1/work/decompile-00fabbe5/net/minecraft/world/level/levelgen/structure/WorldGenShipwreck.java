package net.minecraft.world.level.levelgen.structure;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.SystemUtils;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.EnumBlockMirror;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.block.entity.TileEntityLootable;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureStructurePieceType;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureShipwreckConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureInfo;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessorBlockIgnore;
import net.minecraft.world.level.storage.loot.LootTables;

public class WorldGenShipwreck {

    static final BlockPosition PIVOT = new BlockPosition(4, 0, 15);
    private static final MinecraftKey[] STRUCTURE_LOCATION_BEACHED = new MinecraftKey[]{new MinecraftKey("shipwreck/with_mast"), new MinecraftKey("shipwreck/sideways_full"), new MinecraftKey("shipwreck/sideways_fronthalf"), new MinecraftKey("shipwreck/sideways_backhalf"), new MinecraftKey("shipwreck/rightsideup_full"), new MinecraftKey("shipwreck/rightsideup_fronthalf"), new MinecraftKey("shipwreck/rightsideup_backhalf"), new MinecraftKey("shipwreck/with_mast_degraded"), new MinecraftKey("shipwreck/rightsideup_full_degraded"), new MinecraftKey("shipwreck/rightsideup_fronthalf_degraded"), new MinecraftKey("shipwreck/rightsideup_backhalf_degraded")};
    private static final MinecraftKey[] STRUCTURE_LOCATION_OCEAN = new MinecraftKey[]{new MinecraftKey("shipwreck/with_mast"), new MinecraftKey("shipwreck/upsidedown_full"), new MinecraftKey("shipwreck/upsidedown_fronthalf"), new MinecraftKey("shipwreck/upsidedown_backhalf"), new MinecraftKey("shipwreck/sideways_full"), new MinecraftKey("shipwreck/sideways_fronthalf"), new MinecraftKey("shipwreck/sideways_backhalf"), new MinecraftKey("shipwreck/rightsideup_full"), new MinecraftKey("shipwreck/rightsideup_fronthalf"), new MinecraftKey("shipwreck/rightsideup_backhalf"), new MinecraftKey("shipwreck/with_mast_degraded"), new MinecraftKey("shipwreck/upsidedown_full_degraded"), new MinecraftKey("shipwreck/upsidedown_fronthalf_degraded"), new MinecraftKey("shipwreck/upsidedown_backhalf_degraded"), new MinecraftKey("shipwreck/sideways_full_degraded"), new MinecraftKey("shipwreck/sideways_fronthalf_degraded"), new MinecraftKey("shipwreck/sideways_backhalf_degraded"), new MinecraftKey("shipwreck/rightsideup_full_degraded"), new MinecraftKey("shipwreck/rightsideup_fronthalf_degraded"), new MinecraftKey("shipwreck/rightsideup_backhalf_degraded")};

    public WorldGenShipwreck() {}

    public static void a(DefinedStructureManager definedstructuremanager, BlockPosition blockposition, EnumBlockRotation enumblockrotation, StructurePieceAccessor structurepieceaccessor, Random random, WorldGenFeatureShipwreckConfiguration worldgenfeatureshipwreckconfiguration) {
        MinecraftKey minecraftkey = (MinecraftKey) SystemUtils.a((Object[]) (worldgenfeatureshipwreckconfiguration.isBeached ? WorldGenShipwreck.STRUCTURE_LOCATION_BEACHED : WorldGenShipwreck.STRUCTURE_LOCATION_OCEAN), random);

        structurepieceaccessor.a((StructurePiece) (new WorldGenShipwreck.a(definedstructuremanager, minecraftkey, blockposition, enumblockrotation, worldgenfeatureshipwreckconfiguration.isBeached)));
    }

    public static class a extends DefinedStructurePiece {

        private final boolean isBeached;

        public a(DefinedStructureManager definedstructuremanager, MinecraftKey minecraftkey, BlockPosition blockposition, EnumBlockRotation enumblockrotation, boolean flag) {
            super(WorldGenFeatureStructurePieceType.SHIPWRECK_PIECE, 0, definedstructuremanager, minecraftkey, minecraftkey.toString(), a(enumblockrotation), blockposition);
            this.isBeached = flag;
        }

        public a(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.SHIPWRECK_PIECE, nbttagcompound, worldserver, (minecraftkey) -> {
                return a(EnumBlockRotation.valueOf(nbttagcompound.getString("Rot")));
            });
            this.isBeached = nbttagcompound.getBoolean("isBeached");
        }

        @Override
        protected void a(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super.a(worldserver, nbttagcompound);
            nbttagcompound.setBoolean("isBeached", this.isBeached);
            nbttagcompound.setString("Rot", this.placeSettings.d().name());
        }

        private static DefinedStructureInfo a(EnumBlockRotation enumblockrotation) {
            return (new DefinedStructureInfo()).a(enumblockrotation).a(EnumBlockMirror.NONE).a(WorldGenShipwreck.PIVOT).a((DefinedStructureProcessor) DefinedStructureProcessorBlockIgnore.STRUCTURE_AND_AIR);
        }

        @Override
        protected void a(String s, BlockPosition blockposition, WorldAccess worldaccess, Random random, StructureBoundingBox structureboundingbox) {
            if ("map_chest".equals(s)) {
                TileEntityLootable.a((IBlockAccess) worldaccess, random, blockposition.down(), LootTables.SHIPWRECK_MAP);
            } else if ("treasure_chest".equals(s)) {
                TileEntityLootable.a((IBlockAccess) worldaccess, random, blockposition.down(), LootTables.SHIPWRECK_TREASURE);
            } else if ("supply_chest".equals(s)) {
                TileEntityLootable.a((IBlockAccess) worldaccess, random, blockposition.down(), LootTables.SHIPWRECK_SUPPLY);
            }

        }

        @Override
        public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            int i = generatoraccessseed.getMaxBuildHeight();
            int j = 0;
            BaseBlockPosition baseblockposition = this.template.a();
            HeightMap.Type heightmap_type = this.isBeached ? HeightMap.Type.WORLD_SURFACE_WG : HeightMap.Type.OCEAN_FLOOR_WG;
            int k = baseblockposition.getX() * baseblockposition.getZ();

            if (k == 0) {
                j = generatoraccessseed.a(heightmap_type, this.templatePosition.getX(), this.templatePosition.getZ());
            } else {
                BlockPosition blockposition1 = this.templatePosition.c(baseblockposition.getX() - 1, 0, baseblockposition.getZ() - 1);

                int l;

                for (Iterator iterator = BlockPosition.a(this.templatePosition, blockposition1).iterator(); iterator.hasNext(); i = Math.min(i, l)) {
                    BlockPosition blockposition2 = (BlockPosition) iterator.next();

                    l = generatoraccessseed.a(heightmap_type, blockposition2.getX(), blockposition2.getZ());
                    j += l;
                }

                j /= k;
            }

            int i1 = this.isBeached ? i - baseblockposition.getY() / 2 - random.nextInt(3) : j;

            this.templatePosition = new BlockPosition(this.templatePosition.getX(), i1, this.templatePosition.getZ());
            return super.a(generatoraccessseed, structuremanager, chunkgenerator, random, structureboundingbox, chunkcoordintpair, blockposition);
        }
    }
}
