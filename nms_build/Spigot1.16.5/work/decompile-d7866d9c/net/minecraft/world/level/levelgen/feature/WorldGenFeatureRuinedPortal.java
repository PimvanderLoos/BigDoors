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
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.INamable;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.EnumBlockMirror;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureRuinedPortalConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.WorldGenFeatureRuinedPortalPieces;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;

public class WorldGenFeatureRuinedPortal extends StructureGenerator<WorldGenFeatureRuinedPortalConfiguration> {

    private static final String[] u = new String[]{"ruined_portal/portal_1", "ruined_portal/portal_2", "ruined_portal/portal_3", "ruined_portal/portal_4", "ruined_portal/portal_5", "ruined_portal/portal_6", "ruined_portal/portal_7", "ruined_portal/portal_8", "ruined_portal/portal_9", "ruined_portal/portal_10"};
    private static final String[] v = new String[]{"ruined_portal/giant_portal_1", "ruined_portal/giant_portal_2", "ruined_portal/giant_portal_3"};

    public WorldGenFeatureRuinedPortal(Codec<WorldGenFeatureRuinedPortalConfiguration> codec) {
        super(codec);
    }

    @Override
    public StructureGenerator.a<WorldGenFeatureRuinedPortalConfiguration> a() {
        return WorldGenFeatureRuinedPortal.a::new;
    }

    private static boolean b(BlockPosition blockposition, BiomeBase biomebase) {
        return biomebase.getAdjustedTemperature(blockposition) < 0.15F;
    }

