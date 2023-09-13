package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGenFeatureRuinedPortalPieces extends DefinedStructurePiece {

    private static final Logger LOGGER = LogManager.getLogger();
    private final MinecraftKey e;
    private final EnumBlockRotation f;
    private final EnumBlockMirror g;
    private final WorldGenFeatureRuinedPortalPieces.Position h;
    private final WorldGenFeatureRuinedPortalPieces.a i;

    public WorldGenFeatureRuinedPortalPieces(BlockPosition blockposition, WorldGenFeatureRuinedPortalPieces.Position worldgenfeatureruinedportalpieces_position, WorldGenFeatureRuinedPortalPieces.a worldgenfeatureruinedportalpieces_a, MinecraftKey minecraftkey, DefinedStructure definedstructure, EnumBlockRotation enumblockrotation, EnumBlockMirror enumblockmirror, BlockPosition blockposition1) {
        super(WorldGenFeatureStructurePieceType.J, 0);
        this.c = blockposition;
        this.e = minecraftkey;
        this.f = enumblockrotation;
        this.g = enumblockmirror;
        this.h = worldgenfeatureruinedportalpieces_position;
        this.i = worldgenfeatureruinedportalpieces_a;
        this.a(definedstructure, blockposition1);
    }

    public WorldGenFeatureRuinedPortalPieces(DefinedStructureManager definedstructuremanager, NBTTagCompound nbttagcompound) {
        super(WorldGenFeatureStructurePieceType.J, nbttagcompound);
        this.e = new MinecraftKey(nbttagcompound.getString("Template"));
        this.f = EnumBlockRotation.valueOf(nbttagcompound.getString("Rotation"));
        this.g = EnumBlockMirror.valueOf(nbttagcompound.getString("Mirror"));
        this.h = WorldGenFeatureRuinedPortalPieces.Position.a(nbttagcompound.getString("VerticalPlacement"));
        DataResult dataresult = WorldGenFeatureRuinedPortalPieces.a.a.parse(new Dynamic(DynamicOpsNBT.a, nbttagcompound.get("Properties")));
        Logger logger = WorldGenFeatureRuinedPortalPieces.LOGGER;

        logger.getClass();
        this.i = (WorldGenFeatureRuinedPortalPieces.a) dataresult.getOrThrow(true, logger::error);
        DefinedStructure definedstructure = definedstructuremanager.a(this.e);

        this.a(definedstructure, new BlockPosition(definedstructure.a().getX() / 2, 0, definedstructure.a().getZ() / 2));
    }

    @Override
    protected void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        nbttagcompound.setString("Template", this.e.toString());
        nbttagcompound.setString("Rotation", this.f.name());
        nbttagcompound.setString("Mirror", this.g.name());
        nbttagcompound.setString("VerticalPlacement", this.h.a());
        DataResult dataresult = WorldGenFeatureRuinedPortalPieces.a.a.encodeStart(DynamicOpsNBT.a, this.i);
        Logger logger = WorldGenFeatureRuinedPortalPieces.LOGGER;

        logger.getClass();
        dataresult.resultOrPartial(logger::error).ifPresent((nbtbase) -> {
            nbttagcompound.set("Properties", nbtbase);
        });
    }

    private void a(DefinedStructure definedstructure, BlockPosition blockposition) {
        DefinedStructureProcessorBlockIgnore definedstructureprocessorblockignore = this.i.d ? DefinedStructureProcessorBlockIgnore.b : DefinedStructureProcessorBlockIgnore.d;
        List<DefinedStructureProcessorPredicates> list = Lists.newArrayList();

        list.add(a(Blocks.GOLD_BLOCK, 0.3F, Blocks.AIR));
        list.add(this.c());
        if (!this.i.b) {
            list.add(a(Blocks.NETHERRACK, 0.07F, Blocks.MAGMA_BLOCK));
        }

        DefinedStructureInfo definedstructureinfo = (new DefinedStructureInfo()).a(this.f).a(this.g).a(blockposition).a((DefinedStructureProcessor) definedstructureprocessorblockignore).a((DefinedStructureProcessor) (new DefinedStructureProcessorRule(list))).a((DefinedStructureProcessor) (new DefinedStructureProcessorBlockAge(this.i.c))).a((DefinedStructureProcessor) (new DefinedStructureProcessorLavaSubmergedBlock()));

        if (this.i.g) {
            definedstructureinfo.a((DefinedStructureProcessor) DefinedStructureProcessorBlackstoneReplace.b);
        }

        this.a(definedstructure, this.c, definedstructureinfo);
    }

    private DefinedStructureProcessorPredicates c() {
        return this.h == WorldGenFeatureRuinedPortalPieces.Position.ON_OCEAN_FLOOR ? a(Blocks.LAVA, Blocks.MAGMA_BLOCK) : (this.i.b ? a(Blocks.LAVA, Blocks.NETHERRACK) : a(Blocks.LAVA, 0.2F, Blocks.MAGMA_BLOCK));
    }

    @Override
    public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
        if (!structureboundingbox.b((BaseBlockPosition) this.c)) {
            return true;
        } else {
            structureboundingbox.c(this.a.b(this.b, this.c));
            boolean flag = super.a(generatoraccessseed, structuremanager, chunkgenerator, random, structureboundingbox, chunkcoordintpair, blockposition);

            this.b(random, generatoraccessseed);
            this.a(random, (GeneratorAccess) generatoraccessseed);
            if (this.i.f || this.i.e) {
                BlockPosition.a(this.g()).forEach((blockposition1) -> {
                    if (this.i.f) {
                        this.a(random, (GeneratorAccess) generatoraccessseed, blockposition1);
                    }

                    if (this.i.e) {
                        this.b(random, generatoraccessseed, blockposition1);
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
            EnumDirection enumdirection = EnumDirection.EnumDirectionLimit.HORIZONTAL.a(random);
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
        for (int i = this.n.a + 1; i < this.n.d; ++i) {
            for (int j = this.n.c + 1; j < this.n.f; ++j) {
                BlockPosition blockposition = new BlockPosition(i, this.n.b, j);

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
        boolean flag = this.h == WorldGenFeatureRuinedPortalPieces.Position.ON_LAND_SURFACE || this.h == WorldGenFeatureRuinedPortalPieces.Position.ON_OCEAN_FLOOR;
        BaseBlockPosition baseblockposition = this.n.g();
        int i = baseblockposition.getX();
        int j = baseblockposition.getZ();
        float[] afloat = new float[]{1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.9F, 0.9F, 0.8F, 0.7F, 0.6F, 0.4F, 0.2F};
        int k = afloat.length;
        int l = (this.n.d() + this.n.f()) / 2;
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
                        int j2 = a(generatoraccess, j1, k1, this.h);
                        int k2 = flag ? j2 : Math.min(this.n.b, j2);

                        blockposition_mutableblockposition.d(j1, k2, k1);
                        if (Math.abs(k2 - this.n.b) <= 3 && this.a(generatoraccess, (BlockPosition) blockposition_mutableblockposition)) {
                            this.d(random, generatoraccess, blockposition_mutableblockposition);
                            if (this.i.e) {
                                this.b(random, generatoraccess, blockposition_mutableblockposition);
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

        return !iblockdata.a(Blocks.AIR) && !iblockdata.a(Blocks.OBSIDIAN) && !iblockdata.a(Blocks.CHEST) && (this.h == WorldGenFeatureRuinedPortalPieces.Position.IN_NETHER || !iblockdata.a(Blocks.LAVA));
    }

    private void d(Random random, GeneratorAccess generatoraccess, BlockPosition blockposition) {
        if (!this.i.b && random.nextFloat() < 0.07F) {
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
        return new DefinedStructureProcessorPredicates(new DefinedStructureTestRandomBlock(block, f), DefinedStructureTestTrue.b, block1.getBlockData());
    }

    private static DefinedStructureProcessorPredicates a(Block block, Block block1) {
        return new DefinedStructureProcessorPredicates(new DefinedStructureTestBlock(block), DefinedStructureTestTrue.b, block1.getBlockData());
    }

    public static enum Position {

        ON_LAND_SURFACE("on_land_surface"), PARTLY_BURIED("partly_buried"), ON_OCEAN_FLOOR("on_ocean_floor"), IN_MOUNTAIN("in_mountain"), UNDERGROUND("underground"), IN_NETHER("in_nether");

        private static final Map<String, WorldGenFeatureRuinedPortalPieces.Position> g = (Map) Arrays.stream(values()).collect(Collectors.toMap(WorldGenFeatureRuinedPortalPieces.Position::a, (worldgenfeatureruinedportalpieces_position) -> {
            return worldgenfeatureruinedportalpieces_position;
        }));
        private final String h;

        private Position(String s) {
            this.h = s;
        }

        public String a() {
            return this.h;
        }

        public static WorldGenFeatureRuinedPortalPieces.Position a(String s) {
            return (WorldGenFeatureRuinedPortalPieces.Position) WorldGenFeatureRuinedPortalPieces.Position.g.get(s);
        }
    }

    public static class a {

        public static final Codec<WorldGenFeatureRuinedPortalPieces.a> a = RecordCodecBuilder.create((instance) -> {
            return instance.group(Codec.BOOL.fieldOf("cold").forGetter((worldgenfeatureruinedportalpieces_a) -> {
                return worldgenfeatureruinedportalpieces_a.b;
            }), Codec.FLOAT.fieldOf("mossiness").forGetter((worldgenfeatureruinedportalpieces_a) -> {
                return worldgenfeatureruinedportalpieces_a.c;
            }), Codec.BOOL.fieldOf("air_pocket").forGetter((worldgenfeatureruinedportalpieces_a) -> {
                return worldgenfeatureruinedportalpieces_a.d;
            }), Codec.BOOL.fieldOf("overgrown").forGetter((worldgenfeatureruinedportalpieces_a) -> {
                return worldgenfeatureruinedportalpieces_a.e;
            }), Codec.BOOL.fieldOf("vines").forGetter((worldgenfeatureruinedportalpieces_a) -> {
                return worldgenfeatureruinedportalpieces_a.f;
            }), Codec.BOOL.fieldOf("replace_with_blackstone").forGetter((worldgenfeatureruinedportalpieces_a) -> {
                return worldgenfeatureruinedportalpieces_a.g;
            })).apply(instance, WorldGenFeatureRuinedPortalPieces.a::new);
        });
        public boolean b;
        public float c = 0.2F;
        public boolean d;
        public boolean e;
        public boolean f;
        public boolean g;

        public a() {}

        public <T> a(boolean flag, float f, boolean flag1, boolean flag2, boolean flag3, boolean flag4) {
            this.b = flag;
            this.c = f;
            this.d = flag1;
            this.e = flag2;
            this.f = flag3;
            this.g = flag4;
        }
    }
}
