package net.minecraft.world.entity.monster;

import java.util.Iterator;
import java.util.List;
import net.minecraft.network.protocol.game.PacketPlayOutGameStateChange;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectList;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.level.World;

public class EntityGuardianElder extends EntityGuardian {

    public static final float ELDER_SIZE_SCALE = EntityTypes.ELDER_GUARDIAN.getWidth() / EntityTypes.GUARDIAN.getWidth();

    public EntityGuardianElder(EntityTypes<? extends EntityGuardianElder> entitytypes, World world) {
        super(entitytypes, world);
        this.setPersistenceRequired();
        if (this.randomStrollGoal != null) {
            this.randomStrollGoal.setInterval(400);
        }

    }

    public static AttributeProvider.Builder createAttributes() {
        return EntityGuardian.createAttributes().add(GenericAttributes.MOVEMENT_SPEED, 0.30000001192092896D).add(GenericAttributes.ATTACK_DAMAGE, 8.0D).add(GenericAttributes.MAX_HEALTH, 80.0D);
    }

    @Override
    public int getAttackDuration() {
        return 60;
    }

    @Override
    protected SoundEffect getAmbientSound() {
        return this.isInWaterOrBubble() ? SoundEffects.ELDER_GUARDIAN_AMBIENT : SoundEffects.ELDER_GUARDIAN_AMBIENT_LAND;
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return this.isInWaterOrBubble() ? SoundEffects.ELDER_GUARDIAN_HURT : SoundEffects.ELDER_GUARDIAN_HURT_LAND;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return this.isInWaterOrBubble() ? SoundEffects.ELDER_GUARDIAN_DEATH : SoundEffects.ELDER_GUARDIAN_DEATH_LAND;
    }

    @Override
    protected SoundEffect getFlopSound() {
        return SoundEffects.ELDER_GUARDIAN_FLOP;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        boolean flag = true;

        if ((this.tickCount + this.getId()) % 1200 == 0) {
            MobEffectList mobeffectlist = MobEffects.DIG_SLOWDOWN;
            List<EntityPlayer> list = ((WorldServer) this.level).getPlayers((entityplayer) -> {
                return this.distanceToSqr((Entity) entityplayer) < 2500.0D && entityplayer.gameMode.isSurvival();
            });
            boolean flag1 = true;
            boolean flag2 = true;
            boolean flag3 = true;
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                if (!entityplayer.hasEffect(mobeffectlist) || entityplayer.getEffect(mobeffectlist).getAmplifier() < 2 || entityplayer.getEffect(mobeffectlist).getDuration() < 1200) {
                    entityplayer.connection.send(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.GUARDIAN_ELDER_EFFECT, this.isSilent() ? 0.0F : 1.0F));
                    entityplayer.addEffect(new MobEffect(mobeffectlist, 6000, 2), this);
                }
            }
        }

        if (!this.hasRestriction()) {
            this.restrictTo(this.blockPosition(), 16);
        }

    }
}
