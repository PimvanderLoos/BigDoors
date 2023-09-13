package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityHorseSkeleton extends EntityHorseAbstract {

    private final PathfinderGoalHorseTrap bH = new PathfinderGoalHorseTrap(this);
    private boolean bI;
    private int bJ;

    public EntityHorseSkeleton(World world) {
        super(world);
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(15.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.20000000298023224D);
        this.getAttributeInstance(EntityHorseSkeleton.attributeJumpStrength).setValue(this.dN());
    }

    protected SoundEffect F() {
        super.F();
        return SoundEffects.gS;
    }

    protected SoundEffect cf() {
        super.cf();
        return SoundEffects.gT;
    }

    protected SoundEffect d(DamageSource damagesource) {
        super.d(damagesource);
        return SoundEffects.gU;
    }

    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.UNDEAD;
    }

    public double aG() {
        return super.aG() - 0.1875D;
    }

    @Nullable
    protected MinecraftKey J() {
        return LootTables.K;
    }

    public void n() {
        super.n();
        if (this.dl() && this.bJ++ >= 18000) {
            this.die();
        }

    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityHorseAbstract.c(dataconvertermanager, EntityHorseSkeleton.class);
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setBoolean("SkeletonTrap", this.dl());
        nbttagcompound.setInt("SkeletonTrapTime", this.bJ);
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.p(nbttagcompound.getBoolean("SkeletonTrap"));
        this.bJ = nbttagcompound.getInt("SkeletonTrapTime");
    }

    public boolean dl() {
        return this.bI;
    }

    public void p(boolean flag) {
        if (flag != this.bI) {
            this.bI = flag;
            if (flag) {
                this.goalSelector.a(1, this.bH);
            } else {
                this.goalSelector.a((PathfinderGoal) this.bH);
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
            this.c(entityhuman);
            return true;
        } else if (this.isVehicle()) {
            return super.a(entityhuman, enumhand);
        } else {
            if (flag) {
                if (itemstack.getItem() == Items.SADDLE && !this.dG()) {
                    this.c(entityhuman);
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
