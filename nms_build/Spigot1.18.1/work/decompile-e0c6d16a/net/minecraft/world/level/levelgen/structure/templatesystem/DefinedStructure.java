package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.RegistryBlockID;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.decoration.EntityPainting;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.BlockAccessAir;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnumBlockMirror;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.block.IFluidContainer;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityLootable;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.VoxelShapeBitSet;
import net.minecraft.world.phys.shapes.VoxelShapeDiscrete;

public class DefinedStructure {

    public static final String PALETTE_TAG = "palette";
    public static final String PALETTE_LIST_TAG = "palettes";
    public static final String ENTITIES_TAG = "entities";
    public static final String BLOCKS_TAG = "blocks";
    public static final String BLOCK_TAG_POS = "pos";
    public static final String BLOCK_TAG_STATE = "state";
    public static final String BLOCK_TAG_NBT = "nbt";
    public static final String ENTITY_TAG_POS = "pos";
    public static final String ENTITY_TAG_BLOCKPOS = "blockPos";
    public static final String ENTITY_TAG_NBT = "nbt";
    public static final String SIZE_TAG = "size";
    static final int CHUNK_SIZE = 16;
    public final List<DefinedStructure.a> palettes = Lists.newArrayList();
    public final List<DefinedStructure.EntityInfo> entityInfoList = Lists.newArrayList();
    private BaseBlockPosition size;
    private String author;

    public DefinedStructure() {
        this.size = BaseBlockPosition.ZERO;
        this.author = "?";
    }

    public BaseBlockPosition getSize() {
        return this.size;
    }

    public void setAuthor(String s) {
        this.author = s;
    }

    public String getAuthor() {
        return this.author;
    }

    public void fillFromWorld(World world, BlockPosition blockposition, BaseBlockPosition baseblockposition, boolean flag, @Nullable Block block) {
        if (baseblockposition.getX() >= 1 && baseblockposition.getY() >= 1 && baseblockposition.getZ() >= 1) {
            BlockPosition blockposition1 = blockposition.offset(baseblockposition).offset(-1, -1, -1);
            List<DefinedStructure.BlockInfo> list = Lists.newArrayList();
            List<DefinedStructure.BlockInfo> list1 = Lists.newArrayList();
            List<DefinedStructure.BlockInfo> list2 = Lists.newArrayList();
            BlockPosition blockposition2 = new BlockPosition(Math.min(blockposition.getX(), blockposition1.getX()), Math.min(blockposition.getY(), blockposition1.getY()), Math.min(blockposition.getZ(), blockposition1.getZ()));
            BlockPosition blockposition3 = new BlockPosition(Math.max(blockposition.getX(), blockposition1.getX()), Math.max(blockposition.getY(), blockposition1.getY()), Math.max(blockposition.getZ(), blockposition1.getZ()));

            this.size = baseblockposition;
            Iterator iterator = BlockPosition.betweenClosed(blockposition2, blockposition3).iterator();

            while (iterator.hasNext()) {
                BlockPosition blockposition4 = (BlockPosition) iterator.next();
                BlockPosition blockposition5 = blockposition4.subtract(blockposition2);
                IBlockData iblockdata = world.getBlockState(blockposition4);

                if (block == null || !iblockdata.is(block)) {
                    TileEntity tileentity = world.getBlockEntity(blockposition4);
                    DefinedStructure.BlockInfo definedstructure_blockinfo;

                    if (tileentity != null) {
                        definedstructure_blockinfo = new DefinedStructure.BlockInfo(blockposition5, iblockdata, tileentity.saveWithId());
                    } else {
                        definedstructure_blockinfo = new DefinedStructure.BlockInfo(blockposition5, iblockdata, (NBTTagCompound) null);
                    }

                    addToLists(definedstructure_blockinfo, list, list1, list2);
                }
            }

            List<DefinedStructure.BlockInfo> list3 = buildInfoList(list, list1, list2);

            this.palettes.clear();
            this.palettes.add(new DefinedStructure.a(list3));
            if (flag) {
                this.fillEntityList(world, blockposition2, blockposition3.offset(1, 1, 1));
            } else {
                this.entityInfoList.clear();
            }

        }
    }

    private static void addToLists(DefinedStructure.BlockInfo definedstructure_blockinfo, List<DefinedStructure.BlockInfo> list, List<DefinedStructure.BlockInfo> list1, List<DefinedStructure.BlockInfo> list2) {
        if (definedstructure_blockinfo.nbt != null) {
            list1.add(definedstructure_blockinfo);
        } else if (!definedstructure_blockinfo.state.getBlock().hasDynamicShape() && definedstructure_blockinfo.state.isCollisionShapeFullBlock(BlockAccessAir.INSTANCE, BlockPosition.ZERO)) {
            list.add(definedstructure_blockinfo);
        } else {
            list2.add(definedstructure_blockinfo);
        }

    }

