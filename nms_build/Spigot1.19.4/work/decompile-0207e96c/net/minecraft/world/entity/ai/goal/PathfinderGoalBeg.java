package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.world.EnumHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.animal.EntityWolf;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;

public class PathfinderGoalBeg extends PathfinderGoal {

    private final EntityWolf wolf;
    @Nullable
    private EntityHuman player;
    private final World level;
    private final float lookDistance;
    private int lookTime;
    private final PathfinderTargetCondition begTargeting;

    public PathfinderGoalBeg(EntityWolf entitywolf, float f) {
        this.wolf = entitywolf;
        this.level = entitywolf.level;
        this.lookDistance = f;
        this.begTargeting = PathfinderTargetCondition.forNonCombat().range((double) f);
        this.setFlags(EnumSet.of(PathfinderGoal.Type.LOOK));
    }

    @Override
    public boolean canUse() {
        this.player = this.level.getNearestPlayer(this.begTargeting, this.wolf);
        return this.player == null ? false : this.playerHoldingInteresting(this.player);
    }

    @Override
    public boolean canContinueToUse() {
        return !this.player.isAlive() ? false : (this.wolf.distanceToSqr((Entity) this.player) > (double) (this.lookDistance * this.lookDistance) ? false : this.lookTime > 0 && this.playerHoldingInteresting(this.player));
    }

    @Override
    public void start() {
        this.wolf.setIsInterested(true);
        this.lookTime = this.adjustedTickDelay(40 + this.wolf.getRandom().nextInt(40));
    }

    @Override
    public void stop() {
        this.wolf.setIsInterested(false);
        this.player = null;
    }

    @Override
    public void tick() {
        this.wolf.getLookControl().setLookAt(this.player.getX(), this.player.getEyeY(), this.player.getZ(), 10.0F, (float) this.wolf.getMaxHeadXRot());
        --this.lookTime;
    }

    private boolean playerHoldingInteresting(EntityHuman entityhuman) {
        EnumHand[] aenumhand = EnumHand.values();
        int i = aenumhand.length;

        for (int j = 0; j < i; ++j) {
            EnumHand enumhand = aenumhand[j];
            ItemStack itemstack = entityhuman.getItemInHand(enumhand);

            if (this.wolf.isTame() && itemstack.is(Items.BONE)) {
                return true;
            }

            if (this.wolf.isFood(itemstack)) {
                return true;
            }
        }

        return false;
    }
}
