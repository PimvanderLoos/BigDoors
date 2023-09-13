package net.minecraft.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlockAnvil extends BlockFalling {

    public static final BlockStateDirection FACING = BlockFacingHorizontal.FACING;
    public static final BlockStateInteger DAMAGE = BlockStateInteger.of("damage", 0, 2);
    protected static final AxisAlignedBB c = new AxisAlignedBB(0.0D, 0.0D, 0.125D, 1.0D, 1.0D, 0.875D);
    protected static final AxisAlignedBB d = new AxisAlignedBB(0.125D, 0.0D, 0.0D, 0.875D, 1.0D, 1.0D);
    protected static final Logger e = LogManager.getLogger();

    protected BlockAnvil() {
        super(Material.HEAVY);
        this.w(this.blockStateList.getBlockData().set(BlockAnvil.FACING, EnumDirection.NORTH).set(BlockAnvil.DAMAGE, Integer.valueOf(0)));
        this.e(0);
        this.a(CreativeModeTab.c);
    }

    public boolean c(IBlockData iblockdata) {
        return false;
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }

    public boolean b(IBlockData iblockdata) {
        return false;
    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        EnumDirection enumdirection1 = entityliving.getDirection().e();

        try {
            return super.getPlacedState(world, blockposition, enumdirection, f, f1, f2, i, entityliving).set(BlockAnvil.FACING, enumdirection1).set(BlockAnvil.DAMAGE, Integer.valueOf(i >> 2));
        } catch (IllegalArgumentException illegalargumentexception) {
            if (!world.isClientSide) {
                BlockAnvil.e.warn(String.format("Invalid damage property for anvil at %s. Found %d, must be in [0, 1, 2]", new Object[] { blockposition, Integer.valueOf(i >> 2)}));
                if (entityliving instanceof EntityHuman) {
                    entityliving.sendMessage(new ChatMessage("Invalid damage property. Please pick in [0, 1, 2]", new Object[0]));
                }
            }

            return super.getPlacedState(world, blockposition, enumdirection, f, f1, f2, 0, entityliving).set(BlockAnvil.FACING, enumdirection1).set(BlockAnvil.DAMAGE, Integer.valueOf(0));
        }
    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        if (!world.isClientSide) {
            entityhuman.openTileEntity(new BlockAnvil.TileEntityContainerAnvil(world, blockposition));
        }

        return true;
    }

    public int getDropData(IBlockData iblockdata) {
        return ((Integer) iblockdata.get(BlockAnvil.DAMAGE)).intValue();
    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockAnvil.FACING);

        return enumdirection.k() == EnumDirection.EnumAxis.X ? BlockAnvil.c : BlockAnvil.d;
    }

    public void a(CreativeModeTab creativemodetab, NonNullList<ItemStack> nonnulllist) {
        nonnulllist.add(new ItemStack(this));
        nonnulllist.add(new ItemStack(this, 1, 1));
        nonnulllist.add(new ItemStack(this, 1, 2));
    }

    protected void a(EntityFallingBlock entityfallingblock) {
        entityfallingblock.a(true);
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1) {
        world.triggerEffect(1031, blockposition, 0);
    }

    public void a_(World world, BlockPosition blockposition) {
        world.triggerEffect(1029, blockposition, 0);
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockAnvil.FACING, EnumDirection.fromType2(i & 3)).set(BlockAnvil.DAMAGE, Integer.valueOf((i & 15) >> 2));
    }

    public int toLegacyData(IBlockData iblockdata) {
        byte b0 = 0;
        int i = b0 | ((EnumDirection) iblockdata.get(BlockAnvil.FACING)).get2DRotationValue();

        i |= ((Integer) iblockdata.get(BlockAnvil.DAMAGE)).intValue() << 2;
        return i;
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return iblockdata.getBlock() != this ? iblockdata : iblockdata.set(BlockAnvil.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockAnvil.FACING)));
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockAnvil.FACING, BlockAnvil.DAMAGE});
    }

    public static class TileEntityContainerAnvil implements ITileEntityContainer {

        private final World a;
        private final BlockPosition b;

        public TileEntityContainerAnvil(World world, BlockPosition blockposition) {
            this.a = world;
            this.b = blockposition;
        }

        public String getName() {
            return "anvil";
        }

        public boolean hasCustomName() {
            return false;
        }

        public IChatBaseComponent getScoreboardDisplayName() {
            return new ChatMessage(Blocks.ANVIL.a() + ".name", new Object[0]);
        }

        public Container createContainer(PlayerInventory playerinventory, EntityHuman entityhuman) {
            return new ContainerAnvil(playerinventory, this.a, this.b, entityhuman);
        }

        public String getContainerName() {
            return "minecraft:anvil";
        }
    }
}
