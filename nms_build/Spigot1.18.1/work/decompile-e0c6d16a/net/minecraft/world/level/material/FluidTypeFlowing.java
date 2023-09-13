package net.minecraft.world.level.material;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockDoor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IFluidContainer;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapes;

public abstract class FluidTypeFlowing extends FluidType {

    public static final BlockStateBoolean FALLING = BlockProperties.FALLING;
    public static final BlockStateInteger LEVEL = BlockProperties.LEVEL_FLOWING;
    private static final int CACHE_SIZE = 200;
    private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.a>> OCCLUSION_CACHE = ThreadLocal.withInitial(() -> {
        Object2ByteLinkedOpenHashMap<Block.a> object2bytelinkedopenhashmap = new Object2ByteLinkedOpenHashMap<Block.a>(200) {
            protected void rehash(int i) {}
        };

        object2bytelinkedopenhashmap.defaultReturnValue((byte) 127);
        return object2bytelinkedopenhashmap;
    });
    private final Map<Fluid, VoxelShape> shapes = Maps.newIdentityHashMap();

    public FluidTypeFlowing() {}

    @Override
    protected void createFluidStateDefinition(BlockStateList.a<FluidType, Fluid> blockstatelist_a) {
        blockstatelist_a.add(FluidTypeFlowing.FALLING);
    }

    @Override
    public Vec3D getFlow(IBlockAccess iblockaccess, BlockPosition blockposition, Fluid fluid) {
        double d0 = 0.0D;
        double d1 = 0.0D;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator.next();

            blockposition_mutableblockposition.setWithOffset(blockposition, enumdirection);
            Fluid fluid1 = iblockaccess.getFluidState(blockposition_mutableblockposition);

            if (this.affectsFlow(fluid1)) {
                float f = fluid1.getOwnHeight();
                float f1 = 0.0F;

                if (f == 0.0F) {
                    if (!iblockaccess.getBlockState(blockposition_mutableblockposition).getMaterial().blocksMotion()) {
                        BlockPosition blockposition1 = blockposition_mutableblockposition.below();
                        Fluid fluid2 = iblockaccess.getFluidState(blockposition1);

                        if (this.affectsFlow(fluid2)) {
                            f = fluid2.getOwnHeight();
                            if (f > 0.0F) {
                                f1 = fluid.getOwnHeight() - (f - 0.8888889F);
                            }
                        }
                    }
                } else if (f > 0.0F) {
                    f1 = fluid.getOwnHeight() - f;
                }

                if (f1 != 0.0F) {
                    d0 += (double) ((float) enumdirection.getStepX() * f1);
                    d1 += (double) ((float) enumdirection.getStepZ() * f1);
                }
            }
        }

        Vec3D vec3d = new Vec3D(d0, 0.0D, d1);

        if ((Boolean) fluid.getValue(FluidTypeFlowing.FALLING)) {
            Iterator iterator1 = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            while (iterator1.hasNext()) {
                EnumDirection enumdirection1 = (EnumDirection) iterator1.next();

                blockposition_mutableblockposition.setWithOffset(blockposition, enumdirection1);
                if (this.isSolidFace(iblockaccess, blockposition_mutableblockposition, enumdirection1) || this.isSolidFace(iblockaccess, blockposition_mutableblockposition.above(), enumdirection1)) {
                    vec3d = vec3d.normalize().add(0.0D, -6.0D, 0.0D);
                    break;
                }
            }
        }

