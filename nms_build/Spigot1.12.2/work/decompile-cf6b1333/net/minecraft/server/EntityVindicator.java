package net.minecraft.server;

import com.google.common.base.Predicate;
import javax.annotation.Nullable;

public class EntityVindicator extends EntityIllagerAbstract {

    private boolean b;
    private static final Predicate<Entity> c = new Predicate() {
        public boolean a(@Nullable Entity entity) {
            return entity instanceof EntityLiving && ((EntityLiving) entity).cS();
        }

        public boolean apply(@Nullable Object object) {
            return this.a((Entity) object);
        }
    };

    public EntityVindicator(World world) {
        super(world);
        this.setSize(0.6F, 1.95F);
    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityInsentient.a(dataconvertermanager, EntityVindicator.class);
    }

    protected void r() {
        super.r();
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, 1.0D, false));
        this.goalSelector.a(8, new PathfinderGoalRandomStroll(this, 0.6D));
        this.goalSelector.a(9, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 3.0F, 1.0F));
        this.goalSelector.a(10, new PathfinderGoalLookAtPlayer(this, EntityInsentient.class, 8.0F));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, true, new Class[] { EntityVindicator.class}));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget(this, EntityVillager.class, true));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget(this, EntityIronGolem.class, true));
        this.targetSelector.a(4, new EntityVindicator.a(this));
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.3499999940395355D);
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(12.0D);
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(24.0D);
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(5.0D);
    }

    protected void i() {
        super.i();
    }

    protected MinecraftKey J() {
        return LootTables.av;
    }

    public void a(boolean flag) {
        this.a(1, flag);
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        if (this.b) {
            nbttagcompound.setBoolean("Johnny", true);
        }

    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("Johnny", 99)) {
            this.b = nbttagcompound.getBoolean("Johnny");
        }

    }

    @Nullable
    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, @Nullable GroupDataEntity groupdataentity) {
        GroupDataEntity groupdataentity1 = super.prepare(difficultydamagescaler, groupdataentity);

        this.a(difficultydamagescaler);
        this.b(difficultydamagescaler);
        return groupdataentity1;
    }

    protected void a(DifficultyDamageScaler difficultydamagescaler) {
        this.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
    }

    protected void M() {
        super.M();
        this.a(this.getGoalTarget() != null);
    }

    public boolean r(Entity entity) {
        return super.r(entity) ? true : (entity instanceof EntityLiving && ((EntityLiving) entity).getMonsterType() == EnumMonsterType.ILLAGER ? this.aY() == null && entity.aY() == null : false);
    }

    public void setCustomName(String s) {
        super.setCustomName(s);
        if (!this.b && "Johnny".equals(s)) {
            this.b = true;
        }

    }

    protected SoundEffect F() {
        return SoundEffects.iq;
    }

    protected SoundEffect cf() {
        return SoundEffects.ir;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.is;
    }

    static class a extends PathfinderGoalNearestAttackableTarget<EntityLiving> {

        public a(EntityVindicator entityvindicator) {
            super(entityvindicator, EntityLiving.class, 0, true, true, EntityVindicator.c);
        }

        public boolean a() {
            return ((EntityVindicator) this.e).b && super.a();
        }
    }
}
