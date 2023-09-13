package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityHorseSkeleton extends EntityHorseAbstract {

    private final PathfinderGoalHorseTrap bG = new PathfinderGoalHorseTrap(this);
    private boolean bH;
    private int bI;

    public EntityHorseSkeleton(World world) {
        super(world);
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(15.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.20000000298023224D);
        this.getAttributeInstance(EntityHorseSkeleton.attributeJumpStrength).setValue(this.dI());
    }

    protected SoundEffect G() {
        super.G();
        return SoundEffects.fR;
    }

    protected SoundEffect bX() {
        super.bX();
        return SoundEffects.fS;
    }

    protected SoundEffect bW() {
        super.bW();
        return SoundEffects.fT;
    }

    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.UNDEAD;
    }

    public double ay() {
        return super.ay() - 0.1875D;
    }

    @Nullable
    protected MinecraftKey J() {
        return LootTables.K;
    }

    public void n() {
        super.n();
        if (this.dh() && this.bI++ >= 18000) {
            this.die();
        }

    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityHorseAbstract.c(dataconvertermanager, EntityHorseSkeleton.class);
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setBoolean("SkeletonTrap", this.dh());
        nbttagcompound.setInt("SkeletonTrapTime", this.bI);
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.p(nbttagcompound.getBoolean("SkeletonTrap"));
        this.bI = nbttagcompound.getInt("SkeletonTrapTime");
    }

    public boolean dh() {
        return this.bH;
    }

    public void p(boolean flag) {
        if (flag != this.bH) {
            this.bH = flag;
            if (flag) {
                this.goalSelector.a(1, this.bG);
            } else {
                this.goalSelector.a((PathfinderGoal) this.bG);
            }

        }
    }

    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);
        boolean flag = !itemstack.isEmpty();

        if (flag && itemstack.getItem() == Items.SPAWN_EGG) {
            return super.a(entityhuman, enumhand);
        } else if (!this.isTamed()) {
            return false;
        } else if (this.isBaby()) {
            return super.a(entityhuman, enumhand);
        } else if (entityhuman.isSneaking()) {
            this.f(entityhuman);
            return true;
        } else if (this.isVehicle()) {
            return super.a(entityhuman, enumhand);
        } else {
            if (flag) {
                if (itemstack.getItem() == Items.SADDLE && !this.dB()) {
                    this.f(entityhuman);
                    return true;
                }

                if (itemstack.a(entityhuman, (EntityLiving) this, enumhand)) {
                    return true;
                }
            }

            this.g(entityhuman);
            return true;
        }
    }
}
