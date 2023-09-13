package net.minecraft.world.entity.item;

import java.util.Iterator;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.CrashReportSystemDetails;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutBlockChange;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.tags.TagsFluid;
import net.minecraft.util.MathHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.item.context.BlockActionContextDirectional;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.level.RayTrace;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockAnvil;
import net.minecraft.world.level.block.BlockConcretePowder;
import net.minecraft.world.level.block.BlockFalling;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;

public class EntityFallingBlock extends Entity {

    private static final int REMOVAL_DELAY_MILLIS = 50;
    private IBlockData blockState;
    public int time;
    public boolean dropItem;
    private boolean cancelDrop;
    public boolean hurtEntities;
    private int fallDamageMax;
    private float fallDamagePerDistance;
    private long removeAtMillis;
    @Nullable
    public NBTTagCompound blockData;
    protected static final DataWatcherObject<BlockPosition> DATA_START_POS = DataWatcher.defineId(EntityFallingBlock.class, DataWatcherRegistry.BLOCK_POS);

    public EntityFallingBlock(EntityTypes<? extends EntityFallingBlock> entitytypes, World world) {
        super(entitytypes, world);
        this.blockState = Blocks.SAND.defaultBlockState();
        this.dropItem = true;
        this.fallDamageMax = 40;
    }

