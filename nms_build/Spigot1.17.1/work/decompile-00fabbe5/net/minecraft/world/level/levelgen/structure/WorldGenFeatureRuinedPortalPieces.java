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
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
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
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureInfo;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessor;
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
        super(WorldGenFeatureStructurePieceType.RUINED_PORTAL, 0, definedstructuremanager, minecraftkey, minecraftkey.toString(), a(enumblockmirror, enumblockrotation, worldgenfeatureruinedportalpieces_position, blockposition1, worldgenfeatureruinedportalpieces_a), blockposition);
        this.verticalPlacement = worldgenfeatureruinedportalpieces_position;
        this.properties = worldgenfeatureruinedportalpieces_a;
    }

    public WorldGenFeatureRuinedPortalPieces(WorldServer worldserver, NBTTagCompound nbttagcompound) {
        super(WorldGenFeatureStructurePieceType.RUINED_PORTAL, nbttagcompound, worldserver, (minecraftkey) -> {
            return a(worldserver, nbttagcompound, minecraftkey);
        });
        this.verticalPlacement = WorldGenFeatureRuinedPortalPieces.Position.a(nbttagcompound.getString("VerticalPlacement"));
        DataResult dataresult = WorldGenFeatureRuinedPortalPieces.a.CODEC.parse(new Dynamic(DynamicOpsNBT.INSTANCE, nbttagcompound.get("Properties")));
        Logger logger = WorldGenFeatureRuinedPortalPieces.LOGGER;

        Objects.requireNonNull(logger);
        this.properties = (WorldGenFeatureRuinedPortalPieces.a) dataresult.getOrThrow(true, logger::error);
    }

    @Override
    protected void a(WorldServer worldserver, NBTTagCompound nbttagcompound) {
        super.a(worldserver, nbttagcompound);
        nbttagcompound.setString("Rotation", this.placeSettings.d().name());
        nbttagcompound.setString("Mirror", this.placeSettings.c().name());
        nbttagcompound.setString("VerticalPlacement", this.verticalPlacement.a());
        DataResult dataresult = WorldGenFeatureRuinedPortalPieces.a.CODEC.encodeStart(DynamicOpsNBT.INSTANCE, this.properties);
        Logger logger = WorldGenFeatureRuinedPortalPieces.LOGGER;

        Objects.requireNonNull(logger);
        dataresult.resultOrPartial(logger::error).ifPresent((nbtbase) -> {
            nbttagcompound.set("Properties", nbtbase);
        });
    }

    private static DefinedStructureInfo a(WorldServer worldserver, NBTTagCompound nbttagcompound, MinecraftKey minecraftkey) {
        DefinedStructure definedstructure = worldserver.p().a(minecraftkey);
        BlockPosition blockposition = new BlockPosition(definedstructure.a().getX() / 2, 0, definedstructure.a().getZ() / 2);
        EnumBlockMirror enumblockmirror = EnumBlockMirror.valueOf(nbttagcompound.getString("Mirror"));
        EnumBlockRotation enumblockrotation = EnumBlockRotation.valueOf(nbttagcompound.getString("Rotation"));
        WorldGenFeatureRuinedPortalPieces.Position worldgenfeatureruinedportalpieces_position = WorldGenFeatureRuinedPortalPieces.Position.a(nbttagcompound.getString("VerticalPlacement"));
        DataResult dataresult = WorldGenFeatureRuinedPortalPieces.a.CODEC.parse(new Dynamic(DynamicOpsNBT.INSTANCE, nbttagcompound.get("Properties")));
        Logger logger = WorldGenFeatureRuinedPortalPieces.LOGGER;

        Objects.requireNonNull(logger);
        return a(enumblockmirror, enumblockrotation, worldgenfeatureruinedportalpieces_position, blockposition, (WorldGenFeatureRuinedPortalPieces.a) dataresult.getOrThrow(true, logger::error));
    }

    private static DefinedStructureInfo a(EnumBlockMirror enumblockmirror, EnumBlockRotation enumblockrotation, WorldGenFeatureRuinedPortalPieces.Position worldgenfeatureruinedportalpieces_position, BlockPosition blockposition, WorldGenFeatureRuinedPortalPieces.a worldgenfeatureruinedportalpieces_a) {
        DefinedStructureProcessorBlockIgnore definedstructureprocessorblockignore = worldgenfeatureruinedportalpieces_a.airPocket ? DefinedStructureProcessorBlockIgnore.STRUCTURE_BLOCK : DefinedStructureProcessorBlockIgnore.STRUCTURE_AND_AIR;
        List<DefinedStructureProcessorPredicates> list = Lists.newArrayList();

        list.add(a(Blocks.GOLD_BLOCK, 0.3F, Blocks.AIR));
        list.add(a(worldgenfeatureruinedportalpieces_position, worldgenfeatureruinedportalpieces_a));
        if (!worldgenfeatureruinedportalpieces_a.cold) {
            list.add(a(Blocks.NETHERRACK, 0.07F, Blocks.MAGMA_BLOCK));
        }

        DefinedStructureInfo definedstructureinfo = (new DefinedStructureInfo()).a(enumblockrotation).a(enumblockmirror).a(blockposition).a((DefinedStructureProcessor) definedstructureprocessorblockignore).a((DefinedStructureProcessor) (new DefinedStructureProcessorRule(list))).a((DefinedStructureProcessor) (new DefinedStructureProcessorBlockAge(worldgenfeatureruinedportalpieces_a.mossiness))).a((DefinedStructureProcessor) (new ProtectedBlockProcessor(TagsBlock.FEATURES_CANNOT_REPLACE.a()))).a((DefinedStructureProcessor) (new DefinedStructureProcessorLavaSubmergedBlock()));

        if (worldgenfeatureruinedportalpieces_a.replaceWithBlackstone) {
            definedstructureinfo.a((DefinedStructureProcessor) DefinedStructureProcessorBlackstoneReplace.INSTANCE);
        }

        return definedstructureinfo;
    }

    private static DefinedStructureProcessorPredicates a(WorldGenFeatureRuinedPortalPieces.Position worldgenfeatureruinedportalpieces_position, WorldGenFeatureRuinedPortalPieces.a worldgenfeatureruinedportalpieces_a) {
        return worldgenfeatureruinedportalpieces_position == WorldGenFeatureRuinedPortalPieces.Position.ON_OCEAN_FLOOR ? a(Blocks.LAVA, Blocks.MAGMA_BLOCK) : (worldgenfeatureruinedportalpieces_a.cold ? a(Blocks.LAVA, Blocks.NETHERRACK) : a(Blocks.LAVA, 0.2F, Blocks.MAGMA_BLOCK));
    }

    @Override
    public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
        StructureBoundingBox structureboundingbox1 = this.template.b(this.placeSettings, this.templatePosition);

        if (!structureboundingbox.b((BaseBlockPosition) structureboundingbox1.f())) {
            return true;
        } else {
            structureboundingbox.b(structureboundingbox1);
            boolean flag = super.a(generatoraccessseed, structuremanager, chunkgenerator, random, structureboundingbox, chunkcoordintpair, blockposition);

            this.b(random, generatoraccessseed);
            this.a(random, (GeneratorAccess) generatoraccessseed);
            if (this.properties.vines || this.properties.overgrown) {
                BlockPosition.a(this.f()).forEach((blockposition1) -> {
                    if (this.properties.vines) {
                        this.a(random, (GeneratorAccess) generatoraccessseed, blockposition1);
                    }

                    if (this.properties.overgrown) {
                        this.b(random, (GeneratorAccess) generatoraccessseed, blockposition1);
                    }

                });
            }

            return flag;
        }
    }

    @Override
    protected void a(String s, BlockPosition blockposition, WorldAccess worldaccess, Random random, StructureBoundingBox structureboundingbox) {}

    private void a(Random random, GeneratorAccess generatoraccess, BlockPosition blockposition) {
        IBlockData iblockdata = generatoraccess.getType(blockposition);

        if (!iblockdata.isAir() && !iblockdata.a(Blocks.VINE)) {
            EnumDirection enumdirection = b(random);
            BlockPosition blockposition1 = blockposition.shift(enumdirection);
            IBlockData iblockdata1 = generatoraccess.getType(blockposition1);

            if (iblockdata1.isAir()) {
                if (Block.a(iblockdata.getCollisionShape(generatoraccess, blockposition), enumdirection)) {
                    BlockStateBoolean blockstateboolean = BlockVine.getDirection(enumdirection.opposite());

                    generatoraccess.setTypeAndData(blockposition1, (IBlockData) Blocks.VINE.getBlockData().set(blockstateboolean, true), 3);
                }
            }
        }
    }

    private void b(Random random, GeneratorAccess generatoraccess, BlockPosition blockposition) {
        if (random.nextFloat() < 0.5F && generatoraccess.getType(blockposition).a(Blocks.NETHERRACK) && generatoraccess.getType(blockposition.up()).isAir()) {
            generatoraccess.setTypeAndData(blockposition.up(), (IBlockData) Blocks.JUNGLE_LEAVES.getBlockData().set(BlockLeaves.PERSISTENT, true), 3);
        }

    }

    private void a(Random random, GeneratorAccess generatoraccess) {
        for (int i = this.boundingBox.g() + 1; i < this.boundingBox.j(); ++i) {
            for (int j = this.boundingBox.i() + 1; j < this.boundingBox.l(); ++j) {
                BlockPosition blockposition = new BlockPosition(i, this.boundingBox.h(), j);

                if (generatoraccess.getType(blockposition).a(Blocks.NETHERRACK)) {
                    this.c(random, generatoraccess, blockposition.down());
                }
            }
        }

    }

    private void c(Random random, GeneratorAccess generatoraccess, BlockPosition blockposition) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.i();

        this.d(random, generatoraccess, blockposition_mutableblockposition);
        int i = 8;

        while (i > 0 && random.nextFloat() < 0.5F) {
            blockposition_mutableblockposition.c(EnumDirection.DOWN);
            --i;
            this.d(random, generatoraccess, blockposition_mutableblockposition);
        }

    }

    private void b(Random random, GeneratorAccess generatoraccess) {
        boolean flag = this.verticalPlacement == WorldGenFeatureRuinedPortalPieces.Position.ON_LAND_SURFACE || this.verticalPlacement == WorldGenFeatureRuinedPortalPieces.Position.ON_OCEAN_FLOOR;
        BlockPosition blockposition = this.boundingBox.f();
        int i = blockposition.getX();
        int j = blockposition.getZ();
        float[] afloat = new float[]{1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.9F, 0.9F, 0.8F, 0.7F, 0.6F, 0.4F, 0.2F};
        int k = afloat.length;
        int l = (this.boundingBox.c() + this.boundingBox.e()) / 2;
        int i1 = random.nextInt(Math.max(1, 8 - l / 2));
        boolean flag1 = true;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = BlockPosition.ZERO.i();

        for (int j1 = i - k; j1 <= i + k; ++j1) {
            for (int k1 = j - k; k1 <= j + k; ++k1) {
                int l1 = Math.abs(j1 - i) + Math.abs(k1 - j);
                int i2 = Math.max(0, l1 + i1);

                if (i2 < k) {
                    float f = afloat[i2];

                    if (random.nextDouble() < (double) f) {
                        int j2 = a(generatoraccess, j1, k1, this.verticalPlacement);
                        int k2 = flag ? j2 : Math.min(this.boundingBox.h(), j2);

                        blockposition_mutableblockposition.d(j1, k2, k1);
                        if (Math.abs(k2 - this.boundingBox.h()) <= 3 && this.a(generatoraccess, (BlockPosition) blockposition_mutableblockposition)) {
                            this.d(random, generatoraccess, blockposition_mutableblockposition);
                            if (this.properties.overgrown) {
                                this.b(random, generatoraccess, (BlockPosition) blockposition_mutableblockposition);
                            }

                            this.c(random, generatoraccess, blockposition_mutableblockposition.down());
                        }
                    }
                }
            }
        }

    }

    private boolean a(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        IBlockData iblockdata = generatoraccess.getType(blockposition);

        return !iblockdata.a(Blocks.AIR) && !iblockdata.a(Blocks.OBSIDIAN) && !iblockdata.a((Tag) TagsBlock.FEATURES_CANNOT_REPLACE) && (this.verticalPlacement == WorldGenFeatureRuinedPortalPieces.Position.IN_NETHER || !iblockdata.a(Blocks.LAVA));
    }

    private void d(Random random, GeneratorAccess generatoraccess, BlockPosition blockposition) {
        if (!this.properties.cold && random.nextFloat() < 0.07F) {
            generatoraccess.setTypeAndData(blockposition, Blocks.MAGMA_BLOCK.getBlockData(), 3);
        } else {
            generatoraccess.setTypeAndData(blockposition, Blocks.NETHERRACK.getBlockData(), 3);
        }

    }

    private static int a(GeneratorAccess generatoraccess, int i, int j, WorldGenFeatureRuinedPortalPieces.Position worldgenfeatureruinedportalpieces_position) {
        return generatoraccess.a(a(worldgenfeatureruinedportalpieces_position), i, j) - 1;
    }

    public static HeightMap.Type a(WorldGenFeatureRuinedPortalPieces.Position worldgenfeatureruinedportalpieces_position) {
        return worldgenfeatureruinedportalpieces_position == WorldGenFeatureRuinedPortalPieces.Position.ON_OCEAN_FLOOR ? HeightMap.Type.OCEAN_FLOOR_WG : HeightMap.Type.WORLD_SURFACE_WG;
    }

    private static DefinedStructureProcessorPredicates a(Block block, float f, Block block1) {
        return new DefinedStructureProcessorPredicates(new DefinedStructureTestRandomBlock(block, f), DefinedStructureTestTrue.INSTANCE, block1.getBlockData());
    }

    private static DefinedStructureProcessorPredicates a(Block block, Block block1) {
        return new DefinedStructureProcessorPredicates(new DefinedStructureTestBlock(block), DefinedStructureTestTrue.INSTANCE, block1.getBlockData());
    }

    public static enum Position {

        ON_LAND_SURFACE("on_land_surface"), PARTLY_BURIED("partly_buried"), ON_OCEAN_FLOOR("on_ocean_floor"), IN_MOUNTAIN("in_mountain"), UNDERGROUND("underground"), IN_NETHER("in_nether");

        private static final Map<String, WorldGenFeatureRuinedPortalPieces.Position> BY_NAME = (Map) Arrays.stream(values()).collect(Collectors.toMap(WorldGenFeatureRuinedPortalPieces.Position::a, (worldgenfeatureruinedportalpieces_position) -> {
            return worldgenfeatureruinedportalpieces_position;
        }));
        private final String name;

        private Position(String s) {
            this.name = s;
        }

        public String a() {
            return this.name;
        }

        public static WorldGenFeatureRuinedPortalPieces.Position a(String s) {
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

        public <T> a(boolean flag, float f, boolean flag1, boolean flag2, boolean flag3, boolean flag4) {
            this.cold = flag;
            this.mossiness = f;
            this.airPocket = flag1;
            this.overgrown = flag2;
            this.vines = flag3;
            this.replaceWithBlackstone = flag4;
        }
    }
}
