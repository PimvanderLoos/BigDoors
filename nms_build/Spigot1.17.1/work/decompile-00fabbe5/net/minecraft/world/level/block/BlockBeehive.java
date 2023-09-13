package net.minecraft.world.level.block;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.animal.EntityBee;
import net.minecraft.world.entity.boss.wither.EntityWither;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.item.EntityTNTPrimed;
import net.minecraft.world.entity.monster.EntityCreeper;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityWitherSkull;
import net.minecraft.world.entity.vehicle.EntityMinecartTNT;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityBeehive;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateDirection;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockBeehive extends BlockTileEntity {

    private static final EnumDirection[] SPAWN_DIRECTIONS = new EnumDirection[]{EnumDirection.WEST, EnumDirection.EAST, EnumDirection.SOUTH};
    public static final BlockStateDirection FACING = BlockFacingHorizontal.FACING;
    public static final BlockStateInteger HONEY_LEVEL = BlockProperties.LEVEL_HONEY;
    public static final int MAX_HONEY_LEVELS = 5;
    private static final int SHEARED_HONEYCOMB_COUNT = 3;

    public BlockBeehive(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.k((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BlockBeehive.HONEY_LEVEL, 0)).set(BlockBeehive.FACING, EnumDirection.NORTH));
    }

    @Override
    public boolean isComplexRedstone(IBlockData iblockdata) {
        return true;
    }

    @Override
    public int a(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return (Integer) iblockdata.get(BlockBeehive.HONEY_LEVEL);
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

            CriterionTriggers.BEE_NEST_DESTROYED.a((EntityPlayer) entityhuman, iblockdata, itemstack, tileentitybeehive.getBeeCount());
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
                    entitybee.setGoalTarget((EntityLiving) list1.get(world.random.nextInt(i)));
                }
            }
        }

    }

    public static void a(World world, BlockPosition blockposition) {
        a(world, blockposition, new ItemStack(Items.HONEYCOMB, 3));
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        ItemStack itemstack = entityhuman.b(enumhand);
        int i = (Integer) iblockdata.get(BlockBeehive.HONEY_LEVEL);
        boolean flag = false;

        if (i >= 5) {
            Item item = itemstack.getItem();

            if (itemstack.a(Items.SHEARS)) {
                world.playSound(entityhuman, entityhuman.locX(), entityhuman.locY(), entityhuman.locZ(), SoundEffects.BEEHIVE_SHEAR, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                a(world, blockposition);
                itemstack.damage(1, entityhuman, (entityhuman1) -> {
                    entityhuman1.broadcastItemBreak(enumhand);
                });
                flag = true;
                world.a((Entity) entityhuman, GameEvent.SHEAR, blockposition);
            } else if (itemstack.a(Items.GLASS_BOTTLE)) {
                itemstack.subtract(1);
                world.playSound(entityhuman, entityhuman.locX(), entityhuman.locY(), entityhuman.locZ(), SoundEffects.BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                if (itemstack.isEmpty()) {
                    entityhuman.a(enumhand, new ItemStack(Items.HONEY_BOTTLE));
                } else if (!entityhuman.getInventory().pickup(new ItemStack(Items.HONEY_BOTTLE))) {
                    entityhuman.drop(new ItemStack(Items.HONEY_BOTTLE), false);
                }

                flag = true;
                world.a((Entity) entityhuman, GameEvent.FLUID_PICKUP, blockposition);
            }

            if (!world.isClientSide() && flag) {
                entityhuman.b(StatisticList.ITEM_USED.b(item));
            }
        }

        if (flag) {
            if (!BlockCampfire.a(world, blockposition)) {
                if (this.c(world, blockposition)) {
                    this.b(world, blockposition);
                }

                this.a(world, iblockdata, blockposition, entityhuman, TileEntityBeehive.ReleaseStatus.EMERGENCY);
            } else {
                this.a(world, iblockdata, blockposition);
            }

            return EnumInteractionResult.a(world.isClientSide);
        } else {
            return super.interact(iblockdata, world, blockposition, entityhuman, enumhand, movingobjectpositionblock);
        }
    }

    private boolean c(World world, BlockPosition blockposition) {
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
        world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockBeehive.HONEY_LEVEL, 0), 3);
    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if ((Integer) iblockdata.get(BlockBeehive.HONEY_LEVEL) >= 5) {
            for (int i = 0; i < random.nextInt(1) + 1; ++i) {
                this.a(world, blockposition, iblockdata);
            }
        }

    }

    private void a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (iblockdata.getFluid().isEmpty() && world.random.nextFloat() >= 0.3F) {
            VoxelShape voxelshape = iblockdata.getCollisionShape(world, blockposition);
            double d0 = voxelshape.c(EnumDirection.EnumAxis.Y);

            if (d0 >= 1.0D && !iblockdata.a((Tag) TagsBlock.IMPERMEABLE)) {
                double d1 = voxelshape.b(EnumDirection.EnumAxis.Y);

                if (d1 > 0.0D) {
                    this.a(world, blockposition, voxelshape, (double) blockposition.getY() + d1 - 0.05D);
                } else {
                    BlockPosition blockposition1 = blockposition.down();
                    IBlockData iblockdata1 = world.getType(blockposition1);
                    VoxelShape voxelshape1 = iblockdata1.getCollisionShape(world, blockposition1);
                    double d2 = voxelshape1.c(EnumDirection.EnumAxis.Y);

                    if ((d2 < 1.0D || !iblockdata1.r(world, blockposition1)) && iblockdata1.getFluid().isEmpty()) {
                        this.a(world, blockposition, voxelshape, (double) blockposition.getY() - 0.05D);
                    }
                }
            }

        }
    }

    private void a(World world, BlockPosition blockposition, VoxelShape voxelshape, double d0) {
        this.a(world, (double) blockposition.getX() + voxelshape.b(EnumDirection.EnumAxis.X), (double) blockposition.getX() + voxelshape.c(EnumDirection.EnumAxis.X), (double) blockposition.getZ() + voxelshape.b(EnumDirection.EnumAxis.Z), (double) blockposition.getZ() + voxelshape.c(EnumDirection.EnumAxis.Z), d0);
    }

    private void a(World world, double d0, double d1, double d2, double d3, double d4) {
        world.addParticle(Particles.DRIPPING_HONEY, MathHelper.d(world.random.nextDouble(), d0, d1), d4, MathHelper.d(world.random.nextDouble(), d2, d3), 0.0D, 0.0D, 0.0D);
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return (IBlockData) this.getBlockData().set(BlockBeehive.FACING, blockactioncontext.g().opposite());
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockBeehive.HONEY_LEVEL, BlockBeehive.FACING);
    }

    @Override
    public EnumRenderType b_(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    @Nullable
    @Override
    public TileEntity createTile(BlockPosition blockposition, IBlockData iblockdata) {
        return new TileEntityBeehive(blockposition, iblockdata);
    }

    @Nullable
    @Override
    public <T extends TileEntity> BlockEntityTicker<T> a(World world, IBlockData iblockdata, TileEntityTypes<T> tileentitytypes) {
        return world.isClientSide ? null : a(tileentitytypes, TileEntityTypes.BEEHIVE, TileEntityBeehive::a);
    }

    @Override
    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        if (!world.isClientSide && entityhuman.isCreative() && world.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityBeehive) {
                TileEntityBeehive tileentitybeehive = (TileEntityBeehive) tileentity;
                ItemStack itemstack = new ItemStack(this);
                int i = (Integer) iblockdata.get(BlockBeehive.HONEY_LEVEL);
                boolean flag = !tileentitybeehive.isEmpty();

                if (flag || i > 0) {
                    NBTTagCompound nbttagcompound;

                    if (flag) {
                        nbttagcompound = new NBTTagCompound();
                        nbttagcompound.set("Bees", tileentitybeehive.j());
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

    public static EnumDirection a(Random random) {
        return (EnumDirection) SystemUtils.a((Object[]) BlockBeehive.SPAWN_DIRECTIONS, random);
    }
}
