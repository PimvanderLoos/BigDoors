package net.minecraft.server;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class BlockBed extends BlockFacingHorizontal implements ITileEntity {

    public static final BlockStateEnum<BlockPropertyBedPart> PART = BlockProperties.aE;
    public static final BlockStateBoolean OCCUPIED = BlockProperties.t;
    protected static final VoxelShape c = Block.a(0.0D, 3.0D, 0.0D, 16.0D, 9.0D, 16.0D);
    protected static final VoxelShape d = Block.a(0.0D, 0.0D, 0.0D, 3.0D, 3.0D, 3.0D);
    protected static final VoxelShape e = Block.a(0.0D, 0.0D, 13.0D, 3.0D, 3.0D, 16.0D);
    protected static final VoxelShape f = Block.a(13.0D, 0.0D, 0.0D, 16.0D, 3.0D, 3.0D);
    protected static final VoxelShape g = Block.a(13.0D, 0.0D, 13.0D, 16.0D, 3.0D, 16.0D);
    protected static final VoxelShape h = VoxelShapes.a(BlockBed.c, BlockBed.d, BlockBed.f);
    protected static final VoxelShape i = VoxelShapes.a(BlockBed.c, BlockBed.e, BlockBed.g);
    protected static final VoxelShape j = VoxelShapes.a(BlockBed.c, BlockBed.d, BlockBed.e);
    protected static final VoxelShape k = VoxelShapes.a(BlockBed.c, BlockBed.f, BlockBed.g);
    private final EnumColor color;

    public BlockBed(EnumColor enumcolor, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.color = enumcolor;
        this.j((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockBed.PART, BlockPropertyBedPart.FOOT)).set(BlockBed.OCCUPIED, false));
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
    public void fallOn(World world, BlockPosition blockposition, Entity entity, float f) {
        super.fallOn(world, blockposition, entity, f * 0.5F);
    }

    @Override
    public void a(IBlockAccess iblockaccess, Entity entity) {
        if (entity.bs()) {
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
        return enumdirection == a((BlockPropertyBedPart) iblockdata.get(BlockBed.PART), (EnumDirection) iblockdata.get(BlockBed.FACING)) ? (iblockdata1.a((Block) this) && iblockdata1.get(BlockBed.PART) != iblockdata.get(BlockBed.PART) ? (IBlockData) iblockdata.set(BlockBed.OCCUPIED, iblockdata1.get(BlockBed.OCCUPIED)) : Blocks.AIR.getBlockData()) : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
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

                if (iblockdata1.getBlock() == this && iblockdata1.get(BlockBed.PART) == BlockPropertyBedPart.HEAD) {
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
        EnumDirection enumdirection = blockactioncontext.f();
        BlockPosition blockposition = blockactioncontext.getClickPosition();
        BlockPosition blockposition1 = blockposition.shift(enumdirection);

        return blockactioncontext.getWorld().getType(blockposition1).a(blockactioncontext) ? (IBlockData) this.getBlockData().set(BlockBed.FACING, enumdirection) : null;
    }

    @Override
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        EnumDirection enumdirection = g(iblockdata).opposite();

        switch (enumdirection) {
            case NORTH:
                return BlockBed.h;
            case SOUTH:
                return BlockBed.i;
            case WEST:
                return BlockBed.j;
            default:
                return BlockBed.k;
        }
    }

    public static EnumDirection g(IBlockData iblockdata) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockBed.FACING);

        return iblockdata.get(BlockBed.PART) == BlockPropertyBedPart.HEAD ? enumdirection.opposite() : enumdirection;
    }

    public static Optional<Vec3D> a(EntityTypes<?> entitytypes, IWorldReader iworldreader, BlockPosition blockposition, int i) {
        EnumDirection enumdirection = (EnumDirection) iworldreader.getType(blockposition).get(BlockBed.FACING);
        int j = blockposition.getX();
        int k = blockposition.getY();
        int l = blockposition.getZ();

        for (int i1 = 0; i1 <= 1; ++i1) {
            int j1 = j - enumdirection.getAdjacentX() * i1 - 1;
            int k1 = l - enumdirection.getAdjacentZ() * i1 - 1;
            int l1 = j1 + 2;
            int i2 = k1 + 2;

            for (int j2 = j1; j2 <= l1; ++j2) {
                for (int k2 = k1; k2 <= i2; ++k2) {
                    BlockPosition blockposition1 = new BlockPosition(j2, k, k2);
                    Optional<Vec3D> optional = a(entitytypes, iworldreader, blockposition1);

                    if (optional.isPresent()) {
                        if (i <= 0) {
                            return optional;
                        }

                        --i;
                    }
                }
            }
        }

        return Optional.empty();
    }

    public static Optional<Vec3D> a(EntityTypes<?> entitytypes, IWorldReader iworldreader, BlockPosition blockposition) {
        VoxelShape voxelshape = iworldreader.getType(blockposition).getCollisionShape(iworldreader, blockposition);

        if (voxelshape.c(EnumDirection.EnumAxis.Y) > 0.4375D) {
            return Optional.empty();
        } else {
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.i();

            while (blockposition_mutableblockposition.getY() >= 0 && blockposition.getY() - blockposition_mutableblockposition.getY() <= 2 && iworldreader.getType(blockposition_mutableblockposition).getCollisionShape(iworldreader, blockposition_mutableblockposition).isEmpty()) {
                blockposition_mutableblockposition.c(EnumDirection.DOWN);
            }

            VoxelShape voxelshape1 = iworldreader.getType(blockposition_mutableblockposition).getCollisionShape(iworldreader, blockposition_mutableblockposition);

            if (voxelshape1.isEmpty()) {
                return Optional.empty();
            } else {
                double d0 = (double) blockposition_mutableblockposition.getY() + voxelshape1.c(EnumDirection.EnumAxis.Y) + 2.0E-7D;

                if ((double) blockposition.getY() - d0 > 2.0D) {
                    return Optional.empty();
                } else {
                    Vec3D vec3d = new Vec3D((double) blockposition_mutableblockposition.getX() + 0.5D, d0, (double) blockposition_mutableblockposition.getZ() + 0.5D);
                    AxisAlignedBB axisalignedbb = entitytypes.a(vec3d.x, vec3d.y, vec3d.z);

                    if (iworldreader.b(axisalignedbb)) {
                        Stream stream = iworldreader.a(axisalignedbb.b(0.0D, -0.20000000298023224D, 0.0D));

                        entitytypes.getClass();
                        if (stream.noneMatch(entitytypes::a)) {
                            return Optional.of(vec3d);
                        }
                    }

                    return Optional.empty();
                }
            }
        }
    }

    @Override
    public EnumPistonReaction getPushReaction(IBlockData iblockdata) {
        return EnumPistonReaction.DESTROY;
    }

    @Override
    public EnumRenderType b(IBlockData iblockdata) {
        return EnumRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockBed.FACING, BlockBed.PART, BlockBed.OCCUPIED);
    }

    @Override
    public TileEntity createTile(IBlockAccess iblockaccess) {
        return new TileEntityBed(this.color);
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

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
