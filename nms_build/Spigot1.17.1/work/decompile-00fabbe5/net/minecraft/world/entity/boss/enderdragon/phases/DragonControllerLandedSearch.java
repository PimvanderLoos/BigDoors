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
    private static final PathfinderTargetCondition CHARGE_TARGETING = PathfinderTargetCondition.a().a(150.0D);
    private final PathfinderTargetCondition scanTargeting;
    private int scanningTime;

    public DragonControllerLandedSearch(EntityEnderDragon entityenderdragon) {
        super(entityenderdragon);
        this.scanTargeting = PathfinderTargetCondition.a().a(20.0D).a((entityliving) -> {
            return Math.abs(entityliving.locY() - entityenderdragon.locY()) <= 10.0D;
        });
    }

    @Override
    public void c() {
        ++this.scanningTime;
        EntityHuman entityhuman = this.dragon.level.a(this.scanTargeting, this.dragon, this.dragon.locX(), this.dragon.locY(), this.dragon.locZ());

        if (entityhuman != null) {
            if (this.scanningTime > 25) {
                this.dragon.getDragonControllerManager().setControllerPhase(DragonControllerPhase.SITTING_ATTACKING);
            } else {
                Vec3D vec3d = (new Vec3D(entityhuman.locX() - this.dragon.locX(), 0.0D, entityhuman.locZ() - this.dragon.locZ())).d();
                Vec3D vec3d1 = (new Vec3D((double) MathHelper.sin(this.dragon.getYRot() * 0.017453292F), 0.0D, (double) (-MathHelper.cos(this.dragon.getYRot() * 0.017453292F)))).d();
                float f = (float) vec3d1.b(vec3d);
                float f1 = (float) (Math.acos((double) f) * 57.2957763671875D) + 0.5F;

                if (f1 < 0.0F || f1 > 10.0F) {
                    double d0 = entityhuman.locX() - this.dragon.head.locX();
                    double d1 = entityhuman.locZ() - this.dragon.head.locZ();
                    double d2 = MathHelper.a(MathHelper.f(180.0D - MathHelper.d(d0, d1) * 57.2957763671875D - (double) this.dragon.getYRot()), -100.0D, 100.0D);

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
            entityhuman = this.dragon.level.a(DragonControllerLandedSearch.CHARGE_TARGETING, this.dragon, this.dragon.locX(), this.dragon.locY(), this.dragon.locZ());
            this.dragon.getDragonControllerManager().setControllerPhase(DragonControllerPhase.TAKEOFF);
            if (entityhuman != null) {
                this.dragon.getDragonControllerManager().setControllerPhase(DragonControllerPhase.CHARGING_PLAYER);
                ((DragonControllerCharge) this.dragon.getDragonControllerManager().b(DragonControllerPhase.CHARGING_PLAYER)).a(new Vec3D(entityhuman.locX(), entityhuman.locY(), entityhuman.locZ()));
            }
        }

    }

    @Override
    public void d() {
        this.scanningTime = 0;
    }

    @Override
    public DragonControllerPhase<DragonControllerLandedSearch> getControllerPhase() {
        return DragonControllerPhase.SITTING_SCANNING;
    }
}
