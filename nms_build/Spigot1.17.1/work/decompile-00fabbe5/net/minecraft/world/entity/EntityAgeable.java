package net.minecraft.world.entity;

import javax.annotation.Nullable;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;

public abstract class EntityAgeable extends EntityCreature {

    private static final DataWatcherObject<Boolean> DATA_BABY_ID = DataWatcher.a(EntityAgeable.class, DataWatcherRegistry.BOOLEAN);
    public static final int BABY_START_AGE = -24000;
    private static final int FORCED_AGE_PARTICLE_TICKS = 40;
    protected int age;
    protected int forcedAge;
    protected int forcedAgeTimer;

    protected EntityAgeable(EntityTypes<? extends EntityAgeable> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    public GroupDataEntity prepare(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        if (groupdataentity == null) {
            groupdataentity = new EntityAgeable.a(true);
        }

        EntityAgeable.a entityageable_a = (EntityAgeable.a) groupdataentity;

        if (entityageable_a.c() && entityageable_a.a() > 0 && this.random.nextFloat() <= entityageable_a.d()) {
            this.setAgeRaw(-24000);
        }

        entityageable_a.b();
        return super.prepare(worldaccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) groupdataentity, nbttagcompound);
    }

    @Nullable
    public abstract EntityAgeable createChild(WorldServer worldserver, EntityAgeable entityageable);

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityAgeable.DATA_BABY_ID, false);
    }

    public boolean canBreed() {
        return false;
    }

    public int getAge() {
        return this.level.isClientSide ? ((Boolean) this.entityData.get(EntityAgeable.DATA_BABY_ID) ? -1 : 1) : this.age;
    }

    public void setAge(int i, boolean flag) {
        int j = this.getAge();
        int k = j;

        j += i * 20;
        if (j > 0) {
            j = 0;
        }

        int l = j - k;

        this.setAgeRaw(j);
        if (flag) {
            this.forcedAge += l;
            if (this.forcedAgeTimer == 0) {
                this.forcedAgeTimer = 40;
            }
        }

        if (this.getAge() == 0) {
            this.setAgeRaw(this.forcedAge);
        }

    }

    public void setAge(int i) {
        this.setAge(i, false);
    }

    public void setAgeRaw(int i) {
        int j = this.age;

        this.age = i;
        if (j < 0 && i >= 0 || j >= 0 && i < 0) {
            this.entityData.set(EntityAgeable.DATA_BABY_ID, i < 0);
            this.n();
        }

    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setInt("Age", this.getAge());
        nbttagcompound.setInt("ForcedAge", this.forcedAge);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.setAgeRaw(nbttagcompound.getInt("Age"));
        this.forcedAge = nbttagcompound.getInt("ForcedAge");
    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityAgeable.DATA_BABY_ID.equals(datawatcherobject)) {
            this.updateSize();
        }

        super.a(datawatcherobject);
    }

    @Override
    public void movementTick() {
        super.movementTick();
        if (this.level.isClientSide) {
            if (this.forcedAgeTimer > 0) {
                if (this.forcedAgeTimer % 4 == 0) {
                    this.level.addParticle(Particles.HAPPY_VILLAGER, this.d(1.0D), this.da() + 0.5D, this.g(1.0D), 0.0D, 0.0D, 0.0D);
                }

                --this.forcedAgeTimer;
            }
        } else if (this.isAlive()) {
            int i = this.getAge();

            if (i < 0) {
                ++i;
                this.setAgeRaw(i);
            } else if (i > 0) {
                --i;
                this.setAgeRaw(i);
            }
        }

    }

    protected void n() {}

    @Override
    public boolean isBaby() {
        return this.getAge() < 0;
    }

    @Override
    public void setBaby(boolean flag) {
        this.setAgeRaw(flag ? -24000 : 0);
    }

    public static class a implements GroupDataEntity {

        private int groupSize;
        private final boolean shouldSpawnBaby;
        private final float babySpawnChance;

        private a(boolean flag, float f) {
            this.shouldSpawnBaby = flag;
            this.babySpawnChance = f;
        }

        public a(boolean flag) {
            this(flag, 0.05F);
        }

        public a(float f) {
            this(true, f);
        }

        public int a() {
            return this.groupSize;
        }

        public void b() {
            ++this.groupSize;
        }

        public boolean c() {
            return this.shouldSpawnBaby;
        }

        public float d() {
            return this.babySpawnChance;
        }
    }
}
