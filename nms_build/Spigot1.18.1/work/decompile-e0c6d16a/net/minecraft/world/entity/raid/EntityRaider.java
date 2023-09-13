package net.minecraft.world.entity.raid;

import com.google.common.collect.Lists;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
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

    protected static final DataWatcherObject<Boolean> IS_CELEBRATING = DataWatcher.defineId(EntityRaider.class, DataWatcherRegistry.BOOLEAN);
    static final Predicate<EntityItem> ALLOWED_ITEMS = (entityitem) -> {
        return !entityitem.hasPickUpDelay() && entityitem.isAlive() && ItemStack.matches(entityitem.getItem(), Raid.getLeaderBannerInstance());
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
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new EntityRaider.b<>(this));
        this.goalSelector.addGoal(3, new PathfinderGoalRaid<>(this));
        this.goalSelector.addGoal(4, new EntityRaider.d(this, 1.0499999523162842D, 1));
        this.goalSelector.addGoal(5, new EntityRaider.c(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntityRaider.IS_CELEBRATING, false);
    }

    public abstract void applyRaidBuffs(int i, boolean flag);

    public boolean canJoinRaid() {
        return this.canJoinRaid;
    }

    public void setCanJoinRaid(boolean flag) {
        this.canJoinRaid = flag;
    }

    @Override
    public void aiStep() {
        if (this.level instanceof WorldServer && this.isAlive()) {
            Raid raid = this.getCurrentRaid();

            if (this.canJoinRaid()) {
                if (raid == null) {
                    if (this.level.getGameTime() % 20L == 0L) {
                        Raid raid1 = ((WorldServer) this.level).getRaidAt(this.blockPosition());

                        if (raid1 != null && PersistentRaid.canJoinRaid(this, raid1)) {
                            raid1.joinRaid(raid1.getGroupsSpawned(), this, (BlockPosition) null, true);
                        }
                    }
                } else {
                    EntityLiving entityliving = this.getTarget();

                    if (entityliving != null && (entityliving.getType() == EntityTypes.PLAYER || entityliving.getType() == EntityTypes.IRON_GOLEM)) {
                        this.noActionTime = 0;
                    }
                }
            }
        }

        super.aiStep();
    }

    @Override
    protected void updateNoActionTime() {
        this.noActionTime += 2;
    }

    @Override
    public void die(DamageSource damagesource) {
        if (this.level instanceof WorldServer) {
            Entity entity = damagesource.getEntity();
            Raid raid = this.getCurrentRaid();

            if (raid != null) {
                if (this.isPatrolLeader()) {
                    raid.removeLeader(this.getWave());
                }

                if (entity != null && entity.getType() == EntityTypes.PLAYER) {
                    raid.addHeroOfTheVillage(entity);
                }

                raid.removeFromRaid(this, false);
            }

            if (this.isPatrolLeader() && raid == null && ((WorldServer) this.level).getRaidAt(this.blockPosition()) == null) {
                ItemStack itemstack = this.getItemBySlot(EnumItemSlot.HEAD);
                EntityHuman entityhuman = null;

                if (entity instanceof EntityHuman) {
                    entityhuman = (EntityHuman) entity;
                } else if (entity instanceof EntityWolf) {
                    EntityWolf entitywolf = (EntityWolf) entity;
                    EntityLiving entityliving = entitywolf.getOwner();

                    if (entitywolf.isTame() && entityliving instanceof EntityHuman) {
                        entityhuman = (EntityHuman) entityliving;
                    }
                }

                if (!itemstack.isEmpty() && ItemStack.matches(itemstack, Raid.getLeaderBannerInstance()) && entityhuman != null) {
                    MobEffect mobeffect = entityhuman.getEffect(MobEffects.BAD_OMEN);
                    byte b0 = 1;
                    int i;

                    if (mobeffect != null) {
                        i = b0 + mobeffect.getAmplifier();
                        entityhuman.removeEffectNoUpdate(MobEffects.BAD_OMEN);
                    } else {
                        i = b0 - 1;
                    }

                    i = MathHelper.clamp(i, (int) 0, (int) 4);
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
    public boolean canJoinPatrol() {
        return !this.hasActiveRaid();
    }

    public void setCurrentRaid(@Nullable Raid raid) {
        this.raid = raid;
    }

    @Nullable
    public Raid getCurrentRaid() {
        return this.raid;
    }

    public boolean hasActiveRaid() {
        return this.getCurrentRaid() != null && this.getCurrentRaid().isActive();
    }

    public void setWave(int i) {
        this.wave = i;
    }

    public int getWave() {
        return this.wave;
    }

    public boolean isCelebrating() {
        return (Boolean) this.entityData.get(EntityRaider.IS_CELEBRATING);
    }

    public void setCelebrating(boolean flag) {
        this.entityData.set(EntityRaider.IS_CELEBRATING, flag);
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putInt("Wave", this.wave);
        nbttagcompound.putBoolean("CanJoinRaid", this.canJoinRaid);
        if (this.raid != null) {
            nbttagcompound.putInt("RaidId", this.raid.getId());
        }

    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        this.wave = nbttagcompound.getInt("Wave");
        this.canJoinRaid = nbttagcompound.getBoolean("CanJoinRaid");
        if (nbttagcompound.contains("RaidId", 3)) {
            if (this.level instanceof WorldServer) {
                this.raid = ((WorldServer) this.level).getRaids().get(nbttagcompound.getInt("RaidId"));
            }

            if (this.raid != null) {
                this.raid.addWaveMob(this.wave, this, false);
                if (this.isPatrolLeader()) {
                    this.raid.setLeader(this.wave, this);
                }
            }
        }

    }

    @Override
    protected void pickUpItem(EntityItem entityitem) {
        ItemStack itemstack = entityitem.getItem();
        boolean flag = this.hasActiveRaid() && this.getCurrentRaid().getLeader(this.getWave()) != null;

        if (this.hasActiveRaid() && !flag && ItemStack.matches(itemstack, Raid.getLeaderBannerInstance())) {
            EnumItemSlot enumitemslot = EnumItemSlot.HEAD;
            ItemStack itemstack1 = this.getItemBySlot(enumitemslot);
            double d0 = (double) this.getEquipmentDropChance(enumitemslot);

            if (!itemstack1.isEmpty() && (double) Math.max(this.random.nextFloat() - 0.1F, 0.0F) < d0) {
                this.spawnAtLocation(itemstack1);
            }

            this.onItemPickup(entityitem);
            this.setItemSlot(enumitemslot, itemstack);
            this.take(entityitem, itemstack.getCount());
            entityitem.discard();
            this.getCurrentRaid().setLeader(this.getWave(), this);
            this.setPatrolLeader(true);
        } else {
            super.pickUpItem(entityitem);
        }

    }

    @Override
    public boolean removeWhenFarAway(double d0) {
        return this.getCurrentRaid() == null ? super.removeWhenFarAway(d0) : false;
    }

    @Override
    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.getCurrentRaid() != null;
    }

    public int getTicksOutsideRaid() {
        return this.ticksOutsideRaid;
    }

    public void setTicksOutsideRaid(int i) {
        this.ticksOutsideRaid = i;
    }

    @Override
    public boolean hurt(DamageSource damagesource, float f) {
        if (this.hasActiveRaid()) {
            this.getCurrentRaid().updateBossbar();
        }

        return super.hurt(damagesource, f);
    }

    @Nullable
    @Override
    public GroupDataEntity finalizeSpawn(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        this.setCanJoinRaid(this.getType() != EntityTypes.WITCH || enummobspawn != EnumMobSpawn.NATURAL);
        return super.finalizeSpawn(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    public abstract SoundEffect getCelebrateSound();

    public class b<T extends EntityRaider> extends PathfinderGoal {

        private final T mob;

        public b(EntityRaider entityraider) {
            this.mob = entityraider;
            this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean canUse() {
            Raid raid = this.mob.getCurrentRaid();

            if (this.mob.hasActiveRaid() && !this.mob.getCurrentRaid().isOver() && this.mob.canBeLeader() && !ItemStack.matches(this.mob.getItemBySlot(EnumItemSlot.HEAD), Raid.getLeaderBannerInstance())) {
                EntityRaider entityraider = raid.getLeader(this.mob.getWave());

                if (entityraider == null || !entityraider.isAlive()) {
                    List<EntityItem> list = this.mob.level.getEntitiesOfClass(EntityItem.class, this.mob.getBoundingBox().inflate(16.0D, 8.0D, 16.0D), EntityRaider.ALLOWED_ITEMS);

                    if (!list.isEmpty()) {
                        return this.mob.getNavigation().moveTo((Entity) list.get(0), 1.149999976158142D);
                    }
                }

                return false;
            } else {
                return false;
            }
        }

        @Override
        public void tick() {
            if (this.mob.getNavigation().getTargetPos().closerThan((IPosition) this.mob.position(), 1.414D)) {
                List<EntityItem> list = this.mob.level.getEntitiesOfClass(EntityItem.class, this.mob.getBoundingBox().inflate(4.0D, 4.0D, 4.0D), EntityRaider.ALLOWED_ITEMS);

                if (!list.isEmpty()) {
                    this.mob.pickUpItem((EntityItem) list.get(0));
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
            this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean canUse() {
            this.updateVisited();
            return this.isValidRaid() && this.hasSuitablePoi() && this.raider.getTarget() == null;
        }

        private boolean isValidRaid() {
            return this.raider.hasActiveRaid() && !this.raider.getCurrentRaid().isOver();
        }

        private boolean hasSuitablePoi() {
            WorldServer worldserver = (WorldServer) this.raider.level;
            BlockPosition blockposition = this.raider.blockPosition();
            Optional<BlockPosition> optional = worldserver.getPoiManager().getRandom((villageplacetype) -> {
                return villageplacetype == VillagePlaceType.HOME;
            }, this::hasNotVisited, VillagePlace.Occupancy.ANY, blockposition, 48, this.raider.random);

            if (!optional.isPresent()) {
                return false;
            } else {
                this.poiPos = ((BlockPosition) optional.get()).immutable();
                return true;
            }
        }

        @Override
        public boolean canContinueToUse() {
            return this.raider.getNavigation().isDone() ? false : this.raider.getTarget() == null && !this.poiPos.closerThan((IPosition) this.raider.position(), (double) (this.raider.getBbWidth() + (float) this.distanceToPoi)) && !this.stuck;
        }

        @Override
        public void stop() {
            if (this.poiPos.closerThan((IPosition) this.raider.position(), (double) this.distanceToPoi)) {
                this.visited.add(this.poiPos);
            }

        }

        @Override
        public void start() {
            super.start();
            this.raider.setNoActionTime(0);
            this.raider.getNavigation().moveTo((double) this.poiPos.getX(), (double) this.poiPos.getY(), (double) this.poiPos.getZ(), this.speedModifier);
            this.stuck = false;
        }

        @Override
        public void tick() {
            if (this.raider.getNavigation().isDone()) {
                Vec3D vec3d = Vec3D.atBottomCenterOf(this.poiPos);
                Vec3D vec3d1 = DefaultRandomPos.getPosTowards(this.raider, 16, 7, vec3d, 0.3141592741012573D);

                if (vec3d1 == null) {
                    vec3d1 = DefaultRandomPos.getPosTowards(this.raider, 8, 7, vec3d, 1.5707963705062866D);
                }

                if (vec3d1 == null) {
                    this.stuck = true;
                    return;
                }

                this.raider.getNavigation().moveTo(vec3d1.x, vec3d1.y, vec3d1.z, this.speedModifier);
            }

        }

        private boolean hasNotVisited(BlockPosition blockposition) {
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

        private void updateVisited() {
            if (this.visited.size() > 2) {
                this.visited.remove(0);
            }

        }
    }

    public class c extends PathfinderGoal {

        private final EntityRaider mob;

        c(EntityRaider entityraider) {
            this.mob = entityraider;
            this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean canUse() {
            Raid raid = this.mob.getCurrentRaid();

            return this.mob.isAlive() && this.mob.getTarget() == null && raid != null && raid.isLoss();
        }

        @Override
        public void start() {
            this.mob.setCelebrating(true);
            super.start();
        }

        @Override
        public void stop() {
            this.mob.setCelebrating(false);
            super.stop();
        }

        @Override
        public void tick() {
            if (!this.mob.isSilent() && this.mob.random.nextInt(this.adjustedTickDelay(100)) == 0) {
                EntityRaider.this.playSound(EntityRaider.this.getCelebrateSound(), EntityRaider.this.getSoundVolume(), EntityRaider.this.getVoicePitch());
            }

            if (!this.mob.isPassenger() && this.mob.random.nextInt(this.adjustedTickDelay(50)) == 0) {
                this.mob.getJumpControl().jump();
            }

            super.tick();
        }
    }

    protected class a extends PathfinderGoal {

        private final EntityRaider mob;
        private final float hostileRadiusSqr;
        public final PathfinderTargetCondition shoutTargeting = PathfinderTargetCondition.forNonCombat().range(8.0D).ignoreLineOfSight().ignoreInvisibilityTesting();

        public a(EntityIllagerAbstract entityillagerabstract, float f) {
            this.mob = entityillagerabstract;
            this.hostileRadiusSqr = f * f;
            this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean canUse() {
            EntityLiving entityliving = this.mob.getLastHurtByMob();

            return this.mob.getCurrentRaid() == null && this.mob.isPatrolling() && this.mob.getTarget() != null && !this.mob.isAggressive() && (entityliving == null || entityliving.getType() != EntityTypes.PLAYER);
        }

        @Override
        public void start() {
            super.start();
            this.mob.getNavigation().stop();
            List<EntityRaider> list = this.mob.level.getNearbyEntities(EntityRaider.class, this.shoutTargeting, this.mob, this.mob.getBoundingBox().inflate(8.0D, 8.0D, 8.0D));
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityRaider entityraider = (EntityRaider) iterator.next();

                entityraider.setTarget(this.mob.getTarget());
            }

        }

        @Override
        public void stop() {
            super.stop();
            EntityLiving entityliving = this.mob.getTarget();

            if (entityliving != null) {
                List<EntityRaider> list = this.mob.level.getNearbyEntities(EntityRaider.class, this.shoutTargeting, this.mob, this.mob.getBoundingBox().inflate(8.0D, 8.0D, 8.0D));
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    EntityRaider entityraider = (EntityRaider) iterator.next();

                    entityraider.setTarget(entityliving);
                    entityraider.setAggressive(true);
                }

                this.mob.setAggressive(true);
            }

        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            EntityLiving entityliving = this.mob.getTarget();

            if (entityliving != null) {
                if (this.mob.distanceToSqr((Entity) entityliving) > (double) this.hostileRadiusSqr) {
                    this.mob.getLookControl().setLookAt(entityliving, 30.0F, 30.0F);
                    if (this.mob.random.nextInt(50) == 0) {
                        this.mob.playAmbientSound();
                    }
                } else {
                    this.mob.setAggressive(true);
                }

                super.tick();
            }
        }
    }
}
