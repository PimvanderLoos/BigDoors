package net.minecraft.world.entity;

import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.World;

public interface IEntityAngerable {

    String TAG_ANGER_TIME = "AngerTime";
    String TAG_ANGRY_AT = "AngryAt";

    int getRemainingPersistentAngerTime();

    void setRemainingPersistentAngerTime(int i);

    @Nullable
    UUID getPersistentAngerTarget();

    void setPersistentAngerTarget(@Nullable UUID uuid);

    void startPersistentAngerTimer();

    default void addPersistentAngerSaveData(NBTTagCompound nbttagcompound) {
        nbttagcompound.putInt("AngerTime", this.getRemainingPersistentAngerTime());
        if (this.getPersistentAngerTarget() != null) {
            nbttagcompound.putUUID("AngryAt", this.getPersistentAngerTarget());
        }

    }

    default void readPersistentAngerSaveData(World world, NBTTagCompound nbttagcompound) {
        this.setRemainingPersistentAngerTime(nbttagcompound.getInt("AngerTime"));
        if (world instanceof WorldServer) {
            if (!nbttagcompound.hasUUID("AngryAt")) {
                this.setPersistentAngerTarget((UUID) null);
            } else {
                UUID uuid = nbttagcompound.getUUID("AngryAt");

                this.setPersistentAngerTarget(uuid);
                Entity entity = ((WorldServer) world).getEntity(uuid);

                if (entity != null) {
                    if (entity instanceof EntityInsentient) {
                        this.setLastHurtByMob((EntityInsentient) entity);
                    }

                    if (entity.getType() == EntityTypes.PLAYER) {
                        this.setLastHurtByPlayer((EntityHuman) entity);
                    }

                }
            }
        }
    }

    default void updatePersistentAnger(WorldServer worldserver, boolean flag) {
        EntityLiving entityliving = this.getTarget();
        UUID uuid = this.getPersistentAngerTarget();

        if ((entityliving == null || entityliving.isDeadOrDying()) && uuid != null && worldserver.getEntity(uuid) instanceof EntityInsentient) {
            this.stopBeingAngry();
        } else {
            if (entityliving != null && !Objects.equals(uuid, entityliving.getUUID())) {
                this.setPersistentAngerTarget(entityliving.getUUID());
                this.startPersistentAngerTimer();
            }

            if (this.getRemainingPersistentAngerTime() > 0 && (entityliving == null || entityliving.getType() != EntityTypes.PLAYER || !flag)) {
                this.setRemainingPersistentAngerTime(this.getRemainingPersistentAngerTime() - 1);
                if (this.getRemainingPersistentAngerTime() == 0) {
                    this.stopBeingAngry();
                }
            }

        }
    }

    default boolean isAngryAt(EntityLiving entityliving) {
        return !this.canAttack(entityliving) ? false : (entityliving.getType() == EntityTypes.PLAYER && this.isAngryAtAllPlayers(entityliving.level) ? true : entityliving.getUUID().equals(this.getPersistentAngerTarget()));
    }

    default boolean isAngryAtAllPlayers(World world) {
        return world.getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER) && this.isAngry() && this.getPersistentAngerTarget() == null;
    }

    default boolean isAngry() {
        return this.getRemainingPersistentAngerTime() > 0;
    }

    default void playerDied(EntityHuman entityhuman) {
        if (entityhuman.level.getGameRules().getBoolean(GameRules.RULE_FORGIVE_DEAD_PLAYERS)) {
            if (entityhuman.getUUID().equals(this.getPersistentAngerTarget())) {
                this.stopBeingAngry();
            }
        }
    }

    default void forgetCurrentTargetAndRefreshUniversalAnger() {
        this.stopBeingAngry();
        this.startPersistentAngerTimer();
    }

    default void stopBeingAngry() {
        this.setLastHurtByMob((EntityLiving) null);
        this.setPersistentAngerTarget((UUID) null);
        this.setTarget((EntityLiving) null);
        this.setRemainingPersistentAngerTime(0);
    }

    @Nullable
    EntityLiving getLastHurtByMob();

    void setLastHurtByMob(@Nullable EntityLiving entityliving);

    void setLastHurtByPlayer(@Nullable EntityHuman entityhuman);

    void setTarget(@Nullable EntityLiving entityliving);

    boolean canAttack(EntityLiving entityliving);

    @Nullable
    EntityLiving getTarget();
}
