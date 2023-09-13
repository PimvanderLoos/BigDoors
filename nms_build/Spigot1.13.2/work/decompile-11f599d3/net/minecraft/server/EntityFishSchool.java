package net.minecraft.server;

import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public abstract class EntityFishSchool extends EntityFish {

    private EntityFishSchool a;
    private int b = 1;

    public EntityFishSchool(EntityTypes<?> entitytypes, World world) {
        super(entitytypes, world);
    }

    protected void n() {
        super.n();
        this.goalSelector.a(5, new PathfinderGoalFishSchool(this));
    }

    public int dg() {
        return this.dA();
    }

    public int dA() {
        return super.dg();
    }

    protected boolean dy() {
        return !this.dB();
    }

    public boolean dB() {
        return this.a != null && this.a.isAlive();
    }

    public EntityFishSchool a(EntityFishSchool entityfishschool) {
        this.a = entityfishschool;
        entityfishschool.dH();
        return entityfishschool;
    }

    public void dC() {
        this.a.dI();
        this.a = null;
    }

    private void dH() {
        ++this.b;
    }

    private void dI() {
        --this.b;
    }

    public boolean dD() {
        return this.dE() && this.b < this.dA();
    }

    public void tick() {
        super.tick();
        if (this.dE() && this.world.random.nextInt(200) == 1) {
            List<EntityFish> list = this.world.a(this.getClass(), this.getBoundingBox().grow(8.0D, 8.0D, 8.0D));

            if (list.size() <= 1) {
                this.b = 1;
            }
        }

    }

    public boolean dE() {
        return this.b > 1;
    }

    public boolean dF() {
        return this.h(this.a) <= 121.0D;
    }

    public void dG() {
        if (this.dB()) {
            this.getNavigation().a((Entity) this.a, 1.0D);
        }

    }

    public void a(Stream<EntityFishSchool> stream) {
        stream.limit((long) (this.dA() - this.b)).filter((entityfishschool) -> {
            return entityfishschool != this;
        }).forEach((entityfishschool) -> {
            entityfishschool.a(this);
        });
    }

    @Nullable
    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        super.prepare(difficultydamagescaler, (GroupDataEntity) groupdataentity, nbttagcompound);
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
