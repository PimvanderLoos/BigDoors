package net.minecraft.world.entity.animal.horse;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalPanic;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalTarget;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.npc.EntityVillagerTrader;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;

public class EntityLlamaTrader extends EntityLlama {

    private int despawnDelay = 47999;

    public EntityLlamaTrader(EntityTypes<? extends EntityLlamaTrader> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    public boolean ge() {
        return true;
    }

    @Override
    protected EntityLlama gj() {
        return (EntityLlama) EntityTypes.TRADER_LLAMA.a(this.level);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setInt("DespawnDelay", this.despawnDelay);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("DespawnDelay", 99)) {
            this.despawnDelay = nbttagcompound.getInt("DespawnDelay");
        }

    }

    @Override
    protected void initPathfinder() {
        super.initPathfinder();
        this.goalSelector.a(1, new PathfinderGoalPanic(this, 2.0D));
        this.targetSelector.a(1, new EntityLlamaTrader.a(this));
    }

    public void y(int i) {
        this.despawnDelay = i;
    }

    @Override
    protected void h(EntityHuman entityhuman) {
        Entity entity = this.getLeashHolder();

        if (!(entity instanceof EntityVillagerTrader)) {
            super.h(entityhuman);
        }
    }

    @Override
    public void movementTick() {
        super.movementTick();
        if (!this.level.isClientSide) {
            this.go();
        }

    }

    private void go() {
        if (this.gp()) {
            this.despawnDelay = this.gq() ? ((EntityVillagerTrader) this.getLeashHolder()).getDespawnDelay() - 1 : this.despawnDelay - 1;
            if (this.despawnDelay <= 0) {
                this.unleash(true, false);
                this.die();
            }

        }
    }

    private boolean gp() {
        return !this.isTamed() && !this.gr() && !this.hasSinglePlayerPassenger();
    }

    private boolean gq() {
        return this.getLeashHolder() instanceof EntityVillagerTrader;
    }

    private boolean gr() {
        return this.isLeashed() && !this.gq();
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        if (enummobspawn == EnumMobSpawn.EVENT) {
            this.setAgeRaw(0);
        }

        if (groupdataentity == null) {
            groupdataentity = new EntityAgeable.a(false);
        }

        return super.prepare(worldaccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) groupdataentity, nbttagcompound);
    }

    protected static class a extends PathfinderGoalTarget {

        private final EntityLlama llama;
        private EntityLiving ownerLastHurtBy;
        private int timestamp;

        public a(EntityLlama entityllama) {
            super(entityllama, false);
            this.llama = entityllama;
            this.a(EnumSet.of(PathfinderGoal.Type.TARGET));
        }

        @Override
        public boolean a() {
            if (!this.llama.isLeashed()) {
                return false;
            } else {
                Entity entity = this.llama.getLeashHolder();

                if (!(entity instanceof EntityVillagerTrader)) {
                    return false;
                } else {
                    EntityVillagerTrader entityvillagertrader = (EntityVillagerTrader) entity;

                    this.ownerLastHurtBy = entityvillagertrader.getLastDamager();
                    int i = entityvillagertrader.dH();

                    return i != this.timestamp && this.a(this.ownerLastHurtBy, PathfinderTargetCondition.DEFAULT);
                }
            }
        }

        @Override
        public void c() {
            this.mob.setGoalTarget(this.ownerLastHurtBy);
            Entity entity = this.llama.getLeashHolder();

            if (entity instanceof EntityVillagerTrader) {
                this.timestamp = ((EntityVillagerTrader) entity).dH();
            }

            super.c();
        }
    }
}
