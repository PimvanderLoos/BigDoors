package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class BlockBeehive extends BlockTileEntity {

    public static final EnumDirection[] a = new EnumDirection[]{EnumDirection.WEST, EnumDirection.EAST, EnumDirection.SOUTH};
    public static final BlockStateDirection b = BlockFacingHorizontal.FACING;
    public static final BlockStateInteger c = BlockProperties.ao;

    public BlockBeehive(Block.Info block_info) {
        super(block_info);
        this.p((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockBeehive.c, 0)).set(BlockBeehive.b, EnumDirection.NORTH));
    }

    @Override
    public boolean isComplexRedstone(IBlockData iblockdata) {
        return true;
    }

    @Override
    public int a(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return (Integer) iblockdata.get(BlockBeehive.c);
    }

    @Override
    public void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, @Nullable TileEntity tileentity, ItemStack itemstack) {
        super.a(world, entityhuman, blockposition, iblockdata, tileentity, itemstack);
        if (!world.isClientSide && tileentity instanceof TileEntityBeehive) {
            TileEntityBeehive tileentitybeehive = (TileEntityBeehive) tileentity;

            if (EnchantmentManager.getEnchantmentLevel(Enchantments.SILK_TOUCH, itemstack) == 0) {
                tileentitybeehive.a(entityhuman, iblockdata, TileEntityBeehive.ReleaseStatus.EMERGENCY);
                world.updateAdjacentComparators(blockposition, this);
                this.b(world, blockposition);
            }

            CriterionTriggers.L.a((EntityPlayer) entityhuman, iblockdata.getBlock(), itemstack, tileentitybeehive.j());
        }

    }

    private void b(World world, BlockPosition blockposition) {
        List<EntityBee> list = world.a(EntityBee.class, (new AxisAlignedBB(blockposition)).grow(8.0D, 6.0D, 8.0D));

        if (!list.isEmpty()) {
            List<EntityHuman> list1 = world.a(EntityHuman.class, (new AxisAlignedBB(blockposition)).grow(8.0D, 6.0D, 8.0D));
            int i = list1.size();
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityBee entitybee = (EntityBee) iterator.next();

                if (entitybee.getGoalTarget() == null) {
                    entitybee.a((Entity) list1.get(world.random.nextInt(i)));
                }
            }
        }

    }

    public static void a(World world, BlockPosition blockposition) {
        a(world, blockposition, new ItemStack(Items.pU, 3));
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        ItemStack itemstack = entityhuman.b(enumhand);
        ItemStack itemstack1 = itemstack.cloneItemStack();
        int i = (Integer) iblockdata.get(BlockBeehive.c);
        boolean flag = false;

        if (i >= 5) {
            if (itemstack.getItem() == Items.SHEARS) {
                world.playSound(entityhuman, entityhuman.locX(), entityhuman.locY(), entityhuman.locZ(), SoundEffects.BLOCK_BEEHIVE_SHEAR, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                a(world, blockposition);
                itemstack.damage(1, entityhuman, (entityhuman1) -> {
                    entityhuman1.broadcastItemBreak(enumhand);
                });
                flag = true;
            } else if (itemstack.getItem() == Items.GLASS_BOTTLE) {
                itemstack.subtract(1);
                world.playSound(entityhuman, entityhuman.locX(), entityhuman.locY(), entityhuman.locZ(), SoundEffects.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                if (itemstack.isEmpty()) {
                    entityhuman.a(enumhand, new ItemStack(Items.pX));
                } else if (!entityhuman.inventory.pickup(new ItemStack(Items.pX))) {
                    entityhuman.drop(new ItemStack(Items.pX), false);
                }

                flag = true;
            }
        }

        if (flag) {
            if (!BlockCampfire.b(world, blockposition, 5)) {
                if (this.d(world, blockposition)) {
                    this.b(world, blockposition);
                }

                this.a(world, iblockdata, blockposition, entityhuman, TileEntityBeehive.ReleaseStatus.EMERGENCY);
            } else {
                this.a(world, iblockdata, blockposition);
                if (entityhuman instanceof EntityPlayer) {
                    CriterionTriggers.J.a((EntityPlayer) entityhuman, blockposition, itemstack1);
                }
            }

            return EnumInteractionResult.SUCCESS;
        } else {
            return super.interact(iblockdata, world, blockposition, entityhuman, enumhand, movingobjectpositionblock);
        }
    }

    private boolean d(World world, BlockPosition blockposition) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (tileentity instanceof TileEntityBeehive) {
            TileEntityBeehive tileentitybeehive = (TileEntityBeehive) tileentity;

            return !tileentitybeehive.isEmpty();
        } else {
            return false;
        }
    }

    public void a(World world, IBlockData iblockdata, BlockPosition blockposition, @Nullable EntityHuman entityhuman, TileEntityBeehive.ReleaseStatus tileentitybeehive_releasestatus) {
        this.a(world, iblockdata, blockposition);
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (tileentity instanceof TileEntityBeehive) {
            TileEntityBeehive tileentitybeehive = (TileEntityBeehive) tileentity;

            tileentitybeehive.a(entityhuman, iblockdata, tileentitybeehive_releasestatus);
        }

    }

    public void a(World world, IBlockData iblockdata, BlockPosition blockposition) {
        world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockBeehive.c, 0), 3);
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return (IBlockData) this.getBlockData().set(BlockBeehive.b, blockactioncontext.f().opposite());
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockBeehive.c, BlockBeehive.b);
    }

    @Override
    public EnumRenderType c(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    @Nullable
    @Override
    public TileEntity createTile(IBlockAccess iblockaccess) {
        return new TileEntityBeehive();
    }

    @Override
    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        if (!world.isClientSide && entityhuman.isCreative() && world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityBeehive) {
                TileEntityBeehive tileentitybeehive = (TileEntityBeehive) tileentity;
                ItemStack itemstack = new ItemStack(this);
                int i = (Integer) iblockdata.get(BlockBeehive.c);
                boolean flag = !tileentitybeehive.isEmpty();

                if (!flag && i == 0) {
                    return;
                }

                NBTTagCompound nbttagcompound;

                if (flag) {
                    nbttagcompound = new NBTTagCompound();
                    nbttagcompound.set("Bees", tileentitybeehive.m());
                    itemstack.a("BlockEntityTag", (NBTBase) nbttagcompound);
                }

                nbttagcompound = new NBTTagCompound();
                nbttagcompound.setInt("honey_level", i);
                itemstack.a("BlockStateTag", (NBTBase) nbttagcompound);
                EntityItem entityitem = new EntityItem(world, (double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), itemstack);

                entityitem.defaultPickupDelay();
                world.addEntity(entityitem);
            }
        }

        super.a(world, blockposition, iblockdata, entityhuman);
    }

    @Override
    public List<ItemStack> a(IBlockData iblockdata, LootTableInfo.Builder loottableinfo_builder) {
        Entity entity = (Entity) loottableinfo_builder.b(LootContextParameters.THIS_ENTITY);

        if (entity instanceof EntityTNTPrimed || entity instanceof EntityCreeper || entity instanceof EntityWitherSkull || entity instanceof EntityWither || entity instanceof EntityMinecartTNT) {
            TileEntity tileentity = (TileEntity) loottableinfo_builder.b(LootContextParameters.BLOCK_ENTITY);

            if (tileentity instanceof TileEntityBeehive) {
                TileEntityBeehive tileentitybeehive = (TileEntityBeehive) tileentity;

                tileentitybeehive.a((EntityHuman) null, iblockdata, TileEntityBeehive.ReleaseStatus.EMERGENCY);
            }
        }

        return super.a(iblockdata, loottableinfo_builder);
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (generatoraccess.getType(blockposition1).getBlock() instanceof BlockFire) {
            TileEntity tileentity = generatoraccess.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityBeehive) {
                TileEntityBeehive tileentitybeehive = (TileEntityBeehive) tileentity;

                tileentitybeehive.a((EntityHuman) null, iblockdata, TileEntityBeehive.ReleaseStatus.EMERGENCY);
            }
        }

        return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }
}