    private static List<DefinedStructure.BlockInfo> buildInfoList(List<DefinedStructure.BlockInfo> list, List<DefinedStructure.BlockInfo> list1, List<DefinedStructure.BlockInfo> list2) {
        Comparator<DefinedStructure.BlockInfo> comparator = Comparator.comparingInt((definedstructure_blockinfo) -> {
            return definedstructure_blockinfo.pos.getY();
        }).thenComparingInt((definedstructure_blockinfo) -> {
            return definedstructure_blockinfo.pos.getX();
        }).thenComparingInt((definedstructure_blockinfo) -> {
            return definedstructure_blockinfo.pos.getZ();
        });

        list.sort(comparator);
        list2.sort(comparator);
        list1.sort(comparator);
        List<DefinedStructure.BlockInfo> list3 = Lists.newArrayList();

        list3.addAll(list);
        list3.addAll(list2);
        list3.addAll(list1);
        return list3;
    }

    private void fillEntityList(World world, BlockPosition blockposition, BlockPosition blockposition1) {
        List<Entity> list = world.getEntitiesOfClass(Entity.class, new AxisAlignedBB(blockposition, blockposition1), (entity) -> {
            return !(entity instanceof EntityHuman);
        });

        this.entityInfoList.clear();

        Vec3D vec3d;
        NBTTagCompound nbttagcompound;
        BlockPosition blockposition2;

        for (Iterator iterator = list.iterator(); iterator.hasNext(); this.entityInfoList.add(new DefinedStructure.EntityInfo(vec3d, blockposition2, nbttagcompound.copy()))) {
            Entity entity = (Entity) iterator.next();

            vec3d = new Vec3D(entity.getX() - (double) blockposition.getX(), entity.getY() - (double) blockposition.getY(), entity.getZ() - (double) blockposition.getZ());
            nbttagcompound = new NBTTagCompound();
            entity.save(nbttagcompound);
            if (entity instanceof EntityPainting) {
                blockposition2 = ((EntityPainting) entity).getPos().subtract(blockposition);
            } else {
                blockposition2 = new BlockPosition(vec3d);
            }
        }

    }

    public List<DefinedStructure.BlockInfo> filterBlocks(BlockPosition blockposition, DefinedStructureInfo definedstructureinfo, Block block) {
        return this.filterBlocks(blockposition, definedstructureinfo, block, true);
    }

    public List<DefinedStructure.BlockInfo> filterBlocks(BlockPosition blockposition, DefinedStructureInfo definedstructureinfo, Block block, boolean flag) {
        List<DefinedStructure.BlockInfo> list = Lists.newArrayList();
        StructureBoundingBox structureboundingbox = definedstructureinfo.getBoundingBox();

        if (this.palettes.isEmpty()) {
            return Collections.emptyList();
        } else {
            Iterator iterator = definedstructureinfo.getRandomPalette(this.palettes, blockposition).blocks(block).iterator();

            while (iterator.hasNext()) {
                DefinedStructure.BlockInfo definedstructure_blockinfo = (DefinedStructure.BlockInfo) iterator.next();
                BlockPosition blockposition1 = flag ? calculateRelativePosition(definedstructureinfo, definedstructure_blockinfo.pos).offset(blockposition) : definedstructure_blockinfo.pos;

                if (structureboundingbox == null || structureboundingbox.isInside(blockposition1)) {
                    list.add(new DefinedStructure.BlockInfo(blockposition1, definedstructure_blockinfo.state.rotate(definedstructureinfo.getRotation()), definedstructure_blockinfo.nbt));
                }
            }

            return list;
        }
    }

    public BlockPosition calculateConnectedPosition(DefinedStructureInfo definedstructureinfo, BlockPosition blockposition, DefinedStructureInfo definedstructureinfo1, BlockPosition blockposition1) {
        BlockPosition blockposition2 = calculateRelativePosition(definedstructureinfo, blockposition);
        BlockPosition blockposition3 = calculateRelativePosition(definedstructureinfo1, blockposition1);

        return blockposition2.subtract(blockposition3);
    }

    public static BlockPosition calculateRelativePosition(DefinedStructureInfo definedstructureinfo, BlockPosition blockposition) {
        return transform(blockposition, definedstructureinfo.getMirror(), definedstructureinfo.getRotation(), definedstructureinfo.getRotationPivot());
    }

