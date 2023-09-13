package net.minecraft.server;

import java.util.EnumSet;
import javax.annotation.Nullable;

public class EntityLlamaTrader extends EntityLlama {

    private int bF = 47999;

    public EntityLlamaTrader(EntityTypes<? extends EntityLlamaTrader> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected EntityLlama fa() {
        return (EntityLlama) EntityTypes.TRADER_LLAMA.a(this.world);
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("DespawnDelay", this.bF);
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("DespawnDelay", 99)) {
            this.bF = nbttagcompound.getInt("DespawnDelay");
        }

    }

    @Override
    protected void initPathfinder() {
        super.initPathfinder();
        this.goalSelector.a(1, new PathfinderGoalPanic(this, 2.0D));
        this.targetSelector.a(1, new EntityLlamaTrader.a(this));
    }

    @Override
    protected void g(EntityHuman entityhuman) {
        Entity entity = this.getLeashHolder();

        if (!(entity instanceof EntityVillagerTrader)) {
            super.g(entityhuman);
        }
    }

    @Override
    public void movementTick() {
        super.movementTick();
        if (!this.world.isClientSide) {
            this.ff();
        }

    }

    private void ff() {
        if (this.fg()) {
            this.bF = this.fh() ? ((EntityVillagerTrader) this.getLeashHolder()).eA() - 1 : this.bF - 1;
            if (this.bF <= 0) {
                this.unleash(true, false);
                this.die();
            }

        }
    }

    private boolean fg() {
        return !this.isTamed() && !this.fi() && !this.hasSinglePlayerPassenger();
    }

    private boolean fh() {
        return this.getLeashHolder() instanceof EntityVillagerTrader;
    }

    private boolean fi() {
        return this.isLeashed() && !this.fh();
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(GeneratorAccess generatoraccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        if (enummobspawn == EnumMobSpawn.EVENT) {
            this.setAgeRaw(0);
        }

        if (groupdataentity == null) {
            groupdataentity = new EntityAgeable.a();
            ((EntityAgeable.a) groupdataentity).a(false);
        }

        return super.prepare(generatoraccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) groupdataentity, nbttagcompound);
    }

    public class a extends PathfinderGoalTarget {

        private final EntityLlama b;
        private EntityLiving c;
        private int d;

        public a(EntityLlama entityllama) {
            super(entityllama, false);
            this.b = entityllama;
            this.a(EnumSet.of(PathfinderGoal.Type.TARGET));
        }

        @Override
        public boolean a() {
            if (!this.b.isLeashed()) {
                return false;
            } else {
                Entity entity = this.b.getLeashHolder();

                if (!(entity instanceof EntityVillagerTrader)) {
                    return false;
                } else {
                    EntityVillagerTrader entityvillagertrader = (EntityVillagerTrader) entity;

                    this.c = entityvillagertrader.getLastDamager();
                    int i = entityvillagertrader.cI();

                    return i != this.d && this.a(this.c, PathfinderTargetCondition.a);
                }
            }
        }

        @Override
        public void c() {
            this.e.setGoalTarget(this.c);
            Entity entity = this.b.getLeashHolder();

            if (entity instanceof EntityVillagerTrader) {
                this.d = ((EntityVillagerTrader) entity).cI();
            }

            super.c();
        }
    }
}
