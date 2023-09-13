package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockLeaves;
import net.minecraft.world.level.block.BlockVine;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnumBlockMirror;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureStructurePieceType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureInfo;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessorBlackstoneReplace;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessorBlockAge;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessorBlockIgnore;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessorLavaSubmergedBlock;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessorPredicates;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessorRule;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureTestBlock;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureTestRandomBlock;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureTestTrue;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProtectedBlockProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGenFeatureRuinedPortalPieces extends DefinedStructurePiece {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final float PROBABILITY_OF_GOLD_GONE = 0.3F;
    private static final float PROBABILITY_OF_MAGMA_INSTEAD_OF_NETHERRACK = 0.07F;
    private static final float PROBABILITY_OF_MAGMA_INSTEAD_OF_LAVA = 0.2F;
    private static final float DEFAULT_MOSSINESS = 0.2F;
    private final WorldGenFeatureRuinedPortalPieces.Position verticalPlacement;
    private final WorldGenFeatureRuinedPortalPieces.a properties;

    public WorldGenFeatureRuinedPortalPieces(DefinedStructureManager definedstructuremanager, BlockPosition blockposition, WorldGenFeatureRuinedPortalPieces.Position worldgenfeatureruinedportalpieces_position, WorldGenFeatureRuinedPortalPieces.a worldgenfeatureruinedportalpieces_a, MinecraftKey minecraftkey, DefinedStructure definedstructure, EnumBlockRotation enumblockrotation, EnumBlockMirror enumblockmirror, BlockPosition blockposition1) {
        super(WorldGenFeatureStructurePieceType.RUINED_PORTAL, 0, definedstructuremanager, minecraftkey, minecraftkey.toString(), makeSettings(enumblockmirror, enumblockrotation, worldgenfeatureruinedportalpieces_position, blockposition1, worldgenfeatureruinedportalpieces_a), blockposition);
        this.verticalPlacement = worldgenfeatureruinedportalpieces_position;
        this.properties = worldgenfeatureruinedportalpieces_a;
    }

    public WorldGenFeatureRuinedPortalPieces(DefinedStructureManager definedstructuremanager, NBTTagCompound nbttagcompound) {
        super(WorldGenFeatureStructurePieceType.RUINED_PORTAL, nbttagcompound, definedstructuremanager, (minecraftkey) -> {
            return makeSettings(definedstructuremanager, nbttagcompound, minecraftkey);
        });
        this.verticalPlacement = WorldGenFeatureRuinedPortalPieces.Position.byName(nbttagcompound.getString("VerticalPlacement"));
        DataResult dataresult = WorldGenFeatureRuinedPortalPieces.a.CODEC.parse(new Dynamic(DynamicOpsNBT.INSTANCE, nbttagcompound.get("Properties")));
        Logger logger = WorldGenFeatureRuinedPortalPieces.LOGGER;

        Objects.requireNonNull(logger);
        this.properties = (WorldGenFeatureRuinedPortalPieces.a) dataresult.getOrThrow(true, logger::error);
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext structurepieceserializationcontext, NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(structurepieceserializationcontext, nbttagcompound);
        nbttagcompound.putString("Rotation", this.placeSettings.getRotation().name());
        nbttagcompound.putString("Mirror", this.placeSettings.getMirror().name());
        nbttagcompound.putString("VerticalPlacement", this.verticalPlacement.getName());
        DataResult dataresult = WorldGenFeatureRuinedPortalPieces.a.CODEC.encodeStart(DynamicOpsNBT.INSTANCE, this.properties);
        Logger logger = WorldGenFeatureRuinedPortalPieces.LOGGER;

        Objects.requireNonNull(logger);
        dataresult.resultOrPartial(logger::error).ifPresent((nbtbase) -> {
            nbttagcompound.put("Properties", nbtbase);
        });
    }

    private static DefinedStructureInfo makeSettings(DefinedStructureManager definedstructuremanager, NBTTagCompound nbttagcompound, MinecraftKey minecraftkey) {
        DefinedStructure definedstructure = definedstructuremanager.getOrCreate(minecraftkey);
        BlockPosition blockposition = new BlockPosition(definedstructure.getSize().getX() / 2, 0, definedstructure.getSize().getZ() / 2);
        EnumBlockMirror enumblockmirror = EnumBlockMirror.valueOf(nbttagcompound.getString("Mirror"));
        EnumBlockRotation enumblockrotation = EnumBlockRotation.valueOf(nbttagcompound.getString("Rotation"));
        WorldGenFeatureRuinedPortalPieces.Position worldgenfeatureruinedportalpieces_position = WorldGenFeatureRuinedPortalPieces.Position.byName(nbttagcompound.getString("VerticalPlacement"));
        DataResult dataresult = WorldGenFeatureRuinedPortalPieces.a.CODEC.parse(new Dynamic(DynamicOpsNBT.INSTANCE, nbttagcompound.get("Properties")));
        Logger logger = WorldGenFeatureRuinedPortalPieces.LOGGER;

        Objects.requireNonNull(logger);
        return makeSettings(enumblockmirror, enumblockrotation, worldgenfeatureruinedportalpieces_position, blockposition, (WorldGenFeatureRuinedPortalPieces.a) dataresult.getOrThrow(true, logger::error));
    }

    private static DefinedStructureInfo makeSettings(EnumBlockMirror enumblockmirror, EnumBlockRotation enumblockrotation, WorldGenFeatureRuinedPortalPieces.Position worldgenfeatureruinedportalpieces_position, BlockPosition blockposition, WorldGenFeatureRuinedPortalPieces.a worldgenfeatureruinedportalpieces_a) {
        DefinedStructureProcessorBlockIgnore definedstructureprocessorblockignore = worldgenfeatureruinedportalpieces_a.airPocket ? DefinedStructureProcessorBlockIgnore.STRUCTURE_BLOCK : DefinedStructureProcessorBlockIgnore.STRUCTURE_AND_AIR;
        List<DefinedStructureProcessorPredicates> list = Lists.newArrayList();

        list.add(getBlockReplaceRule(Blocks.GOLD_BLOCK, 0.3F, Blocks.AIR));
        list.add(getLavaProcessorRule(worldgenfeatureruinedportalpieces_position, worldgenfeatureruinedportalpieces_a));
        if (!worldgenfeatureruinedportalpieces_a.cold) {
            list.add(getBlockReplaceRule(Blocks.NETHERRACK, 0.07F, Blocks.MAGMA_BLOCK));
        }

        DefinedStructureInfo definedstructureinfo = (new DefinedStructureInfo()).setRotation(enumblockrotation).setMirror(enumblockmirror).setRotationPivot(blockposition).addProcessor(definedstructureprocessorblockignore).addProcessor(new DefinedStructureProcessorRule(list)).addProcessor(new DefinedStructureProcessorBlockAge(worldgenfeatureruinedportalpieces_a.mossiness)).addProcessor(new ProtectedBlockProcessor(TagsBlock.FEATURES_CANNOT_REPLACE.getName())).addProcessor(new DefinedStructureProcessorLavaSubmergedBlock());

        if (worldgenfeatureruinedportalpieces_a.replaceWithBlackstone) {
            definedstructureinfo.addProcessor(DefinedStructureProcessorBlackstoneReplace.INSTANCE);
        }

        return definedstructureinfo;
    }

    private static DefinedStructureProcessorPredicates getLavaProcessorRule(WorldGenFeatureRuinedPortalPieces.Position worldgenfeatureruinedportalpieces_position, WorldGenFeatureRuinedPortalPieces.a worldgenfeatureruinedportalpieces_a) {
        return worldgenfeatureruinedportalpieces_position == WorldGenFeatureRuinedPortalPieces.Position.ON_OCEAN_FLOOR ? getBlockReplaceRule(Blocks.LAVA, Blocks.MAGMA_BLOCK) : (worldgenfeatureruinedportalpieces_a.cold ? getBlockReplaceRule(Blocks.LAVA, Blocks.NETHERRACK) : getBlockReplaceRule(Blocks.LAVA, 0.2F, Blocks.MAGMA_BLOCK));
    }

    @Override
    public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
        StructureBoundingBox structureboundingbox1 = this.template.getBoundingBox(this.placeSettings, this.templatePosition);

        if (structureboundingbox.isInside(structureboundingbox1.getCenter())) {
            structureboundingbox.encapsulate(structureboundingbox1);
            super.postProcess(generatoraccessseed, structuremanager, chunkgenerator, random, structureboundingbox, chunkcoordintpair, blockposition);
            this.spreadNetherrack(random, generatoraccessseed);
            this.addNetherrackDripColumnsBelowPortal(random, generatoraccessseed);
            if (this.properties.vines || this.properties.overgrown) {
                BlockPosition.betweenClosedStream(this.getBoundingBox()).forEach((blockposition1) -> {
                    if (this.properties.vines) {
                        this.maybeAddVines(random, generatoraccessseed, blockposition1);
                    }

                    if (this.properties.overgrown) {
                        this.maybeAddLeavesAbove(random, generatoraccessseed, blockposition1);
                    }

                });
            }

        }
    }

    @Override
    protected void handleDataMarker(String s, BlockPosition blockposition, WorldAccess worldaccess, Random random, StructureBoundingBox structureboundingbox) {}

    private void maybeAddVines(Random random, GeneratorAccess generatoraccess, BlockPosition blockposition) {
        IBlockData iblockdata = generatoraccess.getBlockState(blockposition);

        if (!iblockdata.isAir() && !iblockdata.is(Blocks.VINE)) {
            EnumDirection enumdirection = getRandomHorizontalDirection(random);
            BlockPosition blockposition1 = blockposition.relative(enumdirection);
            IBlockData iblockdata1 = generatoraccess.getBlockState(blockposition1);

            if (iblockdata1.isAir()) {
                if (Block.isFaceFull(iblockdata.getCollisionShape(generatoraccess, blockposition), enumdirection)) {
                    BlockStateBoolean blockstateboolean = BlockVine.getPropertyForFace(enumdirection.getOpposite());

                    generatoraccess.setBlock(blockposition1, (IBlockData) Blocks.VINE.defaultBlockState().setValue(blockstateboolean, true), 3);
                }
            }
        }
    }

    private void maybeAddLeavesAbove(Random random, GeneratorAccess generatoraccess, BlockPosition blockposition) {
        if (random.nextFloat() < 0.5F && generatoraccess.getBlockState(blockposition).is(Blocks.NETHERRACK) && generatoraccess.getBlockState(blockposition.above()).isAir()) {
            generatoraccess.setBlock(blockposition.above(), (IBlockData) Blocks.JUNGLE_LEAVES.defaultBlockState().setValue(BlockLeaves.PERSISTENT, true), 3);
        }

    }

    private void addNetherrackDripColumnsBelowPortal(Random random, GeneratorAccess generatoraccess) {
        for (int i = this.boundingBox.minX() + 1; i < this.boundingBox.maxX(); ++i) {
            for (int j = this.boundingBox.minZ() + 1; j < this.boundingBox.maxZ(); ++j) {
                BlockPosition blockposition = new BlockPosition(i, this.boundingBox.minY(), j);

                if (generatoraccess.getBlockState(blockposition).is(Blocks.NETHERRACK)) {
                    this.addNetherrackDripColumn(random, generatoraccess, blockposition.below());
                }
            }
        }

    }

    private void addNetherrackDripColumn(Random random, GeneratorAccess generatoraccess, BlockPosition blockposition) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.mutable();

        this.placeNetherrackOrMagma(random, generatoraccess, blockposition_mutableblockposition);
        int i = 8;

        while (i > 0 && random.nextFloat() < 0.5F) {
            blockposition_mutableblockposition.move(EnumDirection.DOWN);
            --i;
            this.placeNetherrackOrMagma(random, generatoraccess, blockposition_mutableblockposition);
        }

    }

    private void spreadNetherrack(Random random, GeneratorAccess generatoraccess) {
        boolean flag = this.verticalPlacement == WorldGenFeatureRuinedPortalPieces.Position.ON_LAND_SURFACE || this.verticalPlacement == WorldGenFeatureRuinedPortalPieces.Position.ON_OCEAN_FLOOR;
        BlockPosition blockposition = this.boundingBox.getCenter();
        int i = blockposition.getX();
        int j = blockposition.getZ();
        float[] afloat = new float[]{1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.9F, 0.9F, 0.8F, 0.7F, 0.6F, 0.4F, 0.2F};
        int k = afloat.length;
        int l = (this.boundingBox.getXSpan() + this.boundingBox.getZSpan()) / 2;
        int i1 = random.nextInt(Math.max(1, 8 - l / 2));
        boolean flag1 = true;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = BlockPosition.ZERO.mutable();

        for (int j1 = i - k; j1 <= i + k; ++j1) {
            for (int k1 = j - k; k1 <= j + k; ++k1) {
                int l1 = Math.abs(j1 - i) + Math.abs(k1 - j);
                int i2 = Math.max(0, l1 + i1);

                if (i2 < k) {
                    float f = afloat[i2];

                    if (random.nextDouble() < (double) f) {
                        int j2 = getSurfaceY(generatoraccess, j1, k1, this.verticalPlacement);
                        int k2 = flag ? j2 : Math.min(this.boundingBox.minY(), j2);

                        blockposition_mutableblockposition.set(j1, k2, k1);
                        if (Math.abs(k2 - this.boundingBox.minY()) <= 3 && this.canBlockBeReplacedByNetherrackOrMagma(generatoraccess, blockposition_mutableblockposition)) {
                            this.placeNetherrackOrMagma(random, generatoraccess, blockposition_mutableblockposition);
                            if (this.properties.overgrown) {
                                this.maybeAddLeavesAbove(random, generatoraccess, blockposition_mutableblockposition);
                            }

                            this.addNetherrackDripColumn(random, generatoraccess, blockposition_mutableblockposition.below());
                        }
                    }
                }
            }
        }

    }

    private boolean canBlockBeReplacedByNetherrackOrMagma(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        IBlockData iblockdata = generatoraccess.getBlockState(blockposition);

        return !iblockdata.is(Blocks.AIR) && !iblockdata.is(Blocks.OBSIDIAN) && !iblockdata.is((Tag) TagsBlock.FEATURES_CANNOT_REPLACE) && (this.verticalPlacement == WorldGenFeatureRuinedPortalPieces.Position.IN_NETHER || !iblockdata.is(Blocks.LAVA));
    }

    private void placeNetherrackOrMagma(Random random, GeneratorAccess generatoraccess, BlockPosition blockposition) {
        if (!this.properties.cold && random.nextFloat() < 0.07F) {
            generatoraccess.setBlock(blockposition, Blocks.MAGMA_BLOCK.defaultBlockState(), 3);
        } else {
            generatoraccess.setBlock(blockposition, Blocks.NETHERRACK.defaultBlockState(), 3);
        }

    }

    private static int getSurfaceY(GeneratorAccess generatoraccess, int i, int j, WorldGenFeatureRuinedPortalPieces.Position worldgenfeatureruinedportalpieces_position) {
        return generatoraccess.getHeight(getHeightMapType(worldgenfeatureruinedportalpieces_position), i, j) - 1;
    }

    public static HeightMap.Type getHeightMapType(WorldGenFeatureRuinedPortalPieces.Position worldgenfeatureruinedportalpieces_position) {
        return worldgenfeatureruinedportalpieces_position == WorldGenFeatureRuinedPortalPieces.Position.ON_OCEAN_FLOOR ? HeightMap.Type.OCEAN_FLOOR_WG : HeightMap.Type.WORLD_SURFACE_WG;
    }

    private static DefinedStructureProcessorPredicates getBlockReplaceRule(Block block, float f, Block block1) {
        return new DefinedStructureProcessorPredicates(new DefinedStructureTestRandomBlock(block, f), DefinedStructureTestTrue.INSTANCE, block1.defaultBlockState());
    }

    private static DefinedStructureProcessorPredicates getBlockReplaceRule(Block block, Block block1) {
        return new DefinedStructureProcessorPredicates(new DefinedStructureTestBlock(block), DefinedStructureTestTrue.INSTANCE, block1.defaultBlockState());
    }

    public static enum Position {

        ON_LAND_SURFACE("on_land_surface"), PARTLY_BURIED("partly_buried"), ON_OCEAN_FLOOR("on_ocean_floor"), IN_MOUNTAIN("in_mountain"), UNDERGROUND("underground"), IN_NETHER("in_nether");

        private static final Map<String, WorldGenFeatureRuinedPortalPieces.Position> BY_NAME = (Map) Arrays.stream(values()).collect(Collectors.toMap(WorldGenFeatureRuinedPortalPieces.Position::getName, (worldgenfeatureruinedportalpieces_position) -> {
            return worldgenfeatureruinedportalpieces_position;
        }));
        private final String name;

        private Position(String s) {
            this.name = s;
        }

        public String getName() {
            return this.name;
        }

        public static WorldGenFeatureRuinedPortalPieces.Position byName(String s) {
            return (WorldGenFeatureRuinedPortalPieces.Position) WorldGenFeatureRuinedPortalPieces.Position.BY_NAME.get(s);
        }
    }

    public static class a {

        public static final Codec<WorldGenFeatureRuinedPortalPieces.a> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(Codec.BOOL.fieldOf("cold").forGetter((worldgenfeatureruinedportalpieces_a) -> {
                return worldgenfeatureruinedportalpieces_a.cold;
            }), Codec.FLOAT.fieldOf("mossiness").forGetter((worldgenfeatureruinedportalpieces_a) -> {
                return worldgenfeatureruinedportalpieces_a.mossiness;
            }), Codec.BOOL.fieldOf("air_pocket").forGetter((worldgenfeatureruinedportalpieces_a) -> {
                return worldgenfeatureruinedportalpieces_a.airPocket;
            }), Codec.BOOL.fieldOf("overgrown").forGetter((worldgenfeatureruinedportalpieces_a) -> {
                return worldgenfeatureruinedportalpieces_a.overgrown;
            }), Codec.BOOL.fieldOf("vines").forGetter((worldgenfeatureruinedportalpieces_a) -> {
                return worldgenfeatureruinedportalpieces_a.vines;
            }), Codec.BOOL.fieldOf("replace_with_blackstone").forGetter((worldgenfeatureruinedportalpieces_a) -> {
                return worldgenfeatureruinedportalpieces_a.replaceWithBlackstone;
            })).apply(instance, WorldGenFeatureRuinedPortalPieces.a::new);
        });
        public boolean cold;
        public float mossiness = 0.2F;
        public boolean airPocket;
        public boolean overgrown;
        public boolean vines;
        public boolean replaceWithBlackstone;

        public a() {}

        public a(boolean flag, float f, boolean flag1, boolean flag2, boolean flag3, boolean flag4) {
            this.cold = flag;
            this.mossiness = f;
            this.airPocket = flag1;
            this.overgrown = flag2;
            this.vines = flag3;
            this.replaceWithBlackstone = flag4;
        }
    }
}