    private static int b(Random random, ChunkGenerator chunkgenerator, WorldGenFeatureRuinedPortalPieces.Position worldgenfeatureruinedportalpieces_position, boolean flag, int i, int j, StructureBoundingBox structureboundingbox) {
        int k;

        if (worldgenfeatureruinedportalpieces_position == WorldGenFeatureRuinedPortalPieces.Position.IN_NETHER) {
            if (flag) {
                k = a(random, 32, 100);
            } else if (random.nextFloat() < 0.5F) {
                k = a(random, 27, 29);
            } else {
                k = a(random, 29, 100);
            }
        } else {
            int l;

            if (worldgenfeatureruinedportalpieces_position == WorldGenFeatureRuinedPortalPieces.Position.IN_MOUNTAIN) {
                l = i - j;
                k = b(random, 70, l);
            } else if (worldgenfeatureruinedportalpieces_position == WorldGenFeatureRuinedPortalPieces.Position.UNDERGROUND) {
                l = i - j;
                k = b(random, 15, l);
            } else if (worldgenfeatureruinedportalpieces_position == WorldGenFeatureRuinedPortalPieces.Position.PARTLY_BURIED) {
                k = i - j + a(random, 2, 8);
            } else {
                k = i;
            }
        }

        List<BlockPosition> list = ImmutableList.of(new BlockPosition(structureboundingbox.a, 0, structureboundingbox.c), new BlockPosition(structureboundingbox.d, 0, structureboundingbox.c), new BlockPosition(structureboundingbox.a, 0, structureboundingbox.f), new BlockPosition(structureboundingbox.d, 0, structureboundingbox.f));
        List<IBlockAccess> list1 = (List) list.stream().map((blockposition) -> {
            return chunkgenerator.a(blockposition.getX(), blockposition.getZ());
        }).collect(Collectors.toList());
        HeightMap.Type heightmap_type = worldgenfeatureruinedportalpieces_position == WorldGenFeatureRuinedPortalPieces.Position.ON_OCEAN_FLOOR ? HeightMap.Type.OCEAN_FLOOR_WG : HeightMap.Type.WORLD_SURFACE_WG;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        int i1;

        for (i1 = k; i1 > 15; --i1) {
            int j1 = 0;

            blockposition_mutableblockposition.d(0, i1, 0);
            Iterator iterator = list1.iterator();

            while (iterator.hasNext()) {
                IBlockAccess iblockaccess = (IBlockAccess) iterator.next();
                IBlockData iblockdata = iblockaccess.getType(blockposition_mutableblockposition);

                if (iblockdata != null && heightmap_type.e().test(iblockdata)) {
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
        return random.nextInt(j - i + 1) + i;
    }

    private static int b(Random random, int i, int j) {
        return i < j ? a(random, i, j) : j;
    }

    public static enum Type implements INamable {

        STANDARD("standard"), DESERT("desert"), JUNGLE("jungle"), SWAMP("swamp"), MOUNTAIN("mountain"), OCEAN("ocean"), NETHER("nether");

        public static final Codec<WorldGenFeatureRuinedPortal.Type> h = INamable.a(WorldGenFeatureRuinedPortal.Type::values, WorldGenFeatureRuinedPortal.Type::a);
        private static final Map<String, WorldGenFeatureRuinedPortal.Type> i = (Map) Arrays.stream(values()).collect(Collectors.toMap(WorldGenFeatureRuinedPortal.Type::b, (worldgenfeatureruinedportal_type) -> {
            return worldgenfeatureruinedportal_type;
        }));
        private final String j;

        private Type(String s) {
            this.j = s;
        }

        public String b() {
            return this.j;
        }

        public static WorldGenFeatureRuinedPortal.Type a(String s) {
            return (WorldGenFeatureRuinedPortal.Type) WorldGenFeatureRuinedPortal.Type.i.get(s);
        }

        @Override
        public String getName() {
            return this.j;
        }
    }

    public static class a extends StructureStart<WorldGenFeatureRuinedPortalConfiguration> {

        protected a(StructureGenerator<WorldGenFeatureRuinedPortalConfiguration> structuregenerator, int i, int j, StructureBoundingBox structureboundingbox, int k, long l) {
            super(structuregenerator, i, j, structureboundingbox, k, l);
        }

        public void a(IRegistryCustom iregistrycustom, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, int i, int j, BiomeBase biomebase, WorldGenFeatureRuinedPortalConfiguration worldgenfeatureruinedportalconfiguration) {
            WorldGenFeatureRuinedPortalPieces.a worldgenfeatureruinedportalpieces_a = new WorldGenFeatureRuinedPortalPieces.a();
            WorldGenFeatureRuinedPortalPieces.Position worldgenfeatureruinedportalpieces_position;

            if (worldgenfeatureruinedportalconfiguration.b == WorldGenFeatureRuinedPortal.Type.DESERT) {
                worldgenfeatureruinedportalpieces_position = WorldGenFeatureRuinedPortalPieces.Position.PARTLY_BURIED;
                worldgenfeatureruinedportalpieces_a.d = false;
                worldgenfeatureruinedportalpieces_a.c = 0.0F;
            } else if (worldgenfeatureruinedportalconfiguration.b == WorldGenFeatureRuinedPortal.Type.JUNGLE) {
                worldgenfeatureruinedportalpieces_position = WorldGenFeatureRuinedPortalPieces.Position.ON_LAND_SURFACE;
                worldgenfeatureruinedportalpieces_a.d = this.d.nextFloat() < 0.5F;
                worldgenfeatureruinedportalpieces_a.c = 0.8F;
                worldgenfeatureruinedportalpieces_a.e = true;
                worldgenfeatureruinedportalpieces_a.f = true;
            } else if (worldgenfeatureruinedportalconfiguration.b == WorldGenFeatureRuinedPortal.Type.SWAMP) {
                worldgenfeatureruinedportalpieces_position = WorldGenFeatureRuinedPortalPieces.Position.ON_OCEAN_FLOOR;
                worldgenfeatureruinedportalpieces_a.d = false;
                worldgenfeatureruinedportalpieces_a.c = 0.5F;
                worldgenfeatureruinedportalpieces_a.f = true;
            } else {
                boolean flag;

                if (worldgenfeatureruinedportalconfiguration.b == WorldGenFeatureRuinedPortal.Type.MOUNTAIN) {
                    flag = this.d.nextFloat() < 0.5F;
                    worldgenfeatureruinedportalpieces_position = flag ? WorldGenFeatureRuinedPortalPieces.Position.IN_MOUNTAIN : WorldGenFeatureRuinedPortalPieces.Position.ON_LAND_SURFACE;
                    worldgenfeatureruinedportalpieces_a.d = flag || this.d.nextFloat() < 0.5F;
                } else if (worldgenfeatureruinedportalconfiguration.b == WorldGenFeatureRuinedPortal.Type.OCEAN) {
                    worldgenfeatureruinedportalpieces_position = WorldGenFeatureRuinedPortalPieces.Position.ON_OCEAN_FLOOR;
                    worldgenfeatureruinedportalpieces_a.d = false;
                    worldgenfeatureruinedportalpieces_a.c = 0.8F;
                } else if (worldgenfeatureruinedportalconfiguration.b == WorldGenFeatureRuinedPortal.Type.NETHER) {
                    worldgenfeatureruinedportalpieces_position = WorldGenFeatureRuinedPortalPieces.Position.IN_NETHER;
                    worldgenfeatureruinedportalpieces_a.d = this.d.nextFloat() < 0.5F;
                    worldgenfeatureruinedportalpieces_a.c = 0.0F;
                    worldgenfeatureruinedportalpieces_a.g = true;
                } else {
                    flag = this.d.nextFloat() < 0.5F;
                    worldgenfeatureruinedportalpieces_position = flag ? WorldGenFeatureRuinedPortalPieces.Position.UNDERGROUND : WorldGenFeatureRuinedPortalPieces.Position.ON_LAND_SURFACE;
                    worldgenfeatureruinedportalpieces_a.d = flag || this.d.nextFloat() < 0.5F;
                }
            }

            MinecraftKey minecraftkey;

            if (this.d.nextFloat() < 0.05F) {
                minecraftkey = new MinecraftKey(WorldGenFeatureRuinedPortal.v[this.d.nextInt(WorldGenFeatureRuinedPortal.v.length)]);
            } else {
                minecraftkey = new MinecraftKey(WorldGenFeatureRuinedPortal.u[this.d.nextInt(WorldGenFeatureRuinedPortal.u.length)]);
            }

            DefinedStructure definedstructure = definedstructuremanager.a(minecraftkey);
            EnumBlockRotation enumblockrotation = (EnumBlockRotation) SystemUtils.a((Object[]) EnumBlockRotation.values(), (Random) this.d);
            EnumBlockMirror enumblockmirror = this.d.nextFloat() < 0.5F ? EnumBlockMirror.NONE : EnumBlockMirror.FRONT_BACK;
            BlockPosition blockposition = new BlockPosition(definedstructure.a().getX() / 2, 0, definedstructure.a().getZ() / 2);
            BlockPosition blockposition1 = (new ChunkCoordIntPair(i, j)).l();
            StructureBoundingBox structureboundingbox = definedstructure.a(blockposition1, enumblockrotation, blockposition, enumblockmirror);
            BaseBlockPosition baseblockposition = structureboundingbox.g();
            int k = baseblockposition.getX();
            int l = baseblockposition.getZ();
            int i1 = chunkgenerator.getBaseHeight(k, l, WorldGenFeatureRuinedPortalPieces.a(worldgenfeatureruinedportalpieces_position)) - 1;
            int j1 = WorldGenFeatureRuinedPortal.b(this.d, chunkgenerator, worldgenfeatureruinedportalpieces_position, worldgenfeatureruinedportalpieces_a.d, i1, structureboundingbox.e(), structureboundingbox);
            BlockPosition blockposition2 = new BlockPosition(blockposition1.getX(), j1, blockposition1.getZ());

            if (worldgenfeatureruinedportalconfiguration.b == WorldGenFeatureRuinedPortal.Type.MOUNTAIN || worldgenfeatureruinedportalconfiguration.b == WorldGenFeatureRuinedPortal.Type.OCEAN || worldgenfeatureruinedportalconfiguration.b == WorldGenFeatureRuinedPortal.Type.STANDARD) {
                worldgenfeatureruinedportalpieces_a.b = WorldGenFeatureRuinedPortal.b(blockposition2, biomebase);
            }

            this.b.add(new WorldGenFeatureRuinedPortalPieces(blockposition2, worldgenfeatureruinedportalpieces_position, worldgenfeatureruinedportalpieces_a, minecraftkey, definedstructure, enumblockrotation, enumblockmirror, blockposition));
            this.b();
        }
    }
}
