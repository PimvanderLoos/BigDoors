package net.minecraft.world.entity.item;

import java.util.Iterator;
import java.util.function.Predicate;
import net.minecraft.CrashReportSystemDetails;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTBase;
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

    private IBlockData blockState;
    public int time;
    public boolean dropItem;
    private boolean cancelDrop;
    public boolean hurtEntities;
    private int fallDamageMax;
    private float fallDamagePerDistance;
    public NBTTagCompound blockData;
    protected static final DataWatcherObject<BlockPosition> DATA_START_POS = DataWatcher.a(EntityFallingBlock.class, DataWatcherRegistry.BLOCK_POS);

    public EntityFallingBlock(EntityTypes<? extends EntityFallingBlock> entitytypes, World world) {
        super(entitytypes, world);
        this.blockState = Blocks.SAND.getBlockData();
        this.dropItem = true;
        this.fallDamageMax = 40;
    }

    public EntityFallingBlock(World world, double d0, double d1, double d2, IBlockData iblockdata) {
        this(EntityTypes.FALLING_BLOCK, world);
        this.blockState = iblockdata;
        this.blocksBuilding = true;
        this.setPosition(d0, d1 + (double) ((1.0F - this.getHeight()) / 2.0F), d2);
        this.setMot(Vec3D.ZERO);
        this.xo = d0;
        this.yo = d1;
        this.zo = d2;
        this.a(this.getChunkCoordinates());
    }

    @Override
    public boolean ca() {
        return false;
    }

    public void a(BlockPosition blockposition) {
        this.entityData.set(EntityFallingBlock.DATA_START_POS, blockposition);
    }

    public BlockPosition h() {
        return (BlockPosition) this.entityData.get(EntityFallingBlock.DATA_START_POS);
    }

    @Override
    protected Entity.MovementEmission aI() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    protected void initDatawatcher() {
        this.entityData.register(EntityFallingBlock.DATA_START_POS, BlockPosition.ZERO);
    }

    @Override
    public boolean isInteractable() {
        return !this.isRemoved();
    }

    @Override
    public void tick() {
        if (this.blockState.isAir()) {
            this.die();
        } else {
            Block block = this.blockState.getBlock();
            BlockPosition blockposition;

            if (this.time++ == 0) {
                blockposition = this.getChunkCoordinates();
                if (this.level.getType(blockposition).a(block)) {
                    this.level.a(blockposition, false);
                } else if (!this.level.isClientSide) {
                    this.die();
                    return;
                }
            }

            if (!this.isNoGravity()) {
                this.setMot(this.getMot().add(0.0D, -0.04D, 0.0D));
            }

            this.move(EnumMoveType.SELF, this.getMot());
            if (!this.level.isClientSide) {
                blockposition = this.getChunkCoordinates();
                boolean flag = this.blockState.getBlock() instanceof BlockConcretePowder;
                boolean flag1 = flag && this.level.getFluid(blockposition).a((Tag) TagsFluid.WATER);
                double d0 = this.getMot().g();

                if (flag && d0 > 1.0D) {
                    MovingObjectPositionBlock movingobjectpositionblock = this.level.rayTrace(new RayTrace(new Vec3D(this.xo, this.yo, this.zo), this.getPositionVector(), RayTrace.BlockCollisionOption.COLLIDER, RayTrace.FluidCollisionOption.SOURCE_ONLY, this));

                    if (movingobjectpositionblock.getType() != MovingObjectPosition.EnumMovingObjectType.MISS && this.level.getFluid(movingobjectpositionblock.getBlockPosition()).a((Tag) TagsFluid.WATER)) {
                        blockposition = movingobjectpositionblock.getBlockPosition();
                        flag1 = true;
                    }
                }

                if (!this.onGround && !flag1) {
                    if (!this.level.isClientSide && (this.time > 100 && (blockposition.getY() <= this.level.getMinBuildHeight() || blockposition.getY() > this.level.getMaxBuildHeight()) || this.time > 600)) {
                        if (this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                            this.a((IMaterial) block);
                        }

                        this.die();
                    }
                } else {
                    IBlockData iblockdata = this.level.getType(blockposition);

                    this.setMot(this.getMot().d(0.7D, -0.5D, 0.7D));
                    if (!iblockdata.a(Blocks.MOVING_PISTON)) {
                        if (!this.cancelDrop) {
                            boolean flag2 = iblockdata.a((BlockActionContext) (new BlockActionContextDirectional(this.level, blockposition, EnumDirection.DOWN, ItemStack.EMPTY, EnumDirection.UP)));
                            boolean flag3 = BlockFalling.canFallThrough(this.level.getType(blockposition.down())) && (!flag || !flag1);
                            boolean flag4 = this.blockState.canPlace(this.level, blockposition) && !flag3;

                            if (flag2 && flag4) {
                                if (this.blockState.b(BlockProperties.WATERLOGGED) && this.level.getFluid(blockposition).getType() == FluidTypes.WATER) {
                                    this.blockState = (IBlockData) this.blockState.set(BlockProperties.WATERLOGGED, true);
                                }

                                if (this.level.setTypeAndData(blockposition, this.blockState, 3)) {
                                    ((WorldServer) this.level).getChunkProvider().chunkMap.broadcast(this, new PacketPlayOutBlockChange(blockposition, this.level.getType(blockposition)));
                                    this.die();
                                    if (block instanceof Fallable) {
                                        ((Fallable) block).a(this.level, blockposition, this.blockState, iblockdata, this);
                                    }

                                    if (this.blockData != null && this.blockState.isTileEntity()) {
                                        TileEntity tileentity = this.level.getTileEntity(blockposition);

                                        if (tileentity != null) {
                                            NBTTagCompound nbttagcompound = tileentity.save(new NBTTagCompound());
                                            Iterator iterator = this.blockData.getKeys().iterator();

                                            while (iterator.hasNext()) {
                                                String s = (String) iterator.next();
                                                NBTBase nbtbase = this.blockData.get(s);

                                                if (!"x".equals(s) && !"y".equals(s) && !"z".equals(s)) {
                                                    nbttagcompound.set(s, nbtbase.clone());
                                                }
                                            }

                                            try {
                                                tileentity.load(nbttagcompound);
                                            } catch (Exception exception) {
                                                EntityFallingBlock.LOGGER.error("Failed to load block entity from falling block", exception);
                                            }

                                            tileentity.update();
                                        }
                                    }
                                } else if (this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                                    this.die();
                                    this.a(block, blockposition);
                                    this.a((IMaterial) block);
                                }
                            } else {
                                this.die();
                                if (this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                                    this.a(block, blockposition);
                                    this.a((IMaterial) block);
                                }
                            }
                        } else {
                            this.die();
                            this.a(block, blockposition);
                        }
                    }
                }
            }

            this.setMot(this.getMot().a(0.98D));
        }
    }

    public void a(Block block, BlockPosition blockposition) {
        if (block instanceof Fallable) {
            ((Fallable) block).a(this.level, blockposition, this);
        }

    }

    @Override
    public boolean a(float f, float f1, DamageSource damagesource) {
        if (!this.hurtEntities) {
            return false;
        } else {
            int i = MathHelper.f(f - 1.0F);

            if (i < 0) {
                return false;
            } else {
                Predicate predicate;
                DamageSource damagesource1;

                if (this.blockState.getBlock() instanceof Fallable) {
                    Fallable fallable = (Fallable) this.blockState.getBlock();

                    predicate = fallable.T_();
                    damagesource1 = fallable.b();
                } else {
                    predicate = IEntitySelector.NO_SPECTATORS;
                    damagesource1 = DamageSource.FALLING_BLOCK;
                }

                float f2 = (float) Math.min(MathHelper.d((float) i * this.fallDamagePerDistance), this.fallDamageMax);

                this.level.getEntities(this, this.getBoundingBox(), predicate).forEach((entity) -> {
                    entity.damageEntity(damagesource1, f2);
                });
                boolean flag = this.blockState.a((Tag) TagsBlock.ANVIL);

                if (flag && f2 > 0.0F && this.random.nextFloat() < 0.05F + (float) i * 0.05F) {
                    IBlockData iblockdata = BlockAnvil.e(this.blockState);

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
    protected void saveData(NBTTagCompound nbttagcompound) {
        nbttagcompound.set("BlockState", GameProfileSerializer.a(this.blockState));
        nbttagcompound.setInt("Time", this.time);
        nbttagcompound.setBoolean("DropItem", this.dropItem);
        nbttagcompound.setBoolean("HurtEntities", this.hurtEntities);
        nbttagcompound.setFloat("FallHurtAmount", this.fallDamagePerDistance);
        nbttagcompound.setInt("FallHurtMax", this.fallDamageMax);
        if (this.blockData != null) {
            nbttagcompound.set("TileEntityData", this.blockData);
        }

    }

    @Override
    protected void loadData(NBTTagCompound nbttagcompound) {
        this.blockState = GameProfileSerializer.c(nbttagcompound.getCompound("BlockState"));
        this.time = nbttagcompound.getInt("Time");
        if (nbttagcompound.hasKeyOfType("HurtEntities", 99)) {
            this.hurtEntities = nbttagcompound.getBoolean("HurtEntities");
            this.fallDamagePerDistance = nbttagcompound.getFloat("FallHurtAmount");
            this.fallDamageMax = nbttagcompound.getInt("FallHurtMax");
        } else if (this.blockState.a((Tag) TagsBlock.ANVIL)) {
            this.hurtEntities = true;
        }

        if (nbttagcompound.hasKeyOfType("DropItem", 99)) {
            this.dropItem = nbttagcompound.getBoolean("DropItem");
        }

        if (nbttagcompound.hasKeyOfType("TileEntityData", 10)) {
            this.blockData = nbttagcompound.getCompound("TileEntityData");
        }

        if (this.blockState.isAir()) {
            this.blockState = Blocks.SAND.getBlockData();
        }

    }

    public World i() {
        return this.level;
    }

    public void b(float f, int i) {
        this.hurtEntities = true;
        this.fallDamagePerDistance = f;
        this.fallDamageMax = i;
    }

    @Override
    public boolean cg() {
        return false;
    }

    @Override
    public void appendEntityCrashDetails(CrashReportSystemDetails crashreportsystemdetails) {
        super.appendEntityCrashDetails(crashreportsystemdetails);
        crashreportsystemdetails.a("Immitating BlockState", (Object) this.blockState.toString());
    }

    public IBlockData getBlock() {
        return this.blockState;
    }

    @Override
    public boolean cy() {
        return true;
    }

    @Override
    public Packet<?> getPacket() {
        return new PacketPlayOutSpawnEntity(this, Block.getCombinedId(this.getBlock()));
    }

    @Override
    public void a(PacketPlayOutSpawnEntity packetplayoutspawnentity) {
        super.a(packetplayoutspawnentity);
        this.blockState = Block.getByCombinedId(packetplayoutspawnentity.m());
        this.blocksBuilding = true;
        double d0 = packetplayoutspawnentity.d();
        double d1 = packetplayoutspawnentity.e();
        double d2 = packetplayoutspawnentity.f();

        this.setPosition(d0, d1 + (double) ((1.0F - this.getHeight()) / 2.0F), d2);
        this.a(this.getChunkCoordinates());
    }
}
