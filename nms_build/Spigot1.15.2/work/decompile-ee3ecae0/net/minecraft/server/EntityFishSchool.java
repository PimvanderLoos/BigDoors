package net.minecraft.server;

import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public abstract class EntityFishSchool extends EntityFish {

    private EntityFishSchool b;
    private int c = 1;

    public EntityFishSchool(EntityTypes<? extends EntityFishSchool> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected void initPathfinder() {
        super.initPathfinder();
        this.goalSelector.a(5, new PathfinderGoalFishSchool(this));
    }

    @Override
    public int getMaxSpawnGroup() {
        return this.es();
    }

    public int es() {
        return super.getMaxSpawnGroup();
    }

    @Override
    protected boolean eq() {
        return !this.et();
    }

    public boolean et() {
        return this.b != null && this.b.isAlive();
    }

    public EntityFishSchool a(EntityFishSchool entityfishschool) {
        this.b = entityfishschool;
        entityfishschool.ez();
        return entityfishschool;
    }

    public void eu() {
        this.b.eA();
        this.b = null;
    }

    private void ez() {
        ++this.c;
    }

    private void eA() {
        --this.c;
    }

    public boolean ev() {
        return this.ew() && this.c < this.es();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.ew() && this.world.random.nextInt(200) == 1) {
            List<EntityFish> list = this.world.a(this.getClass(), this.getBoundingBox().grow(8.0D, 8.0D, 8.0D));

            if (list.size() <= 1) {
                this.c = 1;
            }
        }

    }

    public boolean ew() {
        return this.c > 1;
    }

    public boolean ex() {
        return this.h((Entity) this.b) <= 121.0D;
    }

    public void ey() {
        if (this.et()) {
            this.getNavigation().a((Entity) this.b, 1.0D);
        }

    }

    public void a(Stream<EntityFishSchool> stream) {
        stream.limit((long) (this.es() - this.c)).filter((entityfishschool) -> {
            return entityfishschool != this;
        }).forEach((entityfishschool) -> {
            entityfishschool.a(this);
        });
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(GeneratorAccess generatoraccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        super.prepare(generatoraccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) groupdataentity, nbttagcompound);
        if (groupdataentity == null) {
            groupdataentity = new EntityFishSchool.a(this);
        } else {
            this.a(((EntityFishSchool.a) groupdataentity).a);
        }

        return (GroupDataEntity) groupdataentity;
    }

    public static class a implements GroupDataEntity {

        public final EntityFishSchool a;

        public a(EntityFishSchool entityfishschool) {
            this.a = entityfishschool;
        }
    }
}
