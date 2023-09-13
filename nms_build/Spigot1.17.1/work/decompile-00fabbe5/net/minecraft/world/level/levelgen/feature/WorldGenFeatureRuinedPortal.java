package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.INamable;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.BlockColumn;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.EnumBlockMirror;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureRuinedPortalConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.WorldGenFeatureRuinedPortalPieces;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;

public class WorldGenFeatureRuinedPortal extends StructureGenerator<WorldGenFeatureRuinedPortalConfiguration> {

    static final String[] STRUCTURE_LOCATION_PORTALS = new String[]{"ruined_portal/portal_1", "ruined_portal/portal_2", "ruined_portal/portal_3", "ruined_portal/portal_4", "ruined_portal/portal_5", "ruined_portal/portal_6", "ruined_portal/portal_7", "ruined_portal/portal_8", "ruined_portal/portal_9", "ruined_portal/portal_10"};
    static final String[] STRUCTURE_LOCATION_GIANT_PORTALS = new String[]{"ruined_portal/giant_portal_1", "ruined_portal/giant_portal_2", "ruined_portal/giant_portal_3"};
    private static final float PROBABILITY_OF_GIANT_PORTAL = 0.05F;
    private static final float PROBABILITY_OF_AIR_POCKET = 0.5F;
    private static final float PROBABILITY_OF_UNDERGROUND = 0.5F;
    private static final float UNDERWATER_MOSSINESS = 0.8F;
    private static final float JUNGLE_MOSSINESS = 0.8F;
    private static final float SWAMP_MOSSINESS = 0.5F;
    private static final int MIN_Y = 15;

    public WorldGenFeatureRuinedPortal(Codec<WorldGenFeatureRuinedPortalConfiguration> codec) {
        super(codec);
    }

    @Override
    public StructureGenerator.a<WorldGenFeatureRuinedPortalConfiguration> a() {
        return WorldGenFeatureRuinedPortal.a::new;
    }

    static boolean a(BlockPosition blockposition, BiomeBase biomebase) {
        return biomebase.getAdjustedTemperature(blockposition) < 0.15F;
    }

    static int a(Random random, ChunkGenerator chunkgenerator, WorldGenFeatureRuinedPortalPieces.Position worldgenfeatureruinedportalpieces_position, boolean flag, int i, int j, StructureBoundingBox structureboundingbox, LevelHeightAccessor levelheightaccessor) {
        int k;

        if (worldgenfeatureruinedportalpieces_position == WorldGenFeatureRuinedPortalPieces.Position.IN_NETHER) {
            if (flag) {
                k = MathHelper.b(random, 32, 100);
            } else if (random.nextFloat() < 0.5F) {
                k = MathHelper.b(random, 27, 29);
            } else {
                k = MathHelper.b(random, 29, 100);
            }
        } else {
            int l;

            if (worldgenfeatureruinedportalpieces_position == WorldGenFeatureRuinedPortalPieces.Position.IN_MOUNTAIN) {
                l = i - j;
                k = a(random, 70, l);
            } else if (worldgenfeatureruinedportalpieces_position == WorldGenFeatureRuinedPortalPieces.Position.UNDERGROUND) {
                l = i - j;
                k = a(random, 15, l);
            } else if (worldgenfeatureruinedportalpieces_position == WorldGenFeatureRuinedPortalPieces.Position.PARTLY_BURIED) {
                k = i - j + MathHelper.b(random, 2, 8);
            } else {
                k = i;
            }
        }

        List<BlockPosition> list = ImmutableList.of(new BlockPosition(structureboundingbox.g(), 0, structureboundingbox.i()), new BlockPosition(structureboundingbox.j(), 0, structureboundingbox.i()), new BlockPosition(structureboundingbox.g(), 0, structureboundingbox.l()), new BlockPosition(structureboundingbox.j(), 0, structureboundingbox.l()));
        List<BlockColumn> list1 = (List) list.stream().map((blockposition) -> {
            return chunkgenerator.getBaseColumn(blockposition.getX(), blockposition.getZ(), levelheightaccessor);
        }).collect(Collectors.toList());
        HeightMap.Type heightmap_type = worldgenfeatureruinedportalpieces_position == WorldGenFeatureRuinedPortalPieces.Position.ON_OCEAN_FLOOR ? HeightMap.Type.OCEAN_FLOOR_WG : HeightMap.Type.WORLD_SURFACE_WG;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        int i1;

        for (i1 = k; i1 > 15; --i1) {
            int j1 = 0;

            blockposition_mutableblockposition.d(0, i1, 0);
            Iterator iterator = list1.iterator();

            while (iterator.hasNext()) {
                BlockColumn blockcolumn = (BlockColumn) iterator.next();
                IBlockData iblockdata = blockcolumn.a(blockposition_mutableblockposition);

                if (heightmap_type.e().test(iblockdata)) {
                    ++j1;
                    if (j1 == 3) {
                        return i1;
                    }
                }
            }
        }

        return i1;
    }

