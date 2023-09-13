package net.minecraft.server;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import javax.annotation.Nullable;

public final class IEntitySelector {

    public static final Predicate<Entity> a = new Predicate() {
        public boolean a(@Nullable Entity entity) {
            return entity.isAlive();
        }

        public boolean apply(@Nullable Object object) {
            return this.a((Entity) object);
        }
    };
    public static final Predicate<Entity> b = new Predicate() {
        public boolean a(@Nullable Entity entity) {
            return entity.isAlive() && !entity.isVehicle() && !entity.isPassenger();
        }

        public boolean apply(@Nullable Object object) {
            return this.a((Entity) object);
        }
    };
    public static final Predicate<Entity> c = new Predicate() {
        public boolean a(@Nullable Entity entity) {
            return entity instanceof IInventory && entity.isAlive();
        }

        public boolean apply(@Nullable Object object) {
            return this.a((Entity) object);
        }
    };
    public static final Predicate<Entity> d = new Predicate() {
        public boolean a(@Nullable Entity entity) {
            return !(entity instanceof EntityHuman) || !((EntityHuman) entity).isSpectator() && !((EntityHuman) entity).z();
        }

        public boolean apply(@Nullable Object object) {
            return this.a((Entity) object);
        }
    };
    public static final Predicate<Entity> e = new Predicate() {
        public boolean a(@Nullable Entity entity) {
            return !(entity instanceof EntityHuman) || !((EntityHuman) entity).isSpectator();
        }

        public boolean apply(@Nullable Object object) {
            return this.a((Entity) object);
        }
    };

    public static <T extends Entity> Predicate<T> a(final double d0, final double d1, final double d2, double d3) {
        final double d4 = d3 * d3;

        return new Predicate() {
            public boolean a(@Nullable T t0) {
                return t0 != null && t0.d(d0, d1, d2) <= d3;
            }

            public boolean apply(@Nullable Object object) {
                return this.a((Entity) object);
            }
        };
    }

    public static <T extends Entity> Predicate<T> a(final Entity entity) {
        final ScoreboardTeamBase scoreboardteambase = entity.aY();
        final ScoreboardTeamBase.EnumTeamPush scoreboardteambase_enumteampush = scoreboardteambase == null ? ScoreboardTeamBase.EnumTeamPush.ALWAYS : scoreboardteambase.getCollisionRule();

        return scoreboardteambase_enumteampush == ScoreboardTeamBase.EnumTeamPush.NEVER ? Predicates.alwaysFalse() : Predicates.and(IEntitySelector.e, new Predicate() {
            public boolean a(@Nullable Entity entity) {
                if (!entity.isCollidable()) {
                    return false;
                } else if (entity1.world.isClientSide && (!(entity instanceof EntityHuman) || !((EntityHuman) entity).cZ())) {
                    return false;
                } else {
                    ScoreboardTeamBase scoreboardteambase = entity.aY();
                    ScoreboardTeamBase.EnumTeamPush scoreboardteambase_enumteampush = scoreboardteambase == null ? ScoreboardTeamBase.EnumTeamPush.ALWAYS : scoreboardteambase.getCollisionRule();

                    if (scoreboardteambase_enumteampush == ScoreboardTeamBase.EnumTeamPush.NEVER) {
                        return false;
                    } else {
                        boolean flag = scoreboardteambase1 != null && scoreboardteambase1.isAlly(scoreboardteambase);

                        return (scoreboardteambase_enumteampush1 == ScoreboardTeamBase.EnumTeamPush.HIDE_FOR_OWN_TEAM || scoreboardteambase_enumteampush == ScoreboardTeamBase.EnumTeamPush.HIDE_FOR_OWN_TEAM) && flag ? false : scoreboardteambase_enumteampush1 != ScoreboardTeamBase.EnumTeamPush.HIDE_FOR_OTHER_TEAMS && scoreboardteambase_enumteampush != ScoreboardTeamBase.EnumTeamPush.HIDE_FOR_OTHER_TEAMS || flag;
                    }
                }
            }

            public boolean apply(@Nullable Object object) {
                return this.a((Entity) object);
            }
        });
    }

    public static Predicate<Entity> b(final Entity entity) {
        return new Predicate() {
            public boolean a(@Nullable Entity entity) {
                while (true) {
                    if (entity.isPassenger()) {
                        entity = entity.bJ();
                        if (entity != entity1) {
                            continue;
                        }

                        return false;
                    }

                    return true;
                }
            }

            public boolean apply(@Nullable Object object) {
                return this.a((Entity) object);
            }
        };
    }

    public static class EntitySelectorEquipable implements Predicate<Entity> {

        private final ItemStack a;

        public EntitySelectorEquipable(ItemStack itemstack) {
            this.a = itemstack;
        }

        public boolean a(@Nullable Entity entity) {
            if (!entity.isAlive()) {
                return false;
            } else if (!(entity instanceof EntityLiving)) {
                return false;
            } else {
                EntityLiving entityliving = (EntityLiving) entity;

                return !entityliving.getEquipment(EntityInsentient.d(this.a)).isEmpty() ? false : (entityliving instanceof EntityInsentient ? ((EntityInsentient) entityliving).cX() : (entityliving instanceof EntityArmorStand ? true : entityliving instanceof EntityHuman));
            }
        }

        public boolean apply(@Nullable Object object) {
            return this.a((Entity) object);
        }
    }
}
