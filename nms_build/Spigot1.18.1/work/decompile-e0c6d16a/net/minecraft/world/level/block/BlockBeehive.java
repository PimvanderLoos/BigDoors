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
import net.minecraft.world.item.ItemBlock;
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
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockBeehive.HONEY_LEVEL, 0)).setValue(BlockBeehive.FACING, EnumDirection.NORTH));
    }

    @Override
    public boolean hasAnalogOutputSignal(IBlockData iblockdata) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return (Integer) iblockdata.getValue(BlockBeehive.HONEY_LEVEL);
    }

    @Override
    public void playerDestroy(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, @Nullable TileEntity tileentity, ItemStack itemstack) {
        super.playerDestroy(world, entityhuman, blockposition, iblockdata, tileentity, itemstack);
        if (!world.isClientSide && tileentity instanceof TileEntityBeehive) {
            TileEntityBeehive tileentitybeehive = (TileEntityBeehive) tileentity;

            if (EnchantmentManager.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, itemstack) == 0) {
                tileentitybeehive.emptyAllLivingFromHive(entityhuman, iblockdata, TileEntityBeehive.ReleaseStatus.EMERGENCY);
                world.updateNeighbourForOutputSignal(blockposition, this);
                this.angerNearbyBees(world, blockposition);
            }

            CriterionTriggers.BEE_NEST_DESTROYED.trigger((EntityPlayer) entityhuman, iblockdata, itemstack, tileentitybeehive.getOccupantCount());
        }

    }

    private void angerNearbyBees(World world, BlockPosition blockposition) {
        List<EntityBee> list = world.getEntitiesOfClass(EntityBee.class, (new AxisAlignedBB(blockposition)).inflate(8.0D, 6.0D, 8.0D));

        if (!list.isEmpty()) {
            List<EntityHuman> list1 = world.getEntitiesOfClass(EntityHuman.class, (new AxisAlignedBB(blockposition)).inflate(8.0D, 6.0D, 8.0D));
            int i = list1.size();
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityBee entitybee = (EntityBee) iterator.next();

                if (entitybee.getTarget() == null) {
                    entitybee.setTarget((EntityLiving) list1.get(world.random.nextInt(i)));
                }
            }
        }

    }

    public static void dropHoneycomb(World world, BlockPosition blockposition) {
        popResource(world, blockposition, new ItemStack(Items.HONEYCOMB, 3));
    }

    @Override
    public EnumInteractionResult use(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);
        int i = (Integer) iblockdata.getValue(BlockBeehive.HONEY_LEVEL);
        boolean flag = false;

        if (i >= 5) {
            Item item = itemstack.getItem();

            if (itemstack.is(Items.SHEARS)) {
                world.playSound(entityhuman, entityhuman.getX(), entityhuman.getY(), entityhuman.getZ(), SoundEffects.BEEHIVE_SHEAR, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                dropHoneycomb(world, blockposition);
                itemstack.hurtAndBreak(1, entityhuman, (entityhuman1) -> {
                    entityhuman1.broadcastBreakEvent(enumhand);
                });
                flag = true;
                world.gameEvent(entityhuman, GameEvent.SHEAR, blockposition);
            } else if (itemstack.is(Items.GLASS_BOTTLE)) {
                itemstack.shrink(1);
                world.playSound(entityhuman, entityhuman.getX(), entityhuman.getY(), entityhuman.getZ(), SoundEffects.BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                if (itemstack.isEmpty()) {
                    entityhuman.setItemInHand(enumhand, new ItemStack(Items.HONEY_BOTTLE));
                } else if (!entityhuman.getInventory().add(new ItemStack(Items.HONEY_BOTTLE))) {
                    entityhuman.drop(new ItemStack(Items.HONEY_BOTTLE), false);
                }

                flag = true;
                world.gameEvent(entityhuman, GameEvent.FLUID_PICKUP, blockposition);
            }

            if (!world.isClientSide() && flag) {
                entityhuman.awardStat(StatisticList.ITEM_USED.get(item));
            }
        }

        if (flag) {
            if (!BlockCampfire.isSmokeyPos(world, blockposition)) {
                if (this.hiveContainsBees(world, blockposition)) {
                    this.angerNearbyBees(world, blockposition);
                }

                this.releaseBeesAndResetHoneyLevel(world, iblockdata, blockposition, entityhuman, TileEntityBeehive.ReleaseStatus.EMERGENCY);
            } else {
                this.resetHoneyLevel(world, iblockdata, blockposition);
            }

            return EnumInteractionResult.sidedSuccess(world.isClientSide);
        } else {
            return super.use(iblockdata, world, blockposition, entityhuman, enumhand, movingobjectpositionblock);
        }
    }

    private boolean hiveContainsBees(World world, BlockPosition blockposition) {
        TileEntity tileentity = world.getBlockEntity(blockposition);

        if (tileentity instanceof TileEntityBeehive) {
            TileEntityBeehive tileentitybeehive = (TileEntityBeehive) tileentity;

            return !tileentitybeehive.isEmpty();
        } else {
            return false;
        }
    }

    public void releaseBeesAndResetHoneyLevel(World world, IBlockData iblockdata, BlockPosition blockposition, @Nullable EntityHuman entityhuman, TileEntityBeehive.ReleaseStatus tileentitybeehive_releasestatus) {
        this.resetHoneyLevel(world, iblockdata, blockposition);
        TileEntity tileentity = world.getBlockEntity(blockposition);

        if (tileentity instanceof TileEntityBeehive) {
            TileEntityBeehive tileentitybeehive = (TileEntityBeehive) tileentity;

            tileentitybeehive.emptyAllLivingFromHive(entityhuman, iblockdata, tileentitybeehive_releasestatus);
        }

    }

    public void resetHoneyLevel(World world, IBlockData iblockdata, BlockPosition blockposition) {
        world.setBlock(blockposition, (IBlockData) iblockdata.setValue(BlockBeehive.HONEY_LEVEL, 0), 3);
    }

    @Override
    public void animateTick(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if ((Integer) iblockdata.getValue(BlockBeehive.HONEY_LEVEL) >= 5) {
            for (int i = 0; i < random.nextInt(1) + 1; ++i) {
                this.trySpawnDripParticles(world, blockposition, iblockdata);
            }
        }

    }

    private void trySpawnDripParticles(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (iblockdata.getFluidState().isEmpty() && world.random.nextFloat() >= 0.3F) {
            VoxelShape voxelshape = iblockdata.getCollisionShape(world, blockposition);
            double d0 = voxelshape.max(EnumDirection.EnumAxis.Y);

            if (d0 >= 1.0D && !iblockdata.is((Tag) TagsBlock.IMPERMEABLE)) {
                double d1 = voxelshape.min(EnumDirection.EnumAxis.Y);

                if (d1 > 0.0D) {
                    this.spawnParticle(world, blockposition, voxelshape, (double) blockposition.getY() + d1 - 0.05D);
                } else {
                    BlockPosition blockposition1 = blockposition.below();
                    IBlockData iblockdata1 = world.getBlockState(blockposition1);
                    VoxelShape voxelshape1 = iblockdata1.getCollisionShape(world, blockposition1);
                    double d2 = voxelshape1.max(EnumDirection.EnumAxis.Y);

                    if ((d2 < 1.0D || !iblockdata1.isCollisionShapeFullBlock(world, blockposition1)) && iblockdata1.getFluidState().isEmpty()) {
                        this.spawnParticle(world, blockposition, voxelshape, (double) blockposition.getY() - 0.05D);
                    }
                }
            }

        }
    }

    private void spawnParticle(World world, BlockPosition blockposition, VoxelShape voxelshape, double d0) {
        this.spawnFluidParticle(world, (double) blockposition.getX() + voxelshape.min(EnumDirection.EnumAxis.X), (double) blockposition.getX() + voxelshape.max(EnumDirection.EnumAxis.X), (double) blockposition.getZ() + voxelshape.min(EnumDirection.EnumAxis.Z), (double) blockposition.getZ() + voxelshape.max(EnumDirection.EnumAxis.Z), d0);
    }

    private void spawnFluidParticle(World world, double d0, double d1, double d2, double d3, double d4) {
        world.addParticle(Particles.DRIPPING_HONEY, MathHelper.lerp(world.random.nextDouble(), d0, d1), d4, MathHelper.lerp(world.random.nextDouble(), d2, d3), 0.0D, 0.0D, 0.0D);
    }

    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        return (IBlockData) this.defaultBlockState().setValue(BlockBeehive.FACING, blockactioncontext.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockBeehive.HONEY_LEVEL, BlockBeehive.FACING);
    }

    @Override
    public EnumRenderType getRenderShape(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(BlockPosition blockposition, IBlockData iblockdata) {
        return new TileEntityBeehive(blockposition, iblockdata);
    }

    @Nullable
    @Override
    public <T extends TileEntity> BlockEntityTicker<T> getTicker(World world, IBlockData iblockdata, TileEntityTypes<T> tileentitytypes) {
        return world.isClientSide ? null : createTickerHelper(tileentitytypes, TileEntityTypes.BEEHIVE, TileEntityBeehive::serverTick);
    }

    @Override
    public void playerWillDestroy(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        if (!world.isClientSide && entityhuman.isCreative() && world.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
            TileEntity tileentity = world.getBlockEntity(blockposition);

            if (tileentity instanceof TileEntityBeehive) {
                TileEntityBeehive tileentitybeehive = (TileEntityBeehive) tileentity;
                ItemStack itemstack = new ItemStack(this);
                int i = (Integer) iblockdata.getValue(BlockBeehive.HONEY_LEVEL);
                boolean flag = !tileentitybeehive.isEmpty();

                if (flag || i > 0) {
                    NBTTagCompound nbttagcompound;

                    if (flag) {
                        nbttagcompound = new NBTTagCompound();
                        nbttagcompound.put("Bees", tileentitybeehive.writeBees());
                        ItemBlock.setBlockEntityData(itemstack, TileEntityTypes.BEEHIVE, nbttagcompound);
                    }

                    nbttagcompound = new NBTTagCompound();
                    nbttagcompound.putInt("honey_level", i);
                    itemstack.addTagElement("BlockStateTag", nbttagcompound);
                    EntityItem entityitem = new EntityItem(world, (double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), itemstack);

                    entityitem.setDefaultPickUpDelay();
                    world.addFreshEntity(entityitem);
                }
            }
        }

        super.playerWillDestroy(world, blockposition, iblockdata, entityhuman);
    }

    @Override
    public List<ItemStack> getDrops(IBlockData iblockdata, LootTableInfo.Builder loottableinfo_builder) {
        Entity entity = (Entity) loottableinfo_builder.getOptionalParameter(LootContextParameters.THIS_ENTITY);

        if (entity instanceof EntityTNTPrimed || entity instanceof EntityCreeper || entity instanceof EntityWitherSkull || entity instanceof EntityWither || entity instanceof EntityMinecartTNT) {
            TileEntity tileentity = (TileEntity) loottableinfo_builder.getOptionalParameter(LootContextParameters.BLOCK_ENTITY);

            if (tileentity instanceof TileEntityBeehive) {
                TileEntityBeehive tileentitybeehive = (TileEntityBeehive) tileentity;

                tileentitybeehive.emptyAllLivingFromHive((EntityHuman) null, iblockdata, TileEntityBeehive.ReleaseStatus.EMERGENCY);
            }
        }

        return super.getDrops(iblockdata, loottableinfo_builder);
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (generatoraccess.getBlockState(blockposition1).getBlock() instanceof BlockFire) {
            TileEntity tileentity = generatoraccess.getBlockEntity(blockposition);

            if (tileentity instanceof TileEntityBeehive) {
                TileEntityBeehive tileentitybeehive = (TileEntityBeehive) tileentity;

                tileentitybeehive.emptyAllLivingFromHive((EntityHuman) null, iblockdata, TileEntityBeehive.ReleaseStatus.EMERGENCY);
            }
        }

        return super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    public static EnumDirection getRandomOffset(Random random) {
        return (EnumDirection) SystemUtils.getRandom((Object[]) BlockBeehive.SPAWN_DIRECTIONS, random);
    }
}
