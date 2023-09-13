package net.minecraft.server;

import java.util.Calendar;
import javax.annotation.Nullable;

public abstract class EntitySkeletonAbstract extends EntityMonster implements IRangedEntity {

    private static final DataWatcherObject<Boolean> a = DataWatcher.a(EntitySkeletonAbstract.class, DataWatcherRegistry.h);
    private final PathfinderGoalBowShoot<EntitySkeletonAbstract> b = new PathfinderGoalBowShoot(this, 1.0D, 20, 15.0F);
    private final PathfinderGoalMeleeAttack c = new PathfinderGoalMeleeAttack(this, 1.2D, flag) {
        public void d() {
            super.d();
            EntitySkeletonAbstract.this.p(false);
        }

        public void c() {
            super.c();
            EntitySkeletonAbstract.this.p(true);
        }
    };

    public EntitySkeletonAbstract(World world) {
        super(world);
        this.setSize(0.6F, 1.99F);
        this.dm();
    }

    protected void r() {
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalRestrictSun(this));
        this.goalSelector.a(3, new PathfinderGoalFleeSun(this, 1.0D));
        this.goalSelector.a(3, new PathfinderGoalAvoidTarget(this, EntityWolf.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.a(5, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(6, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, false, new Class[0]));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget(this, EntityIronGolem.class, true));
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.25D);
    }

    protected void i() {
        super.i();
        this.datawatcher.register(EntitySkeletonAbstract.a, Boolean.valueOf(false));
    }

    protected void a(BlockPosition blockposition, Block block) {
        this.a(this.p(), 0.15F, 1.0F);
    }

    abstract SoundEffect p();

    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.UNDEAD;
    }

    public void n() {
        if (this.world.D() && !this.world.isClientSide) {
            float f = this.aw();
            BlockPosition blockposition = this.bJ() instanceof EntityBoat ? (new BlockPosition(this.locX, (double) Math.round(this.locY), this.locZ)).up() : new BlockPosition(this.locX, (double) Math.round(this.locY), this.locZ);

            if (f > 0.5F && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && this.world.h(blockposition)) {
                boolean flag = true;
                ItemStack itemstack = this.getEquipment(EnumItemSlot.HEAD);

                if (!itemstack.isEmpty()) {
                    if (itemstack.f()) {
                        itemstack.setData(itemstack.i() + this.random.nextInt(2));
                        if (itemstack.i() >= itemstack.k()) {
                            this.b(itemstack);
                            this.setSlot(EnumItemSlot.HEAD, ItemStack.a);
                        }
                    }

                    flag = false;
                }

                if (flag) {
                    this.setOnFire(8);
                }
            }
        }

        super.n();
    }

    public void aE() {
        super.aE();
        if (this.bJ() instanceof EntityCreature) {
            EntityCreature entitycreature = (EntityCreature) this.bJ();

            this.aN = entitycreature.aN;
        }

    }

    protected void a(DifficultyDamageScaler difficultydamagescaler) {
        super.a(difficultydamagescaler);
        this.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.BOW));
    }

    @Nullable
    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, @Nullable GroupDataEntity groupdataentity) {
        groupdataentity = super.prepare(difficultydamagescaler, groupdataentity);
        this.a(difficultydamagescaler);
        this.b(difficultydamagescaler);
        this.dm();
        this.m(this.random.nextFloat() < 0.55F * difficultydamagescaler.d());
        if (this.getEquipment(EnumItemSlot.HEAD).isEmpty()) {
            Calendar calendar = this.world.ae();

            if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31 && this.random.nextFloat() < 0.25F) {
                this.setSlot(EnumItemSlot.HEAD, new ItemStack(this.random.nextFloat() < 0.1F ? Blocks.LIT_PUMPKIN : Blocks.PUMPKIN));
                this.dropChanceArmor[EnumItemSlot.HEAD.b()] = 0.0F;
            }
        }

        return groupdataentity;
    }

    public void dm() {
        if (this.world != null && !this.world.isClientSide) {
            this.goalSelector.a((PathfinderGoal) this.c);
            this.goalSelector.a((PathfinderGoal) this.b);
            ItemStack itemstack = this.getItemInMainHand();

            if (itemstack.getItem() == Items.BOW) {
                byte b0 = 20;

                if (this.world.getDifficulty() != EnumDifficulty.HARD) {
                    b0 = 40;
                }

                this.b.b(b0);
                this.goalSelector.a(4, this.b);
            } else {
                this.goalSelector.a(4, this.c);
            }

        }
    }

    public void a(EntityLiving entityliving, float f) {
        EntityArrow entityarrow = this.a(f);
        double d0 = entityliving.locX - this.locX;
        double d1 = entityliving.getBoundingBox().b + (double) (entityliving.length / 3.0F) - entityarrow.locY;
        double d2 = entityliving.locZ - this.locZ;
        double d3 = (double) MathHelper.sqrt(d0 * d0 + d2 * d2);

        entityarrow.shoot(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F, (float) (14 - this.world.getDifficulty().a() * 4));
        this.a(SoundEffects.gW, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.world.addEntity(entityarrow);
    }

    protected EntityArrow a(float f) {
        EntityTippedArrow entitytippedarrow = new EntityTippedArrow(this.world, this);

        entitytippedarrow.a((EntityLiving) this, f);
        return entitytippedarrow;
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.dm();
    }

    public void setSlot(EnumItemSlot enumitemslot, ItemStack itemstack) {
        super.setSlot(enumitemslot, itemstack);
        if (!this.world.isClientSide && enumitemslot == EnumItemSlot.MAINHAND) {
            this.dm();
        }

    }

    public float getHeadHeight() {
        return 1.74F;
    }

    public double aF() {
        return -0.6D;
    }

    public void p(boolean flag) {
        this.datawatcher.set(EntitySkeletonAbstract.a, Boolean.valueOf(flag));
    }
}