    private static int a(Random random, int i, int j) {
        return i < j ? MathHelper.b(random, i, j) : j;
    }

    public static enum Type implements INamable {

        STANDARD("standard"), DESERT("desert"), JUNGLE("jungle"), SWAMP("swamp"), MOUNTAIN("mountain"), OCEAN("ocean"), NETHER("nether");

        public static final Codec<WorldGenFeatureRuinedPortal.Type> CODEC = INamable.a(WorldGenFeatureRuinedPortal.Type::values, WorldGenFeatureRuinedPortal.Type::a);
        private static final Map<String, WorldGenFeatureRuinedPortal.Type> BY_NAME = (Map) Arrays.stream(values()).collect(Collectors.toMap(WorldGenFeatureRuinedPortal.Type::a, (worldgenfeatureruinedportal_type) -> {
            return worldgenfeatureruinedportal_type;
        }));
        private final String name;

        private Type(String s) {
            this.name = s;
        }

        public String a() {
            return this.name;
        }

        public static WorldGenFeatureRuinedPortal.Type a(String s) {
            return (WorldGenFeatureRuinedPortal.Type) WorldGenFeatureRuinedPortal.Type.BY_NAME.get(s);
        }

        @Override
        public String getName() {
            return this.name;
        }
    }

    public static class a extends StructureStart<WorldGenFeatureRuinedPortalConfiguration> {

        protected a(StructureGenerator<WorldGenFeatureRuinedPortalConfiguration> structuregenerator, ChunkCoordIntPair chunkcoordintpair, int i, long j) {
            super(structuregenerator, chunkcoordintpair, i, j);
        }

