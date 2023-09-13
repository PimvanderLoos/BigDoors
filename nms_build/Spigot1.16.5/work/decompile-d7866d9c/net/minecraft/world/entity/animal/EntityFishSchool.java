package net.minecraft.world.entity.animal;

import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFishSchool;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;

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
        return this.eN();
    }

    public int eN() {
        return super.getMaxSpawnGroup();
    }

    @Override
    protected boolean eL() {
        return !this.eO();
    }

    public boolean eO() {
        return this.b != null && this.b.isAlive();
    }

    public EntityFishSchool a(EntityFishSchool entityfishschool) {
        this.b = entityfishschool;
        entityfishschool.eU();
        return entityfishschool;
    }

    public void eP() {
        this.b.eV();
        this.b = null;
    }

    private void eU() {
        ++this.c;
    }

    private void eV() {
        --this.c;
    }

    public boolean eQ() {
        return this.eR() && this.c < this.eN();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.eR() && this.world.random.nextInt(200) == 1) {
            List<EntityFish> list = this.world.a(this.getClass(), this.getBoundingBox().grow(8.0D, 8.0D, 8.0D));

            if (list.size() <= 1) {
                this.c = 1;
            }
        }

    }

    public boolean eR() {
        return this.c > 1;
    }

    public boolean eS() {
        return this.h((Entity) this.b) <= 121.0D;
    }

    public void eT() {
        if (this.eO()) {
            this.getNavigation().a((Entity) this.b, 1.0D);
        }

    }

    public void a(Stream<EntityFishSchool> stream) {
        stream.limit((long) (this.eN() - this.c)).filter((entityfishschool) -> {
            return entityfishschool != this;
        }).forEach((entityfishschool) -> {
            entityfishschool.a(this);
        });
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        super.prepare(worldaccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) groupdataentity, nbttagcompound);
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
