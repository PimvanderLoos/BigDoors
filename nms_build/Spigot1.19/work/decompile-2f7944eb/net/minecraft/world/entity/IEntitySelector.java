package net.minecraft.world.entity;

import com.google.common.base.Predicates;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.IInventory;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.scores.ScoreboardTeamBase;

public final class IEntitySelector {

    public static final Predicate<Entity> ENTITY_STILL_ALIVE = Entity::isAlive;
    public static final Predicate<Entity> LIVING_ENTITY_STILL_ALIVE = (entity) -> {
        return entity.isAlive() && entity instanceof EntityLiving;
    };
    public static final Predicate<Entity> ENTITY_NOT_BEING_RIDDEN = (entity) -> {
        return entity.isAlive() && !entity.isVehicle() && !entity.isPassenger();
    };
    public static final Predicate<Entity> CONTAINER_ENTITY_SELECTOR = (entity) -> {
        return entity instanceof IInventory && entity.isAlive();
    };
    public static final Predicate<Entity> NO_CREATIVE_OR_SPECTATOR = (entity) -> {
        return !(entity instanceof EntityHuman) || !entity.isSpectator() && !((EntityHuman) entity).isCreative();
    };
    public static final Predicate<Entity> NO_SPECTATORS = (entity) -> {
        return !entity.isSpectator();
    };
    public static final Predicate<Entity> CAN_BE_COLLIDED_WITH = IEntitySelector.NO_SPECTATORS.and(Entity::canBeCollidedWith);

    private IEntitySelector() {}

    public static Predicate<Entity> withinDistance(double d0, double d1, double d2, double d3) {
        double d4 = d3 * d3;

        return (entity) -> {
            return entity != null && entity.distanceToSqr(d0, d1, d2) <= d4;
        };
    }

    public static Predicate<Entity> pushableBy(Entity entity) {
        ScoreboardTeamBase scoreboardteambase = entity.getTeam();
        ScoreboardTeamBase.EnumTeamPush scoreboardteambase_enumteampush = scoreboardteambase == null ? ScoreboardTeamBase.EnumTeamPush.ALWAYS : scoreboardteambase.getCollisionRule();

        return (Predicate) (scoreboardteambase_enumteampush == ScoreboardTeamBase.EnumTeamPush.NEVER ? Predicates.alwaysFalse() : IEntitySelector.NO_SPECTATORS.and((entity1) -> {
            if (!entity1.isPushable()) {
                return false;
            } else if (entity.level.isClientSide && (!(entity1 instanceof EntityHuman) || !((EntityHuman) entity1).isLocalPlayer())) {
                return false;
            } else {
                ScoreboardTeamBase scoreboardteambase1 = entity1.getTeam();
                ScoreboardTeamBase.EnumTeamPush scoreboardteambase_enumteampush1 = scoreboardteambase1 == null ? ScoreboardTeamBase.EnumTeamPush.ALWAYS : scoreboardteambase1.getCollisionRule();

                if (scoreboardteambase_enumteampush1 == ScoreboardTeamBase.EnumTeamPush.NEVER) {
                    return false;
                } else {
                    boolean flag = scoreboardteambase != null && scoreboardteambase.isAlliedTo(scoreboardteambase1);

                    return (scoreboardteambase_enumteampush == ScoreboardTeamBase.EnumTeamPush.PUSH_OWN_TEAM || scoreboardteambase_enumteampush1 == ScoreboardTeamBase.EnumTeamPush.PUSH_OWN_TEAM) && flag ? false : scoreboardteambase_enumteampush != ScoreboardTeamBase.EnumTeamPush.PUSH_OTHER_TEAMS && scoreboardteambase_enumteampush1 != ScoreboardTeamBase.EnumTeamPush.PUSH_OTHER_TEAMS || flag;
                }
            }
        }));
    }

    public static Predicate<Entity> notRiding(Entity entity) {
        return (entity1) -> {
            while (true) {
                if (entity1.isPassenger()) {
                    entity1 = entity1.getVehicle();
                    if (entity1 != entity) {
                        continue;
                    }

                    return false;
                }

                return true;
            }
        };
    }

    public static class EntitySelectorEquipable implements Predicate<Entity> {

        private final ItemStack itemStack;

        public EntitySelectorEquipable(ItemStack itemstack) {
            this.itemStack = itemstack;
        }

        public boolean test(@Nullable Entity entity) {
            if (!entity.isAlive()) {
                return false;
            } else if (!(entity instanceof EntityLiving)) {
                return false;
            } else {
                EntityLiving entityliving = (EntityLiving) entity;

                return entityliving.canTakeItem(this.itemStack);
            }
        }
    }
}
