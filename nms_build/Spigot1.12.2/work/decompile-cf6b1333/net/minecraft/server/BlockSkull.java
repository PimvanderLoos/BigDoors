package net.minecraft.server;

import com.google.common.base.Predicate;
import java.util.Iterator;
import java.util.Random;
import javax.annotation.Nullable;

public class BlockSkull extends BlockTileEntity {

    public static final BlockStateDirection FACING = BlockDirectional.FACING;
    public static final BlockStateBoolean NODROP = BlockStateBoolean.of("nodrop");
    private static final Predicate<ShapeDetectorBlock> B = new Predicate() {
        public boolean a(@Nullable ShapeDetectorBlock shapedetectorblock) {
            return shapedetectorblock.a() != null && shapedetectorblock.a().getBlock() == Blocks.SKULL && shapedetectorblock.b() instanceof TileEntitySkull && ((TileEntitySkull) shapedetectorblock.b()).getSkullType() == 1;
        }

        public boolean apply(@Nullable Object object) {
            return this.a((ShapeDetectorBlock) object);
        }
    };
    protected static final AxisAlignedBB c = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 0.5D, 0.75D);
    protected static final AxisAlignedBB d = new AxisAlignedBB(0.25D, 0.25D, 0.5D, 0.75D, 0.75D, 1.0D);
    protected static final AxisAlignedBB e = new AxisAlignedBB(0.25D, 0.25D, 0.0D, 0.75D, 0.75D, 0.5D);
    protected static final AxisAlignedBB f = new AxisAlignedBB(0.5D, 0.25D, 0.25D, 1.0D, 0.75D, 0.75D);
    protected static final AxisAlignedBB g = new AxisAlignedBB(0.0D, 0.25D, 0.25D, 0.5D, 0.75D, 0.75D);
    private ShapeDetector C;
    private ShapeDetector D;

    protected BlockSkull() {
        super(Material.ORIENTABLE);
        this.w(this.blockStateList.getBlockData().set(BlockSkull.FACING, EnumDirection.NORTH).set(BlockSkull.NODROP, Boolean.valueOf(false)));
    }

    public String getName() {
        return LocaleI18n.get("tile.skull.skeleton.name");
    }

    public boolean b(IBlockData iblockdata) {
        return false;
    }

    public boolean c(IBlockData iblockdata) {
        return false;
    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        switch ((EnumDirection) iblockdata.get(BlockSkull.FACING)) {
        case UP:
        default:
            return BlockSkull.c;

        case NORTH:
            return BlockSkull.d;

        case SOUTH:
            return BlockSkull.e;

        case WEST:
            return BlockSkull.f;

        case EAST:
            return BlockSkull.g;
        }
    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        return this.getBlockData().set(BlockSkull.FACING, entityliving.getDirection()).set(BlockSkull.NODROP, Boolean.valueOf(false));
    }

    public TileEntity a(World world, int i) {
        return new TileEntitySkull();
    }

    public ItemStack a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        int i = 0;
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (tileentity instanceof TileEntitySkull) {
            i = ((TileEntitySkull) tileentity).getSkullType();
        }

        return new ItemStack(Items.SKULL, 1, i);
    }

    public void dropNaturally(World world, BlockPosition blockposition, IBlockData iblockdata, float f, int i) {}

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        if (entityhuman.abilities.canInstantlyBuild) {
            iblockdata = iblockdata.set(BlockSkull.NODROP, Boolean.valueOf(true));
            world.setTypeAndData(blockposition, iblockdata, 4);
        }

        super.a(world, blockposition, iblockdata, entityhuman);
    }

    public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (!world.isClientSide) {
            if (!((Boolean) iblockdata.get(BlockSkull.NODROP)).booleanValue()) {
                TileEntity tileentity = world.getTileEntity(blockposition);

                if (tileentity instanceof TileEntitySkull) {
                    TileEntitySkull tileentityskull = (TileEntitySkull) tileentity;
                    ItemStack itemstack = this.a(world, blockposition, iblockdata);

                    if (tileentityskull.getSkullType() == 3 && tileentityskull.getGameProfile() != null) {
                        itemstack.setTag(new NBTTagCompound());
                        NBTTagCompound nbttagcompound = new NBTTagCompound();

                        GameProfileSerializer.serialize(nbttagcompound, tileentityskull.getGameProfile());
                        itemstack.getTag().set("SkullOwner", nbttagcompound);
                    }

                    a(world, blockposition, itemstack);
                }
            }

            super.remove(world, blockposition, iblockdata);
        }
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Items.SKULL;
    }

    public boolean b(World world, BlockPosition blockposition, ItemStack itemstack) {
        return itemstack.getData() == 1 && blockposition.getY() >= 2 && world.getDifficulty() != EnumDifficulty.PEACEFUL && !world.isClientSide ? this.e().a(world, blockposition) != null : false;
    }

    public void a(World world, BlockPosition blockposition, TileEntitySkull tileentityskull) {
        if (tileentityskull.getSkullType() == 1 && blockposition.getY() >= 2 && world.getDifficulty() != EnumDifficulty.PEACEFUL && !world.isClientSide) {
            ShapeDetector shapedetector = this.g();
            ShapeDetector.ShapeDetectorCollection shapedetector_shapedetectorcollection = shapedetector.a(world, blockposition);

            if (shapedetector_shapedetectorcollection != null) {
                int i;

                for (i = 0; i < 3; ++i) {
                    ShapeDetectorBlock shapedetectorblock = shapedetector_shapedetectorcollection.a(i, 0, 0);

                    world.setTypeAndData(shapedetectorblock.getPosition(), shapedetectorblock.a().set(BlockSkull.NODROP, Boolean.valueOf(true)), 2);
                }

                for (i = 0; i < shapedetector.c(); ++i) {
                    for (int j = 0; j < shapedetector.b(); ++j) {
                        ShapeDetectorBlock shapedetectorblock1 = shapedetector_shapedetectorcollection.a(i, j, 0);

                        world.setTypeAndData(shapedetectorblock1.getPosition(), Blocks.AIR.getBlockData(), 2);
                    }
                }

                BlockPosition blockposition1 = shapedetector_shapedetectorcollection.a(1, 0, 0).getPosition();
                EntityWither entitywither = new EntityWither(world);
                BlockPosition blockposition2 = shapedetector_shapedetectorcollection.a(1, 2, 0).getPosition();

                entitywither.setPositionRotation((double) blockposition2.getX() + 0.5D, (double) blockposition2.getY() + 0.55D, (double) blockposition2.getZ() + 0.5D, shapedetector_shapedetectorcollection.getFacing().k() == EnumDirection.EnumAxis.X ? 0.0F : 90.0F, 0.0F);
                entitywither.aN = shapedetector_shapedetectorcollection.getFacing().k() == EnumDirection.EnumAxis.X ? 0.0F : 90.0F;
                entitywither.p();
                Iterator iterator = world.a(EntityPlayer.class, entitywither.getBoundingBox().g(50.0D)).iterator();

                while (iterator.hasNext()) {
                    EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                    CriterionTriggers.m.a(entityplayer, (Entity) entitywither);
                }

                world.addEntity(entitywither);

                int k;

                for (k = 0; k < 120; ++k) {
                    world.addParticle(EnumParticle.SNOWBALL, (double) blockposition1.getX() + world.random.nextDouble(), (double) (blockposition1.getY() - 2) + world.random.nextDouble() * 3.9D, (double) blockposition1.getZ() + world.random.nextDouble(), 0.0D, 0.0D, 0.0D, new int[0]);
                }

                for (k = 0; k < shapedetector.c(); ++k) {
                    for (int l = 0; l < shapedetector.b(); ++l) {
                        ShapeDetectorBlock shapedetectorblock2 = shapedetector_shapedetectorcollection.a(k, l, 0);

                        world.update(shapedetectorblock2.getPosition(), Blocks.AIR, false);
                    }
                }

            }
        }
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockSkull.FACING, EnumDirection.fromType1(i & 7)).set(BlockSkull.NODROP, Boolean.valueOf((i & 8) > 0));
    }

    public int toLegacyData(IBlockData iblockdata) {
        byte b0 = 0;
        int i = b0 | ((EnumDirection) iblockdata.get(BlockSkull.FACING)).a();

        if (((Boolean) iblockdata.get(BlockSkull.NODROP)).booleanValue()) {
            i |= 8;
        }

        return i;
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return iblockdata.set(BlockSkull.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockSkull.FACING)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockSkull.FACING)));
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockSkull.FACING, BlockSkull.NODROP});
    }

    protected ShapeDetector e() {
        if (this.C == null) {
            this.C = ShapeDetectorBuilder.a().a(new String[] { "   ", "###", "~#~"}).a('#', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.SOUL_SAND))).a('~', ShapeDetectorBlock.a(MaterialPredicate.a(Material.AIR))).b();
        }

        return this.C;
    }

    protected ShapeDetector g() {
        if (this.D == null) {
            this.D = ShapeDetectorBuilder.a().a(new String[] { "^^^", "###", "~#~"}).a('#', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.SOUL_SAND))).a('^', BlockSkull.B).a('~', ShapeDetectorBlock.a(MaterialPredicate.a(Material.AIR))).b();
        }

        return this.D;
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }
}
