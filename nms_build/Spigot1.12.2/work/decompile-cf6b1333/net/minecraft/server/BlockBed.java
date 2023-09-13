package net.minecraft.server;

import java.util.Iterator;
import java.util.Random;
import javax.annotation.Nullable;

public class BlockBed extends BlockFacingHorizontal implements ITileEntity {

    public static final BlockStateEnum<BlockBed.EnumBedPart> PART = BlockStateEnum.of("part", BlockBed.EnumBedPart.class);
    public static final BlockStateBoolean OCCUPIED = BlockStateBoolean.of("occupied");
    protected static final AxisAlignedBB c = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5625D, 1.0D);

    public BlockBed() {
        super(Material.CLOTH);
        this.w(this.blockStateList.getBlockData().set(BlockBed.PART, BlockBed.EnumBedPart.FOOT).set(BlockBed.OCCUPIED, Boolean.valueOf(false)));
        this.isTileEntity = true;
    }

    public MaterialMapColor c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        if (iblockdata.get(BlockBed.PART) == BlockBed.EnumBedPart.FOOT) {
            TileEntity tileentity = iblockaccess.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityBed) {
                EnumColor enumcolor = ((TileEntityBed) tileentity).a();

                return MaterialMapColor.a(enumcolor);
            }
        }

        return MaterialMapColor.f;
    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        if (world.isClientSide) {
            return true;
        } else {
            if (iblockdata.get(BlockBed.PART) != BlockBed.EnumBedPart.HEAD) {
                blockposition = blockposition.shift((EnumDirection) iblockdata.get(BlockBed.FACING));
                iblockdata = world.getType(blockposition);
                if (iblockdata.getBlock() != this) {
                    return true;
                }
            }

            if (world.worldProvider.e() && world.getBiome(blockposition) != Biomes.j) {
                if (((Boolean) iblockdata.get(BlockBed.OCCUPIED)).booleanValue()) {
                    EntityHuman entityhuman1 = this.c(world, blockposition);

                    if (entityhuman1 != null) {
                        entityhuman.a((IChatBaseComponent) (new ChatMessage("tile.bed.occupied", new Object[0])), true);
                        return true;
                    }

                    iblockdata = iblockdata.set(BlockBed.OCCUPIED, Boolean.valueOf(false));
                    world.setTypeAndData(blockposition, iblockdata, 4);
                }

                EntityHuman.EnumBedResult entityhuman_enumbedresult = entityhuman.a(blockposition);

                if (entityhuman_enumbedresult == EntityHuman.EnumBedResult.OK) {
                    iblockdata = iblockdata.set(BlockBed.OCCUPIED, Boolean.valueOf(true));
                    world.setTypeAndData(blockposition, iblockdata, 4);
                    return true;
                } else {
                    if (entityhuman_enumbedresult == EntityHuman.EnumBedResult.NOT_POSSIBLE_NOW) {
                        entityhuman.a((IChatBaseComponent) (new ChatMessage("tile.bed.noSleep", new Object[0])), true);
                    } else if (entityhuman_enumbedresult == EntityHuman.EnumBedResult.NOT_SAFE) {
                        entityhuman.a((IChatBaseComponent) (new ChatMessage("tile.bed.notSafe", new Object[0])), true);
                    } else if (entityhuman_enumbedresult == EntityHuman.EnumBedResult.TOO_FAR_AWAY) {
                        entityhuman.a((IChatBaseComponent) (new ChatMessage("tile.bed.tooFarAway", new Object[0])), true);
                    }

                    return true;
                }
            } else {
                world.setAir(blockposition);
                BlockPosition blockposition1 = blockposition.shift(((EnumDirection) iblockdata.get(BlockBed.FACING)).opposite());

                if (world.getType(blockposition1).getBlock() == this) {
                    world.setAir(blockposition1);
                }

                world.createExplosion((Entity) null, (double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D, 5.0F, true, true);
                return true;
            }
        }
    }

    @Nullable
    private EntityHuman c(World world, BlockPosition blockposition) {
        Iterator iterator = world.players.iterator();

        EntityHuman entityhuman;

        do {
            if (!iterator.hasNext()) {
                return null;
            }

            entityhuman = (EntityHuman) iterator.next();
        } while (!entityhuman.isSleeping() || !entityhuman.bedPosition.equals(blockposition));

        return entityhuman;
    }

    public boolean c(IBlockData iblockdata) {
        return false;
    }

    public boolean b(IBlockData iblockdata) {
        return false;
    }

    public void fallOn(World world, BlockPosition blockposition, Entity entity, float f) {
        super.fallOn(world, blockposition, entity, f * 0.5F);
    }

    public void a(World world, Entity entity) {
        if (entity.isSneaking()) {
            super.a(world, entity);
        } else if (entity.motY < 0.0D) {
            entity.motY = -entity.motY * 0.6600000262260437D;
            if (!(entity instanceof EntityLiving)) {
                entity.motY *= 0.8D;
            }
        }

    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockBed.FACING);

        if (iblockdata.get(BlockBed.PART) == BlockBed.EnumBedPart.FOOT) {
            if (world.getType(blockposition.shift(enumdirection)).getBlock() != this) {
                world.setAir(blockposition);
            }
        } else if (world.getType(blockposition.shift(enumdirection.opposite())).getBlock() != this) {
            if (!world.isClientSide) {
                this.b(world, blockposition, iblockdata, 0);
            }

            world.setAir(blockposition);
        }

    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return iblockdata.get(BlockBed.PART) == BlockBed.EnumBedPart.FOOT ? Items.a : Items.BED;
    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockBed.c;
    }

    @Nullable
    public static BlockPosition a(World world, BlockPosition blockposition, int i) {
        EnumDirection enumdirection = (EnumDirection) world.getType(blockposition).get(BlockBed.FACING);
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

                    if (b(world, blockposition1)) {
                        if (i <= 0) {
                            return blockposition1;
                        }

                        --i;
                    }
                }
            }
        }

        return null;
    }

    protected static boolean b(World world, BlockPosition blockposition) {
        return world.getType(blockposition.down()).q() && !world.getType(blockposition).getMaterial().isBuildable() && !world.getType(blockposition.up()).getMaterial().isBuildable();
    }

    public void dropNaturally(World world, BlockPosition blockposition, IBlockData iblockdata, float f, int i) {
        if (iblockdata.get(BlockBed.PART) == BlockBed.EnumBedPart.HEAD) {
            TileEntity tileentity = world.getTileEntity(blockposition);
            EnumColor enumcolor = tileentity instanceof TileEntityBed ? ((TileEntityBed) tileentity).a() : EnumColor.RED;

            a(world, blockposition, new ItemStack(Items.BED, 1, enumcolor.getColorIndex()));
        }

    }

    public EnumPistonReaction h(IBlockData iblockdata) {
        return EnumPistonReaction.DESTROY;
    }

    public EnumRenderType a(IBlockData iblockdata) {
        return EnumRenderType.ENTITYBLOCK_ANIMATED;
    }

    public ItemStack a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        BlockPosition blockposition1 = blockposition;

        if (iblockdata.get(BlockBed.PART) == BlockBed.EnumBedPart.FOOT) {
            blockposition1 = blockposition.shift((EnumDirection) iblockdata.get(BlockBed.FACING));
        }

        TileEntity tileentity = world.getTileEntity(blockposition1);
        EnumColor enumcolor = tileentity instanceof TileEntityBed ? ((TileEntityBed) tileentity).a() : EnumColor.RED;

        return new ItemStack(Items.BED, 1, enumcolor.getColorIndex());
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        if (entityhuman.abilities.canInstantlyBuild && iblockdata.get(BlockBed.PART) == BlockBed.EnumBedPart.FOOT) {
            BlockPosition blockposition1 = blockposition.shift((EnumDirection) iblockdata.get(BlockBed.FACING));

            if (world.getType(blockposition1).getBlock() == this) {
                world.setAir(blockposition1);
            }
        }

    }

    public void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, TileEntity tileentity, ItemStack itemstack) {
        if (iblockdata.get(BlockBed.PART) == BlockBed.EnumBedPart.HEAD && tileentity instanceof TileEntityBed) {
            TileEntityBed tileentitybed = (TileEntityBed) tileentity;
            ItemStack itemstack1 = tileentitybed.f();

            a(world, blockposition, itemstack1);
        } else {
            super.a(world, entityhuman, blockposition, iblockdata, (TileEntity) null, itemstack);
        }

    }

    public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
        super.remove(world, blockposition, iblockdata);
        world.s(blockposition);
    }

    public IBlockData fromLegacyData(int i) {
        EnumDirection enumdirection = EnumDirection.fromType2(i);

        return (i & 8) > 0 ? this.getBlockData().set(BlockBed.PART, BlockBed.EnumBedPart.HEAD).set(BlockBed.FACING, enumdirection).set(BlockBed.OCCUPIED, Boolean.valueOf((i & 4) > 0)) : this.getBlockData().set(BlockBed.PART, BlockBed.EnumBedPart.FOOT).set(BlockBed.FACING, enumdirection);
    }

    public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        if (iblockdata.get(BlockBed.PART) == BlockBed.EnumBedPart.FOOT) {
            IBlockData iblockdata1 = iblockaccess.getType(blockposition.shift((EnumDirection) iblockdata.get(BlockBed.FACING)));

            if (iblockdata1.getBlock() == this) {
                iblockdata = iblockdata.set(BlockBed.OCCUPIED, iblockdata1.get(BlockBed.OCCUPIED));
            }
        }

        return iblockdata;
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return iblockdata.set(BlockBed.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockBed.FACING)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockBed.FACING)));
    }

    public int toLegacyData(IBlockData iblockdata) {
        byte b0 = 0;
        int i = b0 | ((EnumDirection) iblockdata.get(BlockBed.FACING)).get2DRotationValue();

        if (iblockdata.get(BlockBed.PART) == BlockBed.EnumBedPart.HEAD) {
            i |= 8;
            if (((Boolean) iblockdata.get(BlockBed.OCCUPIED)).booleanValue()) {
                i |= 4;
            }
        }

        return i;
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockBed.FACING, BlockBed.PART, BlockBed.OCCUPIED});
    }

    public TileEntity a(World world, int i) {
        return new TileEntityBed();
    }

    public static enum EnumBedPart implements INamable {

        HEAD("head"), FOOT("foot");

        private final String c;

        private EnumBedPart(String s) {
            this.c = s;
        }

        public String toString() {
            return this.c;
        }

        public String getName() {
            return this.c;
        }
    }
}
