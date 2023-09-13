package net.minecraft.server;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;

public class EntityChicken extends EntityAnimal {

    private static final Set<Item> bF = Sets.newHashSet(new Item[] { Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS});
    public float bx;
    public float by;
    public float bz;
    public float bB;
    public float bC = 1.0F;
    public int bD;
    public boolean bE;

    public EntityChicken(World world) {
        super(world);
        this.setSize(0.4F, 0.7F);
        this.bD = this.random.nextInt(6000) + 6000;
        this.a(PathType.WATER, 0.0F);
    }

    protected void r() {
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalPanic(this, 1.4D));
        this.goalSelector.a(2, new PathfinderGoalBreed(this, 1.0D));
        this.goalSelector.a(3, new PathfinderGoalTempt(this, 1.0D, false, EntityChicken.bF));
        this.goalSelector.a(4, new PathfinderGoalFollowParent(this, 1.1D));
        this.goalSelector.a(5, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
    }

    public float getHeadHeight() {
        return this.length;
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(4.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.25D);
    }

    public void n() {
        super.n();
        this.bB = this.bx;
        this.bz = this.by;
        this.by = (float) ((double) this.by + (double) (this.onGround ? -1 : 4) * 0.3D);
        this.by = MathHelper.a(this.by, 0.0F, 1.0F);
        if (!this.onGround && this.bC < 1.0F) {
            this.bC = 1.0F;
        }

        this.bC = (float) ((double) this.bC * 0.9D);
        if (!this.onGround && this.motY < 0.0D) {
            this.motY *= 0.6D;
        }

        this.bx += this.bC * 2.0F;
        if (!this.world.isClientSide && !this.isBaby() && !this.isChickenJockey() && --this.bD <= 0) {
            this.a(SoundEffects.af, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            this.a(Items.EGG, 1);
            this.bD = this.random.nextInt(6000) + 6000;
        }

    }

    public void e(float f, float f1) {}

    protected SoundEffect F() {
        return SoundEffects.ad;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.ag;
    }

    protected SoundEffect cf() {
        return SoundEffects.ae;
    }

    protected void a(BlockPosition blockposition, Block block) {
        this.a(SoundEffects.ah, 0.15F, 1.0F);
    }

    @Nullable
    protected MinecraftKey J() {
        return LootTables.D;
    }

    public EntityChicken b(EntityAgeable entityageable) {
        return new EntityChicken(this.world);
    }

    public boolean e(ItemStack itemstack) {
        return EntityChicken.bF.contains(itemstack.getItem());
    }

    protected int getExpValue(EntityHuman entityhuman) {
        return this.isChickenJockey() ? 10 : super.getExpValue(entityhuman);
    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityInsentient.a(dataconvertermanager, EntityChicken.class);
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.bE = nbttagcompound.getBoolean("IsChickenJockey");
        if (nbttagcompound.hasKey("EggLayTime")) {
            this.bD = nbttagcompound.getInt("EggLayTime");
        }

    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setBoolean("IsChickenJockey", this.bE);
        nbttagcompound.setInt("EggLayTime", this.bD);
    }

    protected boolean isTypeNotPersistent() {
        return this.isChickenJockey() && !this.isVehicle();
    }

    public void k(Entity entity) {
        super.k(entity);
        float f = MathHelper.sin(this.aN * 0.017453292F);
        float f1 = MathHelper.cos(this.aN * 0.017453292F);
        float f2 = 0.1F;
        float f3 = 0.0F;

        entity.setPosition(this.locX + (double) (0.1F * f), this.locY + (double) (this.length * 0.5F) + entity.aF() + 0.0D, this.locZ - (double) (0.1F * f1));
        if (entity instanceof EntityLiving) {
            ((EntityLiving) entity).aN = this.aN;
        }

    }

    public boolean isChickenJockey() {
        return this.bE;
    }

    public void p(boolean flag) {
        this.bE = flag;
    }

    public EntityAgeable createChild(EntityAgeable entityageable) {
        return this.b(entityageable);
    }
}
