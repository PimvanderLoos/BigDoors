package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.Particles;
import net.minecraft.util.MathHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityAreaEffectCloud;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon;
import net.minecraft.world.phys.Vec3D;

public class DragonControllerLandedFlame extends AbstractDragonControllerLanded {

    private static final int FLAME_DURATION = 200;
    private static final int SITTING_FLAME_ATTACKS_COUNT = 4;
    private static final int WARMUP_TIME = 10;
    private int flameTicks;
    private int flameCount;
    @Nullable
    private EntityAreaEffectCloud flame;

    public DragonControllerLandedFlame(EntityEnderDragon entityenderdragon) {
        super(entityenderdragon);
    }

    @Override
    public void doClientTick() {
        ++this.flameTicks;
        if (this.flameTicks % 2 == 0 && this.flameTicks < 10) {
            Vec3D vec3d = this.dragon.getHeadLookVector(1.0F).normalize();

            vec3d.yRot(-0.7853982F);
            double d0 = this.dragon.head.getX();
            double d1 = this.dragon.head.getY(0.5D);
            double d2 = this.dragon.head.getZ();

            for (int i = 0; i < 8; ++i) {
                double d3 = d0 + this.dragon.getRandom().nextGaussian() / 2.0D;
                double d4 = d1 + this.dragon.getRandom().nextGaussian() / 2.0D;
                double d5 = d2 + this.dragon.getRandom().nextGaussian() / 2.0D;

                for (int j = 0; j < 6; ++j) {
                    this.dragon.level.addParticle(Particles.DRAGON_BREATH, d3, d4, d5, -vec3d.x * 0.07999999821186066D * (double) j, -vec3d.y * 0.6000000238418579D, -vec3d.z * 0.07999999821186066D * (double) j);
                }

                vec3d.yRot(0.19634955F);
            }
        }

    }

    @Override
    public void doServerTick() {
        ++this.flameTicks;
        if (this.flameTicks >= 200) {
            if (this.flameCount >= 4) {
                this.dragon.getPhaseManager().setPhase(DragonControllerPhase.TAKEOFF);
            } else {
                this.dragon.getPhaseManager().setPhase(DragonControllerPhase.SITTING_SCANNING);
            }
        } else if (this.flameTicks == 10) {
            Vec3D vec3d = (new Vec3D(this.dragon.head.getX() - this.dragon.getX(), 0.0D, this.dragon.head.getZ() - this.dragon.getZ())).normalize();
            float f = 5.0F;
            double d0 = this.dragon.head.getX() + vec3d.x * 5.0D / 2.0D;
            double d1 = this.dragon.head.getZ() + vec3d.z * 5.0D / 2.0D;
            double d2 = this.dragon.head.getY(0.5D);
            double d3 = d2;
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(d0, d2, d1);

            while (this.dragon.level.isEmptyBlock(blockposition_mutableblockposition)) {
                --d3;
                if (d3 < 0.0D) {
                    d3 = d2;
                    break;
                }

                blockposition_mutableblockposition.set(d0, d3, d1);
            }

            d3 = (double) (MathHelper.floor(d3) + 1);
            this.flame = new EntityAreaEffectCloud(this.dragon.level, d0, d3, d1);
            this.flame.setOwner(this.dragon);
            this.flame.setRadius(5.0F);
            this.flame.setDuration(200);
            this.flame.setParticle(Particles.DRAGON_BREATH);
            this.flame.addEffect(new MobEffect(MobEffects.HARM));
            this.dragon.level.addFreshEntity(this.flame);
        }

    }

    @Override
    public void begin() {
        this.flameTicks = 0;
        ++this.flameCount;
    }

    @Override
    public void end() {
        if (this.flame != null) {
            this.flame.discard();
            this.flame = null;
        }

    }

    @Override
    public DragonControllerPhase<DragonControllerLandedFlame> getPhase() {
        return DragonControllerPhase.SITTING_FLAMING;
    }

    public void resetFlameCount() {
        this.flameCount = 0;
    }
}