    public EntityFallingBlock(World world, double d0, double d1, double d2, IBlockData iblockdata) {
        this(EntityTypes.FALLING_BLOCK, world);
        this.blockState = iblockdata;
        this.blocksBuilding = true;
        this.setPos(d0, d1 + (double) ((1.0F - this.getBbHeight()) / 2.0F), d2);
        this.setDeltaMovement(Vec3D.ZERO);
        this.xo = d0;
        this.yo = d1;
        this.zo = d2;
        this.setStartPos(this.blockPosition());
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    public void setStartPos(BlockPosition blockposition) {
        this.entityData.set(EntityFallingBlock.DATA_START_POS, blockposition);
    }

    public BlockPosition getStartPos() {
        return (BlockPosition) this.entityData.get(EntityFallingBlock.DATA_START_POS);
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(EntityFallingBlock.DATA_START_POS, BlockPosition.ZERO);
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    public void tick() {
        if (this.blockState.isAir()) {
            this.discard();
        } else if (this.level.isClientSide && this.removeAtMillis > 0L) {
            if (System.currentTimeMillis() >= this.removeAtMillis) {
                super.setRemoved(Entity.RemovalReason.DISCARDED);
            }

        } else {
            Block block = this.blockState.getBlock();
            BlockPosition blockposition;

            if (this.time++ == 0) {
                blockposition = this.blockPosition();
                if (this.level.getBlockState(blockposition).is(block)) {
                    this.level.removeBlock(blockposition, false);
                } else if (!this.level.isClientSide) {
                    this.discard();
                    return;
                }
            }

            if (!this.isNoGravity()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
            }

            this.move(EnumMoveType.SELF, this.getDeltaMovement());
            if (!this.level.isClientSide) {
                blockposition = this.blockPosition();
                boolean flag = this.blockState.getBlock() instanceof BlockConcretePowder;
                boolean flag1 = flag && this.level.getFluidState(blockposition).is((Tag) TagsFluid.WATER);
                double d0 = this.getDeltaMovement().lengthSqr();

                if (flag && d0 > 1.0D) {
                    MovingObjectPositionBlock movingobjectpositionblock = this.level.clip(new RayTrace(new Vec3D(this.xo, this.yo, this.zo), this.position(), RayTrace.BlockCollisionOption.COLLIDER, RayTrace.FluidCollisionOption.SOURCE_ONLY, this));

                    if (movingobjectpositionblock.getType() != MovingObjectPosition.EnumMovingObjectType.MISS && this.level.getFluidState(movingobjectpositionblock.getBlockPos()).is((Tag) TagsFluid.WATER)) {
                        blockposition = movingobjectpositionblock.getBlockPos();
                        flag1 = true;
                    }
                }

                if (!this.onGround && !flag1) {
                    if (!this.level.isClientSide && (this.time > 100 && (blockposition.getY() <= this.level.getMinBuildHeight() || blockposition.getY() > this.level.getMaxBuildHeight()) || this.time > 600)) {
                        if (this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                            this.spawnAtLocation((IMaterial) block);
                        }

                        this.discard();
                    }
                } else {
                    IBlockData iblockdata = this.level.getBlockState(blockposition);

                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.7D, -0.5D, 0.7D));
                    if (!iblockdata.is(Blocks.MOVING_PISTON)) {
                        if (!this.cancelDrop) {
                            boolean flag2 = iblockdata.canBeReplaced((BlockActionContext) (new BlockActionContextDirectional(this.level, blockposition, EnumDirection.DOWN, ItemStack.EMPTY, EnumDirection.UP)));
                            boolean flag3 = BlockFalling.isFree(this.level.getBlockState(blockposition.below())) && (!flag || !flag1);
                            boolean flag4 = this.blockState.canSurvive(this.level, blockposition) && !flag3;

                            if (flag2 && flag4) {
                                if (this.blockState.hasProperty(BlockProperties.WATERLOGGED) && this.level.getFluidState(blockposition).getType() == FluidTypes.WATER) {
                                    this.blockState = (IBlockData) this.blockState.setValue(BlockProperties.WATERLOGGED, true);
                                }

                                if (this.level.setBlock(blockposition, this.blockState, 3)) {
                                    ((WorldServer) this.level).getChunkSource().chunkMap.broadcast(this, new PacketPlayOutBlockChange(blockposition, this.level.getBlockState(blockposition)));
                                    this.discard();
                                    if (block instanceof Fallable) {
                                        ((Fallable) block).onLand(this.level, blockposition, this.blockState, iblockdata, this);
                                    }

                                    if (this.blockData != null && this.blockState.hasBlockEntity()) {
                                        TileEntity tileentity = this.level.getBlockEntity(blockposition);

                                        if (tileentity != null) {
                                            NBTTagCompound nbttagcompound = tileentity.saveWithoutMetadata();
                                            Iterator iterator = this.blockData.getAllKeys().iterator();

                                            while (iterator.hasNext()) {
                                                String s = (String) iterator.next();

                                                nbttagcompound.put(s, this.blockData.get(s).copy());
                                            }

                                            try {
                                                tileentity.load(nbttagcompound);
                                            } catch (Exception exception) {
                                                EntityFallingBlock.LOGGER.error("Failed to load block entity from falling block", exception);
                                            }

                                            tileentity.setChanged();
                                        }
                                    }
                                } else if (this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                                    this.discard();
                                    this.callOnBrokenAfterFall(block, blockposition);
                                    this.spawnAtLocation((IMaterial) block);
                                }
                            } else {
                                this.discard();
                                if (this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                                    this.callOnBrokenAfterFall(block, blockposition);
                                    this.spawnAtLocation((IMaterial) block);
                                }
                            }
                        } else {
                            this.discard();
                            this.callOnBrokenAfterFall(block, blockposition);
                        }
                    }
                }
            }

            this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
        }
    }

    @Override
    public void setRemoved(Entity.RemovalReason entity_removalreason) {
        if (this.level.shouldDelayFallingBlockEntityRemoval(entity_removalreason)) {
            this.removeAtMillis = System.currentTimeMillis() + 50L;
        } else {
            super.setRemoved(entity_removalreason);
        }
    }

    public void callOnBrokenAfterFall(Block block, BlockPosition blockposition) {
        if (block instanceof Fallable) {
            ((Fallable) block).onBrokenAfterFall(this.level, blockposition, this);
        }

    }

