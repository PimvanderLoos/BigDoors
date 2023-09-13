package net.minecraft.server;

import com.google.common.base.Predicate;
import java.util.UUID;
import javax.annotation.Nullable;

public class EntityWolf extends EntityTameableAnimal {

    private static final DataWatcherObject<Float> DATA_HEALTH = DataWatcher.a(EntityWolf.class, DataWatcherRegistry.c);
    private static final DataWatcherObject<Boolean> bC = DataWatcher.a(EntityWolf.class, DataWatcherRegistry.h);
    private static final DataWatcherObject<Integer> bD = DataWatcher.a(EntityWolf.class, DataWatcherRegistry.b);
    private float bE;
    private float bF;
    private boolean bG;
    private boolean bH;
    private float bI;
    private float bJ;

    public EntityWolf(World world) {
        super(world);
        this.setSize(0.6F, 0.85F);
        this.setTamed(false);
    }

    protected void r() {
        this.goalSit = new PathfinderGoalSit(this);
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, this.goalSit);
        this.goalSelector.a(3, new EntityWolf.a(this, EntityLlama.class, 24.0F, 1.5D, 1.5D));
        this.goalSelector.a(4, new PathfinderGoalLeapAtTarget(this, 0.4F));
        this.goalSelector.a(5, new PathfinderGoalMeleeAttack(this, 1.0D, true));
        this.goalSelector.a(6, new PathfinderGoalFollowOwner(this, 1.0D, 10.0F, 2.0F));
        this.goalSelector.a(7, new PathfinderGoalBreed(this, 1.0D));
        this.goalSelector.a(8, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(9, new PathfinderGoalBeg(this, 8.0F));
        this.goalSelector.a(10, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(10, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalOwnerHurtByTarget(this));
        this.targetSelector.a(2, new PathfinderGoalOwnerHurtTarget(this));
        this.targetSelector.a(3, new PathfinderGoalHurtByTarget(this, true, new Class[0]));
        this.targetSelector.a(4, new PathfinderGoalRandomTargetNonTamed(this, EntityAnimal.class, false, new Predicate() {
            public boolean a(@Nullable Entity entity) {
                return entity instanceof EntitySheep || entity instanceof EntityRabbit;
            }

            public boolean apply(@Nullable Object object) {
                return this.a((Entity) object);
            }
        }));
        this.targetSelector.a(5, new PathfinderGoalNearestAttackableTarget(this, EntitySkeletonAbstract.class, false));
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.30000001192092896D);
        if (this.isTamed()) {
            this.getAttributeInstance(GenericAttributes.maxHealth).setValue(20.0D);
        } else {
            this.getAttributeInstance(GenericAttributes.maxHealth).setValue(8.0D);
        }

        this.getAttributeMap().b(GenericAttributes.ATTACK_DAMAGE).setValue(2.0D);
    }

    public void setGoalTarget(@Nullable EntityLiving entityliving) {
        super.setGoalTarget(entityliving);
        if (entityliving == null) {
            this.setAngry(false);
        } else if (!this.isTamed()) {
            this.setAngry(true);
        }

    }

    protected void M() {
        this.datawatcher.set(EntityWolf.DATA_HEALTH, Float.valueOf(this.getHealth()));
    }

    protected void i() {
        super.i();
        this.datawatcher.register(EntityWolf.DATA_HEALTH, Float.valueOf(this.getHealth()));
        this.datawatcher.register(EntityWolf.bC, Boolean.valueOf(false));
        this.datawatcher.register(EntityWolf.bD, Integer.valueOf(EnumColor.RED.getInvColorIndex()));
    }

    protected void a(BlockPosition blockposition, Block block) {
        this.a(SoundEffects.iT, 0.15F, 1.0F);
    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityInsentient.a(dataconvertermanager, EntityWolf.class);
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setBoolean("Angry", this.isAngry());
        nbttagcompound.setByte("CollarColor", (byte) this.getCollarColor().getInvColorIndex());
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setAngry(nbttagcompound.getBoolean("Angry"));
        if (nbttagcompound.hasKeyOfType("CollarColor", 99)) {
            this.setCollarColor(EnumColor.fromInvColorIndex(nbttagcompound.getByte("CollarColor")));
        }

    }

    protected SoundEffect F() {
        return this.isAngry() ? SoundEffects.iO : (this.random.nextInt(3) == 0 ? (this.isTamed() && ((Float) this.datawatcher.get(EntityWolf.DATA_HEALTH)).floatValue() < 10.0F ? SoundEffects.iU : SoundEffects.iR) : SoundEffects.iM);
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.iQ;
    }

