package net.minecraft.world.entity.raid;

import com.google.common.collect.Lists;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRaid;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.village.poi.VillagePlace;
import net.minecraft.world.entity.ai.village.poi.VillagePlaceType;
import net.minecraft.world.entity.animal.EntityWolf;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.monster.EntityIllagerAbstract;
import net.minecraft.world.entity.monster.EntityMonsterPatrolling;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.phys.Vec3D;

public abstract class EntityRaider extends EntityMonsterPatrolling {

    protected static final DataWatcherObject<Boolean> IS_CELEBRATING = DataWatcher.a(EntityRaider.class, DataWatcherRegistry.BOOLEAN);
    static final Predicate<EntityItem> ALLOWED_ITEMS = (entityitem) -> {
        return !entityitem.q() && entityitem.isAlive() && ItemStack.matches(entityitem.getItemStack(), Raid.s());
    };
    @Nullable
    protected Raid raid;
    private int wave;
    private boolean canJoinRaid;
    private int ticksOutsideRaid;

    protected EntityRaider(EntityTypes<? extends EntityRaider> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected void initPathfinder() {
        super.initPathfinder();
        this.goalSelector.a(1, new EntityRaider.b<>(this));
        this.goalSelector.a(3, new PathfinderGoalRaid<>(this));
        this.goalSelector.a(4, new EntityRaider.d(this, 1.0499999523162842D, 1));
        this.goalSelector.a(5, new EntityRaider.c(this));
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityRaider.IS_CELEBRATING, false);
    }

    public abstract void a(int i, boolean flag);

    public boolean isCanJoinRaid() {
        return this.canJoinRaid;
    }

    public void setCanJoinRaid(boolean flag) {
        this.canJoinRaid = flag;
    }

    @Override
    public void movementTick() {
        if (this.level instanceof WorldServer && this.isAlive()) {
            Raid raid = this.fK();

            if (this.isCanJoinRaid()) {
                if (raid == null) {
                    if (this.level.getTime() % 20L == 0L) {
                        Raid raid1 = ((WorldServer) this.level).c(this.getChunkCoordinates());

                        if (raid1 != null && PersistentRaid.a(this, raid1)) {
                            raid1.a(raid1.getGroupsSpawned(), this, (BlockPosition) null, true);
                        }
                    }
                } else {
                    EntityLiving entityliving = this.getGoalTarget();

                    if (entityliving != null && (entityliving.getEntityType() == EntityTypes.PLAYER || entityliving.getEntityType() == EntityTypes.IRON_GOLEM)) {
                        this.noActionTime = 0;
                    }
                }
            }
        }

        super.movementTick();
    }

    @Override
    protected void fA() {
        this.noActionTime += 2;
    }

    @Override
    public void die(DamageSource damagesource) {
        if (this.level instanceof WorldServer) {
            Entity entity = damagesource.getEntity();
            Raid raid = this.fK();

            if (raid != null) {
                if (this.isPatrolLeader()) {
                    raid.c(this.fM());
                }

                if (entity != null && entity.getEntityType() == EntityTypes.PLAYER) {
                    raid.a(entity);
                }

                raid.a(this, false);
            }

            if (this.isPatrolLeader() && raid == null && ((WorldServer) this.level).c(this.getChunkCoordinates()) == null) {
                ItemStack itemstack = this.getEquipment(EnumItemSlot.HEAD);
                EntityHuman entityhuman = null;

                if (entity instanceof EntityHuman) {
                    entityhuman = (EntityHuman) entity;
                } else if (entity instanceof EntityWolf) {
                    EntityWolf entitywolf = (EntityWolf) entity;
                    EntityLiving entityliving = entitywolf.getOwner();

                    if (entitywolf.isTamed() && entityliving instanceof EntityHuman) {
                        entityhuman = (EntityHuman) entityliving;
                    }
                }

                if (!itemstack.isEmpty() && ItemStack.matches(itemstack, Raid.s()) && entityhuman != null) {
                    MobEffect mobeffect = entityhuman.getEffect(MobEffects.BAD_OMEN);
                    byte b0 = 1;
                    int i;

                    if (mobeffect != null) {
                        i = b0 + mobeffect.getAmplifier();
                        entityhuman.c(MobEffects.BAD_OMEN);
                    } else {
                        i = b0 - 1;
                    }

                    i = MathHelper.clamp(i, 0, 4);
                    MobEffect mobeffect1 = new MobEffect(MobEffects.BAD_OMEN, 120000, i, false, false, true);

                    if (!this.level.getGameRules().getBoolean(GameRules.RULE_DISABLE_RAIDS)) {
                        entityhuman.addEffect(mobeffect1);
                    }
                }
            }
        }

        super.die(damagesource);
    }

