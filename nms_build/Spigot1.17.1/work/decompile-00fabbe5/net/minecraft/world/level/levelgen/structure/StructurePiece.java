package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.DataResult;
import java.util.Iterator;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockDispenser;
import net.minecraft.world.level.block.BlockFacingHorizontal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnumBlockMirror;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityChest;
import net.minecraft.world.level.block.entity.TileEntityDispenser;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.NoiseEffect;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureStructurePieceType;
import net.minecraft.world.level.material.Fluid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class StructurePiece {

    private static final Logger LOGGER = LogManager.getLogger();
    protected static final IBlockData CAVE_AIR = Blocks.CAVE_AIR.getBlockData();
    protected StructureBoundingBox boundingBox;
    @Nullable
    private EnumDirection orientation;
    private EnumBlockMirror mirror;
    private EnumBlockRotation rotation;
    protected int genDepth;
    private final WorldGenFeatureStructurePieceType type;
    private static final Set<Block> SHAPE_CHECK_BLOCKS = ImmutableSet.builder().add(Blocks.NETHER_BRICK_FENCE).add(Blocks.TORCH).add(Blocks.WALL_TORCH).add(Blocks.OAK_FENCE).add(Blocks.SPRUCE_FENCE).add(Blocks.DARK_OAK_FENCE).add(Blocks.ACACIA_FENCE).add(Blocks.BIRCH_FENCE).add(Blocks.JUNGLE_FENCE).add(Blocks.LADDER).add(Blocks.IRON_BARS).build();

    protected StructurePiece(WorldGenFeatureStructurePieceType worldgenfeaturestructurepiecetype, int i, StructureBoundingBox structureboundingbox) {
        this.type = worldgenfeaturestructurepiecetype;
        this.genDepth = i;
        this.boundingBox = structureboundingbox;
    }

    public StructurePiece(WorldGenFeatureStructurePieceType worldgenfeaturestructurepiecetype, NBTTagCompound nbttagcompound) {
        int i = nbttagcompound.getInt("GD");
        DataResult dataresult = StructureBoundingBox.CODEC.parse(DynamicOpsNBT.INSTANCE, nbttagcompound.get("BB"));
        Logger logger = StructurePiece.LOGGER;

        Objects.requireNonNull(logger);
        this(worldgenfeaturestructurepiecetype, i, (StructureBoundingBox) dataresult.resultOrPartial(logger::error).orElseThrow(() -> {
            return new IllegalArgumentException("Invalid boundingbox");
        }));
        int j = nbttagcompound.getInt("O");

        this.a(j == -1 ? null : EnumDirection.fromType2(j));
    }

    protected static StructureBoundingBox a(int i, int j, int k, EnumDirection enumdirection, int l, int i1, int j1) {
        return enumdirection.n() == EnumDirection.EnumAxis.Z ? new StructureBoundingBox(i, j, k, i + l - 1, j + i1 - 1, k + j1 - 1) : new StructureBoundingBox(i, j, k, i + j1 - 1, j + i1 - 1, k + l - 1);
    }

    protected static EnumDirection b(Random random) {
        return EnumDirection.EnumDirectionLimit.HORIZONTAL.a(random);
    }

    public final NBTTagCompound a(WorldServer worldserver) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.setString("id", IRegistry.STRUCTURE_PIECE.getKey(this.j()).toString());
        DataResult dataresult = StructureBoundingBox.CODEC.encodeStart(DynamicOpsNBT.INSTANCE, this.boundingBox);
        Logger logger = StructurePiece.LOGGER;

        Objects.requireNonNull(logger);
        dataresult.resultOrPartial(logger::error).ifPresent((nbtbase) -> {
            nbttagcompound.set("BB", nbtbase);
        });
        EnumDirection enumdirection = this.h();

        nbttagcompound.setInt("O", enumdirection == null ? -1 : enumdirection.get2DRotationValue());
        nbttagcompound.setInt("GD", this.genDepth);
        this.a(worldserver, nbttagcompound);
        return nbttagcompound;
    }

    protected abstract void a(WorldServer worldserver, NBTTagCompound nbttagcompound);

    public NoiseEffect ad_() {
        return NoiseEffect.BEARD;
    }

    public void a(StructurePiece structurepiece, StructurePieceAccessor structurepieceaccessor, Random random) {}

    public abstract boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition);

    public StructureBoundingBox f() {
        return this.boundingBox;
    }

    public int g() {
        return this.genDepth;
    }

    public boolean a(ChunkCoordIntPair chunkcoordintpair, int i) {
        int j = chunkcoordintpair.d();
        int k = chunkcoordintpair.e();

        return this.boundingBox.a(j - i, k - i, j + 15 + i, k + 15 + i);
    }

    public BlockPosition ae_() {
        return new BlockPosition(this.boundingBox.f());
    }

    protected BlockPosition.MutableBlockPosition c(int i, int j, int k) {
        return new BlockPosition.MutableBlockPosition(this.a(i, k), this.a(j), this.b(i, k));
    }

    protected int a(int i, int j) {
        EnumDirection enumdirection = this.h();

        if (enumdirection == null) {
            return i;
        } else {
            switch (enumdirection) {
                case NORTH:
                case SOUTH:
                    return this.boundingBox.g() + i;
                case WEST:
                    return this.boundingBox.j() - j;
                case EAST:
                    return this.boundingBox.g() + j;
                default:
                    return i;
            }
        }
    }

    protected int a(int i) {
        return this.h() == null ? i : i + this.boundingBox.h();
    }

    protected int b(int i, int j) {
        EnumDirection enumdirection = this.h();

        if (enumdirection == null) {
            return j;
        } else {
            switch (enumdirection) {
                case NORTH:
                    return this.boundingBox.l() - j;
                case SOUTH:
                    return this.boundingBox.i() + j;
                case WEST:
                case EAST:
                    return this.boundingBox.i() + i;
                default:
                    return j;
            }
        }
    }

    protected void c(GeneratorAccessSeed generatoraccessseed, IBlockData iblockdata, int i, int j, int k, StructureBoundingBox structureboundingbox) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = this.c(i, j, k);

        if (structureboundingbox.b((BaseBlockPosition) blockposition_mutableblockposition)) {
            if (this.a((IWorldReader) generatoraccessseed, i, j, k, structureboundingbox)) {
                if (this.mirror != EnumBlockMirror.NONE) {
                    iblockdata = iblockdata.a(this.mirror);
                }

                if (this.rotation != EnumBlockRotation.NONE) {
                    iblockdata = iblockdata.a(this.rotation);
                }

                generatoraccessseed.setTypeAndData(blockposition_mutableblockposition, iblockdata, 2);
                Fluid fluid = generatoraccessseed.getFluid(blockposition_mutableblockposition);

                if (!fluid.isEmpty()) {
                    generatoraccessseed.getFluidTickList().a(blockposition_mutableblockposition, fluid.getType(), 0);
                }

                if (StructurePiece.SHAPE_CHECK_BLOCKS.contains(iblockdata.getBlock())) {
                    generatoraccessseed.A(blockposition_mutableblockposition).e(blockposition_mutableblockposition);
                }

            }
        }
    }

    protected boolean a(IWorldReader iworldreader, int i, int j, int k, StructureBoundingBox structureboundingbox) {
        return true;
    }

    protected IBlockData a(IBlockAccess iblockaccess, int i, int j, int k, StructureBoundingBox structureboundingbox) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = this.c(i, j, k);

        return !structureboundingbox.b((BaseBlockPosition) blockposition_mutableblockposition) ? Blocks.AIR.getBlockData() : iblockaccess.getType(blockposition_mutableblockposition);
    }

    protected boolean b(IWorldReader iworldreader, int i, int j, int k, StructureBoundingBox structureboundingbox) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = this.c(i, j + 1, k);

        return !structureboundingbox.b((BaseBlockPosition) blockposition_mutableblockposition) ? false : blockposition_mutableblockposition.getY() < iworldreader.a(HeightMap.Type.OCEAN_FLOOR_WG, blockposition_mutableblockposition.getX(), blockposition_mutableblockposition.getZ());
    }

    protected void b(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, int i, int j, int k, int l, int i1, int j1) {
        for (int k1 = j; k1 <= i1; ++k1) {
            for (int l1 = i; l1 <= l; ++l1) {
                for (int i2 = k; i2 <= j1; ++i2) {
                    this.c(generatoraccessseed, Blocks.AIR.getBlockData(), l1, k1, i2, structureboundingbox);
                }
            }
        }

    }

    protected void a(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, int i, int j, int k, int l, int i1, int j1, IBlockData iblockdata, IBlockData iblockdata1, boolean flag) {
        for (int k1 = j; k1 <= i1; ++k1) {
            for (int l1 = i; l1 <= l; ++l1) {
                for (int i2 = k; i2 <= j1; ++i2) {
                    if (!flag || !this.a((IBlockAccess) generatoraccessseed, l1, k1, i2, structureboundingbox).isAir()) {
                        if (k1 != j && k1 != i1 && l1 != i && l1 != l && i2 != k && i2 != j1) {
                            this.c(generatoraccessseed, iblockdata1, l1, k1, i2, structureboundingbox);
                        } else {
                            this.c(generatoraccessseed, iblockdata, l1, k1, i2, structureboundingbox);
                        }
                    }
                }
            }
        }

    }

    protected void a(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, StructureBoundingBox structureboundingbox1, IBlockData iblockdata, IBlockData iblockdata1, boolean flag) {
        this.a(generatoraccessseed, structureboundingbox, structureboundingbox1.g(), structureboundingbox1.h(), structureboundingbox1.i(), structureboundingbox1.j(), structureboundingbox1.k(), structureboundingbox1.l(), iblockdata, iblockdata1, flag);
    }

    protected void a(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, int i, int j, int k, int l, int i1, int j1, boolean flag, Random random, StructurePiece.StructurePieceBlockSelector structurepiece_structurepieceblockselector) {
        for (int k1 = j; k1 <= i1; ++k1) {
            for (int l1 = i; l1 <= l; ++l1) {
                for (int i2 = k; i2 <= j1; ++i2) {
                    if (!flag || !this.a((IBlockAccess) generatoraccessseed, l1, k1, i2, structureboundingbox).isAir()) {
                        structurepiece_structurepieceblockselector.a(random, l1, k1, i2, k1 == j || k1 == i1 || l1 == i || l1 == l || i2 == k || i2 == j1);
                        this.c(generatoraccessseed, structurepiece_structurepieceblockselector.a(), l1, k1, i2, structureboundingbox);
                    }
                }
            }
        }

    }

    protected void a(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, StructureBoundingBox structureboundingbox1, boolean flag, Random random, StructurePiece.StructurePieceBlockSelector structurepiece_structurepieceblockselector) {
        this.a(generatoraccessseed, structureboundingbox, structureboundingbox1.g(), structureboundingbox1.h(), structureboundingbox1.i(), structureboundingbox1.j(), structureboundingbox1.k(), structureboundingbox1.l(), flag, random, structurepiece_structurepieceblockselector);
    }

    protected void a(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, Random random, float f, int i, int j, int k, int l, int i1, int j1, IBlockData iblockdata, IBlockData iblockdata1, boolean flag, boolean flag1) {
        for (int k1 = j; k1 <= i1; ++k1) {
            for (int l1 = i; l1 <= l; ++l1) {
                for (int i2 = k; i2 <= j1; ++i2) {
                    if (random.nextFloat() <= f && (!flag || !this.a((IBlockAccess) generatoraccessseed, l1, k1, i2, structureboundingbox).isAir()) && (!flag1 || this.b(generatoraccessseed, l1, k1, i2, structureboundingbox))) {
                        if (k1 != j && k1 != i1 && l1 != i && l1 != l && i2 != k && i2 != j1) {
                            this.c(generatoraccessseed, iblockdata1, l1, k1, i2, structureboundingbox);
                        } else {
                            this.c(generatoraccessseed, iblockdata, l1, k1, i2, structureboundingbox);
                        }
                    }
                }
            }
        }

    }

    protected void a(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, Random random, float f, int i, int j, int k, IBlockData iblockdata) {
        if (random.nextFloat() < f) {
            this.c(generatoraccessseed, iblockdata, i, j, k, structureboundingbox);
        }

    }

    protected void a(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, int i, int j, int k, int l, int i1, int j1, IBlockData iblockdata, boolean flag) {
        float f = (float) (l - i + 1);
        float f1 = (float) (i1 - j + 1);
        float f2 = (float) (j1 - k + 1);
        float f3 = (float) i + f / 2.0F;
        float f4 = (float) k + f2 / 2.0F;

        for (int k1 = j; k1 <= i1; ++k1) {
            float f5 = (float) (k1 - j) / f1;

            for (int l1 = i; l1 <= l; ++l1) {
                float f6 = ((float) l1 - f3) / (f * 0.5F);

                for (int i2 = k; i2 <= j1; ++i2) {
                    float f7 = ((float) i2 - f4) / (f2 * 0.5F);

                    if (!flag || !this.a((IBlockAccess) generatoraccessseed, l1, k1, i2, structureboundingbox).isAir()) {
                        float f8 = f6 * f6 + f5 * f5 + f7 * f7;

                        if (f8 <= 1.05F) {
                            this.c(generatoraccessseed, iblockdata, l1, k1, i2, structureboundingbox);
                        }
                    }
                }
            }
        }

    }

    protected void a(GeneratorAccessSeed generatoraccessseed, IBlockData iblockdata, int i, int j, int k, StructureBoundingBox structureboundingbox) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = this.c(i, j, k);

        if (structureboundingbox.b((BaseBlockPosition) blockposition_mutableblockposition)) {
            while (this.a(generatoraccessseed.getType(blockposition_mutableblockposition)) && blockposition_mutableblockposition.getY() > generatoraccessseed.getMinBuildHeight() + 1) {
                generatoraccessseed.setTypeAndData(blockposition_mutableblockposition, iblockdata, 2);
                blockposition_mutableblockposition.c(EnumDirection.DOWN);
            }

        }
    }

    protected boolean a(IBlockData iblockdata) {
        return iblockdata.isAir() || iblockdata.getMaterial().isLiquid() || iblockdata.a(Blocks.GLOW_LICHEN) || iblockdata.a(Blocks.SEAGRASS) || iblockdata.a(Blocks.TALL_SEAGRASS);
    }

    protected boolean a(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, Random random, int i, int j, int k, MinecraftKey minecraftkey) {
        return this.a(generatoraccessseed, structureboundingbox, random, this.c(i, j, k), minecraftkey, (IBlockData) null);
    }

    public static IBlockData a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        EnumDirection enumdirection = null;
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection1 = (EnumDirection) iterator.next();
            BlockPosition blockposition1 = blockposition.shift(enumdirection1);
            IBlockData iblockdata1 = iblockaccess.getType(blockposition1);

            if (iblockdata1.a(Blocks.CHEST)) {
                return iblockdata;
            }

            if (iblockdata1.i(iblockaccess, blockposition1)) {
                if (enumdirection != null) {
                    enumdirection = null;
                    break;
                }

                enumdirection = enumdirection1;
            }
        }

        if (enumdirection != null) {
            return (IBlockData) iblockdata.set(BlockFacingHorizontal.FACING, enumdirection.opposite());
        } else {
            EnumDirection enumdirection2 = (EnumDirection) iblockdata.get(BlockFacingHorizontal.FACING);
            BlockPosition blockposition2 = blockposition.shift(enumdirection2);

            if (iblockaccess.getType(blockposition2).i(iblockaccess, blockposition2)) {
                enumdirection2 = enumdirection2.opposite();
                blockposition2 = blockposition.shift(enumdirection2);
            }

            if (iblockaccess.getType(blockposition2).i(iblockaccess, blockposition2)) {
                enumdirection2 = enumdirection2.g();
                blockposition2 = blockposition.shift(enumdirection2);
            }

            if (iblockaccess.getType(blockposition2).i(iblockaccess, blockposition2)) {
                enumdirection2 = enumdirection2.opposite();
                blockposition.shift(enumdirection2);
            }

            return (IBlockData) iblockdata.set(BlockFacingHorizontal.FACING, enumdirection2);
        }
    }

    protected boolean a(WorldAccess worldaccess, StructureBoundingBox structureboundingbox, Random random, BlockPosition blockposition, MinecraftKey minecraftkey, @Nullable IBlockData iblockdata) {
        if (structureboundingbox.b((BaseBlockPosition) blockposition) && !worldaccess.getType(blockposition).a(Blocks.CHEST)) {
            if (iblockdata == null) {
                iblockdata = a((IBlockAccess) worldaccess, blockposition, Blocks.CHEST.getBlockData());
            }

            worldaccess.setTypeAndData(blockposition, iblockdata, 2);
            TileEntity tileentity = worldaccess.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityChest) {
                ((TileEntityChest) tileentity).setLootTable(minecraftkey, random.nextLong());
            }

            return true;
        } else {
            return false;
        }
    }

    protected boolean a(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, Random random, int i, int j, int k, EnumDirection enumdirection, MinecraftKey minecraftkey) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = this.c(i, j, k);

        if (structureboundingbox.b((BaseBlockPosition) blockposition_mutableblockposition) && !generatoraccessseed.getType(blockposition_mutableblockposition).a(Blocks.DISPENSER)) {
            this.c(generatoraccessseed, (IBlockData) Blocks.DISPENSER.getBlockData().set(BlockDispenser.FACING, enumdirection), i, j, k, structureboundingbox);
            TileEntity tileentity = generatoraccessseed.getTileEntity(blockposition_mutableblockposition);

            if (tileentity instanceof TileEntityDispenser) {
                ((TileEntityDispenser) tileentity).setLootTable(minecraftkey, random.nextLong());
            }

            return true;
        } else {
            return false;
        }
    }

    public void a(int i, int j, int k) {
        this.boundingBox.a(i, j, k);
    }

    @Nullable
    public EnumDirection h() {
        return this.orientation;
    }

    public void a(@Nullable EnumDirection enumdirection) {
        this.orientation = enumdirection;
        if (enumdirection == null) {
            this.rotation = EnumBlockRotation.NONE;
            this.mirror = EnumBlockMirror.NONE;
        } else {
            switch (enumdirection) {
                case SOUTH:
                    this.mirror = EnumBlockMirror.LEFT_RIGHT;
                    this.rotation = EnumBlockRotation.NONE;
                    break;
                case WEST:
                    this.mirror = EnumBlockMirror.LEFT_RIGHT;
                    this.rotation = EnumBlockRotation.CLOCKWISE_90;
                    break;
                case EAST:
                    this.mirror = EnumBlockMirror.NONE;
                    this.rotation = EnumBlockRotation.CLOCKWISE_90;
                    break;
                default:
                    this.mirror = EnumBlockMirror.NONE;
                    this.rotation = EnumBlockRotation.NONE;
            }
        }

    }

    public EnumBlockRotation ac_() {
        return this.rotation;
    }

    public EnumBlockMirror i() {
        return this.mirror;
    }

    public WorldGenFeatureStructurePieceType j() {
        return this.type;
    }

    protected abstract static class StructurePieceBlockSelector {

        protected IBlockData next;

        protected StructurePieceBlockSelector() {
            this.next = Blocks.AIR.getBlockData();
        }

        public abstract void a(Random random, int i, int j, int k, boolean flag);

        public IBlockData a() {
            return this.next;
        }
    }
}