    @Override
    public boolean causeFallDamage(float f, float f1, DamageSource damagesource) {
        if (!this.hurtEntities) {
            return false;
        } else {
            int i = MathHelper.ceil(f - 1.0F);

            if (i < 0) {
                return false;
            } else {
                Predicate predicate;
                DamageSource damagesource1;

                if (this.blockState.getBlock() instanceof Fallable) {
                    Fallable fallable = (Fallable) this.blockState.getBlock();

                    predicate = fallable.getHurtsEntitySelector();
                    damagesource1 = fallable.getFallDamageSource();
                } else {
                    predicate = IEntitySelector.NO_SPECTATORS;
                    damagesource1 = DamageSource.FALLING_BLOCK;
                }

                float f2 = (float) Math.min(MathHelper.floor((float) i * this.fallDamagePerDistance), this.fallDamageMax);

                this.level.getEntities((Entity) this, this.getBoundingBox(), predicate).forEach((entity) -> {
                    entity.hurt(damagesource1, f2);
                });
                boolean flag = this.blockState.is((Tag) TagsBlock.ANVIL);

                if (flag && f2 > 0.0F && this.random.nextFloat() < 0.05F + (float) i * 0.05F) {
                    IBlockData iblockdata = BlockAnvil.damage(this.blockState);

                    if (iblockdata == null) {
                        this.cancelDrop = true;
                    } else {
                        this.blockState = iblockdata;
                    }
                }

                return false;
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        nbttagcompound.put("BlockState", GameProfileSerializer.writeBlockState(this.blockState));
        nbttagcompound.putInt("Time", this.time);
        nbttagcompound.putBoolean("DropItem", this.dropItem);
        nbttagcompound.putBoolean("HurtEntities", this.hurtEntities);
        nbttagcompound.putFloat("FallHurtAmount", this.fallDamagePerDistance);
        nbttagcompound.putInt("FallHurtMax", this.fallDamageMax);
        if (this.blockData != null) {
            nbttagcompound.put("TileEntityData", this.blockData);
        }

    }

    @Override
    protected void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        this.blockState = GameProfileSerializer.readBlockState(nbttagcompound.getCompound("BlockState"));
        this.time = nbttagcompound.getInt("Time");
        if (nbttagcompound.contains("HurtEntities", 99)) {
            this.hurtEntities = nbttagcompound.getBoolean("HurtEntities");
            this.fallDamagePerDistance = nbttagcompound.getFloat("FallHurtAmount");
            this.fallDamageMax = nbttagcompound.getInt("FallHurtMax");
        } else if (this.blockState.is((Tag) TagsBlock.ANVIL)) {
            this.hurtEntities = true;
        }

        if (nbttagcompound.contains("DropItem", 99)) {
            this.dropItem = nbttagcompound.getBoolean("DropItem");
        }

        if (nbttagcompound.contains("TileEntityData", 10)) {
            this.blockData = nbttagcompound.getCompound("TileEntityData");
        }

        if (this.blockState.isAir()) {
            this.blockState = Blocks.SAND.defaultBlockState();
        }

    }

    public void setHurtsEntities(float f, int i) {
        this.hurtEntities = true;
        this.fallDamagePerDistance = f;
        this.fallDamageMax = i;
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    public void fillCrashReportCategory(CrashReportSystemDetails crashreportsystemdetails) {
        super.fillCrashReportCategory(crashreportsystemdetails);
        crashreportsystemdetails.setDetail("Immitating BlockState", (Object) this.blockState.toString());
    }

    public IBlockData getBlockState() {
        return this.blockState;
    }

    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new PacketPlayOutSpawnEntity(this, Block.getId(this.getBlockState()));
    }

    @Override
    public void recreateFromPacket(PacketPlayOutSpawnEntity packetplayoutspawnentity) {
        super.recreateFromPacket(packetplayoutspawnentity);
        this.blockState = Block.stateById(packetplayoutspawnentity.getData());
        this.blocksBuilding = true;
        double d0 = packetplayoutspawnentity.getX();
        double d1 = packetplayoutspawnentity.getY();
        double d2 = packetplayoutspawnentity.getZ();

        this.setPos(d0, d1 + (double) ((1.0F - this.getBbHeight()) / 2.0F), d2);
        this.setStartPos(this.blockPosition());
    }
}
