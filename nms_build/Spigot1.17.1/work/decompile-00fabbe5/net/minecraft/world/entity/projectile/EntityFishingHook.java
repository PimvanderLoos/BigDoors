package net.minecraft.world.entity.projectile;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.tags.TagsItem;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityExperienceOrb;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameterSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.MovingObjectPositionEntity;
import net.minecraft.world.phys.Vec3D;

public class EntityFishingHook extends IProjectile {

    private final Random syncronizedRandom;
    private boolean biting;
    private int outOfWaterTime;
    private static final int MAX_OUT_OF_WATER_TIME = 10;
    public static final DataWatcherObject<Integer> DATA_HOOKED_ENTITY = DataWatcher.a(EntityFishingHook.class, DataWatcherRegistry.INT);
    private static final DataWatcherObject<Boolean> DATA_BITING = DataWatcher.a(EntityFishingHook.class, DataWatcherRegistry.BOOLEAN);
    private int life;
    private int nibble;
    private int timeUntilLured;
    private int timeUntilHooked;
    private float fishAngle;
    private boolean openWater;
    @Nullable
    public Entity hookedIn;
    public EntityFishingHook.HookState currentState;
    private final int luck;
    private final int lureSpeed;

    private EntityFishingHook(EntityTypes<? extends EntityFishingHook> entitytypes, World world, int i, int j) {
        super(entitytypes, world);
        this.syncronizedRandom = new Random();
        this.openWater = true;
        this.currentState = EntityFishingHook.HookState.FLYING;
        this.noCulling = true;
        this.luck = Math.max(0, i);
        this.lureSpeed = Math.max(0, j);
    }

    public EntityFishingHook(EntityTypes<? extends EntityFishingHook> entitytypes, World world) {
        this(entitytypes, world, 0, 0);
    }

    public EntityFishingHook(EntityHuman entityhuman, World world, int i, int j) {
        this(EntityTypes.FISHING_BOBBER, world, i, j);
        this.setShooter(entityhuman);
        float f = entityhuman.getXRot();
        float f1 = entityhuman.getYRot();
        float f2 = MathHelper.cos(-f1 * 0.017453292F - 3.1415927F);
        float f3 = MathHelper.sin(-f1 * 0.017453292F - 3.1415927F);
        float f4 = -MathHelper.cos(-f * 0.017453292F);
        float f5 = MathHelper.sin(-f * 0.017453292F);
        double d0 = entityhuman.locX() - (double) f3 * 0.3D;
        double d1 = entityhuman.getHeadY();
        double d2 = entityhuman.locZ() - (double) f2 * 0.3D;

        this.setPositionRotation(d0, d1, d2, f1, f);
        Vec3D vec3d = new Vec3D((double) (-f3), (double) MathHelper.a(-(f5 / f4), -5.0F, 5.0F), (double) (-f2));
        double d3 = vec3d.f();

        vec3d = vec3d.d(0.6D / d3 + 0.5D + this.random.nextGaussian() * 0.0045D, 0.6D / d3 + 0.5D + this.random.nextGaussian() * 0.0045D, 0.6D / d3 + 0.5D + this.random.nextGaussian() * 0.0045D);
        this.setMot(vec3d);
        this.setYRot((float) (MathHelper.d(vec3d.x, vec3d.z) * 57.2957763671875D));
        this.setXRot((float) (MathHelper.d(vec3d.y, vec3d.h()) * 57.2957763671875D));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    @Override
    protected void initDatawatcher() {
        this.getDataWatcher().register(EntityFishingHook.DATA_HOOKED_ENTITY, 0);
        this.getDataWatcher().register(EntityFishingHook.DATA_BITING, false);
    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityFishingHook.DATA_HOOKED_ENTITY.equals(datawatcherobject)) {
            int i = (Integer) this.getDataWatcher().get(EntityFishingHook.DATA_HOOKED_ENTITY);

            this.hookedIn = i > 0 ? this.level.getEntity(i - 1) : null;
        }

        if (EntityFishingHook.DATA_BITING.equals(datawatcherobject)) {
            this.biting = (Boolean) this.getDataWatcher().get(EntityFishingHook.DATA_BITING);
            if (this.biting) {
                this.setMot(this.getMot().x, (double) (-0.4F * MathHelper.a(this.syncronizedRandom, 0.6F, 1.0F)), this.getMot().z);
            }
        }

        super.a(datawatcherobject);
    }

