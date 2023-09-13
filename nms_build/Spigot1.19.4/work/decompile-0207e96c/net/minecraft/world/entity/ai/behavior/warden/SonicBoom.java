package net.minecraft.world.entity.ai.behavior.warden;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.particles.Particles;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.phys.Vec3D;

public class SonicBoom extends Behavior<Warden> {

    private static final int DISTANCE_XZ = 15;
    private static final int DISTANCE_Y = 20;
    private static final double KNOCKBACK_VERTICAL = 0.5D;
    private static final double KNOCKBACK_HORIZONTAL = 2.5D;
    public static final int COOLDOWN = 40;
    private static final int TICKS_BEFORE_PLAYING_SOUND = MathHelper.ceil(34.0D);
    private static final int DURATION = MathHelper.ceil(60.0F);

    public SonicBoom() {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.SONIC_BOOM_COOLDOWN, MemoryStatus.VALUE_ABSENT, MemoryModuleType.SONIC_BOOM_SOUND_COOLDOWN, MemoryStatus.REGISTERED, MemoryModuleType.SONIC_BOOM_SOUND_DELAY, MemoryStatus.REGISTERED), SonicBoom.DURATION);
    }

    protected boolean checkExtraStartConditions(WorldServer worldserver, Warden warden) {
        return warden.closerThan((Entity) warden.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get(), 15.0D, 20.0D);
    }

    protected boolean canStillUse(WorldServer worldserver, Warden warden, long i) {
        return true;
    }

    protected void start(WorldServer worldserver, Warden warden, long i) {
        warden.getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_COOLING_DOWN, true, (long) SonicBoom.DURATION);
        warden.getBrain().setMemoryWithExpiry(MemoryModuleType.SONIC_BOOM_SOUND_DELAY, Unit.INSTANCE, (long) SonicBoom.TICKS_BEFORE_PLAYING_SOUND);
        worldserver.broadcastEntityEvent(warden, (byte) 62);
        warden.playSound(SoundEffects.WARDEN_SONIC_CHARGE, 3.0F, 1.0F);
    }

    protected void tick(WorldServer worldserver, Warden warden, long i) {
        warden.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).ifPresent((entityliving) -> {
            warden.getLookControl().setLookAt(entityliving.position());
        });
        if (!warden.getBrain().hasMemoryValue(MemoryModuleType.SONIC_BOOM_SOUND_DELAY) && !warden.getBrain().hasMemoryValue(MemoryModuleType.SONIC_BOOM_SOUND_COOLDOWN)) {
            warden.getBrain().setMemoryWithExpiry(MemoryModuleType.SONIC_BOOM_SOUND_COOLDOWN, Unit.INSTANCE, (long) (SonicBoom.DURATION - SonicBoom.TICKS_BEFORE_PLAYING_SOUND));
            Optional optional = warden.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);

            Objects.requireNonNull(warden);
            optional.filter(warden::canTargetEntity).filter((entityliving) -> {
                return warden.closerThan(entityliving, 15.0D, 20.0D);
            }).ifPresent((entityliving) -> {
                Vec3D vec3d = warden.position().add(0.0D, 1.600000023841858D, 0.0D);
                Vec3D vec3d1 = entityliving.getEyePosition().subtract(vec3d);
                Vec3D vec3d2 = vec3d1.normalize();

                for (int j = 1; j < MathHelper.floor(vec3d1.length()) + 7; ++j) {
                    Vec3D vec3d3 = vec3d.add(vec3d2.scale((double) j));

                    worldserver.sendParticles(Particles.SONIC_BOOM, vec3d3.x, vec3d3.y, vec3d3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }

                warden.playSound(SoundEffects.WARDEN_SONIC_BOOM, 3.0F, 1.0F);
                entityliving.hurt(worldserver.damageSources().sonicBoom(warden), 10.0F);
                double d0 = 0.5D * (1.0D - entityliving.getAttributeValue(GenericAttributes.KNOCKBACK_RESISTANCE));
                double d1 = 2.5D * (1.0D - entityliving.getAttributeValue(GenericAttributes.KNOCKBACK_RESISTANCE));

                entityliving.push(vec3d2.x() * d1, vec3d2.y() * d0, vec3d2.z() * d1);
            });
        }
    }

    protected void stop(WorldServer worldserver, Warden warden, long i) {
        setCooldown(warden, 40);
    }

    public static void setCooldown(EntityLiving entityliving, int i) {
        entityliving.getBrain().setMemoryWithExpiry(MemoryModuleType.SONIC_BOOM_COOLDOWN, Unit.INSTANCE, (long) i);
    }
}