    @Override
    public boolean fD() {
        return !this.fL();
    }

    public void a(@Nullable Raid raid) {
        this.raid = raid;
    }

    @Nullable
    public Raid fK() {
        return this.raid;
    }

    public boolean fL() {
        return this.fK() != null && this.fK().v();
    }

    public void a(int i) {
        this.wave = i;
    }

    public int fM() {
        return this.wave;
    }

    public boolean fN() {
        return (Boolean) this.entityData.get(EntityRaider.IS_CELEBRATING);
    }

    public void z(boolean flag) {
        this.entityData.set(EntityRaider.IS_CELEBRATING, flag);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setInt("Wave", this.wave);
        nbttagcompound.setBoolean("CanJoinRaid", this.canJoinRaid);
        if (this.raid != null) {
            nbttagcompound.setInt("RaidId", this.raid.getId());
        }

    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.wave = nbttagcompound.getInt("Wave");
        this.canJoinRaid = nbttagcompound.getBoolean("CanJoinRaid");
        if (nbttagcompound.hasKeyOfType("RaidId", 3)) {
            if (this.level instanceof WorldServer) {
                this.raid = ((WorldServer) this.level).getPersistentRaid().a(nbttagcompound.getInt("RaidId"));
            }

            if (this.raid != null) {
                this.raid.a(this.wave, this, false);
                if (this.isPatrolLeader()) {
                    this.raid.a(this.wave, this);
                }
            }
        }

    }

    @Override
    protected void b(EntityItem entityitem) {
        ItemStack itemstack = entityitem.getItemStack();
        boolean flag = this.fL() && this.fK().b(this.fM()) != null;

        if (this.fL() && !flag && ItemStack.matches(itemstack, Raid.s())) {
            EnumItemSlot enumitemslot = EnumItemSlot.HEAD;
            ItemStack itemstack1 = this.getEquipment(enumitemslot);
            double d0 = (double) this.e(enumitemslot);

            if (!itemstack1.isEmpty() && (double) Math.max(this.random.nextFloat() - 0.1F, 0.0F) < d0) {
                this.b(itemstack1);
            }

            this.a(entityitem);
            this.setSlot(enumitemslot, itemstack);
            this.receive(entityitem, itemstack.getCount());
            entityitem.die();
            this.fK().a(this.fM(), this);
            this.setPatrolLeader(true);
        } else {
            super.b(entityitem);
        }

    }

    @Override
    public boolean isTypeNotPersistent(double d0) {
        return this.fK() == null ? super.isTypeNotPersistent(d0) : false;
    }

    @Override
    public boolean isSpecialPersistence() {
        return super.isSpecialPersistence() || this.fK() != null;
    }

    public int fO() {
        return this.ticksOutsideRaid;
    }

    public void b(int i) {
        this.ticksOutsideRaid = i;
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.fL()) {
            this.fK().updateProgress();
        }