    @Override
    public boolean a(double d0) {
        double d1 = 64.0D;

        return d0 < 4096.0D;
    }

    @Override
    public void a(double d0, double d1, double d2, float f, float f1, int i, boolean flag) {}

    @Override
    public void tick() {
        this.syncronizedRandom.setSeed(this.getUniqueID().getLeastSignificantBits() ^ this.level.getTime());
        super.tick();
        EntityHuman entityhuman = this.getOwner();

        if (entityhuman == null) {
            this.die();
        } else if (this.level.isClientSide || !this.a(entityhuman)) {
            if (this.onGround) {
                ++this.life;
                if (this.life >= 1200) {
                    this.die();
                    return;
                }
            } else {
                this.life = 0;
            }

            float f = 0.0F;
            BlockPosition blockposition = this.getChunkCoordinates();
            Fluid fluid = this.level.getFluid(blockposition);

            if (fluid.a((Tag) TagsFluid.WATER)) {
                f = fluid.getHeight(this.level, blockposition);
            }

            boolean flag = f > 0.0F;

            if (this.currentState == EntityFishingHook.HookState.FLYING) {
                if (this.hookedIn != null) {
                    this.setMot(Vec3D.ZERO);
                    this.currentState = EntityFishingHook.HookState.HOOKED_IN_ENTITY;
                    return;
                }

                if (flag) {
                    this.setMot(this.getMot().d(0.3D, 0.2D, 0.3D));
                    this.currentState = EntityFishingHook.HookState.BOBBING;
                    return;
                }

                this.l();
            } else {
                if (this.currentState == EntityFishingHook.HookState.HOOKED_IN_ENTITY) {
                    if (this.hookedIn != null) {
                        if (!this.hookedIn.isRemoved() && this.hookedIn.level.getDimensionKey() == this.level.getDimensionKey()) {
                            this.setPosition(this.hookedIn.locX(), this.hookedIn.e(0.8D), this.hookedIn.locZ());
                        } else {
                            this.updateHookedEntity((Entity) null);
                            this.currentState = EntityFishingHook.HookState.FLYING;
                        }
                    }

                    return;
                }

                if (this.currentState == EntityFishingHook.HookState.BOBBING) {
                    Vec3D vec3d = this.getMot();
                    double d0 = this.locY() + vec3d.y - (double) blockposition.getY() - (double) f;

                    if (Math.abs(d0) < 0.01D) {
                        d0 += Math.signum(d0) * 0.1D;
                    }

                    this.setMot(vec3d.x * 0.9D, vec3d.y - d0 * (double) this.random.nextFloat() * 0.2D, vec3d.z * 0.9D);
                    if (this.nibble <= 0 && this.timeUntilHooked <= 0) {
                        this.openWater = true;
                    } else {
                        this.openWater = this.openWater && this.outOfWaterTime < 10 && this.b(blockposition);
                    }

                    if (flag) {
                        this.outOfWaterTime = Math.max(0, this.outOfWaterTime - 1);
                        if (this.biting) {
                            this.setMot(this.getMot().add(0.0D, -0.1D * (double) this.syncronizedRandom.nextFloat() * (double) this.syncronizedRandom.nextFloat(), 0.0D));
                        }

                        if (!this.level.isClientSide) {
                            this.a(blockposition);
                        }
                    } else {
                        this.outOfWaterTime = Math.min(10, this.outOfWaterTime + 1);
                    }
                }
            }

            if (!fluid.a((Tag) TagsFluid.WATER)) {
                this.setMot(this.getMot().add(0.0D, -0.03D, 0.0D));
            }

            this.move(EnumMoveType.SELF, this.getMot());
            this.z();
            if (this.currentState == EntityFishingHook.HookState.FLYING && (this.onGround || this.horizontalCollision)) {
                this.setMot(Vec3D.ZERO);
            }

            double d1 = 0.92D;

            this.setMot(this.getMot().a(0.92D));
            this.ah();
        }
    }

    private boolean a(EntityHuman entityhuman) {
        ItemStack itemstack = entityhuman.getItemInMainHand();
        ItemStack itemstack1 = entityhuman.getItemInOffHand();
        boolean flag = itemstack.a(Items.FISHING_ROD);
        boolean flag1 = itemstack1.a(Items.FISHING_ROD);

        if (!entityhuman.isRemoved() && entityhuman.isAlive() && (flag || flag1) && this.f(entityhuman) <= 1024.0D) {
            return false;
        } else {
            this.die();
            return true;
        }
    }

