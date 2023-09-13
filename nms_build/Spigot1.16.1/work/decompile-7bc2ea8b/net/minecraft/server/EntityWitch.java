package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class EntityWitch extends EntityRaider implements IRangedEntity {

    private static final UUID b = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
    private static final AttributeModifier bv = new AttributeModifier(EntityWitch.b, "Drinking speed penalty", -0.25D, AttributeModifier.Operation.ADDITION);
    private static final DataWatcherObject<Boolean> bw = DataWatcher.a(EntityWitch.class, DataWatcherRegistry.i);
    private int bx;
    private PathfinderGoalNearestHealableRaider<EntityRaider> by;
    private PathfinderGoalNearestAttackableTargetWitch<EntityHuman> bz;

    public EntityWitch(EntityTypes<? extends EntityWitch> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected void initPathfinder() {
        super.initPathfinder();
        this.by = new PathfinderGoalNearestHealableRaider<>(this, EntityRaider.class, true, (entityliving) -> {
            return entityliving != null && this.fc() && entityliving.getEntityType() != EntityTypes.WITCH;
        });
        this.bz = new PathfinderGoalNearestAttackableTargetWitch<>(this, EntityHuman.class, 10, true, false, (Predicate) null);
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalArrowAttack(this, 1.0D, 60, 10.0F));
        this.goalSelector.a(2, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(3, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(3, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, new Class[]{EntityRaider.class}));
        this.targetSelector.a(2, this.by);
        this.targetSelector.a(3, this.bz);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.getDataWatcher().register(EntityWitch.bw, false);
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.ENTITY_WITCH_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_WITCH_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_WITCH_DEATH;
    }

    public void v(boolean flag) {
        this.getDataWatcher().set(EntityWitch.bw, flag);
    }

    public boolean m() {
        return (Boolean) this.getDataWatcher().get(EntityWitch.bw);
    }

    public static AttributeProvider.Builder eL() {
        return EntityMonster.eS().a(GenericAttributes.MAX_HEALTH, 26.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.25D);
    }

    @Override
    public void movementTick() {
        if (!this.world.isClientSide && this.isAlive()) {
            this.by.j();
            if (this.by.h() <= 0) {
                this.bz.a(true);
            } else {
                this.bz.a(false);
            }

            if (this.m()) {
                if (this.bx-- <= 0) {
                    this.v(false);
                    ItemStack itemstack = this.getItemInMainHand();

                    this.setSlot(EnumItemSlot.MAINHAND, ItemStack.b);
                    if (itemstack.getItem() == Items.POTION) {
                        List<MobEffect> list = PotionUtil.getEffects(itemstack);

                        if (list != null) {
                            Iterator iterator = list.iterator();

                            while (iterator.hasNext()) {
                                MobEffect mobeffect = (MobEffect) iterator.next();

                                this.addEffect(new MobEffect(mobeffect));
                            }
                        }
                    }

                    this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).removeModifier(EntityWitch.bv);
                }
            } else {
                PotionRegistry potionregistry = null;

                if (this.random.nextFloat() < 0.15F && this.a((Tag) TagsFluid.WATER) && !this.hasEffect(MobEffects.WATER_BREATHING)) {
                    potionregistry = Potions.WATER_BREATHING;
                } else if (this.random.nextFloat() < 0.15F && (this.isBurning() || this.dl() != null && this.dl().isFire()) && !this.hasEffect(MobEffects.FIRE_RESISTANCE)) {
                    potionregistry = Potions.FIRE_RESISTANCE;
                } else if (this.random.nextFloat() < 0.05F && this.getHealth() < this.getMaxHealth()) {
                    potionregistry = Potions.HEALING;
                } else if (this.random.nextFloat() < 0.5F && this.getGoalTarget() != null && !this.hasEffect(MobEffects.FASTER_MOVEMENT) && this.getGoalTarget().h((Entity) this) > 121.0D) {
                    potionregistry = Potions.SWIFTNESS;
                }

                if (potionregistry != null) {
                    this.setSlot(EnumItemSlot.MAINHAND, PotionUtil.a(new ItemStack(Items.POTION), potionregistry));
                    this.bx = this.getItemInMainHand().k();
                    this.v(true);
                    if (!this.isSilent()) {
                        this.world.playSound((EntityHuman) null, this.locX(), this.locY(), this.locZ(), SoundEffects.ENTITY_WITCH_DRINK, this.getSoundCategory(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
                    }

                    AttributeModifiable attributemodifiable = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);

                    attributemodifiable.removeModifier(EntityWitch.bv);
                    attributemodifiable.b(EntityWitch.bv);
                }
            }

            if (this.random.nextFloat() < 7.5E-4F) {
                this.world.broadcastEntityEffect(this, (byte) 15);
            }
        }

        super.movementTick();
    }

    @Override
    public SoundEffect eM() {
        return SoundEffects.ENTITY_WITCH_CELEBRATE;
    }

    @Override
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

    @Override
    public void a(EntityLiving entityliving, float f) {
        if (!this.m()) {
            Vec3D vec3d = entityliving.getMot();
            double d0 = entityliving.locX() + vec3d.x - this.locX();
            double d1 = entityliving.getHeadY() - 1.100000023841858D - this.locY();
            double d2 = entityliving.locZ() + vec3d.z - this.locZ();
            float f1 = MathHelper.sqrt(d0 * d0 + d2 * d2);
            PotionRegistry potionregistry = Potions.HARMING;

            if (entityliving instanceof EntityRaider) {
                if (entityliving.getHealth() <= 4.0F) {
                    potionregistry = Potions.HEALING;
                } else {
                    potionregistry = Potions.REGENERATION;
                }

                this.setGoalTarget((EntityLiving) null);
            } else if (f1 >= 8.0F && !entityliving.hasEffect(MobEffects.SLOWER_MOVEMENT)) {
                potionregistry = Potions.SLOWNESS;
            } else if (entityliving.getHealth() >= 8.0F && !entityliving.hasEffect(MobEffects.POISON)) {
                potionregistry = Potions.POISON;
            } else if (f1 <= 3.0F && !entityliving.hasEffect(MobEffects.WEAKNESS) && this.random.nextFloat() < 0.25F) {
                potionregistry = Potions.WEAKNESS;
            }

            EntityPotion entitypotion = new EntityPotion(this.world, this);

            entitypotion.setItem(PotionUtil.a(new ItemStack(Items.SPLASH_POTION), potionregistry));
            entitypotion.pitch -= -20.0F;
            entitypotion.shoot(d0, d1 + (double) (f1 * 0.2F), d2, 0.75F, 8.0F);
            if (!this.isSilent()) {
                this.world.playSound((EntityHuman) null, this.locX(), this.locY(), this.locZ(), SoundEffects.ENTITY_WITCH_THROW, this.getSoundCategory(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
            }

            this.world.addEntity(entitypotion);
        }
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return 1.62F;
    }

    @Override
    public void a(int i, boolean flag) {}

    @Override
    public boolean eO() {
        return false;
    }
}