        return super.damageEntity(damagesource, f);
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        this.setCanJoinRaid(this.getEntityType() != EntityTypes.WITCH || enummobspawn != EnumMobSpawn.NATURAL);
        return super.prepare(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    public abstract SoundEffect t();

    public class b<T extends EntityRaider> extends PathfinderGoal {

        private final T mob;

        public b(EntityRaider entityraider) {
            this.mob = entityraider;
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean a() {
            Raid raid = this.mob.fK();

            if (this.mob.fL() && !this.mob.fK().a() && this.mob.fx() && !ItemStack.matches(this.mob.getEquipment(EnumItemSlot.HEAD), Raid.s())) {
                EntityRaider entityraider = raid.b(this.mob.fM());

                if (entityraider == null || !entityraider.isAlive()) {
                    List<EntityItem> list = this.mob.level.a(EntityItem.class, this.mob.getBoundingBox().grow(16.0D, 8.0D, 16.0D), EntityRaider.ALLOWED_ITEMS);

                    if (!list.isEmpty()) {
                        return this.mob.getNavigation().a((Entity) list.get(0), 1.149999976158142D);
                    }
                }

                return false;
            } else {
                return false;
            }
        }

        @Override
        public void e() {
            if (this.mob.getNavigation().h().a((IPosition) this.mob.getPositionVector(), 1.414D)) {
                List<EntityItem> list = this.mob.level.a(EntityItem.class, this.mob.getBoundingBox().grow(4.0D, 4.0D, 4.0D), EntityRaider.ALLOWED_ITEMS);

                if (!list.isEmpty()) {
                    this.mob.b((EntityItem) list.get(0));
                }
            }

        }
    }

    private static class d extends PathfinderGoal {

        private final EntityRaider raider;
        private final double speedModifier;
        private BlockPosition poiPos;
        private final List<BlockPosition> visited = Lists.newArrayList();
        private final int distanceToPoi;
        private boolean stuck;

        public d(EntityRaider entityraider, double d0, int i) {
            this.raider = entityraider;
            this.speedModifier = d0;
            this.distanceToPoi = i;
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean a() {
            this.j();
            return this.g() && this.h() && this.raider.getGoalTarget() == null;
        }

        private boolean g() {
            return this.raider.fL() && !this.raider.fK().a();
        }

        private boolean h() {
            WorldServer worldserver = (WorldServer) this.raider.level;
            BlockPosition blockposition = this.raider.getChunkCoordinates();
            Optional<BlockPosition> optional = worldserver.A().a((villageplacetype) -> {
                return villageplacetype == VillagePlaceType.HOME;
            }, this::a, VillagePlace.Occupancy.ANY, blockposition, 48, this.raider.random);

            if (!optional.isPresent()) {
                return false;
            } else {
                this.poiPos = ((BlockPosition) optional.get()).immutableCopy();
                return true;
            }
        }

        @Override
        public boolean b() {
            return this.raider.getNavigation().m() ? false : this.raider.getGoalTarget() == null && !this.poiPos.a((IPosition) this.raider.getPositionVector(), (double) (this.raider.getWidth() + (float) this.distanceToPoi)) && !this.stuck;
        }

        @Override
        public void d() {
            if (this.poiPos.a((IPosition) this.raider.getPositionVector(), (double) this.distanceToPoi)) {
                this.visited.add(this.poiPos);
            }

        }

        @Override
        public void c() {
            super.c();
            this.raider.o(0);
            this.raider.getNavigation().a((double) this.poiPos.getX(), (double) this.poiPos.getY(), (double) this.poiPos.getZ(), this.speedModifier);
            this.stuck = false;
        }

        @Override
        public void e() {
            if (this.raider.getNavigation().m()) {
                Vec3D vec3d = Vec3D.c((BaseBlockPosition) this.poiPos);
                Vec3D vec3d1 = DefaultRandomPos.a(this.raider, 16, 7, vec3d, 0.3141592741012573D);

                if (vec3d1 == null) {
                    vec3d1 = DefaultRandomPos.a(this.raider, 8, 7, vec3d, 1.5707963705062866D);
                }

                if (vec3d1 == null) {
                    this.stuck = true;
                    return;
                }

                this.raider.getNavigation().a(vec3d1.x, vec3d1.y, vec3d1.z, this.speedModifier);
            }

        }

        private boolean a(BlockPosition blockposition) {
            Iterator iterator = this.visited.iterator();

            BlockPosition blockposition1;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                blockposition1 = (BlockPosition) iterator.next();
            } while (!Objects.equals(blockposition, blockposition1));

            return false;
        }

        private void j() {
            if (this.visited.size() > 2) {
                this.visited.remove(0);
            }

        }
    }

    public class c extends PathfinderGoal {

        private final EntityRaider mob;

        c(EntityRaider entityraider) {
            this.mob = entityraider;
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean a() {
            Raid raid = this.mob.fK();

            return this.mob.isAlive() && this.mob.getGoalTarget() == null && raid != null && raid.isLoss();
        }

        @Override
        public void c() {
            this.mob.z(true);
            super.c();
        }

        @Override
        public void d() {
            this.mob.z(false);
            super.d();
        }

        @Override
        public void e() {
            if (!this.mob.isSilent() && this.mob.random.nextInt(100) == 0) {
                EntityRaider.this.playSound(EntityRaider.this.t(), EntityRaider.this.getSoundVolume(), EntityRaider.this.ep());
            }

            if (!this.mob.isPassenger() && this.mob.random.nextInt(50) == 0) {
                this.mob.getControllerJump().jump();
            }

            super.e();
        }
    }

    protected class a extends PathfinderGoal {

        private final EntityRaider mob;
        private final float hostileRadiusSqr;
        public final PathfinderTargetCondition shoutTargeting = PathfinderTargetCondition.b().a(8.0D).d().e();

        public a(EntityIllagerAbstract entityillagerabstract, float f) {
            this.mob = entityillagerabstract;
            this.hostileRadiusSqr = f * f;
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean a() {
            EntityLiving entityliving = this.mob.getLastDamager();

            return this.mob.fK() == null && this.mob.isPatrolling() && this.mob.getGoalTarget() != null && !this.mob.isAggressive() && (entityliving == null || entityliving.getEntityType() != EntityTypes.PLAYER);
        }

        @Override
        public void c() {
            super.c();
            this.mob.getNavigation().o();
            List<EntityRaider> list = this.mob.level.a(EntityRaider.class, this.shoutTargeting, (EntityLiving) this.mob, this.mob.getBoundingBox().grow(8.0D, 8.0D, 8.0D));
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityRaider entityraider = (EntityRaider) iterator.next();

                entityraider.setGoalTarget(this.mob.getGoalTarget());
            }

        }

        @Override
        public void d() {
            super.d();
            EntityLiving entityliving = this.mob.getGoalTarget();

            if (entityliving != null) {
                List<EntityRaider> list = this.mob.level.a(EntityRaider.class, this.shoutTargeting, (EntityLiving) this.mob, this.mob.getBoundingBox().grow(8.0D, 8.0D, 8.0D));
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    EntityRaider entityraider = (EntityRaider) iterator.next();

                    entityraider.setGoalTarget(entityliving);
                    entityraider.setAggressive(true);
                }

                this.mob.setAggressive(true);
            }

        }

        @Override
        public void e() {
            EntityLiving entityliving = this.mob.getGoalTarget();

            if (entityliving != null) {
                if (this.mob.f((Entity) entityliving) > (double) this.hostileRadiusSqr) {
                    this.mob.getControllerLook().a(entityliving, 30.0F, 30.0F);
                    if (this.mob.random.nextInt(50) == 0) {
                        this.mob.K();
                    }
                } else {
                    this.mob.setAggressive(true);
                }

                super.e();
            }
        }
    }
}