    public boolean placeInWorld(WorldAccess worldaccess, BlockPosition blockposition, BlockPosition blockposition1, DefinedStructureInfo definedstructureinfo, Random random, int i) {
        if (this.palettes.isEmpty()) {
            return false;
        } else {
            List<DefinedStructure.BlockInfo> list = definedstructureinfo.getRandomPalette(this.palettes, blockposition).blocks();

            if ((!list.isEmpty() || !definedstructureinfo.isIgnoreEntities() && !this.entityInfoList.isEmpty()) && this.size.getX() >= 1 && this.size.getY() >= 1 && this.size.getZ() >= 1) {
                StructureBoundingBox structureboundingbox = definedstructureinfo.getBoundingBox();
                List<BlockPosition> list1 = Lists.newArrayListWithCapacity(definedstructureinfo.shouldKeepLiquids() ? list.size() : 0);
                List<BlockPosition> list2 = Lists.newArrayListWithCapacity(definedstructureinfo.shouldKeepLiquids() ? list.size() : 0);
                List<Pair<BlockPosition, NBTTagCompound>> list3 = Lists.newArrayListWithCapacity(list.size());
                int j = Integer.MAX_VALUE;
                int k = Integer.MAX_VALUE;
                int l = Integer.MAX_VALUE;
                int i1 = Integer.MIN_VALUE;
                int j1 = Integer.MIN_VALUE;
                int k1 = Integer.MIN_VALUE;
                List<DefinedStructure.BlockInfo> list4 = processBlockInfos(worldaccess, blockposition, blockposition1, definedstructureinfo, list);
                Iterator iterator = list4.iterator();

                TileEntity tileentity;

                while (iterator.hasNext()) {
                    DefinedStructure.BlockInfo definedstructure_blockinfo = (DefinedStructure.BlockInfo) iterator.next();
                    BlockPosition blockposition2 = definedstructure_blockinfo.pos;

                    if (structureboundingbox == null || structureboundingbox.isInside(blockposition2)) {
                        Fluid fluid = definedstructureinfo.shouldKeepLiquids() ? worldaccess.getFluidState(blockposition2) : null;
                        IBlockData iblockdata = definedstructure_blockinfo.state.mirror(definedstructureinfo.getMirror()).rotate(definedstructureinfo.getRotation());

                        if (definedstructure_blockinfo.nbt != null) {
                            tileentity = worldaccess.getBlockEntity(blockposition2);
                            Clearable.tryClear(tileentity);
                            worldaccess.setBlock(blockposition2, Blocks.BARRIER.defaultBlockState(), 20);
                        }

                        if (worldaccess.setBlock(blockposition2, iblockdata, i)) {
                            j = Math.min(j, blockposition2.getX());
                            k = Math.min(k, blockposition2.getY());
                            l = Math.min(l, blockposition2.getZ());
                            i1 = Math.max(i1, blockposition2.getX());
                            j1 = Math.max(j1, blockposition2.getY());
                            k1 = Math.max(k1, blockposition2.getZ());
                            list3.add(Pair.of(blockposition2, definedstructure_blockinfo.nbt));
                            if (definedstructure_blockinfo.nbt != null) {
                                tileentity = worldaccess.getBlockEntity(blockposition2);
                                if (tileentity != null) {
                                    if (tileentity instanceof TileEntityLootable) {
                                        definedstructure_blockinfo.nbt.putLong("LootTableSeed", random.nextLong());
                                    }

                                    tileentity.load(definedstructure_blockinfo.nbt);
                                }
                            }

                            if (fluid != null) {
                                if (iblockdata.getFluidState().isSource()) {
                                    list2.add(blockposition2);
                                } else if (iblockdata.getBlock() instanceof IFluidContainer) {
                                    ((IFluidContainer) iblockdata.getBlock()).placeLiquid(worldaccess, blockposition2, iblockdata, fluid);
                                    if (!fluid.isSource()) {
                                        list1.add(blockposition2);
                                    }
                                }
                            }
                        }
                    }
                }

                boolean flag = true;
                EnumDirection[] aenumdirection = new EnumDirection[]{EnumDirection.UP, EnumDirection.NORTH, EnumDirection.EAST, EnumDirection.SOUTH, EnumDirection.WEST};

                Iterator iterator1;
                int l1;
                IBlockData iblockdata1;

                while (flag && !list1.isEmpty()) {
                    flag = false;
                    iterator1 = list1.iterator();

                    while (iterator1.hasNext()) {
                        BlockPosition blockposition3 = (BlockPosition) iterator1.next();
                        Fluid fluid1 = worldaccess.getFluidState(blockposition3);

                        for (l1 = 0; l1 < aenumdirection.length && !fluid1.isSource(); ++l1) {
                            BlockPosition blockposition4 = blockposition3.relative(aenumdirection[l1]);
                            Fluid fluid2 = worldaccess.getFluidState(blockposition4);

                            if (fluid2.isSource() && !list2.contains(blockposition4)) {
                                fluid1 = fluid2;
                            }
                        }

                        if (fluid1.isSource()) {
                            iblockdata1 = worldaccess.getBlockState(blockposition3);
                            Block block = iblockdata1.getBlock();

                            if (block instanceof IFluidContainer) {
                                ((IFluidContainer) block).placeLiquid(worldaccess, blockposition3, iblockdata1, fluid1);
                                flag = true;
                                iterator1.remove();
                            }
                        }
                    }
                }

                if (j <= i1) {
                    if (!definedstructureinfo.getKnownShape()) {
                        VoxelShapeBitSet voxelshapebitset = new VoxelShapeBitSet(i1 - j + 1, j1 - k + 1, k1 - l + 1);
                        int i2 = j;
                        int j2 = k;

                        l1 = l;
                        Iterator iterator2 = list3.iterator();

                        while (iterator2.hasNext()) {
                            Pair<BlockPosition, NBTTagCompound> pair = (Pair) iterator2.next();
                            BlockPosition blockposition5 = (BlockPosition) pair.getFirst();

                            voxelshapebitset.fill(blockposition5.getX() - i2, blockposition5.getY() - j2, blockposition5.getZ() - l1);
                        }

                        updateShapeAtEdge(worldaccess, i, voxelshapebitset, i2, j2, l1);
                    }

                    iterator1 = list3.iterator();

                    while (iterator1.hasNext()) {
                        Pair<BlockPosition, NBTTagCompound> pair1 = (Pair) iterator1.next();
                        BlockPosition blockposition6 = (BlockPosition) pair1.getFirst();

                        if (!definedstructureinfo.getKnownShape()) {
                            iblockdata1 = worldaccess.getBlockState(blockposition6);
                            IBlockData iblockdata2 = Block.updateFromNeighbourShapes(iblockdata1, worldaccess, blockposition6);

                            if (iblockdata1 != iblockdata2) {
                                worldaccess.setBlock(blockposition6, iblockdata2, i & -2 | 16);
                            }

                            worldaccess.blockUpdated(blockposition6, iblockdata2.getBlock());
                        }

                        if (pair1.getSecond() != null) {
                            tileentity = worldaccess.getBlockEntity(blockposition6);
                            if (tileentity != null) {
                                tileentity.setChanged();
                            }
                        }
                    }
                }

                if (!definedstructureinfo.isIgnoreEntities()) {
                    this.placeEntities(worldaccess, blockposition, definedstructureinfo.getMirror(), definedstructureinfo.getRotation(), definedstructureinfo.getRotationPivot(), structureboundingbox, definedstructureinfo.shouldFinalizeEntities());
                }

                return true;
            } else {
                return false;
            }
        }
    }

