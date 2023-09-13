package net.minecraft.world.effect;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.UtilColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.phys.Vec3D;

public final class MobEffectUtil {

    public MobEffectUtil() {}

    public static IChatBaseComponent formatDuration(MobEffect mobeffect, float f) {
        if (mobeffect.isInfiniteDuration()) {
            return IChatBaseComponent.translatable("effect.duration.infinite");
        } else {
            int i = MathHelper.floor((float) mobeffect.getDuration() * f);

            return IChatBaseComponent.literal(UtilColor.formatTickDuration(i));
        }
    }

    public static boolean hasDigSpeed(EntityLiving entityliving) {
        return entityliving.hasEffect(MobEffects.DIG_SPEED) || entityliving.hasEffect(MobEffects.CONDUIT_POWER);
    }

    public static int getDigSpeedAmplification(EntityLiving entityliving) {
        int i = 0;
        int j = 0;

        if (entityliving.hasEffect(MobEffects.DIG_SPEED)) {
            i = entityliving.getEffect(MobEffects.DIG_SPEED).getAmplifier();
        }

        if (entityliving.hasEffect(MobEffects.CONDUIT_POWER)) {
            j = entityliving.getEffect(MobEffects.CONDUIT_POWER).getAmplifier();
        }

        return Math.max(i, j);
    }

    public static boolean hasWaterBreathing(EntityLiving entityliving) {
        return entityliving.hasEffect(MobEffects.WATER_BREATHING) || entityliving.hasEffect(MobEffects.CONDUIT_POWER);
    }

    public static List<EntityPlayer> addEffectToPlayersAround(WorldServer worldserver, @Nullable Entity entity, Vec3D vec3d, double d0, MobEffect mobeffect, int i) {
        MobEffectList mobeffectlist = mobeffect.getEffect();
        List<EntityPlayer> list = worldserver.getPlayers((entityplayer) -> {
            return entityplayer.gameMode.isSurvival() && (entity == null || !entity.isAlliedTo((Entity) entityplayer)) && vec3d.closerThan(entityplayer.position(), d0) && (!entityplayer.hasEffect(mobeffectlist) || entityplayer.getEffect(mobeffectlist).getAmplifier() < mobeffect.getAmplifier() || entityplayer.getEffect(mobeffectlist).endsWithin(i - 1));
        });

        list.forEach((entityplayer) -> {
            entityplayer.addEffect(new MobEffect(mobeffect), entity);
        });
        return list;
    }
}
