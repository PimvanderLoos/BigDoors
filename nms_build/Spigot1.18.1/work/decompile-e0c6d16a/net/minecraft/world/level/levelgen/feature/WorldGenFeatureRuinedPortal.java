package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.INamable;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.BlockColumn;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.EnumBlockMirror;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureRuinedPortalConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.WorldGenFeatureRuinedPortalPieces;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructure;

public class WorldGenFeatureRuinedPortal extends StructureGenerator<WorldGenFeatureRuinedPortalConfiguration> {

    private static final String[] STRUCTURE_LOCATION_PORTALS = new String[]{"ruined_portal/portal_1", "ruined_portal/portal_2", "ruined_portal/portal_3", "ruined_portal/portal_4", "ruined_portal/portal_5", "ruined_portal/portal_6", "ruined_portal/portal_7", "ruined_portal/portal_8", "ruined_portal/portal_9", "ruined_portal/portal_10"};
    private static final String[] STRUCTURE_LOCATION_GIANT_PORTALS = new String[]{"ruined_portal/giant_portal_1", "ruined_portal/giant_portal_2", "ruined_portal/giant_portal_3"};
    private static final float PROBABILITY_OF_GIANT_PORTAL = 0.05F;
    private static final float PROBABILITY_OF_AIR_POCKET = 0.5F;
    private static final float PROBABILITY_OF_UNDERGROUND = 0.5F;
    private static final float UNDERWATER_MOSSINESS = 0.8F;
    private static final float JUNGLE_MOSSINESS = 0.8F;
    private static final float SWAMP_MOSSINESS = 0.5F;
    private static final int MIN_Y_INDEX = 15;

    public WorldGenFeatureRuinedPortal(Codec<WorldGenFeatureRuinedPortalConfiguration> codec) {
        super(codec, WorldGenFeatureRuinedPortal::pieceGeneratorSupplier);
    }

    private static Optional<PieceGenerator<WorldGenFeatureRuinedPortalConfiguration>> pieceGeneratorSupplier(PieceGeneratorSupplier.a<WorldGenFeatureRuinedPortalConfiguration> piecegeneratorsupplier_a) {
        WorldGenFeatureRuinedPortalPieces.a worldgenfeatureruinedportalpieces_a = new WorldGenFeatureRuinedPortalPieces.a();
        WorldGenFeatureRuinedPortalConfiguration worldgenfeatureruinedportalconfiguration = (WorldGenFeatureRuinedPortalConfiguration) piecegeneratorsupplier_a.config();
        SeededRandom seededrandom = new SeededRandom(new LegacyRandomSource(0L));

        seededrandom.setLargeFeatureSeed(piecegeneratorsupplier_a.seed(), piecegeneratorsupplier_a.chunkPos().x, piecegeneratorsupplier_a.chunkPos().z);
        WorldGenFeatureRuinedPortalPieces.Position worldgenfeatureruinedportalpieces_position;

        if (worldgenfeatureruinedportalconfiguration.portalType == WorldGenFeatureRuinedPortal.Type.DESERT) {
            worldgenfeatureruinedportalpieces_position = WorldGenFeatureRuinedPortalPieces.Position.PARTLY_BURIED;
            worldgenfeatureruinedportalpieces_a.airPocket = false;
            worldgenfeatureruinedportalpieces_a.mossiness = 0.0F;
        } else if (worldgenfeatureruinedportalconfiguration.portalType == WorldGenFeatureRuinedPortal.Type.JUNGLE) {
            worldgenfeatureruinedportalpieces_position = WorldGenFeatureRuinedPortalPieces.Position.ON_LAND_SURFACE;
            worldgenfeatureruinedportalpieces_a.airPocket = seededrandom.nextFloat() < 0.5F;
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
                flag = seededrandom.nextFloat() < 0.5F;
                worldgenfeatureruinedportalpieces_position = flag ? WorldGenFeatureRuinedPortalPieces.Position.IN_MOUNTAIN : WorldGenFeatureRuinedPortalPieces.Position.ON_LAND_SURFACE;
                worldgenfeatureruinedportalpieces_a.airPocket = flag || seededrandom.nextFloat() < 0.5F;
            } else if (worldgenfeatureruinedportalconfiguration.portalType == WorldGenFeatureRuinedPortal.Type.OCEAN) {
                worldgenfeatureruinedportalpieces_position = WorldGenFeatureRuinedPortalPieces.Position.ON_OCEAN_FLOOR;
                worldgenfeatureruinedportalpieces_a.airPocket = false;
                worldgenfeatureruinedportalpieces_a.mossiness = 0.8F;
            } else if (worldgenfeatureruinedportalconfiguration.portalType == WorldGenFeatureRuinedPortal.Type.NETHER) {
                worldgenfeatureruinedportalpieces_position = WorldGenFeatureRuinedPortalPieces.Position.IN_NETHER;
                worldgenfeatureruinedportalpieces_a.airPocket = seededrandom.nextFloat() < 0.5F;
                worldgenfeatureruinedportalpieces_a.mossiness = 0.0F;
                worldgenfeatureruinedportalpieces_a.replaceWithBlackstone = true;
            } else {
                flag = seededrandom.nextFloat() < 0.5F;
                worldgenfeatureruinedportalpieces_position = flag ? WorldGenFeatureRuinedPortalPieces.Position.UNDERGROUND : WorldGenFeatureRuinedPortalPieces.Position.ON_LAND_SURFACE;
                worldgenfeatureruinedportalpieces_a.airPocket = flag || seededrandom.nextFloat() < 0.5F;
            }
        }