    private void l() {
        MovingObjectPosition movingobjectposition = ProjectileHelper.a((Entity) this, this::a);

        this.a(movingobjectposition);
    }

    @Override
    protected boolean a(Entity entity) {
        return super.a(entity) || entity.isAlive() && entity instanceof EntityItem;
    }

    @Override
    protected void a(MovingObjectPositionEntity movingobjectpositionentity) {
        super.a(movingobjectpositionentity);
        if (!this.level.isClientSide) {
            this.updateHookedEntity(movingobjectpositionentity.getEntity());
        }

    }

    @Override
    protected void a(MovingObjectPositionBlock movingobjectpositionblock) {
        super.a(movingobjectpositionblock);
        this.setMot(this.getMot().d().a(movingobjectpositionblock.a((Entity) this)));
    }

    public void updateHookedEntity(@Nullable Entity entity) {
        this.hookedIn = entity;
        this.getDataWatcher().set(EntityFishingHook.DATA_HOOKED_ENTITY, entity == null ? 0 : entity.getId() + 1);
    }

    private void a(BlockPosition blockposition) {
        WorldServer worldserver = (WorldServer) this.level;
        int i = 1;
        BlockPosition blockposition1 = blockposition.up();

        if (this.random.nextFloat() < 0.25F && this.level.isRainingAt(blockposition1)) {
            ++i;
        }

        if (this.random.nextFloat() < 0.5F && !this.level.g(blockposition1)) {
            --i;
        }

        if (this.nibble > 0) {
            --this.nibble;
            if (this.nibble <= 0) {
                this.timeUntilLured = 0;
                this.timeUntilHooked = 0;
                this.getDataWatcher().set(EntityFishingHook.DATA_BITING, false);
            }
        } else {
            float f;
            float f1;
            float f2;
            double d0;
            double d1;
            double d2;
            IBlockData iblockdata;

            if (this.timeUntilHooked > 0) {
                this.timeUntilHooked -= i;
                if (this.timeUntilHooked > 0) {
                    this.fishAngle = (float) ((double) this.fishAngle + this.random.nextGaussian() * 4.0D);
                    f = this.fishAngle * 0.017453292F;
                    f1 = MathHelper.sin(f);
                    f2 = MathHelper.cos(f);
                    d0 = this.locX() + (double) (f1 * (float) this.timeUntilHooked * 0.1F);
                    d1 = (double) ((float) MathHelper.floor(this.locY()) + 1.0F);
                    d2 = this.locZ() + (double) (f2 * (float) this.timeUntilHooked * 0.1F);
                    iblockdata = worldserver.getType(new BlockPosition(d0, d1 - 1.0D, d2));
                    if (iblockdata.a(Blocks.WATER)) {
                        if (this.random.nextFloat() < 0.15F) {
                            worldserver.a(Particles.BUBBLE, d0, d1 - 0.10000000149011612D, d2, 1, (double) f1, 0.1D, (double) f2, 0.0D);
                        }

                        float f3 = f1 * 0.04F;
                        float f4 = f2 * 0.04F;

                        worldserver.a(Particles.FISHING, d0, d1, d2, 0, (double) f4, 0.01D, (double) (-f3), 1.0D);
                        worldserver.a(Particles.FISHING, d0, d1, d2, 0, (double) (-f4), 0.01D, (double) f3, 1.0D);
                    }
                } else {
                    this.playSound(SoundEffects.FISHING_BOBBER_SPLASH, 0.25F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
                    double d3 = this.locY() + 0.5D;

                    worldserver.a(Particles.BUBBLE, this.locX(), d3, this.locZ(), (int) (1.0F + this.getWidth() * 20.0F), (double) this.getWidth(), 0.0D, (double) this.getWidth(), 0.20000000298023224D);
                    worldserver.a(Particles.FISHING, this.locX(), d3, this.locZ(), (int) (1.0F + this.getWidth() * 20.0F), (double) this.getWidth(), 0.0D, (double) this.getWidth(), 0.20000000298023224D);
                    this.nibble = MathHelper.nextInt(this.random, 20, 40);
                    this.getDataWatcher().set(EntityFishingHook.DATA_BITING, true);
                }
            } else if (this.timeUntilLured > 0) {
                this.timeUntilLured -= i;
                f = 0.15F;
                if (this.timeUntilLured < 20) {
                    f = (float) ((double) f + (double) (20 - this.timeUntilLured) * 0.05D);
                } else if (this.timeUntilLured < 40) {
                    f = (float) ((double) f + (double) (40 - this.timeUntilLured) * 0.02D);
                } else if (this.timeUntilLured < 60) {
                    f = (float) ((double) f + (double) (60 - this.timeUntilLured) * 0.01D);
                }

                if (this.random.nextFloat() < f) {
                    f1 = MathHelper.a(this.random, 0.0F, 360.0F) * 0.017453292F;
                    f2 = MathHelper.a(this.random, 25.0F, 60.0F);
                    d0 = this.locX() + (double) (MathHelper.sin(f1) * f2 * 0.1F);
                    d1 = (double) ((float) MathHelper.floor(this.locY()) + 1.0F);
                    d2 = this.locZ() + (double) (MathHelper.cos(f1) * f2 * 0.1F);
                    iblockdata = worldserver.getType(new BlockPosition(d0, d1 - 1.0D, d2));
                    if (iblockdata.a(Blocks.WATER)) {
                        worldserver.a(Particles.SPLASH, d0, d1, d2, 2 + this.random.nextInt(2), 0.10000000149011612D, 0.0D, 0.10000000149011612D, 0.0D);
                    }
                }

                if (this.timeUntilLured <= 0) {
                    this.fishAngle = MathHelper.a(this.random, 0.0F, 360.0F);
                    this.timeUntilHooked = MathHelper.nextInt(this.random, 20, 80);
                }
            } else {
                this.timeUntilLured = MathHelper.nextInt(this.random, 100, 600);
                this.timeUntilLured -= this.lureSpeed * 20 * 5;
            }
        }

    }

    private boolean b(BlockPosition blockposition) {
        EntityFishingHook.WaterPosition entityfishinghook_waterposition = EntityFishingHook.WaterPosition.INVALID;

        for (int i = -1; i <= 2; ++i) {
            EntityFishingHook.WaterPosition entityfishinghook_waterposition1 = this.a(blockposition.c(-2, i, -2), blockposition.c(2, i, 2));

            switch (entityfishinghook_waterposition1) {
                case INVALID:
                    return false;
                case ABOVE_WATER:
                    if (entityfishinghook_waterposition == EntityFishingHook.WaterPosition.INVALID) {
                        return false;
                    }
                    break;
                case INSIDE_WATER:
                    if (entityfishinghook_waterposition == EntityFishingHook.WaterPosition.ABOVE_WATER) {
                        return false;
                    }
            }

            entityfishinghook_waterposition = entityfishinghook_waterposition1;
        }

        return true;
    }

    private EntityFishingHook.WaterPosition a(BlockPosition blockposition, BlockPosition blockposition1) {
        return (EntityFishingHook.WaterPosition) BlockPosition.b(blockposition, blockposition1).map(this::c).reduce((entityfishinghook_waterposition, entityfishinghook_waterposition1) -> {
            return entityfishinghook_waterposition == entityfishinghook_waterposition1 ? entityfishinghook_waterposition : EntityFishingHook.WaterPosition.INVALID;
        }).orElse(EntityFishingHook.WaterPosition.INVALID);
    }

    private EntityFishingHook.WaterPosition c(BlockPosition blockposition) {
        IBlockData iblockdata = this.level.getType(blockposition);

        if (!iblockdata.isAir() && !iblockdata.a(Blocks.LILY_PAD)) {
            Fluid fluid = iblockdata.getFluid();

            return fluid.a((Tag) TagsFluid.WATER) && fluid.isSource() && iblockdata.getCollisionShape(this.level, blockposition).isEmpty() ? EntityFishingHook.WaterPosition.INSIDE_WATER : EntityFishingHook.WaterPosition.INVALID;
        } else {
            return EntityFishingHook.WaterPosition.ABOVE_WATER;
        }
    }

    public boolean isInOpenWater() {
        return this.openWater;
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {}

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {}

    public int a(ItemStack itemstack) {
        EntityHuman entityhuman = this.getOwner();

        if (!this.level.isClientSide && entityhuman != null && !this.a(entityhuman)) {
            int i = 0;

            if (this.hookedIn != null) {
                this.reel(this.hookedIn);
                CriterionTriggers.FISHING_ROD_HOOKED.a((EntityPlayer) entityhuman, itemstack, this, (Collection) Collections.emptyList());
                this.level.broadcastEntityEffect(this, (byte) 31);
                i = this.hookedIn instanceof EntityItem ? 3 : 5;
            } else if (this.nibble > 0) {
                LootTableInfo.Builder loottableinfo_builder = (new LootTableInfo.Builder((WorldServer) this.level)).set(LootContextParameters.ORIGIN, this.getPositionVector()).set(LootContextParameters.TOOL, itemstack).set(LootContextParameters.THIS_ENTITY, this).a(this.random).a((float) this.luck + entityhuman.fF());
                LootTable loottable = this.level.getMinecraftServer().getLootTableRegistry().getLootTable(LootTables.FISHING);
                List<ItemStack> list = loottable.populateLoot(loottableinfo_builder.build(LootContextParameterSets.FISHING));

                CriterionTriggers.FISHING_ROD_HOOKED.a((EntityPlayer) entityhuman, itemstack, this, (Collection) list);
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    ItemStack itemstack1 = (ItemStack) iterator.next();
                    EntityItem entityitem = new EntityItem(this.level, this.locX(), this.locY(), this.locZ(), itemstack1);
                    double d0 = entityhuman.locX() - this.locX();
                    double d1 = entityhuman.locY() - this.locY();
                    double d2 = entityhuman.locZ() - this.locZ();
                    double d3 = 0.1D;

                    entityitem.setMot(d0 * 0.1D, d1 * 0.1D + Math.sqrt(Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2)) * 0.08D, d2 * 0.1D);
                    this.level.addEntity(entityitem);
                    entityhuman.level.addEntity(new EntityExperienceOrb(entityhuman.level, entityhuman.locX(), entityhuman.locY() + 0.5D, entityhuman.locZ() + 0.5D, this.random.nextInt(6) + 1));
                    if (itemstack1.a((Tag) TagsItem.FISHES)) {
                        entityhuman.a(StatisticList.FISH_CAUGHT, 1);
                    }
                }

                i = 1;
            }

            if (this.onGround) {
                i = 2;
            }

            this.die();
            return i;
        } else {
            return 0;
        }
    }

