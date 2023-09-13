package net.minecraft.world.entity.animal.horse;

import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMonsterType;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;

public class EntityHorseSkeleton extends EntityHorseAbstract {

    private final PathfinderGoalHorseTrap skeletonTrapGoal = new PathfinderGoalHorseTrap(this);
    private static final int TRAP_MAX_LIFE = 18000;
    private boolean isTrap;
    private int trapTime;

    public EntityHorseSkeleton(EntityTypes<? extends EntityHorseSkeleton> entitytypes, World world) {
        super(entitytypes, world);
    }

    public static AttributeProvider.Builder t() {
        return fS().a(GenericAttributes.MAX_HEALTH, 15.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.20000000298023224D);
    }

    @Override
    protected void p() {
        this.getAttributeInstance(GenericAttributes.JUMP_STRENGTH).setValue(this.ga());
    }

    @Override
    protected void fF() {}

    @Override
    protected SoundEffect getSoundAmbient() {
        super.getSoundAmbient();
        return this.a((Tag) TagsFluid.WATER) ? SoundEffects.SKELETON_HORSE_AMBIENT_WATER : SoundEffects.SKELETON_HORSE_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        super.getSoundDeath();
        return SoundEffects.SKELETON_HORSE_DEATH;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        super.getSoundHurt(damagesource);
        return SoundEffects.SKELETON_HORSE_HURT;
    }

    @Override
    protected SoundEffect getSoundSwim() {
        if (this.onGround) {
            if (!this.isVehicle()) {
                return SoundEffects.SKELETON_HORSE_STEP_WATER;
            }

            ++this.gallopSoundCounter;
            if (this.gallopSoundCounter > 5 && this.gallopSoundCounter % 3 == 0) {
                return SoundEffects.SKELETON_HORSE_GALLOP_WATER;
            }

            if (this.gallopSoundCounter <= 5) {
                return SoundEffects.SKELETON_HORSE_STEP_WATER;
            }
        }

        return SoundEffects.SKELETON_HORSE_SWIM;
    }

    @Override
    protected void d(float f) {
        if (this.onGround) {
            super.d(0.3F);
        } else {
            super.d(Math.min(0.1F, f * 25.0F));
        }

    }

    @Override
    protected void fX() {
        if (this.isInWater()) {
            this.playSound(SoundEffects.SKELETON_HORSE_JUMP_WATER, 0.4F, 1.0F);
        } else {
            super.fX();
        }

    }

    @Override
    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.UNDEAD;
    }

    @Override
    public double bl() {
        return super.bl() - 0.1875D;
    }

    @Override
    public void movementTick() {
        super.movementTick();
        if (this.fw() && this.trapTime++ >= 18000) {
            this.die();
        }

    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setBoolean("SkeletonTrap", this.fw());
        nbttagcompound.setInt("SkeletonTrapTime", this.trapTime);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.v(nbttagcompound.getBoolean("SkeletonTrap"));
        this.trapTime = nbttagcompound.getInt("SkeletonTrapTime");
    }

    @Override
    public boolean bC() {
        return true;
    }

    @Override
    protected float ev() {
        return 0.96F;
    }

    public boolean fw() {
        return this.isTrap;
    }

    public void v(boolean flag) {
        if (flag != this.isTrap) {
            this.isTrap = flag;
            if (flag) {
                this.goalSelector.a(1, this.skeletonTrapGoal);
            } else {
                this.goalSelector.a((PathfinderGoal) this.skeletonTrapGoal);
            }

        }
    }

    @Nullable
    @Override
    public EntityAgeable createChild(WorldServer worldserver, EntityAgeable entityageable) {
        return (EntityAgeable) EntityTypes.SKELETON_HORSE.a((World) worldserver);
    }

    @Override
    public EnumInteractionResult b(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (!this.isTamed()) {
            return EnumInteractionResult.PASS;
        } else if (this.isBaby()) {
            return super.b(entityhuman, enumhand);
        } else if (entityhuman.eZ()) {
            this.f(entityhuman);
            return EnumInteractionResult.a(this.level.isClientSide);
        } else if (this.isVehicle()) {
            return super.b(entityhuman, enumhand);
        } else {
            if (!itemstack.isEmpty()) {
                if (itemstack.a(Items.SADDLE) && !this.hasSaddle()) {
                    this.f(entityhuman);
                    return EnumInteractionResult.a(this.level.isClientSide);
                }

                EnumInteractionResult enuminteractionresult = itemstack.a(entityhuman, (EntityLiving) this, enumhand);

                if (enuminteractionresult.a()) {
                    return enuminteractionresult;
                }
            }

            this.h(entityhuman);
            return EnumInteractionResult.a(this.level.isClientSide);
        }
    }
}
