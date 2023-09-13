package net.minecraft.server;

import com.google.common.base.Predicate;
import java.util.Iterator;
import javax.annotation.Nullable;

public class EntityLlama extends EntityHorseChestedAbstract implements IRangedEntity {

    private static final DataWatcherObject<Integer> bG = DataWatcher.a(EntityLlama.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Integer> bH = DataWatcher.a(EntityLlama.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Integer> bI = DataWatcher.a(EntityLlama.class, DataWatcherRegistry.b);
    private boolean bJ;
    @Nullable
    private EntityLlama bK;
    @Nullable
    private EntityLlama bL;

    public EntityLlama(World world) {
        super(world);
        this.setSize(0.9F, 1.87F);
    }

    private void p(int i) {
        this.datawatcher.set(EntityLlama.bG, Integer.valueOf(Math.max(1, Math.min(5, i))));
    }

    private void dT() {
        int i = this.random.nextFloat() < 0.04F ? 5 : 3;

        this.p(1 + this.random.nextInt(i));
    }

    public int dL() {
        return ((Integer) this.datawatcher.get(EntityLlama.bG)).intValue();
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("Variant", this.getVariant());
        nbttagcompound.setInt("Strength", this.dL());
        if (!this.inventoryChest.getItem(1).isEmpty()) {
            nbttagcompound.set("DecorItem", this.inventoryChest.getItem(1).save(new NBTTagCompound()));
        }

    }

    public void a(NBTTagCompound nbttagcompound) {
        this.p(nbttagcompound.getInt("Strength"));
        super.a(nbttagcompound);
        this.setVariant(nbttagcompound.getInt("Variant"));
        if (nbttagcompound.hasKeyOfType("DecorItem", 10)) {
            this.inventoryChest.setItem(1, new ItemStack(nbttagcompound.getCompound("DecorItem")));
        }

        this.dy();
    }

    protected void r() {
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalTame(this, 1.2D));
        this.goalSelector.a(2, new PathfinderGoalLlamaFollow(this, 2.0999999046325684D));
        this.goalSelector.a(3, new PathfinderGoalArrowAttack(this, 1.25D, 40, 20.0F));
        this.goalSelector.a(3, new PathfinderGoalPanic(this, 1.2D));
        this.goalSelector.a(4, new PathfinderGoalBreed(this, 1.0D));
        this.goalSelector.a(5, new PathfinderGoalFollowParent(this, 1.0D));
        this.goalSelector.a(6, new PathfinderGoalRandomStrollLand(this, 0.7D));
        this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new EntityLlama.c(this));
        this.targetSelector.a(2, new EntityLlama.a(this));
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(40.0D);
    }

    protected void i() {
        super.i();
        this.datawatcher.register(EntityLlama.bG, Integer.valueOf(0));
        this.datawatcher.register(EntityLlama.bH, Integer.valueOf(-1));
        this.datawatcher.register(EntityLlama.bI, Integer.valueOf(0));
    }

    public int getVariant() {
        return MathHelper.clamp(((Integer) this.datawatcher.get(EntityLlama.bI)).intValue(), 0, 3);
    }

    public void setVariant(int i) {
        this.datawatcher.set(EntityLlama.bI, Integer.valueOf(i));
    }

    protected int di() {
        return this.isCarryingChest() ? 2 + 3 * this.dl() : super.di();
    }

    public void k(Entity entity) {
        if (this.w(entity)) {
            float f = MathHelper.cos(this.aN * 0.017453292F);
            float f1 = MathHelper.sin(this.aN * 0.017453292F);
            float f2 = 0.3F;

            entity.setPosition(this.locX + (double) (0.3F * f1), this.locY + this.ay() + entity.ax(), this.locZ - (double) (0.3F * f));
        }
    }

    public double ay() {
        return (double) this.length * 0.67D;
    }

    public boolean cR() {
        return false;
    }

