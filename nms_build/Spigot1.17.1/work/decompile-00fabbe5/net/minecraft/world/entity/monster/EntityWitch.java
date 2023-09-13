package net.minecraft.world.entity.monster;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.core.particles.Particles;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifiable;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalArrowAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTargetWitch;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestHealableRaider;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityPotion;
import net.minecraft.world.entity.raid.EntityRaider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionRegistry;
import net.minecraft.world.item.alchemy.PotionUtil;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;

public class EntityWitch extends EntityRaider implements IRangedEntity {

    private static final UUID SPEED_MODIFIER_DRINKING_UUID = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
    private static final AttributeModifier SPEED_MODIFIER_DRINKING = new AttributeModifier(EntityWitch.SPEED_MODIFIER_DRINKING_UUID, "Drinking speed penalty", -0.25D, AttributeModifier.Operation.ADDITION);
    private static final DataWatcherObject<Boolean> DATA_USING_ITEM = DataWatcher.a(EntityWitch.class, DataWatcherRegistry.BOOLEAN);
    private int usingTime;
    private PathfinderGoalNearestHealableRaider<EntityRaider> healRaidersGoal;
    private PathfinderGoalNearestAttackableTargetWitch<EntityHuman> attackPlayersGoal;

    public EntityWitch(EntityTypes<? extends EntityWitch> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected void initPathfinder() {
        super.initPathfinder();
        this.healRaidersGoal = new PathfinderGoalNearestHealableRaider<>(this, EntityRaider.class, true, (entityliving) -> {
            return entityliving != null && this.fL() && entityliving.getEntityType() != EntityTypes.WITCH;
        });
        this.attackPlayersGoal = new PathfinderGoalNearestAttackableTargetWitch<>(this, EntityHuman.class, 10, true, false, (Predicate) null);
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalArrowAttack(this, 1.0D, 60, 10.0F));
        this.goalSelector.a(2, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(3, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(3, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, new Class[]{EntityRaider.class}));
        this.targetSelector.a(2, this.healRaidersGoal);
        this.targetSelector.a(3, this.attackPlayersGoal);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.getDataWatcher().register(EntityWitch.DATA_USING_ITEM, false);
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.WITCH_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.WITCH_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.WITCH_DEATH;
    }

    public void x(boolean flag) {
        this.getDataWatcher().set(EntityWitch.DATA_USING_ITEM, flag);
    }

    public boolean n() {
        return (Boolean) this.getDataWatcher().get(EntityWitch.DATA_USING_ITEM);
    }

    public static AttributeProvider.Builder p() {
        return EntityMonster.fB().a(GenericAttributes.MAX_HEALTH, 26.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.25D);
    }

    @Override
    public void movementTick() {
        if (!this.level.isClientSide && this.isAlive()) {
            this.healRaidersGoal.j();
            if (this.healRaidersGoal.h() <= 0) {
                this.attackPlayersGoal.a(true);
            } else {
                this.attackPlayersGoal.a(false);
            }

            if (this.n()) {
                if (this.usingTime-- <= 0) {
                    this.x(false);
                    ItemStack itemstack = this.getItemInMainHand();

                    this.setSlot(EnumItemSlot.MAINHAND, ItemStack.EMPTY);
                    if (itemstack.a(Items.POTION)) {
                        List<MobEffect> list = PotionUtil.getEffects(itemstack);

                        if (list != null) {
                            Iterator iterator = list.iterator();

                            while (iterator.hasNext()) {
                                MobEffect mobeffect = (MobEffect) iterator.next();

                                this.addEffect(new MobEffect(mobeffect));
                            }
                        }
                    }

                    this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).removeModifier(EntityWitch.SPEED_MODIFIER_DRINKING);
                }
            } else {
                PotionRegistry potionregistry = null;

                if (this.random.nextFloat() < 0.15F && this.a((Tag) TagsFluid.WATER) && !this.hasEffect(MobEffects.WATER_BREATHING)) {
                    potionregistry = Potions.WATER_BREATHING;
                } else if (this.random.nextFloat() < 0.15F && (this.isBurning() || this.dW() != null && this.dW().isFire()) && !this.hasEffect(MobEffects.FIRE_RESISTANCE)) {
                    potionregistry = Potions.FIRE_RESISTANCE;
                } else if (this.random.nextFloat() < 0.05F && this.getHealth() < this.getMaxHealth()) {
                    potionregistry = Potions.HEALING;
                } else if (this.random.nextFloat() < 0.5F && this.getGoalTarget() != null && !this.hasEffect(MobEffects.MOVEMENT_SPEED) && this.getGoalTarget().f((Entity) this) > 121.0D) {
                    potionregistry = Potions.SWIFTNESS;
                }

                if (potionregistry != null) {
                    this.setSlot(EnumItemSlot.MAINHAND, PotionUtil.a(new ItemStack(Items.POTION), potionregistry));
                    this.usingTime = this.getItemInMainHand().o();
                    this.x(true);
                    if (!this.isSilent()) {
                        this.level.playSound((EntityHuman) null, this.locX(), this.locY(), this.locZ(), SoundEffects.WITCH_DRINK, this.getSoundCategory(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
                    }

                    AttributeModifiable attributemodifiable = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);

                    attributemodifiable.removeModifier(EntityWitch.SPEED_MODIFIER_DRINKING);
                    attributemodifiable.b(EntityWitch.SPEED_MODIFIER_DRINKING);
                }
            }

            if (this.random.nextFloat() < 7.5E-4F) {
                this.level.broadcastEntityEffect(this, (byte) 15);
            }
        }

        super.movementTick();
    }

    @Override
    public SoundEffect t() {
        return SoundEffects.WITCH_CELEBRATE;
    }

    @Override
    public void a(byte b0) {
        if (b0 == 15) {
            for (int i = 0; i < this.random.nextInt(35) + 10; ++i) {
                this.level.addParticle(Particles.WITCH, this.locX() + this.random.nextGaussian() * 0.12999999523162842D, this.getBoundingBox().maxY + 0.5D + this.random.nextGaussian() * 0.12999999523162842D, this.locZ() + this.random.nextGaussian() * 0.12999999523162842D, 0.0D, 0.0D, 0.0D);
            }
        } else {
            super.a(b0);
        }

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
        if (!this.n()) {
            Vec3D vec3d = entityliving.getMot();
            double d0 = entityliving.locX() + vec3d.x - this.locX();
            double d1 = entityliving.getHeadY() - 1.100000023841858D - this.locY();
            double d2 = entityliving.locZ() + vec3d.z - this.locZ();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            PotionRegistry potionregistry = Potions.HARMING;

            if (entityliving instanceof EntityRaider) {
                if (entityliving.getHealth() <= 4.0F) {
                    potionregistry = Potions.HEALING;
                } else {
                    potionregistry = Potions.REGENERATION;
                }

                this.setGoalTarget((EntityLiving) null);
            } else if (d3 >= 8.0D && !entityliving.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
                potionregistry = Potions.SLOWNESS;
            } else if (entityliving.getHealth() >= 8.0F && !entityliving.hasEffect(MobEffects.POISON)) {
                potionregistry = Potions.POISON;
            } else if (d3 <= 3.0D && !entityliving.hasEffect(MobEffects.WEAKNESS) && this.random.nextFloat() < 0.25F) {
                potionregistry = Potions.WEAKNESS;
            }

            EntityPotion entitypotion = new EntityPotion(this.level, this);

            entitypotion.setItem(PotionUtil.a(new ItemStack(Items.SPLASH_POTION), potionregistry));
            entitypotion.setXRot(entitypotion.getXRot() - -20.0F);
            entitypotion.shoot(d0, d1 + d3 * 0.2D, d2, 0.75F, 8.0F);
            if (!this.isSilent()) {
                this.level.playSound((EntityHuman) null, this.locX(), this.locY(), this.locZ(), SoundEffects.WITCH_THROW, this.getSoundCategory(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
            }

            this.level.addEntity(entitypotion);
        }
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return 1.62F;
    }

    @Override
    public void a(int i, boolean flag) {}

    @Override
    public boolean fx() {
        return false;
    }
}
