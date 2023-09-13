package net.minecraft.server;

import com.google.common.base.Predicate;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class EntityGuardianElder extends EntityGuardian {

    public EntityGuardianElder(World world) {
        super(world);
        this.setSize(this.width * 2.35F, this.length * 2.35F);
        this.cW();
        if (this.goalRandomStroll != null) {
            this.goalRandomStroll.setTimeBetweenMovement(400);
        }

    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.30000001192092896D);
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(8.0D);
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(80.0D);
    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityInsentient.a(dataconvertermanager, EntityGuardianElder.class);
    }

    @Nullable
    protected MinecraftKey J() {
        return LootTables.y;
    }

    public int p() {
        return 60;
    }

    protected SoundEffect F() {
        return this.isInWater() ? SoundEffects.aI : SoundEffects.aJ;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return this.isInWater() ? SoundEffects.aO : SoundEffects.aP;
    }

    protected SoundEffect cf() {
        return this.isInWater() ? SoundEffects.aL : SoundEffects.aM;
    }

    protected SoundEffect dn() {
        return SoundEffects.aN;
    }

    protected void M() {
        super.M();
        boolean flag = true;

        if ((this.ticksLived + this.getId()) % 1200 == 0) {
            MobEffectList mobeffectlist = MobEffects.SLOWER_DIG;
            List list = this.world.b(EntityPlayer.class, new Predicate() {
                public boolean a(@Nullable EntityPlayer entityplayer) {
                    return EntityGuardianElder.this.h(entityplayer) < 2500.0D && entityplayer.playerInteractManager.c();
                }

                public boolean apply(@Nullable Object object) {
                    return this.a((EntityPlayer) object);
                }
            });
            boolean flag1 = true;
            boolean flag2 = true;
            boolean flag3 = true;
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                if (!entityplayer.hasEffect(mobeffectlist) || entityplayer.getEffect(mobeffectlist).getAmplifier() < 2 || entityplayer.getEffect(mobeffectlist).getDuration() < 1200) {
                    entityplayer.playerConnection.sendPacket(new PacketPlayOutGameStateChange(10, 0.0F));
                    entityplayer.addEffect(new MobEffect(mobeffectlist, 6000, 2));
                }
            }
        }

        if (!this.dj()) {
            this.a(new BlockPosition(this), 16);
        }

    }
}