    public static void updateShapeAtEdge(GeneratorAccess generatoraccess, int i, VoxelShapeDiscrete voxelshapediscrete, int j, int k, int l) {
        voxelshapediscrete.forAllFaces((enumdirection, i1, j1, k1) -> {
            BlockPosition blockposition = new BlockPosition(j + i1, k + j1, l + k1);
            BlockPosition blockposition1 = blockposition.relative(enumdirection);
            IBlockData iblockdata = generatoraccess.getBlockState(blockposition);
            IBlockData iblockdata1 = generatoraccess.getBlockState(blockposition1);
            IBlockData iblockdata2 = iblockdata.updateShape(enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);

            if (iblockdata != iblockdata2) {
                generatoraccess.setBlock(blockposition, iblockdata2, i & -2);
            }

            IBlockData iblockdata3 = iblockdata1.updateShape(enumdirection.getOpposite(), iblockdata2, generatoraccess, blockposition1, blockposition);

            if (iblockdata1 != iblockdata3) {
                generatoraccess.setBlock(blockposition1, iblockdata3, i & -2);
            }

        });
    }

    public static List<DefinedStructure.BlockInfo> processBlockInfos(GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1, DefinedStructureInfo definedstructureinfo, List<DefinedStructure.BlockInfo> list) {
        List<DefinedStructure.BlockInfo> list1 = Lists.newArrayList();
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            DefinedStructure.BlockInfo definedstructure_blockinfo = (DefinedStructure.BlockInfo) iterator.next();
            BlockPosition blockposition2 = calculateRelativePosition(definedstructureinfo, definedstructure_blockinfo.pos).offset(blockposition);
            DefinedStructure.BlockInfo definedstructure_blockinfo1 = new DefinedStructure.BlockInfo(blockposition2, definedstructure_blockinfo.state, definedstructure_blockinfo.nbt != null ? definedstructure_blockinfo.nbt.copy() : null);

            for (Iterator iterator1 = definedstructureinfo.getProcessors().iterator(); definedstructure_blockinfo1 != null && iterator1.hasNext(); definedstructure_blockinfo1 = ((DefinedStructureProcessor) iterator1.next()).processBlock(generatoraccess, blockposition, blockposition1, definedstructure_blockinfo, definedstructure_blockinfo1, definedstructureinfo)) {
                ;
            }

            if (definedstructure_blockinfo1 != null) {
                list1.add(definedstructure_blockinfo1);
            }
        }

