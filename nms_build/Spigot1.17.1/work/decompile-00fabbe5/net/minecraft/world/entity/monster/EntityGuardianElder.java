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

    public static final float ELDER_SIZE_SCALE = EntityTypes.ELDER_GUARDIAN.k() / EntityTypes.GUARDIAN.k();

    public EntityGuardianElder(EntityTypes<? extends EntityGuardianElder> entitytypes, World world) {
        super(entitytypes, world);
        this.setPersistent();
        if (this.randomStrollGoal != null) {
            this.randomStrollGoal.setTimeBetweenMovement(400);
        }

    }

    public static AttributeProvider.Builder n() {
        return EntityGuardian.fw().a(GenericAttributes.MOVEMENT_SPEED, 0.30000001192092896D).a(GenericAttributes.ATTACK_DAMAGE, 8.0D).a(GenericAttributes.MAX_HEALTH, 80.0D);
    }

    @Override
    public int p() {
        return 60;
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return this.aO() ? SoundEffects.ELDER_GUARDIAN_AMBIENT : SoundEffects.ELDER_GUARDIAN_AMBIENT_LAND;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return this.aO() ? SoundEffects.ELDER_GUARDIAN_HURT : SoundEffects.ELDER_GUARDIAN_HURT_LAND;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return this.aO() ? SoundEffects.ELDER_GUARDIAN_DEATH : SoundEffects.ELDER_GUARDIAN_DEATH_LAND;
    }

    @Override
    protected SoundEffect getSoundFlop() {
        return SoundEffects.ELDER_GUARDIAN_FLOP;
    }

    @Override
    protected void mobTick() {
        super.mobTick();
        boolean flag = true;

        if ((this.tickCount + this.getId()) % 1200 == 0) {
            MobEffectList mobeffectlist = MobEffects.DIG_SLOWDOWN;
            List<EntityPlayer> list = ((WorldServer) this.level).a((entityplayer) -> {
                return this.f((Entity) entityplayer) < 2500.0D && entityplayer.gameMode.d();
            });
            boolean flag1 = true;
            boolean flag2 = true;
            boolean flag3 = true;
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                if (!entityplayer.hasEffect(mobeffectlist) || entityplayer.getEffect(mobeffectlist).getAmplifier() < 2 || entityplayer.getEffect(mobeffectlist).getDuration() < 1200) {
                    entityplayer.connection.sendPacket(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.GUARDIAN_ELDER_EFFECT, this.isSilent() ? 0.0F : 1.0F));
                    entityplayer.addEffect(new MobEffect(mobeffectlist, 6000, 2), this);
                }
            }
        }

        if (!this.fl()) {
            this.a(this.getChunkCoordinates(), 16);
        }

    }
}