        MinecraftKey minecraftkey;

        if (seededrandom.nextFloat() < 0.05F) {
            minecraftkey = new MinecraftKey(WorldGenFeatureRuinedPortal.STRUCTURE_LOCATION_GIANT_PORTALS[seededrandom.nextInt(WorldGenFeatureRuinedPortal.STRUCTURE_LOCATION_GIANT_PORTALS.length)]);
        } else {
            minecraftkey = new MinecraftKey(WorldGenFeatureRuinedPortal.STRUCTURE_LOCATION_PORTALS[seededrandom.nextInt(WorldGenFeatureRuinedPortal.STRUCTURE_LOCATION_PORTALS.length)]);
        }

        DefinedStructure definedstructure = piecegeneratorsupplier_a.structureManager().getOrCreate(minecraftkey);
        EnumBlockRotation enumblockrotation = (EnumBlockRotation) SystemUtils.getRandom((Object[]) EnumBlockRotation.values(), seededrandom);
        EnumBlockMirror enumblockmirror = seededrandom.nextFloat() < 0.5F ? EnumBlockMirror.NONE : EnumBlockMirror.FRONT_BACK;
        BlockPosition blockposition = new BlockPosition(definedstructure.getSize().getX() / 2, 0, definedstructure.getSize().getZ() / 2);
        BlockPosition blockposition1 = piecegeneratorsupplier_a.chunkPos().getWorldPosition();
        StructureBoundingBox structureboundingbox = definedstructure.getBoundingBox(blockposition1, enumblockrotation, blockposition, enumblockmirror);
        BlockPosition blockposition2 = structureboundingbox.getCenter();
        int i = piecegeneratorsupplier_a.chunkGenerator().getBaseHeight(blockposition2.getX(), blockposition2.getZ(), WorldGenFeatureRuinedPortalPieces.getHeightMapType(worldgenfeatureruinedportalpieces_position), piecegeneratorsupplier_a.heightAccessor()) - 1;
        int j = findSuitableY(seededrandom, piecegeneratorsupplier_a.chunkGenerator(), worldgenfeatureruinedportalpieces_position, worldgenfeatureruinedportalpieces_a.airPocket, i, structureboundingbox.getYSpan(), structureboundingbox, piecegeneratorsupplier_a.heightAccessor());
        BlockPosition blockposition3 = new BlockPosition(blockposition1.getX(), j, blockposition1.getZ());

