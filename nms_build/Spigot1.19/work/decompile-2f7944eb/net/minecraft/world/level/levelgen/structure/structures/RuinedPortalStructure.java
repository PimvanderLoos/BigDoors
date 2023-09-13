package net.minecraft.world.level.levelgen.structure.structures;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockColumn;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.EnumBlockMirror;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructure;

public class RuinedPortalStructure extends Structure {

    private static final String[] STRUCTURE_LOCATION_PORTALS = new String[]{"ruined_portal/portal_1", "ruined_portal/portal_2", "ruined_portal/portal_3", "ruined_portal/portal_4", "ruined_portal/portal_5", "ruined_portal/portal_6", "ruined_portal/portal_7", "ruined_portal/portal_8", "ruined_portal/portal_9", "ruined_portal/portal_10"};
    private static final String[] STRUCTURE_LOCATION_GIANT_PORTALS = new String[]{"ruined_portal/giant_portal_1", "ruined_portal/giant_portal_2", "ruined_portal/giant_portal_3"};
    private static final float PROBABILITY_OF_GIANT_PORTAL = 0.05F;
    private static final int MIN_Y_INDEX = 15;
    private final List<RuinedPortalStructure.a> setups;
    public static final Codec<RuinedPortalStructure> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(settingsCodec(instance), ExtraCodecs.nonEmptyList(RuinedPortalStructure.a.CODEC.listOf()).fieldOf("setups").forGetter((ruinedportalstructure) -> {
            return ruinedportalstructure.setups;
        })).apply(instance, RuinedPortalStructure::new);
    });

    public RuinedPortalStructure(Structure.c structure_c, List<RuinedPortalStructure.a> list) {
        super(structure_c);
        this.setups = list;
    }

    public RuinedPortalStructure(Structure.c structure_c, RuinedPortalStructure.a ruinedportalstructure_a) {
        this(structure_c, List.of(ruinedportalstructure_a));
    }

    @Override
    public Optional<Structure.b> findGenerationPoint(Structure.a structure_a) {
        RuinedPortalPiece.a ruinedportalpiece_a = new RuinedPortalPiece.a();
        SeededRandom seededrandom = structure_a.random();
        RuinedPortalStructure.a ruinedportalstructure_a = null;

        if (this.setups.size() > 1) {
            float f = 0.0F;

            RuinedPortalStructure.a ruinedportalstructure_a1;

            for (Iterator iterator = this.setups.iterator(); iterator.hasNext(); f += ruinedportalstructure_a1.weight()) {
                ruinedportalstructure_a1 = (RuinedPortalStructure.a) iterator.next();
            }

            float f1 = seededrandom.nextFloat();
            Iterator iterator1 = this.setups.iterator();

            while (iterator1.hasNext()) {
                RuinedPortalStructure.a ruinedportalstructure_a2 = (RuinedPortalStructure.a) iterator1.next();

                f1 -= ruinedportalstructure_a2.weight() / f;
                if (f1 < 0.0F) {
                    ruinedportalstructure_a = ruinedportalstructure_a2;
                    break;
                }
            }
        } else {
            ruinedportalstructure_a = (RuinedPortalStructure.a) this.setups.get(0);
        }

        if (ruinedportalstructure_a == null) {
            throw new IllegalStateException();
        } else {
            ruinedportalpiece_a.airPocket = sample(seededrandom, ruinedportalstructure_a.airPocketProbability());
            ruinedportalpiece_a.mossiness = ruinedportalstructure_a.mossiness();
            ruinedportalpiece_a.overgrown = ruinedportalstructure_a.overgrown();
            ruinedportalpiece_a.vines = ruinedportalstructure_a.vines();
            ruinedportalpiece_a.replaceWithBlackstone = ruinedportalstructure_a.replaceWithBlackstone();
            MinecraftKey minecraftkey;

            if (seededrandom.nextFloat() < 0.05F) {
                minecraftkey = new MinecraftKey(RuinedPortalStructure.STRUCTURE_LOCATION_GIANT_PORTALS[seededrandom.nextInt(RuinedPortalStructure.STRUCTURE_LOCATION_GIANT_PORTALS.length)]);
            } else {
                minecraftkey = new MinecraftKey(RuinedPortalStructure.STRUCTURE_LOCATION_PORTALS[seededrandom.nextInt(RuinedPortalStructure.STRUCTURE_LOCATION_PORTALS.length)]);
            }

            DefinedStructure definedstructure = structure_a.structureTemplateManager().getOrCreate(minecraftkey);
            EnumBlockRotation enumblockrotation = (EnumBlockRotation) SystemUtils.getRandom((Object[]) EnumBlockRotation.values(), seededrandom);
            EnumBlockMirror enumblockmirror = seededrandom.nextFloat() < 0.5F ? EnumBlockMirror.NONE : EnumBlockMirror.FRONT_BACK;
            BlockPosition blockposition = new BlockPosition(definedstructure.getSize().getX() / 2, 0, definedstructure.getSize().getZ() / 2);
            ChunkGenerator chunkgenerator = structure_a.chunkGenerator();
            LevelHeightAccessor levelheightaccessor = structure_a.heightAccessor();
            RandomState randomstate = structure_a.randomState();
            BlockPosition blockposition1 = structure_a.chunkPos().getWorldPosition();
            StructureBoundingBox structureboundingbox = definedstructure.getBoundingBox(blockposition1, enumblockrotation, blockposition, enumblockmirror);
            BlockPosition blockposition2 = structureboundingbox.getCenter();
            int i = chunkgenerator.getBaseHeight(blockposition2.getX(), blockposition2.getZ(), RuinedPortalPiece.getHeightMapType(ruinedportalstructure_a.placement()), levelheightaccessor, randomstate) - 1;
            int j = findSuitableY(seededrandom, chunkgenerator, ruinedportalstructure_a.placement(), ruinedportalpiece_a.airPocket, i, structureboundingbox.getYSpan(), structureboundingbox, levelheightaccessor, randomstate);
            BlockPosition blockposition3 = new BlockPosition(blockposition1.getX(), j, blockposition1.getZ());

            return Optional.of(new Structure.b(blockposition3, (structurepiecesbuilder) -> {
                if (ruinedportalstructure_a.canBeCold()) {
                    ruinedportalpiece_a.cold = isCold(blockposition3, structure_a.chunkGenerator().getBiomeSource().getNoiseBiome(QuartPos.fromBlock(blockposition3.getX()), QuartPos.fromBlock(blockposition3.getY()), QuartPos.fromBlock(blockposition3.getZ()), randomstate.sampler()));
                }

                structurepiecesbuilder.addPiece(new RuinedPortalPiece(structure_a.structureTemplateManager(), blockposition3, ruinedportalstructure_a.placement(), ruinedportalpiece_a, minecraftkey, definedstructure, enumblockrotation, enumblockmirror, blockposition));
            }));
        }
    }

    private static boolean sample(SeededRandom seededrandom, float f) {
        return f == 0.0F ? false : (f == 1.0F ? true : seededrandom.nextFloat() < f);
    }

    private static boolean isCold(BlockPosition blockposition, Holder<BiomeBase> holder) {
        return ((BiomeBase) holder.value()).coldEnoughToSnow(blockposition);
    }

    private static int findSuitableY(RandomSource randomsource, ChunkGenerator chunkgenerator, RuinedPortalPiece.b ruinedportalpiece_b, boolean flag, int i, int j, StructureBoundingBox structureboundingbox, LevelHeightAccessor levelheightaccessor, RandomState randomstate) {
        int k = levelheightaccessor.getMinBuildHeight() + 15;
        int l;

        if (ruinedportalpiece_b == RuinedPortalPiece.b.IN_NETHER) {
            if (flag) {
                l = MathHelper.randomBetweenInclusive(randomsource, 32, 100);
            } else if (randomsource.nextFloat() < 0.5F) {
                l = MathHelper.randomBetweenInclusive(randomsource, 27, 29);
            } else {
                l = MathHelper.randomBetweenInclusive(randomsource, 29, 100);
            }
        } else {
            int i1;

            if (ruinedportalpiece_b == RuinedPortalPiece.b.IN_MOUNTAIN) {
                i1 = i - j;
                l = getRandomWithinInterval(randomsource, 70, i1);
            } else if (ruinedportalpiece_b == RuinedPortalPiece.b.UNDERGROUND) {
                i1 = i - j;
                l = getRandomWithinInterval(randomsource, k, i1);
            } else if (ruinedportalpiece_b == RuinedPortalPiece.b.PARTLY_BURIED) {
                l = i - j + MathHelper.randomBetweenInclusive(randomsource, 2, 8);
            } else {
                l = i;
            }
        }

        List<BlockPosition> list = ImmutableList.of(new BlockPosition(structureboundingbox.minX(), 0, structureboundingbox.minZ()), new BlockPosition(structureboundingbox.maxX(), 0, structureboundingbox.minZ()), new BlockPosition(structureboundingbox.minX(), 0, structureboundingbox.maxZ()), new BlockPosition(structureboundingbox.maxX(), 0, structureboundingbox.maxZ()));
        List<BlockColumn> list1 = (List) list.stream().map((blockposition) -> {
            return chunkgenerator.getBaseColumn(blockposition.getX(), blockposition.getZ(), levelheightaccessor, randomstate);
        }).collect(Collectors.toList());
        HeightMap.Type heightmap_type = ruinedportalpiece_b == RuinedPortalPiece.b.ON_OCEAN_FLOOR ? HeightMap.Type.OCEAN_FLOOR_WG : HeightMap.Type.WORLD_SURFACE_WG;

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

    private static int getRandomWithinInterval(RandomSource randomsource, int i, int j) {
        return i < j ? MathHelper.randomBetweenInclusive(randomsource, i, j) : j;
    }

    @Override
    public StructureType<?> type() {
        return StructureType.RUINED_PORTAL;
    }

    public static record a(RuinedPortalPiece.b placement, float airPocketProbability, float mossiness, boolean overgrown, boolean vines, boolean canBeCold, boolean replaceWithBlackstone, float weight) {

        public static final Codec<RuinedPortalStructure.a> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(RuinedPortalPiece.b.CODEC.fieldOf("placement").forGetter(RuinedPortalStructure.a::placement), Codec.floatRange(0.0F, 1.0F).fieldOf("air_pocket_probability").forGetter(RuinedPortalStructure.a::airPocketProbability), Codec.floatRange(0.0F, 1.0F).fieldOf("mossiness").forGetter(RuinedPortalStructure.a::mossiness), Codec.BOOL.fieldOf("overgrown").forGetter(RuinedPortalStructure.a::overgrown), Codec.BOOL.fieldOf("vines").forGetter(RuinedPortalStructure.a::vines), Codec.BOOL.fieldOf("can_be_cold").forGetter(RuinedPortalStructure.a::canBeCold), Codec.BOOL.fieldOf("replace_with_blackstone").forGetter(RuinedPortalStructure.a::replaceWithBlackstone), ExtraCodecs.POSITIVE_FLOAT.fieldOf("weight").forGetter(RuinedPortalStructure.a::weight)).apply(instance, RuinedPortalStructure.a::new);
        });
    }
}
