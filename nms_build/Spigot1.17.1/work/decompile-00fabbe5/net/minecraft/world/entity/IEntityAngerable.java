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

    int getAnger();

    void setAnger(int i);

    @Nullable
    UUID getAngerTarget();

    void setAngerTarget(@Nullable UUID uuid);

    void anger();

    default void c(NBTTagCompound nbttagcompound) {
        nbttagcompound.setInt("AngerTime", this.getAnger());
        if (this.getAngerTarget() != null) {
            nbttagcompound.a("AngryAt", this.getAngerTarget());
        }

    }

    default void a(World world, NBTTagCompound nbttagcompound) {
        this.setAnger(nbttagcompound.getInt("AngerTime"));
        if (world instanceof WorldServer) {
            if (!nbttagcompound.b("AngryAt")) {
                this.setAngerTarget((UUID) null);
            } else {
                UUID uuid = nbttagcompound.a("AngryAt");

                this.setAngerTarget(uuid);
                Entity entity = ((WorldServer) world).getEntity(uuid);

                if (entity != null) {
                    if (entity instanceof EntityInsentient) {
                        this.setLastDamager((EntityInsentient) entity);
                    }

                    if (entity.getEntityType() == EntityTypes.PLAYER) {
                        this.e((EntityHuman) entity);
                    }

                }
            }
        }
    }

    default void a(WorldServer worldserver, boolean flag) {
        EntityLiving entityliving = this.getGoalTarget();
        UUID uuid = this.getAngerTarget();

        if ((entityliving == null || entityliving.dV()) && uuid != null && worldserver.getEntity(uuid) instanceof EntityInsentient) {
            this.pacify();
        } else {
            if (entityliving != null && !Objects.equals(uuid, entityliving.getUniqueID())) {
                this.setAngerTarget(entityliving.getUniqueID());
                this.anger();
            }

            if (this.getAnger() > 0 && (entityliving == null || entityliving.getEntityType() != EntityTypes.PLAYER || !flag)) {
                this.setAnger(this.getAnger() - 1);
                if (this.getAnger() == 0) {
                    this.pacify();
                }
            }

        }
    }

    default boolean a_(EntityLiving entityliving) {
        return !this.c(entityliving) ? false : (entityliving.getEntityType() == EntityTypes.PLAYER && this.b(entityliving.level) ? true : entityliving.getUniqueID().equals(this.getAngerTarget()));
    }

    default boolean b(World world) {
        return world.getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER) && this.isAngry() && this.getAngerTarget() == null;
    }

    default boolean isAngry() {
        return this.getAnger() > 0;
    }

    default void a_(EntityHuman entityhuman) {
        if (entityhuman.level.getGameRules().getBoolean(GameRules.RULE_FORGIVE_DEAD_PLAYERS)) {
            if (entityhuman.getUniqueID().equals(this.getAngerTarget())) {
                this.pacify();
            }
        }
    }

    default void H_() {
        this.pacify();
        this.anger();
    }

    default void pacify() {
        this.setLastDamager((EntityLiving) null);
        this.setAngerTarget((UUID) null);
        this.setGoalTarget((EntityLiving) null);
        this.setAnger(0);
    }

    @Nullable
    EntityLiving getLastDamager();

    void setLastDamager(@Nullable EntityLiving entityliving);

    void e(@Nullable EntityHuman entityhuman);

    void setGoalTarget(@Nullable EntityLiving entityliving);

    boolean c(EntityLiving entityliving);

    @Nullable
    EntityLiving getGoalTarget();
}
