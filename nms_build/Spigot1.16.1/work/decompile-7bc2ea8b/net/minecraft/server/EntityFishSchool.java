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
        return this.eO();
    }

    public int eO() {
        return super.getMaxSpawnGroup();
    }

    @Override
    protected boolean eM() {
        return !this.eP();
    }

    public boolean eP() {
        return this.b != null && this.b.isAlive();
    }

    public EntityFishSchool a(EntityFishSchool entityfishschool) {
        this.b = entityfishschool;
        entityfishschool.eV();
        return entityfishschool;
    }

    public void eQ() {
        this.b.eW();
        this.b = null;
    }

    private void eV() {
        ++this.c;
    }

    private void eW() {
        --this.c;
    }

    public boolean eR() {
        return this.eS() && this.c < this.eO();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.eS() && this.world.random.nextInt(200) == 1) {
            List<EntityFish> list = this.world.a(this.getClass(), this.getBoundingBox().grow(8.0D, 8.0D, 8.0D));

            if (list.size() <= 1) {
                this.c = 1;
            }
        }

    }

    public boolean eS() {
        return this.c > 1;
    }

    public boolean eT() {
        return this.h((Entity) this.b) <= 121.0D;
    }

    public void eU() {
        if (this.eP()) {
            this.getNavigation().a((Entity) this.b, 1.0D);
        }

    }

    public void a(Stream<EntityFishSchool> stream) {
        stream.limit((long) (this.eO() - this.c)).filter((entityfishschool) -> {
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
