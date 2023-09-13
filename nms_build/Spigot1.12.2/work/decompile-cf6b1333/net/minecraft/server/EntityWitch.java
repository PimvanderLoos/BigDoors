package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

public class EntityWitch extends EntityMonster implements IRangedEntity {

    private static final UUID a = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
    private static final AttributeModifier b = (new AttributeModifier(EntityWitch.a, "Drinking speed penalty", -0.25D, 0)).a(false);
    private static final DataWatcherObject<Boolean> c = DataWatcher.a(EntityWitch.class, DataWatcherRegistry.h);
    private int bx;

    public EntityWitch(World world) {
        super(world);
        this.setSize(0.6F, 1.95F);
    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityInsentient.a(dataconvertermanager, EntityWitch.class);
    }

    protected void r() {
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalArrowAttack(this, 1.0D, 60, 10.0F));
        this.goalSelector.a(2, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(3, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(3, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, false, new Class[0]));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));
    }

    protected void i() {
        super.i();
        this.getDataWatcher().register(EntityWitch.c, Boolean.valueOf(false));
    }

    protected SoundEffect F() {
        return SoundEffects.ix;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.iA;
    }

    protected SoundEffect cf() {
        return SoundEffects.iy;
    }

    public void a(boolean flag) {
        this.getDataWatcher().set(EntityWitch.c, Boolean.valueOf(flag));
    }

    public boolean p() {
        return ((Boolean) this.getDataWatcher().get(EntityWitch.c)).booleanValue();
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(26.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.25D);
    }

    public void n() {
        if (!this.world.isClientSide) {
            if (this.p()) {
                if (this.bx-- <= 0) {
                    this.a(false);
                    ItemStack itemstack = this.getItemInMainHand();

                    this.setSlot(EnumItemSlot.MAINHAND, ItemStack.a);
                    if (itemstack.getItem() == Items.POTION) {
                        List list = PotionUtil.getEffects(itemstack);

                        if (list != null) {
                            Iterator iterator = list.iterator();

                            while (iterator.hasNext()) {
                                MobEffect mobeffect = (MobEffect) iterator.next();

                                this.addEffect(new MobEffect(mobeffect));
                            }
                        }
                    }

                    this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).c(EntityWitch.b);
                }
            } else {
                PotionRegistry potionregistry = null;

                if (this.random.nextFloat() < 0.15F && this.a(Material.WATER) && !this.hasEffect(MobEffects.WATER_BREATHING)) {
                    potionregistry = Potions.t;
                } else if (this.random.nextFloat() < 0.15F && (this.isBurning() || this.ce() != null && this.ce().o()) && !this.hasEffect(MobEffects.FIRE_RESISTANCE)) {
                    potionregistry = Potions.m;
                } else if (this.random.nextFloat() < 0.05F && this.getHealth() < this.getMaxHealth()) {
                    potionregistry = Potions.v;
                } else if (this.random.nextFloat() < 0.5F && this.getGoalTarget() != null && !this.hasEffect(MobEffects.FASTER_MOVEMENT) && this.getGoalTarget().h(this) > 121.0D) {
                    potionregistry = Potions.o;
                }

                if (potionregistry != null) {
                    this.setSlot(EnumItemSlot.MAINHAND, PotionUtil.a(new ItemStack(Items.POTION), potionregistry));
                    this.bx = this.getItemInMainHand().m();
                    this.a(true);
                    this.world.a((EntityHuman) null, this.locX, this.locY, this.locZ, SoundEffects.iz, this.bK(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
                    AttributeInstance attributeinstance = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);

                    attributeinstance.c(EntityWitch.b);
                    attributeinstance.b(EntityWitch.b);
                }
            }

            if (this.random.nextFloat() < 7.5E-4F) {
                this.world.broadcastEntityEffect(this, (byte) 15);
            }
        }

        super.n();
    }

    protected float applyMagicModifier(DamageSource damagesource, float f) {
        f = super.applyMagicModifier(damagesource, f);
        if (damagesource.getEntity() == this) {
            f = 0.0F;
        }

        if (damagesource.isMagic()) {
            f = (float) ((double) f * 0.15D);
        }

        return f;
    }

    @Nullable
    protected MinecraftKey J() {
        return LootTables.p;
    }

    public void a(EntityLiving entityliving, float f) {
        if (!this.p()) {
            double d0 = entityliving.locY + (double) entityliving.getHeadHeight() - 1.100000023841858D;
            double d1 = entityliving.locX + entityliving.motX - this.locX;
            double d2 = d0 - this.locY;
            double d3 = entityliving.locZ + entityliving.motZ - this.locZ;
            float f1 = MathHelper.sqrt(d1 * d1 + d3 * d3);
            PotionRegistry potionregistry = Potions.x;

            if (f1 >= 8.0F && !entityliving.hasEffect(MobEffects.SLOWER_MOVEMENT)) {
                potionregistry = Potions.r;
            } else if (entityliving.getHealth() >= 8.0F && !entityliving.hasEffect(MobEffects.POISON)) {
                potionregistry = Potions.z;
            } else if (f1 <= 3.0F && !entityliving.hasEffect(MobEffects.WEAKNESS) && this.random.nextFloat() < 0.25F) {
                potionregistry = Potions.I;
            }

            EntityPotion entitypotion = new EntityPotion(this.world, this, PotionUtil.a(new ItemStack(Items.SPLASH_POTION), potionregistry));

            entitypotion.pitch -= -20.0F;
            entitypotion.shoot(d1, d2 + (double) (f1 * 0.2F), d3, 0.75F, 8.0F);
            this.world.a((EntityHuman) null, this.locX, this.locY, this.locZ, SoundEffects.iB, this.bK(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
            this.world.addEntity(entitypotion);
        }
    }

    public float getHeadHeight() {
        return 1.62F;
    }

    public void p(boolean flag) {}
}
