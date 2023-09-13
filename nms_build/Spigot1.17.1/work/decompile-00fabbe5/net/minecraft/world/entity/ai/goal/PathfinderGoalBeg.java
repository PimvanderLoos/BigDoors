package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.EnumHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.animal.EntityWolf;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;

public class PathfinderGoalBeg extends PathfinderGoal {

    private final EntityWolf wolf;
    private EntityHuman player;
    private final World level;
    private final float lookDistance;
    private int lookTime;
    private final PathfinderTargetCondition begTargeting;

    public PathfinderGoalBeg(EntityWolf entitywolf, float f) {
        this.wolf = entitywolf;
        this.level = entitywolf.level;
        this.lookDistance = f;
        this.begTargeting = PathfinderTargetCondition.b().a((double) f);
        this.a(EnumSet.of(PathfinderGoal.Type.LOOK));
    }

    @Override
    public boolean a() {
        this.player = this.level.a(this.begTargeting, (EntityLiving) this.wolf);
        return this.player == null ? false : this.a(this.player);
    }

    @Override
    public boolean b() {
        return !this.player.isAlive() ? false : (this.wolf.f((Entity) this.player) > (double) (this.lookDistance * this.lookDistance) ? false : this.lookTime > 0 && this.a(this.player));
    }

    @Override
    public void c() {
        this.wolf.z(true);
        this.lookTime = 40 + this.wolf.getRandom().nextInt(40);
    }

    @Override
    public void d() {
        this.wolf.z(false);
        this.player = null;
    }

    @Override
    public void e() {
        this.wolf.getControllerLook().a(this.player.locX(), this.player.getHeadY(), this.player.locZ(), 10.0F, (float) this.wolf.eZ());
        --this.lookTime;
    }

    private boolean a(EntityHuman entityhuman) {
        EnumHand[] aenumhand = EnumHand.values();
        int i = aenumhand.length;

        for (int j = 0; j < i; ++j) {
            EnumHand enumhand = aenumhand[j];
            ItemStack itemstack = entityhuman.b(enumhand);

            if (this.wolf.isTamed() && itemstack.a(Items.BONE)) {
                return true;
            }

            if (this.wolf.isBreedItem(itemstack)) {
                return true;
            }
        }

        return false;
    }
}
