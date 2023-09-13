package net.minecraft.world.level.levelgen.structure;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
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
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureInfo;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessorBlockIgnore;
import net.minecraft.world.level.storage.loot.LootTables;

public class WorldGenShipwreck {

    private static final BlockPosition a = new BlockPosition(4, 0, 15);
    private static final MinecraftKey[] b = new MinecraftKey[]{new MinecraftKey("shipwreck/with_mast"), new MinecraftKey("shipwreck/sideways_full"), new MinecraftKey("shipwreck/sideways_fronthalf"), new MinecraftKey("shipwreck/sideways_backhalf"), new MinecraftKey("shipwreck/rightsideup_full"), new MinecraftKey("shipwreck/rightsideup_fronthalf"), new MinecraftKey("shipwreck/rightsideup_backhalf"), new MinecraftKey("shipwreck/with_mast_degraded"), new MinecraftKey("shipwreck/rightsideup_full_degraded"), new MinecraftKey("shipwreck/rightsideup_fronthalf_degraded"), new MinecraftKey("shipwreck/rightsideup_backhalf_degraded")};
    private static final MinecraftKey[] c = new MinecraftKey[]{new MinecraftKey("shipwreck/with_mast"), new MinecraftKey("shipwreck/upsidedown_full"), new MinecraftKey("shipwreck/upsidedown_fronthalf"), new MinecraftKey("shipwreck/upsidedown_backhalf"), new MinecraftKey("shipwreck/sideways_full"), new MinecraftKey("shipwreck/sideways_fronthalf"), new MinecraftKey("shipwreck/sideways_backhalf"), new MinecraftKey("shipwreck/rightsideup_full"), new MinecraftKey("shipwreck/rightsideup_fronthalf"), new MinecraftKey("shipwreck/rightsideup_backhalf"), new MinecraftKey("shipwreck/with_mast_degraded"), new MinecraftKey("shipwreck/upsidedown_full_degraded"), new MinecraftKey("shipwreck/upsidedown_fronthalf_degraded"), new MinecraftKey("shipwreck/upsidedown_backhalf_degraded"), new MinecraftKey("shipwreck/sideways_full_degraded"), new MinecraftKey("shipwreck/sideways_fronthalf_degraded"), new MinecraftKey("shipwreck/sideways_backhalf_degraded"), new MinecraftKey("shipwreck/rightsideup_full_degraded"), new MinecraftKey("shipwreck/rightsideup_fronthalf_degraded"), new MinecraftKey("shipwreck/rightsideup_backhalf_degraded")};

    public static void a(DefinedStructureManager definedstructuremanager, BlockPosition blockposition, EnumBlockRotation enumblockrotation, List<StructurePiece> list, Random random, WorldGenFeatureShipwreckConfiguration worldgenfeatureshipwreckconfiguration) {
        MinecraftKey minecraftkey = (MinecraftKey) SystemUtils.a((Object[]) (worldgenfeatureshipwreckconfiguration.b ? WorldGenShipwreck.b : WorldGenShipwreck.c), random);

        list.add(new WorldGenShipwreck.a(definedstructuremanager, minecraftkey, blockposition, enumblockrotation, worldgenfeatureshipwreckconfiguration.b));
    }

    public static class a extends DefinedStructurePiece {

        private final EnumBlockRotation d;
        private final MinecraftKey e;
        private final boolean f;

        public a(DefinedStructureManager definedstructuremanager, MinecraftKey minecraftkey, BlockPosition blockposition, EnumBlockRotation enumblockrotation, boolean flag) {
            super(WorldGenFeatureStructurePieceType.ab, 0);
            this.c = blockposition;
            this.d = enumblockrotation;
            this.e = minecraftkey;
            this.f = flag;
            this.a(definedstructuremanager);
        }

        public a(DefinedStructureManager definedstructuremanager, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.ab, nbttagcompound);
            this.e = new MinecraftKey(nbttagcompound.getString("Template"));
            this.f = nbttagcompound.getBoolean("isBeached");
            this.d = EnumBlockRotation.valueOf(nbttagcompound.getString("Rot"));
            this.a(definedstructuremanager);
        }

        @Override
        protected void a(NBTTagCompound nbttagcompound) {
            super.a(nbttagcompound);
            nbttagcompound.setString("Template", this.e.toString());
            nbttagcompound.setBoolean("isBeached", this.f);
            nbttagcompound.setString("Rot", this.d.name());
        }

        private void a(DefinedStructureManager definedstructuremanager) {
            DefinedStructure definedstructure = definedstructuremanager.a(this.e);
            DefinedStructureInfo definedstructureinfo = (new DefinedStructureInfo()).a(this.d).a(EnumBlockMirror.NONE).a(WorldGenShipwreck.a).a((DefinedStructureProcessor) DefinedStructureProcessorBlockIgnore.d);

            this.a(definedstructure, this.c, definedstructureinfo);
        }

        @Override
        protected void a(String s, BlockPosition blockposition, WorldAccess worldaccess, Random random, StructureBoundingBox structureboundingbox) {
            if ("map_chest".equals(s)) {
                TileEntityLootable.a((IBlockAccess) worldaccess, random, blockposition.down(), LootTables.H);
            } else if ("treasure_chest".equals(s)) {
                TileEntityLootable.a((IBlockAccess) worldaccess, random, blockposition.down(), LootTables.J);
            } else if ("supply_chest".equals(s)) {
                TileEntityLootable.a((IBlockAccess) worldaccess, random, blockposition.down(), LootTables.I);
            }

        }

        @Override
        public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            int i = 256;
            int j = 0;
            BlockPosition blockposition1 = this.a.a();
            HeightMap.Type heightmap_type = this.f ? HeightMap.Type.WORLD_SURFACE_WG : HeightMap.Type.OCEAN_FLOOR_WG;
            int k = blockposition1.getX() * blockposition1.getZ();

            if (k == 0) {
                j = generatoraccessseed.a(heightmap_type, this.c.getX(), this.c.getZ());
            } else {
                BlockPosition blockposition2 = this.c.b(blockposition1.getX() - 1, 0, blockposition1.getZ() - 1);

                int l;

                for (Iterator iterator = BlockPosition.a(this.c, blockposition2).iterator(); iterator.hasNext(); i = Math.min(i, l)) {
                    BlockPosition blockposition3 = (BlockPosition) iterator.next();

                    l = generatoraccessseed.a(heightmap_type, blockposition3.getX(), blockposition3.getZ());
                    j += l;
                }

                j /= k;
            }

            int i1 = this.f ? i - blockposition1.getY() / 2 - random.nextInt(3) : j;

            this.c = new BlockPosition(this.c.getX(), i1, this.c.getZ());
            return super.a(generatoraccessseed, structuremanager, chunkgenerator, random, structureboundingbox, chunkcoordintpair, blockposition);
        }
    }
}
