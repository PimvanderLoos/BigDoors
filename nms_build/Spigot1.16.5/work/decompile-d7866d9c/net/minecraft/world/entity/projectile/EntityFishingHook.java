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

    private final Random b;
    private boolean c;
    private int d;
    public static final DataWatcherObject<Integer> HOOKED_ENTITY = DataWatcher.a(EntityFishingHook.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Boolean> f = DataWatcher.a(EntityFishingHook.class, DataWatcherRegistry.i);
    private int g;
    private int ag;
    private int waitTime;
    private int ai;
    private float aj;
    private boolean inOpenWater;
    public Entity hooked;
    public EntityFishingHook.HookState hookState;
    private final int an;
    private final int lureLevel;

    private EntityFishingHook(World world, EntityHuman entityhuman, int i, int j) {
        super(EntityTypes.FISHING_BOBBER, world);
        this.b = new Random();
        this.inOpenWater = true;
        this.hookState = EntityFishingHook.HookState.FLYING;
        this.Y = true;
        this.setShooter(entityhuman);
        entityhuman.hookedFish = this;
        this.an = Math.max(0, i);
        this.lureLevel = Math.max(0, j);
    }

    public EntityFishingHook(EntityHuman entityhuman, World world, int i, int j) {
        this(world, entityhuman, i, j);
        float f = entityhuman.pitch;
        float f1 = entityhuman.yaw;
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
        this.yaw = (float) (MathHelper.d(vec3d.x, vec3d.z) * 57.2957763671875D);
        this.pitch = (float) (MathHelper.d(vec3d.y, (double) MathHelper.sqrt(c(vec3d))) * 57.2957763671875D);
        this.lastYaw = this.yaw;
        this.lastPitch = this.pitch;
    }

    @Override
    protected void initDatawatcher() {
        this.getDataWatcher().register(EntityFishingHook.HOOKED_ENTITY, 0);
        this.getDataWatcher().register(EntityFishingHook.f, false);
    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityFishingHook.HOOKED_ENTITY.equals(datawatcherobject)) {
            int i = (Integer) this.getDataWatcher().get(EntityFishingHook.HOOKED_ENTITY);

            this.hooked = i > 0 ? this.world.getEntity(i - 1) : null;
        }

        if (EntityFishingHook.f.equals(datawatcherobject)) {
            this.c = (Boolean) this.getDataWatcher().get(EntityFishingHook.f);
            if (this.c) {
                this.setMot(this.getMot().x, (double) (-0.4F * MathHelper.a(this.b, 0.6F, 1.0F)), this.getMot().z);
            }
        }

        super.a(datawatcherobject);
    }

    @Override
    public void tick() {
        this.b.setSeed(this.getUniqueID().getLeastSignificantBits() ^ this.world.getTime());
        super.tick();
        EntityHuman entityhuman = this.getOwner();

        if (entityhuman == null) {
            this.die();
        } else if (this.world.isClientSide || !this.a(entityhuman)) {
            if (this.onGround) {
                ++this.g;
                if (this.g >= 1200) {
                    this.die();
                    return;
                }
            } else {
                this.g = 0;
            }

            float f = 0.0F;
            BlockPosition blockposition = this.getChunkCoordinates();
            Fluid fluid = this.world.getFluid(blockposition);

            if (fluid.a((Tag) TagsFluid.WATER)) {
                f = fluid.getHeight(this.world, blockposition);
            }

            boolean flag = f > 0.0F;

            if (this.hookState == EntityFishingHook.HookState.FLYING) {
                if (this.hooked != null) {
                    this.setMot(Vec3D.ORIGIN);
                    this.hookState = EntityFishingHook.HookState.HOOKED_IN_ENTITY;
                    return;
                }

                if (flag) {
                    this.setMot(this.getMot().d(0.3D, 0.2D, 0.3D));
                    this.hookState = EntityFishingHook.HookState.BOBBING;
                    return;
                }

                this.m();
            } else {
                if (this.hookState == EntityFishingHook.HookState.HOOKED_IN_ENTITY) {
                    if (this.hooked != null) {
                        if (this.hooked.dead) {
                            this.hooked = null;
                            this.hookState = EntityFishingHook.HookState.FLYING;
                        } else {
                            this.setPosition(this.hooked.locX(), this.hooked.e(0.8D), this.hooked.locZ());
                        }
                    }

                    return;
                }

                if (this.hookState == EntityFishingHook.HookState.BOBBING) {
                    Vec3D vec3d = this.getMot();
                    double d0 = this.locY() + vec3d.y - (double) blockposition.getY() - (double) f;

                    if (Math.abs(d0) < 0.01D) {
                        d0 += Math.signum(d0) * 0.1D;
                    }

                    this.setMot(vec3d.x * 0.9D, vec3d.y - d0 * (double) this.random.nextFloat() * 0.2D, vec3d.z * 0.9D);
                    if (this.ag <= 0 && this.ai <= 0) {
                        this.inOpenWater = true;
                    } else {
                        this.inOpenWater = this.inOpenWater && this.d < 10 && this.b(blockposition);
                    }

                    if (flag) {
                        this.d = Math.max(0, this.d - 1);
                        if (this.c) {
                            this.setMot(this.getMot().add(0.0D, -0.1D * (double) this.b.nextFloat() * (double) this.b.nextFloat(), 0.0D));
                        }

                        if (!this.world.isClientSide) {
                            this.a(blockposition);
                        }
                    } else {
                        this.d = Math.min(10, this.d + 1);
                    }
                }
            }

            if (!fluid.a((Tag) TagsFluid.WATER)) {
                this.setMot(this.getMot().add(0.0D, -0.03D, 0.0D));
            }

            this.move(EnumMoveType.SELF, this.getMot());
            this.x();
            if (this.hookState == EntityFishingHook.HookState.FLYING && (this.onGround || this.positionChanged)) {
                this.setMot(Vec3D.ORIGIN);
            }

            double d1 = 0.92D;

            this.setMot(this.getMot().a(0.92D));
            this.af();
        }
    }

    private boolean a(EntityHuman entityhuman) {
        ItemStack itemstack = entityhuman.getItemInMainHand();
        ItemStack itemstack1 = entityhuman.getItemInOffHand();
        boolean flag = itemstack.getItem() == Items.FISHING_ROD;
        boolean flag1 = itemstack1.getItem() == Items.FISHING_ROD;

        if (!entityhuman.dead && entityhuman.isAlive() && (flag || flag1) && this.h(entityhuman) <= 1024.0D) {
            return false;
        } else {
            this.die();
            return true;
        }
    }

    private void m() {
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
        if (!this.world.isClientSide) {
            this.hooked = movingobjectpositionentity.getEntity();
            this.updateHookedEntity();
        }

    }

    @Override
    protected void a(MovingObjectPositionBlock movingobjectpositionblock) {
        super.a(movingobjectpositionblock);
        this.setMot(this.getMot().d().a(movingobjectpositionblock.a((Entity) this)));
    }

    public void updateHookedEntity() {
        this.getDataWatcher().set(EntityFishingHook.HOOKED_ENTITY, this.hooked.getId() + 1);
    }

    private void a(BlockPosition blockposition) {
        WorldServer worldserver = (WorldServer) this.world;
        int i = 1;
        BlockPosition blockposition1 = blockposition.up();

        if (this.random.nextFloat() < 0.25F && this.world.isRainingAt(blockposition1)) {
            ++i;
        }

        if (this.random.nextFloat() < 0.5F && !this.world.e(blockposition1)) {
            --i;
        }

        if (this.ag > 0) {
            --this.ag;
            if (this.ag <= 0) {
                this.waitTime = 0;
                this.ai = 0;
                this.getDataWatcher().set(EntityFishingHook.f, false);
            }
        } else {
            float f;
            float f1;
            float f2;
            double d0;
            double d1;
            double d2;
            IBlockData iblockdata;

            if (this.ai > 0) {
                this.ai -= i;
                if (this.ai > 0) {
                    this.aj = (float) ((double) this.aj + this.random.nextGaussian() * 4.0D);
                    f = this.aj * 0.017453292F;
                    f1 = MathHelper.sin(f);
                    f2 = MathHelper.cos(f);
                    d0 = this.locX() + (double) (f1 * (float) this.ai * 0.1F);
                    d1 = (double) ((float) MathHelper.floor(this.locY()) + 1.0F);
                    d2 = this.locZ() + (double) (f2 * (float) this.ai * 0.1F);
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
                    this.playSound(SoundEffects.ENTITY_FISHING_BOBBER_SPLASH, 0.25F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
                    double d3 = this.locY() + 0.5D;

                    worldserver.a(Particles.BUBBLE, this.locX(), d3, this.locZ(), (int) (1.0F + this.getWidth() * 20.0F), (double) this.getWidth(), 0.0D, (double) this.getWidth(), 0.20000000298023224D);
                    worldserver.a(Particles.FISHING, this.locX(), d3, this.locZ(), (int) (1.0F + this.getWidth() * 20.0F), (double) this.getWidth(), 0.0D, (double) this.getWidth(), 0.20000000298023224D);
                    this.ag = MathHelper.nextInt(this.random, 20, 40);
                    this.getDataWatcher().set(EntityFishingHook.f, true);
                }
            } else if (this.waitTime > 0) {
                this.waitTime -= i;
                f = 0.15F;
                if (this.waitTime < 20) {
                    f = (float) ((double) f + (double) (20 - this.waitTime) * 0.05D);
                } else if (this.waitTime < 40) {
                    f = (float) ((double) f + (double) (40 - this.waitTime) * 0.02D);
                } else if (this.waitTime < 60) {
                    f = (float) ((double) f + (double) (60 - this.waitTime) * 0.01D);
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

                if (this.waitTime <= 0) {
                    this.aj = MathHelper.a(this.random, 0.0F, 360.0F);
                    this.ai = MathHelper.nextInt(this.random, 20, 80);
                }
            } else {
                this.waitTime = MathHelper.nextInt(this.random, 100, 600);
                this.waitTime -= this.lureLevel * 20 * 5;
            }
        }

    }

    private boolean b(BlockPosition blockposition) {
        EntityFishingHook.WaterPosition entityfishinghook_waterposition = EntityFishingHook.WaterPosition.INVALID;

        for (int i = -1; i <= 2; ++i) {
            EntityFishingHook.WaterPosition entityfishinghook_waterposition1 = this.a(blockposition.b(-2, i, -2), blockposition.b(2, i, 2));

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
        IBlockData iblockdata = this.world.getType(blockposition);

        if (!iblockdata.isAir() && !iblockdata.a(Blocks.LILY_PAD)) {
            Fluid fluid = iblockdata.getFluid();

            return fluid.a((Tag) TagsFluid.WATER) && fluid.isSource() && iblockdata.getCollisionShape(this.world, blockposition).isEmpty() ? EntityFishingHook.WaterPosition.INSIDE_WATER : EntityFishingHook.WaterPosition.INVALID;
        } else {
            return EntityFishingHook.WaterPosition.ABOVE_WATER;
        }
    }

    public boolean isInOpenWater() {
        return this.inOpenWater;
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {}

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {}

    public int b(ItemStack itemstack) {
        EntityHuman entityhuman = this.getOwner();

        if (!this.world.isClientSide && entityhuman != null) {
            int i = 0;

            if (this.hooked != null) {
                this.reel();
                CriterionTriggers.D.a((EntityPlayer) entityhuman, itemstack, this, (Collection) Collections.emptyList());
                this.world.broadcastEntityEffect(this, (byte) 31);
                i = this.hooked instanceof EntityItem ? 3 : 5;
            } else if (this.ag > 0) {
                LootTableInfo.Builder loottableinfo_builder = (new LootTableInfo.Builder((WorldServer) this.world)).set(LootContextParameters.ORIGIN, this.getPositionVector()).set(LootContextParameters.TOOL, itemstack).set(LootContextParameters.THIS_ENTITY, this).a(this.random).a((float) this.an + entityhuman.eU());
                LootTable loottable = this.world.getMinecraftServer().getLootTableRegistry().getLootTable(LootTables.ag);
                List<ItemStack> list = loottable.populateLoot(loottableinfo_builder.build(LootContextParameterSets.FISHING));

                CriterionTriggers.D.a((EntityPlayer) entityhuman, itemstack, this, (Collection) list);
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    ItemStack itemstack1 = (ItemStack) iterator.next();
                    EntityItem entityitem = new EntityItem(this.world, this.locX(), this.locY(), this.locZ(), itemstack1);
                    double d0 = entityhuman.locX() - this.locX();
                    double d1 = entityhuman.locY() - this.locY();
                    double d2 = entityhuman.locZ() - this.locZ();
                    double d3 = 0.1D;

                    entityitem.setMot(d0 * 0.1D, d1 * 0.1D + Math.sqrt(Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2)) * 0.08D, d2 * 0.1D);
                    this.world.addEntity(entityitem);
                    entityhuman.world.addEntity(new EntityExperienceOrb(entityhuman.world, entityhuman.locX(), entityhuman.locY() + 0.5D, entityhuman.locZ() + 0.5D, this.random.nextInt(6) + 1));
                    if (itemstack1.getItem().a((Tag) TagsItem.FISHES)) {
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

    public void reel() {
        Entity entity = this.getShooter();

        if (entity != null) {
            Vec3D vec3d = (new Vec3D(entity.locX() - this.locX(), entity.locY() - this.locY(), entity.locZ() - this.locZ())).a(0.1D);

            this.hooked.setMot(this.hooked.getMot().e(vec3d));
        }
    }

    @Override
    protected boolean playStepSound() {
        return false;
    }

    @Override
    public void die() {
        super.die();
        EntityHuman entityhuman = this.getOwner();

        if (entityhuman != null) {
            entityhuman.hookedFish = null;
        }

    }

    @Nullable
    public EntityHuman getOwner() {
        Entity entity = this.getShooter();

        return entity instanceof EntityHuman ? (EntityHuman) entity : null;
    }

    @Nullable
    public Entity getHooked() {
        return this.hooked;
    }

    @Override
    public boolean canPortal() {
        return false;
    }

    @Override
    public Packet<?> P() {
        Entity entity = this.getShooter();

        return new PacketPlayOutSpawnEntity(this, entity == null ? this.getId() : entity.getId());
    }

    static enum WaterPosition {

        ABOVE_WATER, INSIDE_WATER, INVALID;

        private WaterPosition() {}
    }

    public static enum HookState {

        FLYING, HOOKED_IN_ENTITY, BOBBING;

        private HookState() {}
    }
}