        return list1;
    }

    private void placeEntities(WorldAccess worldaccess, BlockPosition blockposition, EnumBlockMirror enumblockmirror, EnumBlockRotation enumblockrotation, BlockPosition blockposition1, @Nullable StructureBoundingBox structureboundingbox, boolean flag) {
        Iterator iterator = this.entityInfoList.iterator();

        while (iterator.hasNext()) {
            DefinedStructure.EntityInfo definedstructure_entityinfo = (DefinedStructure.EntityInfo) iterator.next();
            BlockPosition blockposition2 = transform(definedstructure_entityinfo.blockPos, enumblockmirror, enumblockrotation, blockposition1).offset(blockposition);

            if (structureboundingbox == null || structureboundingbox.isInside(blockposition2)) {
                NBTTagCompound nbttagcompound = definedstructure_entityinfo.nbt.copy();
                Vec3D vec3d = transform(definedstructure_entityinfo.pos, enumblockmirror, enumblockrotation, blockposition1);
                Vec3D vec3d1 = vec3d.add((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ());
                NBTTagList nbttaglist = new NBTTagList();

                nbttaglist.add(NBTTagDouble.valueOf(vec3d1.x));
                nbttaglist.add(NBTTagDouble.valueOf(vec3d1.y));
                nbttaglist.add(NBTTagDouble.valueOf(vec3d1.z));
                nbttagcompound.put("Pos", nbttaglist);
                nbttagcompound.remove("UUID");
                createEntityIgnoreException(worldaccess, nbttagcompound).ifPresent((entity) -> {
                    float f = entity.mirror(enumblockmirror);

                    f += entity.getYRot() - entity.rotate(enumblockrotation);
                    entity.moveTo(vec3d1.x, vec3d1.y, vec3d1.z, f, entity.getXRot());
                    if (flag && entity instanceof EntityInsentient) {
                        ((EntityInsentient) entity).finalizeSpawn(worldaccess, worldaccess.getCurrentDifficultyAt(new BlockPosition(vec3d1)), EnumMobSpawn.STRUCTURE, (GroupDataEntity) null, nbttagcompound);
                    }

                    worldaccess.addFreshEntityWithPassengers(entity);
                });
            }
        }

    }

    private static Optional<Entity> createEntityIgnoreException(WorldAccess worldaccess, NBTTagCompound nbttagcompound) {
        try {
            return EntityTypes.create(nbttagcompound, (World) worldaccess.getLevel());
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    public BaseBlockPosition getSize(EnumBlockRotation enumblockrotation) {
        switch (enumblockrotation) {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:
                return new BaseBlockPosition(this.size.getZ(), this.size.getY(), this.size.getX());
            default:
                return this.size;
        }
    }

    public static BlockPosition transform(BlockPosition blockposition, EnumBlockMirror enumblockmirror, EnumBlockRotation enumblockrotation, BlockPosition blockposition1) {
        int i = blockposition.getX();
        int j = blockposition.getY();
        int k = blockposition.getZ();
        boolean flag = true;

        switch (enumblockmirror) {
            case LEFT_RIGHT:
                k = -k;
                break;
            case FRONT_BACK:
                i = -i;
                break;
            default:
                flag = false;
        }

        int l = blockposition1.getX();
        int i1 = blockposition1.getZ();

        switch (enumblockrotation) {
            case COUNTERCLOCKWISE_90:
                return new BlockPosition(l - i1 + k, j, l + i1 - i);
            case CLOCKWISE_90:
                return new BlockPosition(l + i1 - k, j, i1 - l + i);
            case CLOCKWISE_180:
                return new BlockPosition(l + l - i, j, i1 + i1 - k);
            default:
                return flag ? new BlockPosition(i, j, k) : blockposition;
        }
    }

    public static Vec3D transform(Vec3D vec3d, EnumBlockMirror enumblockmirror, EnumBlockRotation enumblockrotation, BlockPosition blockposition) {
        double d0 = vec3d.x;
        double d1 = vec3d.y;
        double d2 = vec3d.z;
        boolean flag = true;

        switch (enumblockmirror) {
            case LEFT_RIGHT:
                d2 = 1.0D - d2;
                break;
            case FRONT_BACK:
                d0 = 1.0D - d0;
                break;
            default:
                flag = false;
        }

        int i = blockposition.getX();
        int j = blockposition.getZ();

        switch (enumblockrotation) {
            case COUNTERCLOCKWISE_90:
                return new Vec3D((double) (i - j) + d2, d1, (double) (i + j + 1) - d0);
            case CLOCKWISE_90:
                return new Vec3D((double) (i + j + 1) - d2, d1, (double) (j - i) + d0);
            case CLOCKWISE_180:
                return new Vec3D((double) (i + i + 1) - d0, d1, (double) (j + j + 1) - d2);
            default:
                return flag ? new Vec3D(d0, d1, d2) : vec3d;
        }
    }

    public BlockPosition getZeroPositionWithTransform(BlockPosition blockposition, EnumBlockMirror enumblockmirror, EnumBlockRotation enumblockrotation) {
        return getZeroPositionWithTransform(blockposition, enumblockmirror, enumblockrotation, this.getSize().getX(), this.getSize().getZ());
    }

    public static BlockPosition getZeroPositionWithTransform(BlockPosition blockposition, EnumBlockMirror enumblockmirror, EnumBlockRotation enumblockrotation, int i, int j) {
        --i;
        --j;
        int k = enumblockmirror == EnumBlockMirror.FRONT_BACK ? i : 0;
        int l = enumblockmirror == EnumBlockMirror.LEFT_RIGHT ? j : 0;
        BlockPosition blockposition1 = blockposition;

        switch (enumblockrotation) {
            case COUNTERCLOCKWISE_90:
                blockposition1 = blockposition.offset(l, 0, i - k);
                break;
            case CLOCKWISE_90:
                blockposition1 = blockposition.offset(j - l, 0, k);
                break;
            case CLOCKWISE_180:
                blockposition1 = blockposition.offset(i - k, 0, j - l);
                break;
            case NONE:
                blockposition1 = blockposition.offset(k, 0, l);
        }

        return blockposition1;
    }

    public StructureBoundingBox getBoundingBox(DefinedStructureInfo definedstructureinfo, BlockPosition blockposition) {
        return this.getBoundingBox(blockposition, definedstructureinfo.getRotation(), definedstructureinfo.getRotationPivot(), definedstructureinfo.getMirror());
    }

    public StructureBoundingBox getBoundingBox(BlockPosition blockposition, EnumBlockRotation enumblockrotation, BlockPosition blockposition1, EnumBlockMirror enumblockmirror) {
        return getBoundingBox(blockposition, enumblockrotation, blockposition1, enumblockmirror, this.size);
    }

    @VisibleForTesting
    protected static StructureBoundingBox getBoundingBox(BlockPosition blockposition, EnumBlockRotation enumblockrotation, BlockPosition blockposition1, EnumBlockMirror enumblockmirror, BaseBlockPosition baseblockposition) {
        BaseBlockPosition baseblockposition1 = baseblockposition.offset(-1, -1, -1);
        BlockPosition blockposition2 = transform(BlockPosition.ZERO, enumblockmirror, enumblockrotation, blockposition1);
        BlockPosition blockposition3 = transform(BlockPosition.ZERO.offset(baseblockposition1), enumblockmirror, enumblockrotation, blockposition1);

        return StructureBoundingBox.fromCorners(blockposition2, blockposition3).move(blockposition);
    }

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        if (this.palettes.isEmpty()) {
            nbttagcompound.put("blocks", new NBTTagList());
            nbttagcompound.put("palette", new NBTTagList());
        } else {
            List<DefinedStructure.b> list = Lists.newArrayList();
            DefinedStructure.b definedstructure_b = new DefinedStructure.b();

            list.add(definedstructure_b);

            for (int i = 1; i < this.palettes.size(); ++i) {
                list.add(new DefinedStructure.b());
            }

            NBTTagList nbttaglist = new NBTTagList();
            List<DefinedStructure.BlockInfo> list1 = ((DefinedStructure.a) this.palettes.get(0)).blocks();

            for (int j = 0; j < list1.size(); ++j) {
                DefinedStructure.BlockInfo definedstructure_blockinfo = (DefinedStructure.BlockInfo) list1.get(j);
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                nbttagcompound1.put("pos", this.newIntegerList(definedstructure_blockinfo.pos.getX(), definedstructure_blockinfo.pos.getY(), definedstructure_blockinfo.pos.getZ()));
                int k = definedstructure_b.idFor(definedstructure_blockinfo.state);

                nbttagcompound1.putInt("state", k);
                if (definedstructure_blockinfo.nbt != null) {
                    nbttagcompound1.put("nbt", definedstructure_blockinfo.nbt);
                }

                nbttaglist.add(nbttagcompound1);

                for (int l = 1; l < this.palettes.size(); ++l) {
                    DefinedStructure.b definedstructure_b1 = (DefinedStructure.b) list.get(l);

                    definedstructure_b1.addMapping(((DefinedStructure.BlockInfo) ((DefinedStructure.a) this.palettes.get(l)).blocks().get(j)).state, k);
                }
            }

            nbttagcompound.put("blocks", nbttaglist);
            NBTTagList nbttaglist1;
            Iterator iterator;

            if (list.size() == 1) {
                nbttaglist1 = new NBTTagList();
                iterator = definedstructure_b.iterator();

                while (iterator.hasNext()) {
                    IBlockData iblockdata = (IBlockData) iterator.next();

                    nbttaglist1.add(GameProfileSerializer.writeBlockState(iblockdata));
                }

                nbttagcompound.put("palette", nbttaglist1);
            } else {
                nbttaglist1 = new NBTTagList();
                iterator = list.iterator();

                while (iterator.hasNext()) {
                    DefinedStructure.b definedstructure_b2 = (DefinedStructure.b) iterator.next();
                    NBTTagList nbttaglist2 = new NBTTagList();
                    Iterator iterator1 = definedstructure_b2.iterator();

                    while (iterator1.hasNext()) {
                        IBlockData iblockdata1 = (IBlockData) iterator1.next();

                        nbttaglist2.add(GameProfileSerializer.writeBlockState(iblockdata1));
                    }

                    nbttaglist1.add(nbttaglist2);
                }

                nbttagcompound.put("palettes", nbttaglist1);
            }
        }

        NBTTagList nbttaglist3 = new NBTTagList();

        NBTTagCompound nbttagcompound2;

        for (Iterator iterator2 = this.entityInfoList.iterator(); iterator2.hasNext(); nbttaglist3.add(nbttagcompound2)) {
            DefinedStructure.EntityInfo definedstructure_entityinfo = (DefinedStructure.EntityInfo) iterator2.next();

            nbttagcompound2 = new NBTTagCompound();
            nbttagcompound2.put("pos", this.newDoubleList(definedstructure_entityinfo.pos.x, definedstructure_entityinfo.pos.y, definedstructure_entityinfo.pos.z));
            nbttagcompound2.put("blockPos", this.newIntegerList(definedstructure_entityinfo.blockPos.getX(), definedstructure_entityinfo.blockPos.getY(), definedstructure_entityinfo.blockPos.getZ()));
            if (definedstructure_entityinfo.nbt != null) {
                nbttagcompound2.put("nbt", definedstructure_entityinfo.nbt);
            }
        }

        nbttagcompound.put("entities", nbttaglist3);
        nbttagcompound.put("size", this.newIntegerList(this.size.getX(), this.size.getY(), this.size.getZ()));
        nbttagcompound.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
        return nbttagcompound;
    }

    public void load(NBTTagCompound nbttagcompound) {
        this.palettes.clear();
        this.entityInfoList.clear();
        NBTTagList nbttaglist = nbttagcompound.getList("size", 3);

        this.size = new BaseBlockPosition(nbttaglist.getInt(0), nbttaglist.getInt(1), nbttaglist.getInt(2));
        NBTTagList nbttaglist1 = nbttagcompound.getList("blocks", 10);
        NBTTagList nbttaglist2;
        int i;

        if (nbttagcompound.contains("palettes", 9)) {
            nbttaglist2 = nbttagcompound.getList("palettes", 9);

            for (i = 0; i < nbttaglist2.size(); ++i) {
                this.loadPalette(nbttaglist2.getList(i), nbttaglist1);
            }
        } else {
            this.loadPalette(nbttagcompound.getList("palette", 10), nbttaglist1);
        }

        nbttaglist2 = nbttagcompound.getList("entities", 10);

        for (i = 0; i < nbttaglist2.size(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist2.getCompound(i);
            NBTTagList nbttaglist3 = nbttagcompound1.getList("pos", 6);
            Vec3D vec3d = new Vec3D(nbttaglist3.getDouble(0), nbttaglist3.getDouble(1), nbttaglist3.getDouble(2));
            NBTTagList nbttaglist4 = nbttagcompound1.getList("blockPos", 3);
            BlockPosition blockposition = new BlockPosition(nbttaglist4.getInt(0), nbttaglist4.getInt(1), nbttaglist4.getInt(2));

            if (nbttagcompound1.contains("nbt")) {
                NBTTagCompound nbttagcompound2 = nbttagcompound1.getCompound("nbt");

                this.entityInfoList.add(new DefinedStructure.EntityInfo(vec3d, blockposition, nbttagcompound2));
            }
        }

    }

    private void loadPalette(NBTTagList nbttaglist, NBTTagList nbttaglist1) {
        DefinedStructure.b definedstructure_b = new DefinedStructure.b();

        for (int i = 0; i < nbttaglist.size(); ++i) {
            definedstructure_b.addMapping(GameProfileSerializer.readBlockState(nbttaglist.getCompound(i)), i);
        }

        List<DefinedStructure.BlockInfo> list = Lists.newArrayList();
        List<DefinedStructure.BlockInfo> list1 = Lists.newArrayList();
        List<DefinedStructure.BlockInfo> list2 = Lists.newArrayList();

        for (int j = 0; j < nbttaglist1.size(); ++j) {
            NBTTagCompound nbttagcompound = nbttaglist1.getCompound(j);
            NBTTagList nbttaglist2 = nbttagcompound.getList("pos", 3);
            BlockPosition blockposition = new BlockPosition(nbttaglist2.getInt(0), nbttaglist2.getInt(1), nbttaglist2.getInt(2));
            IBlockData iblockdata = definedstructure_b.stateFor(nbttagcompound.getInt("state"));
            NBTTagCompound nbttagcompound1;

            if (nbttagcompound.contains("nbt")) {
                nbttagcompound1 = nbttagcompound.getCompound("nbt");
            } else {
                nbttagcompound1 = null;
            }

            DefinedStructure.BlockInfo definedstructure_blockinfo = new DefinedStructure.BlockInfo(blockposition, iblockdata, nbttagcompound1);

            addToLists(definedstructure_blockinfo, list, list1, list2);
        }

        List<DefinedStructure.BlockInfo> list3 = buildInfoList(list, list1, list2);

        this.palettes.add(new DefinedStructure.a(list3));
    }

    private NBTTagList newIntegerList(int... aint) {
        NBTTagList nbttaglist = new NBTTagList();
        int[] aint1 = aint;
        int i = aint.length;

        for (int j = 0; j < i; ++j) {
            int k = aint1[j];

            nbttaglist.add(NBTTagInt.valueOf(k));
        }

        return nbttaglist;
    }

    private NBTTagList newDoubleList(double... adouble) {
        NBTTagList nbttaglist = new NBTTagList();
        double[] adouble1 = adouble;
        int i = adouble.length;

        for (int j = 0; j < i; ++j) {
            double d0 = adouble1[j];

            nbttaglist.add(NBTTagDouble.valueOf(d0));
        }

        return nbttaglist;
    }

    public static class BlockInfo {

        public final BlockPosition pos;
        public final IBlockData state;
        public final NBTTagCompound nbt;

        public BlockInfo(BlockPosition blockposition, IBlockData iblockdata, @Nullable NBTTagCompound nbttagcompound) {
            this.pos = blockposition;
            this.state = iblockdata;
            this.nbt = nbttagcompound;
        }

        public String toString() {
            return String.format("<StructureBlockInfo | %s | %s | %s>", this.pos, this.state, this.nbt);
        }
    }

    public static final class a {

        private final List<DefinedStructure.BlockInfo> blocks;
        private final Map<Block, List<DefinedStructure.BlockInfo>> cache = Maps.newHashMap();

        a(List<DefinedStructure.BlockInfo> list) {
            this.blocks = list;
        }

        public List<DefinedStructure.BlockInfo> blocks() {
            return this.blocks;
        }

        public List<DefinedStructure.BlockInfo> blocks(Block block) {
            return (List) this.cache.computeIfAbsent(block, (block1) -> {
                return (List) this.blocks.stream().filter((definedstructure_blockinfo) -> {
                    return definedstructure_blockinfo.state.is(block1);
                }).collect(Collectors.toList());
            });
        }
    }

    public static class EntityInfo {

        public final Vec3D pos;
        public final BlockPosition blockPos;
        public final NBTTagCompound nbt;

        public EntityInfo(Vec3D vec3d, BlockPosition blockposition, NBTTagCompound nbttagcompound) {
            this.pos = vec3d;
            this.blockPos = blockposition;
            this.nbt = nbttagcompound;
        }
    }

    private static class b implements Iterable<IBlockData> {

        public static final IBlockData DEFAULT_BLOCK_STATE = Blocks.AIR.defaultBlockState();
        private final RegistryBlockID<IBlockData> ids = new RegistryBlockID<>(16);
        private int lastId;

        b() {}

        public int idFor(IBlockData iblockdata) {
            int i = this.ids.getId(iblockdata);

            if (i == -1) {
                i = this.lastId++;
                this.ids.addMapping(iblockdata, i);
            }

            return i;
        }

        @Nullable
        public IBlockData stateFor(int i) {
            IBlockData iblockdata = (IBlockData) this.ids.byId(i);

            return iblockdata == null ? DefinedStructure.b.DEFAULT_BLOCK_STATE : iblockdata;
        }

        public Iterator<IBlockData> iterator() {
            return this.ids.iterator();
        }

        public void addMapping(IBlockData iblockdata, int i) {
            this.ids.addMapping(iblockdata, i);
        }
    }
}
