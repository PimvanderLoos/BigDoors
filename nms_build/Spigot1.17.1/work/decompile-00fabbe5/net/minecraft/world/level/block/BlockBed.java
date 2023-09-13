package net.minecraft.world.level.block;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.vehicle.DismountUtil;
import net.minecraft.world.item.EnumColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.ICollisionAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityBed;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockPropertyBedPart;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.BlockStateEnum;
import net.minecraft.world.level.material.EnumPistonReaction;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;
import org.apache.commons.lang3.ArrayUtils;

public class BlockBed extends BlockFacingHorizontal implements ITileEntity {

    public static final BlockStateEnum<BlockPropertyBedPart> PART = BlockProperties.BED_PART;
    public static final BlockStateBoolean OCCUPIED = BlockProperties.OCCUPIED;
    protected static final int HEIGHT = 9;
    protected static final VoxelShape BASE = Block.a(0.0D, 3.0D, 0.0D, 16.0D, 9.0D, 16.0D);
    private static final int LEG_WIDTH = 3;
    protected static final VoxelShape LEG_NORTH_WEST = Block.a(0.0D, 0.0D, 0.0D, 3.0D, 3.0D, 3.0D);
    protected static final VoxelShape LEG_SOUTH_WEST = Block.a(0.0D, 0.0D, 13.0D, 3.0D, 3.0D, 16.0D);
    protected static final VoxelShape LEG_NORTH_EAST = Block.a(13.0D, 0.0D, 0.0D, 16.0D, 3.0D, 3.0D);
    protected static final VoxelShape LEG_SOUTH_EAST = Block.a(13.0D, 0.0D, 13.0D, 16.0D, 3.0D, 16.0D);
    protected static final VoxelShape NORTH_SHAPE = VoxelShapes.a(BlockBed.BASE, BlockBed.LEG_NORTH_WEST, BlockBed.LEG_NORTH_EAST);
    protected static final VoxelShape SOUTH_SHAPE = VoxelShapes.a(BlockBed.BASE, BlockBed.LEG_SOUTH_WEST, BlockBed.LEG_SOUTH_EAST);
    protected static final VoxelShape WEST_SHAPE = VoxelShapes.a(BlockBed.BASE, BlockBed.LEG_NORTH_WEST, BlockBed.LEG_SOUTH_WEST);
    protected static final VoxelShape EAST_SHAPE = VoxelShapes.a(BlockBed.BASE, BlockBed.LEG_NORTH_EAST, BlockBed.LEG_SOUTH_EAST);
    private final EnumColor color;