    protected boolean b(EntityHuman entityhuman, ItemStack itemstack) {
        byte b0 = 0;
        byte b1 = 0;
        float f = 0.0F;
        boolean flag = false;
        Item item = itemstack.getItem();

        if (item == Items.WHEAT) {
            b0 = 10;
            b1 = 3;
            f = 2.0F;
        } else if (item == Item.getItemOf(Blocks.HAY_BLOCK)) {
            b0 = 90;
            b1 = 6;
            f = 10.0F;
            if (this.isTamed() && this.getAge() == 0) {
                flag = true;
                this.c(entityhuman);
            }
        }

        if (this.getHealth() < this.getMaxHealth() && f > 0.0F) {
            this.heal(f);
            flag = true;
        }

        if (this.isBaby() && b0 > 0) {
            this.world.addParticle(EnumParticle.VILLAGER_HAPPY, this.locX + (double) (this.random.nextFloat() * this.width * 2.0F) - (double) this.width, this.locY + 0.5D + (double) (this.random.nextFloat() * this.length), this.locZ + (double) (this.random.nextFloat() * this.width * 2.0F) - (double) this.width, 0.0D, 0.0D, 0.0D, new int[0]);
            if (!this.world.isClientSide) {
                this.setAge(b0);
            }

            flag = true;
        }

        if (b1 > 0 && (flag || !this.isTamed()) && this.getTemper() < this.getMaxDomestication()) {
            flag = true;
            if (!this.world.isClientSide) {
                this.n(b1);
            }
        }

        if (flag && !this.isSilent()) {
            this.world.a((EntityHuman) null, this.locX, this.locY, this.locZ, SoundEffects.dD, this.bC(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
        }

        return flag;
    }

    protected boolean isFrozen() {
        return this.getHealth() <= 0.0F || this.dt();
    }

    @Nullable
    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, @Nullable GroupDataEntity groupdataentity) {
        Object object = super.prepare(difficultydamagescaler, groupdataentity);

        this.dT();
        int i;

        if (object instanceof EntityLlama.b) {
            i = ((EntityLlama.b) object).a;
        } else {
            i = this.random.nextInt(4);
            object = new EntityLlama.b(i, null);
        }

        this.setVariant(i);
        return (GroupDataEntity) object;
    }

    protected SoundEffect dj() {
        return SoundEffects.dA;
    }

    protected SoundEffect G() {
        return SoundEffects.dz;
    }

    protected SoundEffect bW() {
        return SoundEffects.dE;
    }

    protected SoundEffect bX() {
        return SoundEffects.dC;
    }

    protected void a(BlockPosition blockposition, Block block) {
        this.a(SoundEffects.dG, 0.15F, 1.0F);
    }

    protected void dk() {
        this.a(SoundEffects.dB, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
    }

    public void dF() {
        SoundEffect soundeffect = this.dj();

        if (soundeffect != null) {
            this.a(soundeffect, this.ci(), this.cj());
        }

    }

    @Nullable
    protected MinecraftKey J() {
        return LootTables.aw;
    }

    public int dl() {
        return this.dL();
    }

    public boolean dK() {
        return true;
    }

    public boolean f(ItemStack itemstack) {
        return itemstack.getItem() == Item.getItemOf(Blocks.CARPET);
    }

    public boolean dA() {
        return false;
    }

    public void a(IInventory iinventory) {
        EnumColor enumcolor = this.dO();

        super.a(iinventory);
        EnumColor enumcolor1 = this.dO();

        if (this.ticksLived > 20 && enumcolor1 != null && enumcolor1 != enumcolor) {
            this.a(SoundEffects.dH, 0.5F, 1.0F);
        }

    }

    protected void dy() {
        if (!this.world.isClientSide) {
            super.dy();
            this.g(this.inventoryChest.getItem(1));
        }
    }

    private void a(@Nullable EnumColor enumcolor) {
        this.datawatcher.set(EntityLlama.bH, Integer.valueOf(enumcolor == null ? -1 : enumcolor.getColorIndex()));
    }

    private void g(ItemStack itemstack) {
        if (this.f(itemstack)) {
            this.a(EnumColor.fromColorIndex(itemstack.getData()));
        } else {
            this.a((EnumColor) null);
        }

    }

    @Nullable
    public EnumColor dO() {
        int i = ((Integer) this.datawatcher.get(EntityLlama.bH)).intValue();

        return i == -1 ? null : EnumColor.fromColorIndex(i);
    }

    public int getMaxDomestication() {
        return 30;
    }

    public boolean mate(EntityAnimal entityanimal) {
        return entityanimal != this && entityanimal instanceof EntityLlama && this.dG() && ((EntityLlama) entityanimal).dG();
    }

    public EntityLlama b(EntityAgeable entityageable) {
        EntityLlama entityllama = new EntityLlama(this.world);

        this.a(entityageable, (EntityHorseAbstract) entityllama);
        EntityLlama entityllama1 = (EntityLlama) entityageable;
        int i = this.random.nextInt(Math.max(this.dL(), entityllama1.dL())) + 1;

        if (this.random.nextFloat() < 0.03F) {
            ++i;
        }

        entityllama.p(i);
        entityllama.setVariant(this.random.nextBoolean() ? this.getVariant() : entityllama1.getVariant());
        return entityllama;
    }

    private void e(EntityLiving entityliving) {
        EntityLlamaSpit entityllamaspit = new EntityLlamaSpit(this.world, this);
        double d0 = entityliving.locX - this.locX;
        double d1 = entityliving.getBoundingBox().b + (double) (entityliving.length / 3.0F) - entityllamaspit.locY;
        double d2 = entityliving.locZ - this.locZ;
        float f = MathHelper.sqrt(d0 * d0 + d2 * d2) * 0.2F;

        entityllamaspit.shoot(d0, d1 + (double) f, d2, 1.5F, 10.0F);
        this.world.a((EntityHuman) null, this.locX, this.locY, this.locZ, SoundEffects.dF, this.bC(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
        this.world.addEntity(entityllamaspit);
        this.bJ = true;
    }

    private void x(boolean flag) {
        this.bJ = flag;
    }

    public void e(float f, float f1) {
        int i = MathHelper.f((f * 0.5F - 3.0F) * f1);

        if (i > 0) {
            if (f >= 6.0F) {
                this.damageEntity(DamageSource.FALL, (float) i);
                if (this.isVehicle()) {
                    Iterator iterator = this.by().iterator();

                    while (iterator.hasNext()) {
                        Entity entity = (Entity) iterator.next();

                        entity.damageEntity(DamageSource.FALL, (float) i);
                    }
                }
            }

            IBlockData iblockdata = this.world.getType(new BlockPosition(this.locX, this.locY - 0.2D - (double) this.lastYaw, this.locZ));
            Block block = iblockdata.getBlock();

            if (iblockdata.getMaterial() != Material.AIR && !this.isSilent()) {
                SoundEffectType soundeffecttype = block.getStepSound();

                this.world.a((EntityHuman) null, this.locX, this.locY, this.locZ, soundeffecttype.d(), this.bC(), soundeffecttype.a() * 0.5F, soundeffecttype.b() * 0.75F);
            }

        }
    }

    public void dP() {
        if (this.bK != null) {
            this.bK.bL = null;
        }

        this.bK = null;
    }

    public void a(EntityLlama entityllama) {
        this.bK = entityllama;
        this.bK.bL = this;
    }

    public boolean dQ() {
        return this.bL != null;
    }

    public boolean dR() {
        return this.bK != null;
    }

    @Nullable
    public EntityLlama dS() {
        return this.bK;
    }

    protected double dg() {
        return 2.0D;
    }

    protected void dD() {
        if (!this.dR() && this.isBaby()) {
            super.dD();
        }

    }

    public boolean dE() {
        return false;
    }

    public void a(EntityLiving entityliving, float f) {
        this.e(entityliving);
    }

    public EntityAgeable createChild(EntityAgeable entityageable) {
        return this.b(entityageable);
    }

    static class a extends PathfinderGoalNearestAttackableTarget<EntityWolf> {

        public a(EntityLlama entityllama) {
            super(entityllama, EntityWolf.class, 16, false, true, (Predicate) null);
        }

        public boolean a() {
            if (super.a() && this.d != null && !((EntityWolf) this.d).isTamed()) {
                return true;
            } else {
                this.e.setGoalTarget((EntityLiving) null);
                return false;
            }
        }

        protected double i() {
            return super.i() * 0.25D;
        }
    }

    static class c extends PathfinderGoalHurtByTarget {

        public c(EntityLlama entityllama) {
            super(entityllama, false, new Class[0]);
        }

        public boolean b() {
            if (this.e instanceof EntityLlama) {
                EntityLlama entityllama = (EntityLlama) this.e;

                if (entityllama.bJ) {
                    entityllama.x(false);
                    return false;
                }
            }

            return super.b();
        }
    }

    static class b implements GroupDataEntity {

        public int a;

        private b(int i) {
            this.a = i;
        }

        b(int i, Object object) {
            this(i);
        }
    }
}