    @Override
    public void a(byte b0) {
        if (b0 == 31 && this.level.isClientSide && this.hookedIn instanceof EntityHuman && ((EntityHuman) this.hookedIn).fi()) {
            this.reel(this.hookedIn);
        }

        super.a(b0);
    }

    public void reel(Entity entity) {
        Entity entity1 = this.getShooter();

        if (entity1 != null) {
            Vec3D vec3d = (new Vec3D(entity1.locX() - this.locX(), entity1.locY() - this.locY(), entity1.locZ() - this.locZ())).a(0.1D);

            entity.setMot(entity.getMot().e(vec3d));
        }
    }

    @Override
    protected Entity.MovementEmission aI() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    public void a(Entity.RemovalReason entity_removalreason) {
        this.a((EntityFishingHook) null);
        super.a(entity_removalreason);
    }

    @Override
    public void ae() {
        this.a((EntityFishingHook) null);
    }

    @Override
    public void setShooter(@Nullable Entity entity) {
        super.setShooter(entity);
        this.a(this);
    }

    private void a(@Nullable EntityFishingHook entityfishinghook) {
        EntityHuman entityhuman = this.getOwner();

        if (entityhuman != null) {
            entityhuman.fishing = entityfishinghook;
        }

    }

    @Nullable
    public EntityHuman getOwner() {
        Entity entity = this.getShooter();

        return entity instanceof EntityHuman ? (EntityHuman) entity : null;
    }

    @Nullable
    public Entity getHooked() {
        return this.hookedIn;
    }

    @Override
    public boolean canPortal() {
        return false;
    }

    @Override
    public Packet<?> getPacket() {
        Entity entity = this.getShooter();

        return new PacketPlayOutSpawnEntity(this, entity == null ? this.getId() : entity.getId());
    }

    @Override
    public void a(PacketPlayOutSpawnEntity packetplayoutspawnentity) {
        super.a(packetplayoutspawnentity);
        if (this.getOwner() == null) {
            int i = packetplayoutspawnentity.m();

            EntityFishingHook.LOGGER.error("Failed to recreate fishing hook on client. {} (id: {}) is not a valid owner.", this.level.getEntity(i), i);
            this.killEntity();
        }

    }

    public static enum HookState {

        FLYING, HOOKED_IN_ENTITY, BOBBING;

        private HookState() {}
    }

    private static enum WaterPosition {

        ABOVE_WATER, INSIDE_WATER, INVALID;

        private WaterPosition() {}
    }
}