        return vec3d.normalize();
    }

    private boolean affectsFlow(Fluid fluid) {
        return fluid.isEmpty() || fluid.getType().isSame(this);
    }

    protected boolean isSolidFace(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        IBlockData iblockdata = iblockaccess.getBlockState(blockposition);
        Fluid fluid = iblockaccess.getFluidState(blockposition);

        return fluid.getType().isSame(this) ? false : (enumdirection == EnumDirection.UP ? true : (iblockdata.getMaterial() == Material.ICE ? false : iblockdata.isFaceSturdy(iblockaccess, blockposition, enumdirection)));
    }

    protected void spread(GeneratorAccess generatoraccess, BlockPosition blockposition, Fluid fluid) {
        if (!fluid.isEmpty()) {
            IBlockData iblockdata = generatoraccess.getBlockState(blockposition);
            BlockPosition blockposition1 = blockposition.below();
            IBlockData iblockdata1 = generatoraccess.getBlockState(blockposition1);
            Fluid fluid1 = this.getNewLiquid(generatoraccess, blockposition1, iblockdata1);

            if (this.canSpreadTo(generatoraccess, blockposition, iblockdata, EnumDirection.DOWN, blockposition1, iblockdata1, generatoraccess.getFluidState(blockposition1), fluid1.getType())) {
                this.spreadTo(generatoraccess, blockposition1, iblockdata1, EnumDirection.DOWN, fluid1);
                if (this.sourceNeighborCount(generatoraccess, blockposition) >= 3) {
                    this.spreadToSides(generatoraccess, blockposition, fluid, iblockdata);
                }
            } else if (fluid.isSource() || !this.isWaterHole(generatoraccess, fluid1.getType(), blockposition, iblockdata, blockposition1, iblockdata1)) {
                this.spreadToSides(generatoraccess, blockposition, fluid, iblockdata);
            }

        }
    }

    private void spreadToSides(GeneratorAccess generatoraccess, BlockPosition blockposition, Fluid fluid, IBlockData iblockdata) {
        int i = fluid.getAmount() - this.getDropOff(generatoraccess);

        if ((Boolean) fluid.getValue(FluidTypeFlowing.FALLING)) {
            i = 7;
        }

        if (i > 0) {
            Map<EnumDirection, Fluid> map = this.getSpread(generatoraccess, blockposition, iblockdata);
            Iterator iterator = map.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry<EnumDirection, Fluid> entry = (Entry) iterator.next();
                EnumDirection enumdirection = (EnumDirection) entry.getKey();
                Fluid fluid1 = (Fluid) entry.getValue();
                BlockPosition blockposition1 = blockposition.relative(enumdirection);
                IBlockData iblockdata1 = generatoraccess.getBlockState(blockposition1);

                if (this.canSpreadTo(generatoraccess, blockposition, iblockdata, enumdirection, blockposition1, iblockdata1, generatoraccess.getFluidState(blockposition1), fluid1.getType())) {
                    this.spreadTo(generatoraccess, blockposition1, iblockdata1, enumdirection, fluid1);
                }
            }

        }
    }

    protected Fluid getNewLiquid(IWorldReader iworldreader, BlockPosition blockposition, IBlockData iblockdata) {
        int i = 0;
        int j = 0;
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator.next();
            BlockPosition blockposition1 = blockposition.relative(enumdirection);
            IBlockData iblockdata1 = iworldreader.getBlockState(blockposition1);
            Fluid fluid = iblockdata1.getFluidState();

            if (fluid.getType().isSame(this) && this.canPassThroughWall(enumdirection, iworldreader, blockposition, iblockdata, blockposition1, iblockdata1)) {
                if (fluid.isSource()) {
                    ++j;
                }

                i = Math.max(i, fluid.getAmount());
            }
        }

        if (this.canConvertToSource() && j >= 2) {
            IBlockData iblockdata2 = iworldreader.getBlockState(blockposition.below());
            Fluid fluid1 = iblockdata2.getFluidState();

            if (iblockdata2.getMaterial().isSolid() || this.isSourceBlockOfThisType(fluid1)) {
                return this.getSource(false);
            }
        }

        BlockPosition blockposition2 = blockposition.above();
        IBlockData iblockdata3 = iworldreader.getBlockState(blockposition2);
        Fluid fluid2 = iblockdata3.getFluidState();

        if (!fluid2.isEmpty() && fluid2.getType().isSame(this) && this.canPassThroughWall(EnumDirection.UP, iworldreader, blockposition, iblockdata, blockposition2, iblockdata3)) {
            return this.getFlowing(8, true);
        } else {
            int k = i - this.getDropOff(iworldreader);

            return k <= 0 ? FluidTypes.EMPTY.defaultFluidState() : this.getFlowing(k, false);
        }
    }

    private boolean canPassThroughWall(EnumDirection enumdirection, IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, BlockPosition blockposition1, IBlockData iblockdata1) {
        Object2ByteLinkedOpenHashMap object2bytelinkedopenhashmap;

        if (!iblockdata.getBlock().hasDynamicShape() && !iblockdata1.getBlock().hasDynamicShape()) {
            object2bytelinkedopenhashmap = (Object2ByteLinkedOpenHashMap) FluidTypeFlowing.OCCLUSION_CACHE.get();
        } else {
            object2bytelinkedopenhashmap = null;
        }

        Block.a block_a;

        if (object2bytelinkedopenhashmap != null) {
            block_a = new Block.a(iblockdata, iblockdata1, enumdirection);
            byte b0 = object2bytelinkedopenhashmap.getAndMoveToFirst(block_a);

            if (b0 != 127) {
                return b0 != 0;
            }
        } else {
            block_a = null;
        }

        VoxelShape voxelshape = iblockdata.getCollisionShape(iblockaccess, blockposition);
        VoxelShape voxelshape1 = iblockdata1.getCollisionShape(iblockaccess, blockposition1);
        boolean flag = !VoxelShapes.mergedFaceOccludes(voxelshape, voxelshape1, enumdirection);

        if (object2bytelinkedopenhashmap != null) {
            if (object2bytelinkedopenhashmap.size() == 200) {
                object2bytelinkedopenhashmap.removeLastByte();
            }

            object2bytelinkedopenhashmap.putAndMoveToFirst(block_a, (byte) (flag ? 1 : 0));
        }

        return flag;
    }

    public abstract FluidType getFlowing();

    public Fluid getFlowing(int i, boolean flag) {
        return (Fluid) ((Fluid) this.getFlowing().defaultFluidState().setValue(FluidTypeFlowing.LEVEL, i)).setValue(FluidTypeFlowing.FALLING, flag);
    }

    public abstract FluidType getSource();

    public Fluid getSource(boolean flag) {
        return (Fluid) this.getSource().defaultFluidState().setValue(FluidTypeFlowing.FALLING, flag);
    }

    protected abstract boolean canConvertToSource();

    protected void spreadTo(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection, Fluid fluid) {
        if (iblockdata.getBlock() instanceof IFluidContainer) {
            ((IFluidContainer) iblockdata.getBlock()).placeLiquid(generatoraccess, blockposition, iblockdata, fluid);
        } else {
            if (!iblockdata.isAir()) {
                this.beforeDestroyingBlock(generatoraccess, blockposition, iblockdata);
            }

            generatoraccess.setBlock(blockposition, fluid.createLegacyBlock(), 3);
        }

    }

    protected abstract void beforeDestroyingBlock(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata);

    private static short getCacheKey(BlockPosition blockposition, BlockPosition blockposition1) {
        int i = blockposition1.getX() - blockposition.getX();
        int j = blockposition1.getZ() - blockposition.getZ();

        return (short) ((i + 128 & 255) << 8 | j + 128 & 255);
    }

    protected int getSlopeDistance(IWorldReader iworldreader, BlockPosition blockposition, int i, EnumDirection enumdirection, IBlockData iblockdata, BlockPosition blockposition1, Short2ObjectMap<Pair<IBlockData, Fluid>> short2objectmap, Short2BooleanMap short2booleanmap) {
        int j = 1000;
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection1 = (EnumDirection) iterator.next();

            if (enumdirection1 != enumdirection) {
                BlockPosition blockposition2 = blockposition.relative(enumdirection1);
                short short0 = getCacheKey(blockposition1, blockposition2);
                Pair<IBlockData, Fluid> pair = (Pair) short2objectmap.computeIfAbsent(short0, (short1) -> {
                    IBlockData iblockdata1 = iworldreader.getBlockState(blockposition2);

                    return Pair.of(iblockdata1, iblockdata1.getFluidState());
                });
                IBlockData iblockdata1 = (IBlockData) pair.getFirst();
                Fluid fluid = (Fluid) pair.getSecond();

                if (this.canPassThrough(iworldreader, this.getFlowing(), blockposition, iblockdata, enumdirection1, blockposition2, iblockdata1, fluid)) {
                    boolean flag = short2booleanmap.computeIfAbsent(short0, (short1) -> {
                        BlockPosition blockposition3 = blockposition2.below();
                        IBlockData iblockdata2 = iworldreader.getBlockState(blockposition3);

                        return this.isWaterHole(iworldreader, this.getFlowing(), blockposition2, iblockdata1, blockposition3, iblockdata2);
                    });

                    if (flag) {
                        return i;
                    }

                    if (i < this.getSlopeFindDistance(iworldreader)) {
                        int k = this.getSlopeDistance(iworldreader, blockposition2, i + 1, enumdirection1.getOpposite(), iblockdata1, blockposition1, short2objectmap, short2booleanmap);

                        if (k < j) {
                            j = k;
                        }
                    }
                }
            }
        }

        return j;
    }

    private boolean isWaterHole(IBlockAccess iblockaccess, FluidType fluidtype, BlockPosition blockposition, IBlockData iblockdata, BlockPosition blockposition1, IBlockData iblockdata1) {
        return !this.canPassThroughWall(EnumDirection.DOWN, iblockaccess, blockposition, iblockdata, blockposition1, iblockdata1) ? false : (iblockdata1.getFluidState().getType().isSame(this) ? true : this.canHoldFluid(iblockaccess, blockposition1, iblockdata1, fluidtype));
    }

    private boolean canPassThrough(IBlockAccess iblockaccess, FluidType fluidtype, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection, BlockPosition blockposition1, IBlockData iblockdata1, Fluid fluid) {
        return !this.isSourceBlockOfThisType(fluid) && this.canPassThroughWall(enumdirection, iblockaccess, blockposition, iblockdata, blockposition1, iblockdata1) && this.canHoldFluid(iblockaccess, blockposition1, iblockdata1, fluidtype);
    }

    private boolean isSourceBlockOfThisType(Fluid fluid) {
        return fluid.getType().isSame(this) && fluid.isSource();
    }

    protected abstract int getSlopeFindDistance(IWorldReader iworldreader);

    private int sourceNeighborCount(IWorldReader iworldreader, BlockPosition blockposition) {
        int i = 0;
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator.next();
            BlockPosition blockposition1 = blockposition.relative(enumdirection);
            Fluid fluid = iworldreader.getFluidState(blockposition1);

            if (this.isSourceBlockOfThisType(fluid)) {
                ++i;
            }
        }

        return i;
    }

    protected Map<EnumDirection, Fluid> getSpread(IWorldReader iworldreader, BlockPosition blockposition, IBlockData iblockdata) {
        int i = 1000;
        Map<EnumDirection, Fluid> map = Maps.newEnumMap(EnumDirection.class);
        Short2ObjectMap<Pair<IBlockData, Fluid>> short2objectmap = new Short2ObjectOpenHashMap();
        Short2BooleanOpenHashMap short2booleanopenhashmap = new Short2BooleanOpenHashMap();
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator.next();
            BlockPosition blockposition1 = blockposition.relative(enumdirection);
            short short0 = getCacheKey(blockposition, blockposition1);
            Pair<IBlockData, Fluid> pair = (Pair) short2objectmap.computeIfAbsent(short0, (short1) -> {
                IBlockData iblockdata1 = iworldreader.getBlockState(blockposition1);

                return Pair.of(iblockdata1, iblockdata1.getFluidState());
            });
            IBlockData iblockdata1 = (IBlockData) pair.getFirst();
            Fluid fluid = (Fluid) pair.getSecond();
            Fluid fluid1 = this.getNewLiquid(iworldreader, blockposition1, iblockdata1);

            if (this.canPassThrough(iworldreader, fluid1.getType(), blockposition, iblockdata, enumdirection, blockposition1, iblockdata1, fluid)) {
                BlockPosition blockposition2 = blockposition1.below();
                boolean flag = short2booleanopenhashmap.computeIfAbsent(short0, (short1) -> {
                    IBlockData iblockdata2 = iworldreader.getBlockState(blockposition2);

                    return this.isWaterHole(iworldreader, this.getFlowing(), blockposition1, iblockdata1, blockposition2, iblockdata2);
                });
                int j;

                if (flag) {
                    j = 0;
                } else {
                    j = this.getSlopeDistance(iworldreader, blockposition1, 1, enumdirection.getOpposite(), iblockdata1, blockposition, short2objectmap, short2booleanopenhashmap);
                }

                if (j < i) {
                    map.clear();
                }

                if (j <= i) {
                    map.put(enumdirection, fluid1);
                    i = j;
                }
            }
        }

        return map;
    }

    private boolean canHoldFluid(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, FluidType fluidtype) {
        Block block = iblockdata.getBlock();

        if (block instanceof IFluidContainer) {
            return ((IFluidContainer) block).canPlaceLiquid(iblockaccess, blockposition, iblockdata, fluidtype);
        } else if (!(block instanceof BlockDoor) && !iblockdata.is((Tag) TagsBlock.SIGNS) && !iblockdata.is(Blocks.LADDER) && !iblockdata.is(Blocks.SUGAR_CANE) && !iblockdata.is(Blocks.BUBBLE_COLUMN)) {
            Material material = iblockdata.getMaterial();

            return material != Material.PORTAL && material != Material.STRUCTURAL_AIR && material != Material.WATER_PLANT && material != Material.REPLACEABLE_WATER_PLANT ? !material.blocksMotion() : false;
        } else {
            return false;
        }
    }

    protected boolean canSpreadTo(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection, BlockPosition blockposition1, IBlockData iblockdata1, Fluid fluid, FluidType fluidtype) {
        return fluid.canBeReplacedWith(iblockaccess, blockposition1, fluidtype, enumdirection) && this.canPassThroughWall(enumdirection, iblockaccess, blockposition, iblockdata, blockposition1, iblockdata1) && this.canHoldFluid(iblockaccess, blockposition1, iblockdata1, fluidtype);
    }

    protected abstract int getDropOff(IWorldReader iworldreader);

    protected int getSpreadDelay(World world, BlockPosition blockposition, Fluid fluid, Fluid fluid1) {
        return this.getTickDelay(world);
    }

    @Override
    public void tick(World world, BlockPosition blockposition, Fluid fluid) {
        if (!fluid.isSource()) {
            Fluid fluid1 = this.getNewLiquid(world, blockposition, world.getBlockState(blockposition));
            int i = this.getSpreadDelay(world, blockposition, fluid, fluid1);

            if (fluid1.isEmpty()) {
                fluid = fluid1;
                world.setBlock(blockposition, Blocks.AIR.defaultBlockState(), 3);
            } else if (!fluid1.equals(fluid)) {
                fluid = fluid1;
                IBlockData iblockdata = fluid1.createLegacyBlock();

                world.setBlock(blockposition, iblockdata, 2);
                world.scheduleTick(blockposition, fluid1.getType(), i);
                world.updateNeighborsAt(blockposition, iblockdata.getBlock());
            }
        }

        this.spread(world, blockposition, fluid);
    }

    protected static int getLegacyLevel(Fluid fluid) {
        return fluid.isSource() ? 0 : 8 - Math.min(fluid.getAmount(), 8) + ((Boolean) fluid.getValue(FluidTypeFlowing.FALLING) ? 8 : 0);
    }

    private static boolean hasSameAbove(Fluid fluid, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return fluid.getType().isSame(iblockaccess.getFluidState(blockposition.above()).getType());
    }

    @Override
    public float getHeight(Fluid fluid, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return hasSameAbove(fluid, iblockaccess, blockposition) ? 1.0F : fluid.getOwnHeight();
    }

    @Override
    public float getOwnHeight(Fluid fluid) {
        return (float) fluid.getAmount() / 9.0F;
    }

    @Override
    public abstract int getAmount(Fluid fluid);

    @Override
    public VoxelShape getShape(Fluid fluid, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return fluid.getAmount() == 9 && hasSameAbove(fluid, iblockaccess, blockposition) ? VoxelShapes.block() : (VoxelShape) this.shapes.computeIfAbsent(fluid, (fluid1) -> {
            return VoxelShapes.box(0.0D, 0.0D, 0.0D, 1.0D, (double) fluid1.getHeight(iblockaccess, blockposition), 1.0D);
        });
    }
}
