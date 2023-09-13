package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.crafting.RecipeItemStack;

public class PathfinderGoalTempt extends PathfinderGoal {

    private static final PathfinderTargetCondition TEMP_TARGETING = PathfinderTargetCondition.b().a(10.0D).d();
    private final PathfinderTargetCondition targetingConditions;
    protected final EntityCreature mob;
    private final double speedModifier;
    private double px;
    private double py;
    private double pz;
    private double pRotX;
    private double pRotY;
    protected EntityHuman player;
    private int calmDown;
    private boolean isRunning;
    private final RecipeItemStack items;
    private final boolean canScare;

    public PathfinderGoalTempt(EntityCreature entitycreature, double d0, RecipeItemStack recipeitemstack, boolean flag) {
        this.mob = entitycreature;
        this.speedModifier = d0;
        this.items = recipeitemstack;
        this.canScare = flag;
        this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
        this.targetingConditions = PathfinderGoalTempt.TEMP_TARGETING.c().a(this::a);
    }

    @Override
    public boolean a() {
        if (this.calmDown > 0) {
            --this.calmDown;
            return false;
        } else {
            this.player = this.mob.level.a(this.targetingConditions, (EntityLiving) this.mob);
            return this.player != null;
        }
    }

    private boolean a(EntityLiving entityliving) {
        return this.items.test(entityliving.getItemInMainHand()) || this.items.test(entityliving.getItemInOffHand());
    }

    @Override
    public boolean b() {
        if (this.g()) {
            if (this.mob.f((Entity) this.player) < 36.0D) {
                if (this.player.h(this.px, this.py, this.pz) > 0.010000000000000002D) {
                    return false;
                }

                if (Math.abs((double) this.player.getXRot() - this.pRotX) > 5.0D || Math.abs((double) this.player.getYRot() - this.pRotY) > 5.0D) {
                    return false;
                }
            } else {
                this.px = this.player.locX();
                this.py = this.player.locY();
                this.pz = this.player.locZ();
            }

            this.pRotX = (double) this.player.getXRot();
            this.pRotY = (double) this.player.getYRot();
        }

        return this.a();
    }

    protected boolean g() {
        return this.canScare;
    }

    @Override
    public void c() {
        this.px = this.player.locX();
        this.py = this.player.locY();
        this.pz = this.player.locZ();
        this.isRunning = true;
    }

    @Override
    public void d() {
        this.player = null;
        this.mob.getNavigation().o();
        this.calmDown = 100;
        this.isRunning = false;
    }

    @Override
    public void e() {
        this.mob.getControllerLook().a(this.player, (float) (this.mob.fa() + 20), (float) this.mob.eZ());
        if (this.mob.f((Entity) this.player) < 6.25D) {
            this.mob.getNavigation().o();
        } else {
            this.mob.getNavigation().a((Entity) this.player, this.speedModifier);
        }

    }

    public boolean h() {
        return this.isRunning;
    }
}
