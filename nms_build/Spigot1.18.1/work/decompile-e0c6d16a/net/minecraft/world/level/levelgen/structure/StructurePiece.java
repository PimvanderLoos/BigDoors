package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.DataResult;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
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
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.material.Fluid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class StructurePiece {

    private static final Logger LOGGER = LogManager.getLogger();
    protected static final IBlockData CAVE_AIR = Blocks.CAVE_AIR.defaultBlockState();
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

        this.setOrientation(j == -1 ? null : EnumDirection.from2DDataValue(j));
    }

    protected static StructureBoundingBox makeBoundingBox(int i, int j, int k, EnumDirection enumdirection, int l, int i1, int j1) {
        return enumdirection.getAxis() == EnumDirection.EnumAxis.Z ? new StructureBoundingBox(i, j, k, i + l - 1, j + i1 - 1, k + j1 - 1) : new StructureBoundingBox(i, j, k, i + j1 - 1, j + i1 - 1, k + l - 1);
    }

    protected static EnumDirection getRandomHorizontalDirection(Random random) {
        return EnumDirection.EnumDirectionLimit.HORIZONTAL.getRandomDirection(random);
    }

    public final NBTTagCompound createTag(StructurePieceSerializationContext structurepieceserializationcontext) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.putString("id", IRegistry.STRUCTURE_PIECE.getKey(this.getType()).toString());
        DataResult dataresult = StructureBoundingBox.CODEC.encodeStart(DynamicOpsNBT.INSTANCE, this.boundingBox);
        Logger logger = StructurePiece.LOGGER;

        Objects.requireNonNull(logger);
        dataresult.resultOrPartial(logger::error).ifPresent((nbtbase) -> {
            nbttagcompound.put("BB", nbtbase);
        });
        EnumDirection enumdirection = this.getOrientation();

        nbttagcompound.putInt("O", enumdirection == null ? -1 : enumdirection.get2DDataValue());
        nbttagcompound.putInt("GD", this.genDepth);
        this.addAdditionalSaveData(structurepieceserializationcontext, nbttagcompound);
        return nbttagcompound;
    }

    protected abstract void addAdditionalSaveData(StructurePieceSerializationContext structurepieceserializationcontext, NBTTagCompound nbttagcompound);

    public NoiseEffect getNoiseEffect() {
        return NoiseEffect.BEARD;
    }

    public void addChildren(StructurePiece structurepiece, StructurePieceAccessor structurepieceaccessor, Random random) {}

    public abstract void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition);

    public StructureBoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    public int getGenDepth() {
        return this.genDepth;
    }

    public boolean isCloseToChunk(ChunkCoordIntPair chunkcoordintpair, int i) {
        int j = chunkcoordintpair.getMinBlockX();
        int k = chunkcoordintpair.getMinBlockZ();

        return this.boundingBox.intersects(j - i, k - i, j + 15 + i, k + 15 + i);
    }

    public BlockPosition getLocatorPosition() {
        return new BlockPosition(this.boundingBox.getCenter());
    }

    protected BlockPosition.MutableBlockPosition getWorldPos(int i, int j, int k) {
        return new BlockPosition.MutableBlockPosition(this.getWorldX(i, k), this.getWorldY(j), this.getWorldZ(i, k));
    }

    protected int getWorldX(int i, int j) {
        EnumDirection enumdirection = this.getOrientation();

        if (enumdirection == null) {
            return i;
        } else {
            switch (enumdirection) {
                case NORTH:
                case SOUTH:
                    return this.boundingBox.minX() + i;
                case WEST:
                    return this.boundingBox.maxX() - j;
                case EAST:
                    return this.boundingBox.minX() + j;
                default:
                    return i;
            }
        }
    }

    protected int getWorldY(int i) {
        return this.getOrientation() == null ? i : i + this.boundingBox.minY();
    }

    protected int getWorldZ(int i, int j) {
        EnumDirection enumdirection = this.getOrientation();

        if (enumdirection == null) {
            return j;
        } else {
            switch (enumdirection) {
                case NORTH:
                    return this.boundingBox.maxZ() - j;
                case SOUTH:
                    return this.boundingBox.minZ() + j;
                case WEST:
                case EAST:
                    return this.boundingBox.minZ() + i;
                default:
                    return j;
            }
        }
    }

    protected void placeBlock(GeneratorAccessSeed generatoraccessseed, IBlockData iblockdata, int i, int j, int k, StructureBoundingBox structureboundingbox) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = this.getWorldPos(i, j, k);

        if (structureboundingbox.isInside(blockposition_mutableblockposition)) {
            if (this.canBeReplaced(generatoraccessseed, i, j, k, structureboundingbox)) {
                if (this.mirror != EnumBlockMirror.NONE) {
                    iblockdata = iblockdata.mirror(this.mirror);
                }

                if (this.rotation != EnumBlockRotation.NONE) {
                    iblockdata = iblockdata.rotate(this.rotation);
                }

                generatoraccessseed.setBlock(blockposition_mutableblockposition, iblockdata, 2);
                Fluid fluid = generatoraccessseed.getFluidState(blockposition_mutableblockposition);

                if (!fluid.isEmpty()) {
                    generatoraccessseed.scheduleTick(blockposition_mutableblockposition, fluid.getType(), 0);
                }

                if (StructurePiece.SHAPE_CHECK_BLOCKS.contains(iblockdata.getBlock())) {
                    generatoraccessseed.getChunk(blockposition_mutableblockposition).markPosForPostprocessing(blockposition_mutableblockposition);
                }

            }
        }
    }

    protected boolean canBeReplaced(IWorldReader iworldreader, int i, int j, int k, StructureBoundingBox structureboundingbox) {
        return true;
    }

    protected IBlockData getBlock(IBlockAccess iblockaccess, int i, int j, int k, StructureBoundingBox structureboundingbox) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = this.getWorldPos(i, j, k);

        return !structureboundingbox.isInside(blockposition_mutableblockposition) ? Blocks.AIR.defaultBlockState() : iblockaccess.getBlockState(blockposition_mutableblockposition);
    }

    protected boolean isInterior(IWorldReader iworldreader, int i, int j, int k, StructureBoundingBox structureboundingbox) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = this.getWorldPos(i, j + 1, k);

        return !structureboundingbox.isInside(blockposition_mutableblockposition) ? false : blockposition_mutableblockposition.getY() < iworldreader.getHeight(HeightMap.Type.OCEAN_FLOOR_WG, blockposition_mutableblockposition.getX(), blockposition_mutableblockposition.getZ());
    }

    protected void generateAirBox(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, int i, int j, int k, int l, int i1, int j1) {
        for (int k1 = j; k1 <= i1; ++k1) {
            for (int l1 = i; l1 <= l; ++l1) {
                for (int i2 = k; i2 <= j1; ++i2) {
                    this.placeBlock(generatoraccessseed, Blocks.AIR.defaultBlockState(), l1, k1, i2, structureboundingbox);
                }
            }
        }

    }

    protected void generateBox(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, int i, int j, int k, int l, int i1, int j1, IBlockData iblockdata, IBlockData iblockdata1, boolean flag) {
        for (int k1 = j; k1 <= i1; ++k1) {
            for (int l1 = i; l1 <= l; ++l1) {
                for (int i2 = k; i2 <= j1; ++i2) {
                    if (!flag || !this.getBlock(generatoraccessseed, l1, k1, i2, structureboundingbox).isAir()) {
                        if (k1 != j && k1 != i1 && l1 != i && l1 != l && i2 != k && i2 != j1) {
                            this.placeBlock(generatoraccessseed, iblockdata1, l1, k1, i2, structureboundingbox);
                        } else {
                            this.placeBlock(generatoraccessseed, iblockdata, l1, k1, i2, structureboundingbox);
                        }
                    }
                }
            }
        }

    }

    protected void generateBox(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, StructureBoundingBox structureboundingbox1, IBlockData iblockdata, IBlockData iblockdata1, boolean flag) {
        this.generateBox(generatoraccessseed, structureboundingbox, structureboundingbox1.minX(), structureboundingbox1.minY(), structureboundingbox1.minZ(), structureboundingbox1.maxX(), structureboundingbox1.maxY(), structureboundingbox1.maxZ(), iblockdata, iblockdata1, flag);
    }

    protected void generateBox(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, int i, int j, int k, int l, int i1, int j1, boolean flag, Random random, StructurePiece.StructurePieceBlockSelector structurepiece_structurepieceblockselector) {
        for (int k1 = j; k1 <= i1; ++k1) {
            for (int l1 = i; l1 <= l; ++l1) {
                for (int i2 = k; i2 <= j1; ++i2) {
                    if (!flag || !this.getBlock(generatoraccessseed, l1, k1, i2, structureboundingbox).isAir()) {
                        structurepiece_structurepieceblockselector.next(random, l1, k1, i2, k1 == j || k1 == i1 || l1 == i || l1 == l || i2 == k || i2 == j1);
                        this.placeBlock(generatoraccessseed, structurepiece_structurepieceblockselector.getNext(), l1, k1, i2, structureboundingbox);
                    }
                }
            }
        }

    }

    protected void generateBox(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, StructureBoundingBox structureboundingbox1, boolean flag, Random random, StructurePiece.StructurePieceBlockSelector structurepiece_structurepieceblockselector) {
        this.generateBox(generatoraccessseed, structureboundingbox, structureboundingbox1.minX(), structureboundingbox1.minY(), structureboundingbox1.minZ(), structureboundingbox1.maxX(), structureboundingbox1.maxY(), structureboundingbox1.maxZ(), flag, random, structurepiece_structurepieceblockselector);
    }

    protected void generateMaybeBox(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, Random random, float f, int i, int j, int k, int l, int i1, int j1, IBlockData iblockdata, IBlockData iblockdata1, boolean flag, boolean flag1) {
        for (int k1 = j; k1 <= i1; ++k1) {
            for (int l1 = i; l1 <= l; ++l1) {
                for (int i2 = k; i2 <= j1; ++i2) {
                    if (random.nextFloat() <= f && (!flag || !this.getBlock(generatoraccessseed, l1, k1, i2, structureboundingbox).isAir()) && (!flag1 || this.isInterior(generatoraccessseed, l1, k1, i2, structureboundingbox))) {
                        if (k1 != j && k1 != i1 && l1 != i && l1 != l && i2 != k && i2 != j1) {
                            this.placeBlock(generatoraccessseed, iblockdata1, l1, k1, i2, structureboundingbox);
                        } else {
                            this.placeBlock(generatoraccessseed, iblockdata, l1, k1, i2, structureboundingbox);
                        }
                    }
                }
            }
        }

    }

    protected void maybeGenerateBlock(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, Random random, float f, int i, int j, int k, IBlockData iblockdata) {
        if (random.nextFloat() < f) {
            this.placeBlock(generatoraccessseed, iblockdata, i, j, k, structureboundingbox);
        }

    }

    protected void generateUpperHalfSphere(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, int i, int j, int k, int l, int i1, int j1, IBlockData iblockdata, boolean flag) {
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

                    if (!flag || !this.getBlock(generatoraccessseed, l1, k1, i2, structureboundingbox).isAir()) {
                        float f8 = f6 * f6 + f5 * f5 + f7 * f7;

                        if (f8 <= 1.05F) {
                            this.placeBlock(generatoraccessseed, iblockdata, l1, k1, i2, structureboundingbox);
                        }
                    }
                }
            }
        }

    }

    protected void fillColumnDown(GeneratorAccessSeed generatoraccessseed, IBlockData iblockdata, int i, int j, int k, StructureBoundingBox structureboundingbox) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = this.getWorldPos(i, j, k);

        if (structureboundingbox.isInside(blockposition_mutableblockposition)) {
            while (this.isReplaceableByStructures(generatoraccessseed.getBlockState(blockposition_mutableblockposition)) && blockposition_mutableblockposition.getY() > generatoraccessseed.getMinBuildHeight() + 1) {
                generatoraccessseed.setBlock(blockposition_mutableblockposition, iblockdata, 2);
                blockposition_mutableblockposition.move(EnumDirection.DOWN);
            }

        }
    }

    protected boolean isReplaceableByStructures(IBlockData iblockdata) {
        return iblockdata.isAir() || iblockdata.getMaterial().isLiquid() || iblockdata.is(Blocks.GLOW_LICHEN) || iblockdata.is(Blocks.SEAGRASS) || iblockdata.is(Blocks.TALL_SEAGRASS);
    }

    protected boolean createChest(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, Random random, int i, int j, int k, MinecraftKey minecraftkey) {
        return this.createChest(generatoraccessseed, structureboundingbox, random, this.getWorldPos(i, j, k), minecraftkey, (IBlockData) null);
    }

    public static IBlockData reorient(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        EnumDirection enumdirection = null;
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection1 = (EnumDirection) iterator.next();
            BlockPosition blockposition1 = blockposition.relative(enumdirection1);
            IBlockData iblockdata1 = iblockaccess.getBlockState(blockposition1);

            if (iblockdata1.is(Blocks.CHEST)) {
                return iblockdata;
            }

            if (iblockdata1.isSolidRender(iblockaccess, blockposition1)) {
                if (enumdirection != null) {
                    enumdirection = null;
                    break;
                }

                enumdirection = enumdirection1;
            }
        }

        if (enumdirection != null) {
            return (IBlockData) iblockdata.setValue(BlockFacingHorizontal.FACING, enumdirection.getOpposite());
        } else {
            EnumDirection enumdirection2 = (EnumDirection) iblockdata.getValue(BlockFacingHorizontal.FACING);
            BlockPosition blockposition2 = blockposition.relative(enumdirection2);

            if (iblockaccess.getBlockState(blockposition2).isSolidRender(iblockaccess, blockposition2)) {
                enumdirection2 = enumdirection2.getOpposite();
                blockposition2 = blockposition.relative(enumdirection2);
            }

            if (iblockaccess.getBlockState(blockposition2).isSolidRender(iblockaccess, blockposition2)) {
                enumdirection2 = enumdirection2.getClockWise();
                blockposition2 = blockposition.relative(enumdirection2);
            }

            if (iblockaccess.getBlockState(blockposition2).isSolidRender(iblockaccess, blockposition2)) {
                enumdirection2 = enumdirection2.getOpposite();
                blockposition.relative(enumdirection2);
            }

            return (IBlockData) iblockdata.setValue(BlockFacingHorizontal.FACING, enumdirection2);
        }
    }

    protected boolean createChest(WorldAccess worldaccess, StructureBoundingBox structureboundingbox, Random random, BlockPosition blockposition, MinecraftKey minecraftkey, @Nullable IBlockData iblockdata) {
        if (structureboundingbox.isInside(blockposition) && !worldaccess.getBlockState(blockposition).is(Blocks.CHEST)) {
            if (iblockdata == null) {
                iblockdata = reorient(worldaccess, blockposition, Blocks.CHEST.defaultBlockState());
            }

            worldaccess.setBlock(blockposition, iblockdata, 2);
            TileEntity tileentity = worldaccess.getBlockEntity(blockposition);

            if (tileentity instanceof TileEntityChest) {
                ((TileEntityChest) tileentity).setLootTable(minecraftkey, random.nextLong());
            }

            return true;
        } else {
            return false;
        }
    }

    protected boolean createDispenser(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, Random random, int i, int j, int k, EnumDirection enumdirection, MinecraftKey minecraftkey) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = this.getWorldPos(i, j, k);

        if (structureboundingbox.isInside(blockposition_mutableblockposition) && !generatoraccessseed.getBlockState(blockposition_mutableblockposition).is(Blocks.DISPENSER)) {
            this.placeBlock(generatoraccessseed, (IBlockData) Blocks.DISPENSER.defaultBlockState().setValue(BlockDispenser.FACING, enumdirection), i, j, k, structureboundingbox);
            TileEntity tileentity = generatoraccessseed.getBlockEntity(blockposition_mutableblockposition);

            if (tileentity instanceof TileEntityDispenser) {
                ((TileEntityDispenser) tileentity).setLootTable(minecraftkey, random.nextLong());
            }

            return true;
        } else {
            return false;
        }
    }

    public void move(int i, int j, int k) {
        this.boundingBox.move(i, j, k);
    }

    public static StructureBoundingBox createBoundingBox(Stream<StructurePiece> stream) {
        Stream stream1 = stream.map(StructurePiece::getBoundingBox);

        Objects.requireNonNull(stream1);
        return (StructureBoundingBox) StructureBoundingBox.encapsulatingBoxes(stream1::iterator).orElseThrow(() -> {
            return new IllegalStateException("Unable to calculate boundingbox without pieces");
        });
    }

    @Nullable
    public static StructurePiece findCollisionPiece(List<StructurePiece> list, StructureBoundingBox structureboundingbox) {
        Iterator iterator = list.iterator();

        StructurePiece structurepiece;

        do {
            if (!iterator.hasNext()) {
                return null;
            }

            structurepiece = (StructurePiece) iterator.next();
        } while (!structurepiece.getBoundingBox().intersects(structureboundingbox));

        return structurepiece;
    }

    @Nullable
    public EnumDirection getOrientation() {
        return this.orientation;
    }

    public void setOrientation(@Nullable EnumDirection enumdirection) {
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

    public EnumBlockRotation getRotation() {
        return this.rotation;
    }

    public EnumBlockMirror getMirror() {
        return this.mirror;
    }

    public WorldGenFeatureStructurePieceType getType() {
        return this.type;
    }

    protected abstract static class StructurePieceBlockSelector {

        protected IBlockData next;

        protected StructurePieceBlockSelector() {
            this.next = Blocks.AIR.defaultBlockState();
        }

        public abstract void next(Random random, int i, int j, int k, boolean flag);

        public IBlockData getNext() {
            return this.next;
        }
    }
}