        public void a(IRegistryCustom iregistrycustom, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, ChunkCoordIntPair chunkcoordintpair, BiomeBase biomebase, WorldGenFeatureRuinedPortalConfiguration worldgenfeatureruinedportalconfiguration, LevelHeightAccessor levelheightaccessor) {
            WorldGenFeatureRuinedPortalPieces.a worldgenfeatureruinedportalpieces_a = new WorldGenFeatureRuinedPortalPieces.a();
            WorldGenFeatureRuinedPortalPieces.Position worldgenfeatureruinedportalpieces_position;

            if (worldgenfeatureruinedportalconfiguration.portalType == WorldGenFeatureRuinedPortal.Type.DESERT) {
                worldgenfeatureruinedportalpieces_position = WorldGenFeatureRuinedPortalPieces.Position.PARTLY_BURIED;
                worldgenfeatureruinedportalpieces_a.airPocket = false;
                worldgenfeatureruinedportalpieces_a.mossiness = 0.0F;
            } else if (worldgenfeatureruinedportalconfiguration.portalType == WorldGenFeatureRuinedPortal.Type.JUNGLE) {
                worldgenfeatureruinedportalpieces_position = WorldGenFeatureRuinedPortalPieces.Position.ON_LAND_SURFACE;
                worldgenfeatureruinedportalpieces_a.airPocket = this.random.nextFloat() < 0.5F;
                worldgenfeatureruinedportalpieces_a.mossiness = 0.8F;
                worldgenfeatureruinedportalpieces_a.overgrown = true;
                worldgenfeatureruinedportalpieces_a.vines = true;
            } else if (worldgenfeatureruinedportalconfiguration.portalType == WorldGenFeatureRuinedPortal.Type.SWAMP) {
                worldgenfeatureruinedportalpieces_position = WorldGenFeatureRuinedPortalPieces.Position.ON_OCEAN_FLOOR;
                worldgenfeatureruinedportalpieces_a.airPocket = false;
                worldgenfeatureruinedportalpieces_a.mossiness = 0.5F;
                worldgenfeatureruinedportalpieces_a.vines = true;
            } else {
                boolean flag;

                if (worldgenfeatureruinedportalconfiguration.portalType == WorldGenFeatureRuinedPortal.Type.MOUNTAIN) {
                    flag = this.random.nextFloat() < 0.5F;
                    worldgenfeatureruinedportalpieces_position = flag ? WorldGenFeatureRuinedPortalPieces.Position.IN_MOUNTAIN : WorldGenFeatureRuinedPortalPieces.Position.ON_LAND_SURFACE;
                    worldgenfeatureruinedportalpieces_a.airPocket = flag || this.random.nextFloat() < 0.5F;
                } else if (worldgenfeatureruinedportalconfiguration.portalType == WorldGenFeatureRuinedPortal.Type.OCEAN) {
                    worldgenfeatureruinedportalpieces_position = WorldGenFeatureRuinedPortalPieces.Position.ON_OCEAN_FLOOR;
                    worldgenfeatureruinedportalpieces_a.airPocket = false;
                    worldgenfeatureruinedportalpieces_a.mossiness = 0.8F;
                } else if (worldgenfeatureruinedportalconfiguration.portalType == WorldGenFeatureRuinedPortal.Type.NETHER) {
                    worldgenfeatureruinedportalpieces_position = WorldGenFeatureRuinedPortalPieces.Position.IN_NETHER;
                    worldgenfeatureruinedportalpieces_a.airPocket = this.random.nextFloat() < 0.5F;
                    worldgenfeatureruinedportalpieces_a.mossiness = 0.0F;
                    worldgenfeatureruinedportalpieces_a.replaceWithBlackstone = true;
                } else {
                    flag = this.random.nextFloat() < 0.5F;
                    worldgenfeatureruinedportalpieces_position = flag ? WorldGenFeatureRuinedPortalPieces.Position.UNDERGROUND : WorldGenFeatureRuinedPortalPieces.Position.ON_LAND_SURFACE;
                    worldgenfeatureruinedportalpieces_a.airPocket = flag || this.random.nextFloat() < 0.5F;
                }
            }

            MinecraftKey minecraftkey;

            if (this.random.nextFloat() < 0.05F) {
                minecraftkey = new MinecraftKey(WorldGenFeatureRuinedPortal.STRUCTURE_LOCATION_GIANT_PORTALS[this.random.nextInt(WorldGenFeatureRuinedPortal.STRUCTURE_LOCATION_GIANT_PORTALS.length)]);
            } else {
                minecraftkey = new MinecraftKey(WorldGenFeatureRuinedPortal.STRUCTURE_LOCATION_PORTALS[this.random.nextInt(WorldGenFeatureRuinedPortal.STRUCTURE_LOCATION_PORTALS.length)]);
            }

            DefinedStructure definedstructure = definedstructuremanager.a(minecraftkey);
            EnumBlockRotation enumblockrotation = (EnumBlockRotation) SystemUtils.a((Object[]) EnumBlockRotation.values(), (Random) this.random);
            EnumBlockMirror enumblockmirror = this.random.nextFloat() < 0.5F ? EnumBlockMirror.NONE : EnumBlockMirror.FRONT_BACK;
            BlockPosition blockposition = new BlockPosition(definedstructure.a().getX() / 2, 0, definedstructure.a().getZ() / 2);
            BlockPosition blockposition1 = chunkcoordintpair.l();
            StructureBoundingBox structureboundingbox = definedstructure.a(blockposition1, enumblockrotation, blockposition, enumblockmirror);
            BlockPosition blockposition2 = structureboundingbox.f();
            int i = blockposition2.getX();
            int j = blockposition2.getZ();
            int k = chunkgenerator.getBaseHeight(i, j, WorldGenFeatureRuinedPortalPieces.a(worldgenfeatureruinedportalpieces_position), levelheightaccessor) - 1;
            int l = WorldGenFeatureRuinedPortal.a(this.random, chunkgenerator, worldgenfeatureruinedportalpieces_position, worldgenfeatureruinedportalpieces_a.airPocket, k, structureboundingbox.d(), structureboundingbox, levelheightaccessor);
            BlockPosition blockposition3 = new BlockPosition(blockposition1.getX(), l, blockposition1.getZ());

            if (worldgenfeatureruinedportalconfiguration.portalType == WorldGenFeatureRuinedPortal.Type.MOUNTAIN || worldgenfeatureruinedportalconfiguration.portalType == WorldGenFeatureRuinedPortal.Type.OCEAN || worldgenfeatureruinedportalconfiguration.portalType == WorldGenFeatureRuinedPortal.Type.STANDARD) {
                worldgenfeatureruinedportalpieces_a.cold = WorldGenFeatureRuinedPortal.a(blockposition3, biomebase);
            }

            this.a((StructurePiece) (new WorldGenFeatureRuinedPortalPieces(definedstructuremanager, blockposition3, worldgenfeatureruinedportalpieces_position, worldgenfeatureruinedportalpieces_a, minecraftkey, definedstructure, enumblockrotation, enumblockmirror, blockposition)));
        }
    }
}
