package net.minecraft.world.entity.boss.enderdragon.phases;

import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.phys.Vec3D;

public class DragonControllerLandedSearch extends AbstractDragonControllerLanded {

    private static final int SITTING_SCANNING_IDLE_TICKS = 100;
    private static final int SITTING_ATTACK_Y_VIEW_RANGE = 10;
    private static final int SITTING_ATTACK_VIEW_RANGE = 20;
    private static final int SITTING_CHARGE_VIEW_RANGE = 150;
    private static final PathfinderTargetCondition CHARGE_TARGETING = PathfinderTargetCondition.forCombat().range(150.0D);
    private final PathfinderTargetCondition scanTargeting;
    private int scanningTime;

    public DragonControllerLandedSearch(EntityEnderDragon entityenderdragon) {
        super(entityenderdragon);
        this.scanTargeting = PathfinderTargetCondition.forCombat().range(20.0D).selector((entityliving) -> {
            return Math.abs(entityliving.getY() - entityenderdragon.getY()) <= 10.0D;
        });
    }

    @Override
    public void doServerTick() {
        ++this.scanningTime;
        EntityHuman entityhuman = this.dragon.level.getNearestPlayer(this.scanTargeting, this.dragon, this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());

        if (entityhuman != null) {
            if (this.scanningTime > 25) {
                this.dragon.getPhaseManager().setPhase(DragonControllerPhase.SITTING_ATTACKING);
            } else {
                Vec3D vec3d = (new Vec3D(entityhuman.getX() - this.dragon.getX(), 0.0D, entityhuman.getZ() - this.dragon.getZ())).normalize();
                Vec3D vec3d1 = (new Vec3D((double) MathHelper.sin(this.dragon.getYRot() * 0.017453292F), 0.0D, (double) (-MathHelper.cos(this.dragon.getYRot() * 0.017453292F)))).normalize();
                float f = (float) vec3d1.dot(vec3d);
                float f1 = (float) (Math.acos((double) f) * 57.2957763671875D) + 0.5F;

                if (f1 < 0.0F || f1 > 10.0F) {
                    double d0 = entityhuman.getX() - this.dragon.head.getX();
                    double d1 = entityhuman.getZ() - this.dragon.head.getZ();
                    double d2 = MathHelper.clamp(MathHelper.wrapDegrees(180.0D - MathHelper.atan2(d0, d1) * 57.2957763671875D - (double) this.dragon.getYRot()), -100.0D, 100.0D);

                    this.dragon.yRotA *= 0.8F;
                    float f2 = (float) Math.sqrt(d0 * d0 + d1 * d1) + 1.0F;
                    float f3 = f2;

                    if (f2 > 40.0F) {
                        f2 = 40.0F;
                    }

                    this.dragon.yRotA = (float) ((double) this.dragon.yRotA + d2 * (double) (0.7F / f2 / f3));
                    this.dragon.setYRot(this.dragon.getYRot() + this.dragon.yRotA);
                }
            }
        } else if (this.scanningTime >= 100) {
            entityhuman = this.dragon.level.getNearestPlayer(DragonControllerLandedSearch.CHARGE_TARGETING, this.dragon, this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
            this.dragon.getPhaseManager().setPhase(DragonControllerPhase.TAKEOFF);
            if (entityhuman != null) {
                this.dragon.getPhaseManager().setPhase(DragonControllerPhase.CHARGING_PLAYER);
                ((DragonControllerCharge) this.dragon.getPhaseManager().getPhase(DragonControllerPhase.CHARGING_PLAYER)).setTarget(new Vec3D(entityhuman.getX(), entityhuman.getY(), entityhuman.getZ()));
            }
        }

    }

    @Override
    public void begin() {
        this.scanningTime = 0;
    }

    @Override
    public DragonControllerPhase<DragonControllerLandedSearch> getPhase() {
        return DragonControllerPhase.SITTING_SCANNING;
    }
}
