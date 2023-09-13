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

    private EntityFishSchool leader;
    private int schoolSize = 1;

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
        return this.fy();
    }

    public int fy() {
        return super.getMaxSpawnGroup();
    }

    @Override
    protected boolean fw() {
        return !this.fz();
    }

    public boolean fz() {
        return this.leader != null && this.leader.isAlive();
    }

    public EntityFishSchool a(EntityFishSchool entityfishschool) {
        this.leader = entityfishschool;
        entityfishschool.fF();
        return entityfishschool;
    }

    public void fA() {
        this.leader.fG();
        this.leader = null;
    }

    private void fF() {
        ++this.schoolSize;
    }

    private void fG() {
        --this.schoolSize;
    }

    public boolean fB() {
        return this.fC() && this.schoolSize < this.fy();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.fC() && this.level.random.nextInt(200) == 1) {
            List<? extends EntityFish> list = this.level.a(this.getClass(), this.getBoundingBox().grow(8.0D, 8.0D, 8.0D));

            if (list.size() <= 1) {
                this.schoolSize = 1;
            }
        }

    }

    public boolean fC() {
        return this.schoolSize > 1;
    }

    public boolean fD() {
        return this.f((Entity) this.leader) <= 121.0D;
    }

    public void fE() {
        if (this.fz()) {
            this.getNavigation().a((Entity) this.leader, 1.0D);
        }

    }

    public void a(Stream<? extends EntityFishSchool> stream) {
        stream.limit((long) (this.fy() - this.schoolSize)).filter((entityfishschool) -> {
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
            this.a(((EntityFishSchool.a) groupdataentity).leader);
        }

        return (GroupDataEntity) groupdataentity;
    }

    public static class a implements GroupDataEntity {

        public final EntityFishSchool leader;

        public a(EntityFishSchool entityfishschool) {
            this.leader = entityfishschool;
        }
    }
}
