package net.minecraft.server;

import com.google.common.base.Predicate;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class EntityPolarBear extends EntityAnimal {

    private static final DataWatcherObject<Boolean> bw = DataWatcher.a(EntityPolarBear.class, DataWatcherRegistry.h);
    private float bx;
    private float by;
    private int bA;

    public EntityPolarBear(World world) {
        super(world);
        this.setSize(1.3F, 1.4F);
    }

    public EntityAgeable createChild(EntityAgeable entityageable) {
        return new EntityPolarBear(this.world);
    }

    public boolean e(ItemStack itemstack) {
        return false;
    }

    protected void r() {
        super.r();
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new EntityPolarBear.d());
        this.goalSelector.a(1, new EntityPolarBear.e());
        this.goalSelector.a(4, new PathfinderGoalFollowParent(this, 1.25D));
        this.goalSelector.a(5, new PathfinderGoalRandomStroll(this, 1.0D));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new EntityPolarBear.c());
        this.targetSelector.a(2, new EntityPolarBear.a());
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(30.0D);
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(20.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.25D);
        this.getAttributeMap().b(GenericAttributes.ATTACK_DAMAGE);
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(6.0D);
    }

    protected SoundEffect G() {
        return this.isBaby() ? SoundEffects.eM : SoundEffects.eL;
    }

    protected SoundEffect bW() {
        return SoundEffects.eO;
    }

    protected SoundEffect bX() {
        return SoundEffects.eN;
    }

    protected void a(BlockPosition blockposition, Block block) {
        this.a(SoundEffects.eP, 0.15F, 1.0F);
    }

    protected void dh() {
        if (this.bA <= 0) {
            this.a(SoundEffects.eQ, 1.0F, 1.0F);
            this.bA = 40;
        }

    }

    @Nullable
    protected MinecraftKey J() {
        return LootTables.F;
    }

    protected void i() {
        super.i();
        this.datawatcher.register(EntityPolarBear.bw, Boolean.valueOf(false));
    }

    public void A_() {
        super.A_();
        if (this.world.isClientSide) {
            this.bx = this.by;
            if (this.di()) {
                this.by = MathHelper.a(this.by + 1.0F, 0.0F, 6.0F);
            } else {
                this.by = MathHelper.a(this.by - 1.0F, 0.0F, 6.0F);
            }
        }

        if (this.bA > 0) {
            --this.bA;
        }

    }

    public boolean B(Entity entity) {
        boolean flag = entity.damageEntity(DamageSource.mobAttack(this), (float) ((int) this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue()));

        if (flag) {
            this.a((EntityLiving) this, entity);
        }

        return flag;
    }

    public boolean di() {
        return ((Boolean) this.datawatcher.get(EntityPolarBear.bw)).booleanValue();
    }

    public void p(boolean flag) {
        this.datawatcher.set(EntityPolarBear.bw, Boolean.valueOf(flag));
    }

    protected float cp() {
        return 0.98F;
    }

    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, GroupDataEntity groupdataentity) {
        if (groupdataentity instanceof EntityPolarBear.b) {
            if (((EntityPolarBear.b) groupdataentity).a) {
                this.setAgeRaw(-24000);
            }
        } else {
            EntityPolarBear.b entitypolarbear_b = new EntityPolarBear.b(null);

            entitypolarbear_b.a = true;
            groupdataentity = entitypolarbear_b;
        }

        return (GroupDataEntity) groupdataentity;
    }

    class e extends PathfinderGoalPanic {

        public e() {
            super(EntityPolarBear.this, 2.0D);
        }

        public boolean a() {
            return !EntityPolarBear.this.isBaby() && !EntityPolarBear.this.isBurning() ? false : super.a();
        }
    }

    class d extends PathfinderGoalMeleeAttack {

        public d() {
            super(EntityPolarBear.this, 1.25D, true);
        }

        protected void a(EntityLiving entityliving, double d0) {
            double d1 = this.a(entityliving);

            if (d0 <= d1 && this.c <= 0) {
                this.c = 20;
                this.b.B(entityliving);
                EntityPolarBear.this.p(false);
            } else if (d0 <= d1 * 2.0D) {
                if (this.c <= 0) {
                    EntityPolarBear.this.p(false);
                    this.c = 20;
                }

                if (this.c <= 10) {
                    EntityPolarBear.this.p(true);
                    EntityPolarBear.this.dh();
                }
            } else {
                this.c = 20;
                EntityPolarBear.this.p(false);
            }

        }

        public void d() {
            EntityPolarBear.this.p(false);
            super.d();
        }

        protected double a(EntityLiving entityliving) {
            return (double) (4.0F + entityliving.width);
        }
    }

    class a extends PathfinderGoalNearestAttackableTarget<EntityHuman> {

        public a() {
            super(EntityPolarBear.this, EntityHuman.class, 20, true, true, (Predicate) null);
        }

        public boolean a() {
            if (EntityPolarBear.this.isBaby()) {
                return false;
            } else {
                if (super.a()) {
                    List list = EntityPolarBear.this.world.a(EntityPolarBear.class, EntityPolarBear.this.getBoundingBox().grow(8.0D, 4.0D, 8.0D));
                    Iterator iterator = list.iterator();

                    while (iterator.hasNext()) {
                        EntityPolarBear entitypolarbear = (EntityPolarBear) iterator.next();

                        if (entitypolarbear.isBaby()) {
                            return true;
                        }
                    }
                }

                EntityPolarBear.this.setGoalTarget((EntityLiving) null);
                return false;
            }
        }

        protected double i() {
            return super.i() * 0.5D;
        }
    }

    class c extends PathfinderGoalHurtByTarget {

        public c() {
            super(EntityPolarBear.this, false, new Class[0]);
        }

        public void c() {
            super.c();
            if (EntityPolarBear.this.isBaby()) {
                this.f();
                this.d();
            }

        }

        protected void a(EntityCreature entitycreature, EntityLiving entityliving) {
            if (entitycreature instanceof EntityPolarBear && !entitycreature.isBaby()) {
                super.a(entitycreature, entityliving);
            }

        }
    }

    static class b implements GroupDataEntity {

        public boolean a;

        private b() {}

        b(Object object) {
            this();
        }
    }
}