    protected SoundEffect cf() {
        return SoundEffects.iN;
    }

    protected float cq() {
        return 0.4F;
    }

    @Nullable
    protected MinecraftKey J() {
        return LootTables.N;
    }

    public void n() {
        super.n();
        if (!this.world.isClientSide && this.bG && !this.bH && !this.de() && this.onGround) {
            this.bH = true;
            this.bI = 0.0F;
            this.bJ = 0.0F;
            this.world.broadcastEntityEffect(this, (byte) 8);
        }

        if (!this.world.isClientSide && this.getGoalTarget() == null && this.isAngry()) {
            this.setAngry(false);
        }

    }

    public void B_() {
        super.B_();
        this.bF = this.bE;
        if (this.dx()) {
            this.bE += (1.0F - this.bE) * 0.4F;
        } else {
            this.bE += (0.0F - this.bE) * 0.4F;
        }

        if (this.an()) {
            this.bG = true;
            this.bH = false;
            this.bI = 0.0F;
            this.bJ = 0.0F;
        } else if ((this.bG || this.bH) && this.bH) {
            if (this.bI == 0.0F) {
                this.a(SoundEffects.iS, this.cq(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            }

            this.bJ = this.bI;
            this.bI += 0.05F;
            if (this.bJ >= 2.0F) {
                this.bG = false;
                this.bH = false;
                this.bJ = 0.0F;
                this.bI = 0.0F;
            }

            if (this.bI > 0.4F) {
                float f = (float) this.getBoundingBox().b;
                int i = (int) (MathHelper.sin((this.bI - 0.4F) * 3.1415927F) * 7.0F);

                for (int j = 0; j < i; ++j) {
                    float f1 = (this.random.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
                    float f2 = (this.random.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;

                    this.world.addParticle(EnumParticle.WATER_SPLASH, this.locX + (double) f1, (double) (f + 0.8F), this.locZ + (double) f2, this.motX, this.motY, this.motZ, new int[0]);
                }
            }
        }

    }

    public float getHeadHeight() {
        return this.length * 0.8F;
    }

    public int N() {
        return this.isSitting() ? 20 : super.N();
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else {
            Entity entity = damagesource.getEntity();

            if (this.goalSit != null) {
                this.goalSit.setSitting(false);
            }

            if (entity != null && !(entity instanceof EntityHuman) && !(entity instanceof EntityArrow)) {
                f = (f + 1.0F) / 2.0F;
            }

            return super.damageEntity(damagesource, f);
        }
    }

    public boolean B(Entity entity) {
        boolean flag = entity.damageEntity(DamageSource.mobAttack(this), (float) ((int) this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue()));

        if (flag) {
            this.a((EntityLiving) this, entity);
        }

        return flag;
    }

    public void setTamed(boolean flag) {
        super.setTamed(flag);
        if (flag) {
            this.getAttributeInstance(GenericAttributes.maxHealth).setValue(20.0D);
        } else {
            this.getAttributeInstance(GenericAttributes.maxHealth).setValue(8.0D);
        }

        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(4.0D);
    }

    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (this.isTamed()) {
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() instanceof ItemFood) {
                    ItemFood itemfood = (ItemFood) itemstack.getItem();

                    if (itemfood.g() && ((Float) this.datawatcher.get(EntityWolf.DATA_HEALTH)).floatValue() < 20.0F) {
                        if (!entityhuman.abilities.canInstantlyBuild) {
                            itemstack.subtract(1);
                        }

                        this.heal((float) itemfood.getNutrition(itemstack));
                        return true;
                    }
                } else if (itemstack.getItem() == Items.DYE) {
                    EnumColor enumcolor = EnumColor.fromInvColorIndex(itemstack.getData());

                    if (enumcolor != this.getCollarColor()) {
                        this.setCollarColor(enumcolor);
                        if (!entityhuman.abilities.canInstantlyBuild) {
                            itemstack.subtract(1);
                        }

                        return true;
                    }
                }
            }

            if (this.e((EntityLiving) entityhuman) && !this.world.isClientSide && !this.e(itemstack)) {
                this.goalSit.setSitting(!this.isSitting());
                this.bd = false;
                this.navigation.p();
                this.setGoalTarget((EntityLiving) null);
            }
        } else if (itemstack.getItem() == Items.BONE && !this.isAngry()) {
            if (!entityhuman.abilities.canInstantlyBuild) {
                itemstack.subtract(1);
            }

            if (!this.world.isClientSide) {
                if (this.random.nextInt(3) == 0) {
                    this.c(entityhuman);
                    this.navigation.p();
                    this.setGoalTarget((EntityLiving) null);
                    this.goalSit.setSitting(true);
                    this.setHealth(20.0F);
                    this.p(true);
                    this.world.broadcastEntityEffect(this, (byte) 7);
                } else {
                    this.p(false);
                    this.world.broadcastEntityEffect(this, (byte) 6);
                }
            }

            return true;
        }

        return super.a(entityhuman, enumhand);
    }

    public boolean e(ItemStack itemstack) {
        return itemstack.getItem() instanceof ItemFood && ((ItemFood) itemstack.getItem()).g();
    }

    public int cU() {
        return 8;
    }

    public boolean isAngry() {
        return (((Byte) this.datawatcher.get(EntityWolf.bx)).byteValue() & 2) != 0;
    }

    public void setAngry(boolean flag) {
        byte b0 = ((Byte) this.datawatcher.get(EntityWolf.bx)).byteValue();

        if (flag) {
            this.datawatcher.set(EntityWolf.bx, Byte.valueOf((byte) (b0 | 2)));
        } else {
            this.datawatcher.set(EntityWolf.bx, Byte.valueOf((byte) (b0 & -3)));
        }

    }

    public EnumColor getCollarColor() {
        return EnumColor.fromInvColorIndex(((Integer) this.datawatcher.get(EntityWolf.bD)).intValue() & 15);
    }

    public void setCollarColor(EnumColor enumcolor) {
        this.datawatcher.set(EntityWolf.bD, Integer.valueOf(enumcolor.getInvColorIndex()));
    }

    public EntityWolf b(EntityAgeable entityageable) {
        EntityWolf entitywolf = new EntityWolf(this.world);
        UUID uuid = this.getOwnerUUID();

        if (uuid != null) {
            entitywolf.setOwnerUUID(uuid);
            entitywolf.setTamed(true);
        }

        return entitywolf;
    }

    public void t(boolean flag) {
        this.datawatcher.set(EntityWolf.bC, Boolean.valueOf(flag));
    }

    public boolean mate(EntityAnimal entityanimal) {
        if (entityanimal == this) {
            return false;
        } else if (!this.isTamed()) {
            return false;
        } else if (!(entityanimal instanceof EntityWolf)) {
            return false;
        } else {
            EntityWolf entitywolf = (EntityWolf) entityanimal;

            return !entitywolf.isTamed() ? false : (entitywolf.isSitting() ? false : this.isInLove() && entitywolf.isInLove());
        }
    }

    public boolean dx() {
        return ((Boolean) this.datawatcher.get(EntityWolf.bC)).booleanValue();
    }

    public boolean a(EntityLiving entityliving, EntityLiving entityliving1) {
        if (!(entityliving instanceof EntityCreeper) && !(entityliving instanceof EntityGhast)) {
            if (entityliving instanceof EntityWolf) {
                EntityWolf entitywolf = (EntityWolf) entityliving;

                if (entitywolf.isTamed() && entitywolf.getOwner() == entityliving1) {
                    return false;
                }
            }

            return entityliving instanceof EntityHuman && entityliving1 instanceof EntityHuman && !((EntityHuman) entityliving1).a((EntityHuman) entityliving) ? false : !(entityliving instanceof EntityHorseAbstract) || !((EntityHorseAbstract) entityliving).isTamed();
        } else {
            return false;
        }
    }

    public boolean a(EntityHuman entityhuman) {
        return !this.isAngry() && super.a(entityhuman);
    }

    public EntityAgeable createChild(EntityAgeable entityageable) {
        return this.b(entityageable);
    }

    class a<T extends Entity> extends PathfinderGoalAvoidTarget<T> {

        private final EntityWolf d;

        public a(EntityWolf entitywolf, Class oclass, float f, double d0, double d1) {
            super(entitywolf, oclass, f, d0, d1);
            this.d = entitywolf;
        }

        public boolean a() {
            return super.a() && this.b instanceof EntityLlama ? !this.d.isTamed() && this.a((EntityLlama) this.b) : false;
        }

        private boolean a(EntityLlama entityllama) {
            return entityllama.getStrength() >= EntityWolf.this.random.nextInt(5);
        }

        public void c() {
            EntityWolf.this.setGoalTarget((EntityLiving) null);
            super.c();
        }

        public void e() {
            EntityWolf.this.setGoalTarget((EntityLiving) null);
            super.e();
        }
    }
}