    public BlockBed(EnumColor enumcolor, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.color = enumcolor;
        this.k((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BlockBed.PART, BlockPropertyBedPart.FOOT)).set(BlockBed.OCCUPIED, false));
    }

    @Nullable
    public static EnumDirection a(IBlockAccess iblockaccess, BlockPosition blockposition) {
        IBlockData iblockdata = iblockaccess.getType(blockposition);

        return iblockdata.getBlock() instanceof BlockBed ? (EnumDirection) iblockdata.get(BlockBed.FACING) : null;
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (world.isClientSide) {
            return EnumInteractionResult.CONSUME;
        } else {
            if (iblockdata.get(BlockBed.PART) != BlockPropertyBedPart.HEAD) {
                blockposition = blockposition.shift((EnumDirection) iblockdata.get(BlockBed.FACING));
                iblockdata = world.getType(blockposition);
                if (!iblockdata.a((Block) this)) {
                    return EnumInteractionResult.CONSUME;
                }
            }

            if (!a(world)) {
                world.a(blockposition, false);
                BlockPosition blockposition1 = blockposition.shift(((EnumDirection) iblockdata.get(BlockBed.FACING)).opposite());

                if (world.getType(blockposition1).a((Block) this)) {
                    world.a(blockposition1, false);
                }

                world.createExplosion((Entity) null, DamageSource.a(), (ExplosionDamageCalculator) null, (double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D, 5.0F, true, Explosion.Effect.DESTROY);
                return EnumInteractionResult.SUCCESS;
            } else if ((Boolean) iblockdata.get(BlockBed.OCCUPIED)) {
                if (!this.a(world, blockposition)) {
                    entityhuman.a((IChatBaseComponent) (new ChatMessage("block.minecraft.bed.occupied")), true);
                }

                return EnumInteractionResult.SUCCESS;
            } else {
                entityhuman.sleep(blockposition).ifLeft((entityhuman_enumbedresult) -> {
                    if (entityhuman_enumbedresult != null) {
                        entityhuman.a(entityhuman_enumbedresult.a(), true);
                    }

                });
                return EnumInteractionResult.SUCCESS;
            }
        }
    }

    public static boolean a(World world) {
        return world.getDimensionManager().isBedWorks();
    }

    private boolean a(World world, BlockPosition blockposition) {
        List<EntityVillager> list = world.a(EntityVillager.class, new AxisAlignedBB(blockposition), EntityLiving::isSleeping);

        if (list.isEmpty()) {
            return false;
        } else {
            ((EntityVillager) list.get(0)).entityWakeup();
            return true;
        }
    }

    @Override
    public void fallOn(World world, IBlockData iblockdata, BlockPosition blockposition, Entity entity, float f) {
        super.fallOn(world, iblockdata, blockposition, entity, f * 0.5F);
    }

    @Override
    public void a(IBlockAccess iblockaccess, Entity entity) {
        if (entity.bF()) {
            super.a(iblockaccess, entity);
        } else {
            this.a(entity);
        }

    }

    private void a(Entity entity) {
        Vec3D vec3d = entity.getMot();

        if (vec3d.y < 0.0D) {
            double d0 = entity instanceof EntityLiving ? 1.0D : 0.8D;

            entity.setMot(vec3d.x, -vec3d.y * 0.6600000262260437D * d0, vec3d.z);
        }

    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return enumdirection == a((BlockPropertyBedPart) iblockdata.get(BlockBed.PART), (EnumDirection) iblockdata.get(BlockBed.FACING)) ? (iblockdata1.a((Block) this) && iblockdata1.get(BlockBed.PART) != iblockdata.get(BlockBed.PART) ? (IBlockData) iblockdata.set(BlockBed.OCCUPIED, (Boolean) iblockdata1.get(BlockBed.OCCUPIED)) : Blocks.AIR.getBlockData()) : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    private static EnumDirection a(BlockPropertyBedPart blockpropertybedpart, EnumDirection enumdirection) {
        return blockpropertybedpart == BlockPropertyBedPart.FOOT ? enumdirection : enumdirection.opposite();
    }

    @Override
    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        if (!world.isClientSide && entityhuman.isCreative()) {
            BlockPropertyBedPart blockpropertybedpart = (BlockPropertyBedPart) iblockdata.get(BlockBed.PART);

            if (blockpropertybedpart == BlockPropertyBedPart.FOOT) {
                BlockPosition blockposition1 = blockposition.shift(a(blockpropertybedpart, (EnumDirection) iblockdata.get(BlockBed.FACING)));
                IBlockData iblockdata1 = world.getType(blockposition1);

                if (iblockdata1.a((Block) this) && iblockdata1.get(BlockBed.PART) == BlockPropertyBedPart.HEAD) {
                    world.setTypeAndData(blockposition1, Blocks.AIR.getBlockData(), 35);
                    world.a(entityhuman, 2001, blockposition1, Block.getCombinedId(iblockdata1));
                }
            }
        }

        super.a(world, blockposition, iblockdata, entityhuman);
    }

    @Nullable
    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        EnumDirection enumdirection = blockactioncontext.g();
        BlockPosition blockposition = blockactioncontext.getClickPosition();
        BlockPosition blockposition1 = blockposition.shift(enumdirection);

        return blockactioncontext.getWorld().getType(blockposition1).a(blockactioncontext) ? (IBlockData) this.getBlockData().set(BlockBed.FACING, enumdirection) : null;
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        EnumDirection enumdirection = g(iblockdata).opposite();

        switch (enumdirection) {
            case NORTH:
                return BlockBed.NORTH_SHAPE;
            case SOUTH:
                return BlockBed.SOUTH_SHAPE;
            case WEST:
                return BlockBed.WEST_SHAPE;
            default:
                return BlockBed.EAST_SHAPE;
        }
    }

    public static EnumDirection g(IBlockData iblockdata) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockBed.FACING);

        return iblockdata.get(BlockBed.PART) == BlockPropertyBedPart.HEAD ? enumdirection.opposite() : enumdirection;
    }

    public static DoubleBlockFinder.BlockType h(IBlockData iblockdata) {
        BlockPropertyBedPart blockpropertybedpart = (BlockPropertyBedPart) iblockdata.get(BlockBed.PART);

        return blockpropertybedpart == BlockPropertyBedPart.HEAD ? DoubleBlockFinder.BlockType.FIRST : DoubleBlockFinder.BlockType.SECOND;
    }

    private static boolean b(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockaccess.getType(blockposition.down()).getBlock() instanceof BlockBed;
    }

    public static Optional<Vec3D> a(EntityTypes<?> entitytypes, ICollisionAccess icollisionaccess, BlockPosition blockposition, float f) {
        EnumDirection enumdirection = (EnumDirection) icollisionaccess.getType(blockposition).get(BlockBed.FACING);
        EnumDirection enumdirection1 = enumdirection.g();
        EnumDirection enumdirection2 = enumdirection1.a(f) ? enumdirection1.opposite() : enumdirection1;

        if (b((IBlockAccess) icollisionaccess, blockposition)) {
            return a(entitytypes, icollisionaccess, blockposition, enumdirection, enumdirection2);
        } else {
            int[][] aint = a(enumdirection, enumdirection2);
            Optional<Vec3D> optional = a(entitytypes, icollisionaccess, blockposition, aint, true);

            return optional.isPresent() ? optional : a(entitytypes, icollisionaccess, blockposition, aint, false);
        }
    }

    private static Optional<Vec3D> a(EntityTypes<?> entitytypes, ICollisionAccess icollisionaccess, BlockPosition blockposition, EnumDirection enumdirection, EnumDirection enumdirection1) {
        int[][] aint = b(enumdirection, enumdirection1);
        Optional<Vec3D> optional = a(entitytypes, icollisionaccess, blockposition, aint, true);

        if (optional.isPresent()) {
            return optional;
        } else {
            BlockPosition blockposition1 = blockposition.down();
            Optional<Vec3D> optional1 = a(entitytypes, icollisionaccess, blockposition1, aint, true);

            if (optional1.isPresent()) {
                return optional1;
            } else {
                int[][] aint1 = a(enumdirection);
                Optional<Vec3D> optional2 = a(entitytypes, icollisionaccess, blockposition, aint1, true);

                if (optional2.isPresent()) {
                    return optional2;
                } else {
                    Optional<Vec3D> optional3 = a(entitytypes, icollisionaccess, blockposition, aint, false);

                    if (optional3.isPresent()) {
                        return optional3;
                    } else {
                        Optional<Vec3D> optional4 = a(entitytypes, icollisionaccess, blockposition1, aint, false);

                        return optional4.isPresent() ? optional4 : a(entitytypes, icollisionaccess, blockposition, aint1, false);
                    }
                }
            }
        }
    }

    private static Optional<Vec3D> a(EntityTypes<?> entitytypes, ICollisionAccess icollisionaccess, BlockPosition blockposition, int[][] aint, boolean flag) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        int[][] aint1 = aint;
        int i = aint.length;

        for (int j = 0; j < i; ++j) {
            int[] aint2 = aint1[j];

            blockposition_mutableblockposition.d(blockposition.getX() + aint2[0], blockposition.getY(), blockposition.getZ() + aint2[1]);
            Vec3D vec3d = DismountUtil.a(entitytypes, icollisionaccess, blockposition_mutableblockposition, flag);

            if (vec3d != null) {
                return Optional.of(vec3d);
            }
        }

        return Optional.empty();
    }

    @Override
    public EnumPistonReaction getPushReaction(IBlockData iblockdata) {
        return EnumPistonReaction.DESTROY;
    }

    @Override
    public EnumRenderType b_(IBlockData iblockdata) {
        return EnumRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockBed.FACING, BlockBed.PART, BlockBed.OCCUPIED);
    }

    @Override
    public TileEntity createTile(BlockPosition blockposition, IBlockData iblockdata) {
        return new TileEntityBed(blockposition, iblockdata, this.color);
    }

    @Override
    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, @Nullable EntityLiving entityliving, ItemStack itemstack) {
        super.postPlace(world, blockposition, iblockdata, entityliving, itemstack);
        if (!world.isClientSide) {
            BlockPosition blockposition1 = blockposition.shift((EnumDirection) iblockdata.get(BlockBed.FACING));

            world.setTypeAndData(blockposition1, (IBlockData) iblockdata.set(BlockBed.PART, BlockPropertyBedPart.HEAD), 3);
            world.update(blockposition, Blocks.AIR);
            iblockdata.a(world, blockposition, 3);
        }

    }

    public EnumColor c() {
        return this.color;
    }

    @Override
    public long a(IBlockData iblockdata, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.shift((EnumDirection) iblockdata.get(BlockBed.FACING), iblockdata.get(BlockBed.PART) == BlockPropertyBedPart.HEAD ? 0 : 1);

        return MathHelper.c(blockposition1.getX(), blockposition.getY(), blockposition1.getZ());
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }

    private static int[][] a(EnumDirection enumdirection, EnumDirection enumdirection1) {
        return (int[][]) ArrayUtils.addAll(b(enumdirection, enumdirection1), a(enumdirection));
    }

    private static int[][] b(EnumDirection enumdirection, EnumDirection enumdirection1) {
        return new int[][]{{enumdirection1.getAdjacentX(), enumdirection1.getAdjacentZ()}, {enumdirection1.getAdjacentX() - enumdirection.getAdjacentX(), enumdirection1.getAdjacentZ() - enumdirection.getAdjacentZ()}, {enumdirection1.getAdjacentX() - enumdirection.getAdjacentX() * 2, enumdirection1.getAdjacentZ() - enumdirection.getAdjacentZ() * 2}, {-enumdirection.getAdjacentX() * 2, -enumdirection.getAdjacentZ() * 2}, {-enumdirection1.getAdjacentX() - enumdirection.getAdjacentX() * 2, -enumdirection1.getAdjacentZ() - enumdirection.getAdjacentZ() * 2}, {-enumdirection1.getAdjacentX() - enumdirection.getAdjacentX(), -enumdirection1.getAdjacentZ() - enumdirection.getAdjacentZ()}, {-enumdirection1.getAdjacentX(), -enumdirection1.getAdjacentZ()}, {-enumdirection1.getAdjacentX() + enumdirection.getAdjacentX(), -enumdirection1.getAdjacentZ() + enumdirection.getAdjacentZ()}, {enumdirection.getAdjacentX(), enumdirection.getAdjacentZ()}, {enumdirection1.getAdjacentX() + enumdirection.getAdjacentX(), enumdirection1.getAdjacentZ() + enumdirection.getAdjacentZ()}};
    }

    private static int[][] a(EnumDirection enumdirection) {
        return new int[][]{{0, 0}, {-enumdirection.getAdjacentX(), -enumdirection.getAdjacentZ()}};
    }
}