        return !piecegeneratorsupplier_a.validBiome().test(piecegeneratorsupplier_a.chunkGenerator().getNoiseBiome(QuartPos.fromBlock(blockposition3.getX()), QuartPos.fromBlock(blockposition3.getY()), QuartPos.fromBlock(blockposition3.getZ()))) ? Optional.empty() : Optional.of((structurepiecesbuilder, piecegenerator_a) -> {
            if (worldgenfeatureruinedportalconfiguration.portalType == WorldGenFeatureRuinedPortal.Type.MOUNTAIN || worldgenfeatureruinedportalconfiguration.portalType == WorldGenFeatureRuinedPortal.Type.OCEAN || worldgenfeatureruinedportalconfiguration.portalType == WorldGenFeatureRuinedPortal.Type.STANDARD) {
                worldgenfeatureruinedportalpieces_a.cold = isCold(blockposition3, piecegeneratorsupplier_a.chunkGenerator().getNoiseBiome(QuartPos.fromBlock(blockposition3.getX()), QuartPos.fromBlock(blockposition3.getY()), QuartPos.fromBlock(blockposition3.getZ())));
            }

            structurepiecesbuilder.addPiece(new WorldGenFeatureRuinedPortalPieces(piecegenerator_a.structureManager(), blockposition3, worldgenfeatureruinedportalpieces_position, worldgenfeatureruinedportalpieces_a, minecraftkey, definedstructure, enumblockrotation, enumblockmirror, blockposition));
        });
    }

    private static boolean isCold(BlockPosition blockposition, BiomeBase biomebase) {
        return biomebase.coldEnoughToSnow(blockposition);
    }

    private static int findSuitableY(Random random, ChunkGenerator chunkgenerator, WorldGenFeatureRuinedPortalPieces.Position worldgenfeatureruinedportalpieces_position, boolean flag, int i, int j, StructureBoundingBox structureboundingbox, LevelHeightAccessor levelheightaccessor) {
        int k = levelheightaccessor.getMinBuildHeight() + 15;
        int l;

        if (worldgenfeatureruinedportalpieces_position == WorldGenFeatureRuinedPortalPieces.Position.IN_NETHER) {
            if (flag) {
                l = MathHelper.randomBetweenInclusive(random, 32, 100);
            } else if (random.nextFloat() < 0.5F) {
                l = MathHelper.randomBetweenInclusive(random, 27, 29);
            } else {
                l = MathHelper.randomBetweenInclusive(random, 29, 100);
            }
        } else {
            int i1;

            if (worldgenfeatureruinedportalpieces_position == WorldGenFeatureRuinedPortalPieces.Position.IN_MOUNTAIN) {
                i1 = i - j;
                l = getRandomWithinInterval(random, 70, i1);
            } else if (worldgenfeatureruinedportalpieces_position == WorldGenFeatureRuinedPortalPieces.Position.UNDERGROUND) {
                i1 = i - j;
                l = getRandomWithinInterval(random, k, i1);
            } else if (worldgenfeatureruinedportalpieces_position == WorldGenFeatureRuinedPortalPieces.Position.PARTLY_BURIED) {
                l = i - j + MathHelper.randomBetweenInclusive(random, 2, 8);
            } else {
                l = i;
            }
        }

        List<BlockPosition> list = ImmutableList.of(new BlockPosition(structureboundingbox.minX(), 0, structureboundingbox.minZ()), new BlockPosition(structureboundingbox.maxX(), 0, structureboundingbox.minZ()), new BlockPosition(structureboundingbox.minX(), 0, structureboundingbox.maxZ()), new BlockPosition(structureboundingbox.maxX(), 0, structureboundingbox.maxZ()));
        List<BlockColumn> list1 = (List) list.stream().map((blockposition) -> {
            return chunkgenerator.getBaseColumn(blockposition.getX(), blockposition.getZ(), levelheightaccessor);
        }).collect(Collectors.toList());
        HeightMap.Type heightmap_type = worldgenfeatureruinedportalpieces_position == WorldGenFeatureRuinedPortalPieces.Position.ON_OCEAN_FLOOR ? HeightMap.Type.OCEAN_FLOOR_WG : HeightMap.Type.WORLD_SURFACE_WG;

        int j1;

        for (j1 = l; j1 > k; --j1) {
            int k1 = 0;
            Iterator iterator = list1.iterator();

            while (iterator.hasNext()) {
                BlockColumn blockcolumn = (BlockColumn) iterator.next();
                IBlockData iblockdata = blockcolumn.getBlock(j1);

                if (heightmap_type.isOpaque().test(iblockdata)) {
                    ++k1;
                    if (k1 == 3) {
                        return j1;
                    }
                }
            }
        }

        return j1;
    }

    private static int getRandomWithinInterval(Random random, int i, int j) {
        return i < j ? MathHelper.randomBetweenInclusive(random, i, j) : j;
    }

    public static enum Type implements INamable {

        STANDARD("standard"), DESERT("desert"), JUNGLE("jungle"), SWAMP("swamp"), MOUNTAIN("mountain"), OCEAN("ocean"), NETHER("nether");

        public static final Codec<WorldGenFeatureRuinedPortal.Type> CODEC = INamable.fromEnum(WorldGenFeatureRuinedPortal.Type::values, WorldGenFeatureRuinedPortal.Type::byName);
        private static final Map<String, WorldGenFeatureRuinedPortal.Type> BY_NAME = (Map) Arrays.stream(values()).collect(Collectors.toMap(WorldGenFeatureRuinedPortal.Type::getName, (worldgenfeatureruinedportal_type) -> {
            return worldgenfeatureruinedportal_type;
        }));
        private final String name;

        private Type(String s) {
            this.name = s;
        }

        public String getName() {
            return this.name;
        }

        public static WorldGenFeatureRuinedPortal.Type byName(String s) {
            return (WorldGenFeatureRuinedPortal.Type) WorldGenFeatureRuinedPortal.Type.BY_NAME.get(s);
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
