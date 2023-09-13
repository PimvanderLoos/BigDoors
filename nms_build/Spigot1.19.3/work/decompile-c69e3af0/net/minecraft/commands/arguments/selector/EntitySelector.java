package net.minecraft.commands.arguments.selector;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.CriterionConditionValue;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.network.chat.ChatComponentUtils;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;

public class EntitySelector {

    public static final int INFINITE = Integer.MAX_VALUE;
    public static final BiConsumer<Vec3D, List<? extends Entity>> ORDER_ARBITRARY = (vec3d, list) -> {
    };
    private static final EntityTypeTest<Entity, ?> ANY_TYPE = new EntityTypeTest<Entity, Entity>() {
        public Entity tryCast(Entity entity) {
            return entity;
        }

        @Override
        public Class<? extends Entity> getBaseClass() {
            return Entity.class;
        }
    };
    private final int maxResults;
    private final boolean includesEntities;
    private final boolean worldLimited;
    private final Predicate<Entity> predicate;
    private final CriterionConditionValue.DoubleRange range;
    private final Function<Vec3D, Vec3D> position;
    @Nullable
    private final AxisAlignedBB aabb;
    private final BiConsumer<Vec3D, List<? extends Entity>> order;
    private final boolean currentEntity;
    @Nullable
    private final String playerName;
    @Nullable
    private final UUID entityUUID;
    private final EntityTypeTest<Entity, ?> type;
    private final boolean usesSelector;

    public EntitySelector(int i, boolean flag, boolean flag1, Predicate<Entity> predicate, CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange, Function<Vec3D, Vec3D> function, @Nullable AxisAlignedBB axisalignedbb, BiConsumer<Vec3D, List<? extends Entity>> biconsumer, boolean flag2, @Nullable String s, @Nullable UUID uuid, @Nullable EntityTypes<?> entitytypes, boolean flag3) {
        this.maxResults = i;
        this.includesEntities = flag;
        this.worldLimited = flag1;
        this.predicate = predicate;
        this.range = criterionconditionvalue_doublerange;
        this.position = function;
        this.aabb = axisalignedbb;
        this.order = biconsumer;
        this.currentEntity = flag2;
        this.playerName = s;
        this.entityUUID = uuid;
        this.type = (EntityTypeTest) (entitytypes == null ? EntitySelector.ANY_TYPE : entitytypes);
        this.usesSelector = flag3;
    }

    public int getMaxResults() {
        return this.maxResults;
    }

    public boolean includesEntities() {
        return this.includesEntities;
    }

    public boolean isSelfSelector() {
        return this.currentEntity;
    }

    public boolean isWorldLimited() {
        return this.worldLimited;
    }

    public boolean usesSelector() {
        return this.usesSelector;
    }

    private void checkPermissions(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException {
        if (this.usesSelector && !commandlistenerwrapper.hasPermission(2)) {
            throw ArgumentEntity.ERROR_SELECTORS_NOT_ALLOWED.create();
        }
    }

    public Entity findSingleEntity(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException {
        this.checkPermissions(commandlistenerwrapper);
        List<? extends Entity> list = this.findEntities(commandlistenerwrapper);

        if (list.isEmpty()) {
            throw ArgumentEntity.NO_ENTITIES_FOUND.create();
        } else if (list.size() > 1) {
            throw ArgumentEntity.ERROR_NOT_SINGLE_ENTITY.create();
        } else {
            return (Entity) list.get(0);
        }
    }

    public List<? extends Entity> findEntities(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException {
        return this.findEntitiesRaw(commandlistenerwrapper).stream().filter((entity) -> {
            return entity.getType().isEnabled(commandlistenerwrapper.enabledFeatures());
        }).toList();
    }

    private List<? extends Entity> findEntitiesRaw(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException {
        this.checkPermissions(commandlistenerwrapper);
        if (!this.includesEntities) {
            return this.findPlayers(commandlistenerwrapper);
        } else if (this.playerName != null) {
            EntityPlayer entityplayer = commandlistenerwrapper.getServer().getPlayerList().getPlayerByName(this.playerName);

            return (List) (entityplayer == null ? Collections.emptyList() : Lists.newArrayList(new EntityPlayer[]{entityplayer}));
        } else if (this.entityUUID != null) {
            Iterator iterator = commandlistenerwrapper.getServer().getAllLevels().iterator();

            Entity entity;

            do {
                if (!iterator.hasNext()) {
                    return Collections.emptyList();
                }

                WorldServer worldserver = (WorldServer) iterator.next();

                entity = worldserver.getEntity(this.entityUUID);
            } while (entity == null);

            return Lists.newArrayList(new Entity[]{entity});
        } else {
            Vec3D vec3d = (Vec3D) this.position.apply(commandlistenerwrapper.getPosition());
            Predicate<Entity> predicate = this.getPredicate(vec3d);

            if (this.currentEntity) {
                return (List) (commandlistenerwrapper.getEntity() != null && predicate.test(commandlistenerwrapper.getEntity()) ? Lists.newArrayList(new Entity[]{commandlistenerwrapper.getEntity()}) : Collections.emptyList());
            } else {
                List<Entity> list = Lists.newArrayList();

                if (this.isWorldLimited()) {
                    this.addEntities(list, commandlistenerwrapper.getLevel(), vec3d, predicate);
                } else {
                    Iterator iterator1 = commandlistenerwrapper.getServer().getAllLevels().iterator();

                    while (iterator1.hasNext()) {
                        WorldServer worldserver1 = (WorldServer) iterator1.next();

                        this.addEntities(list, worldserver1, vec3d, predicate);
                    }
                }

                return this.sortAndLimit(vec3d, list);
            }
        }
    }

    private void addEntities(List<Entity> list, WorldServer worldserver, Vec3D vec3d, Predicate<Entity> predicate) {
        int i = this.getResultLimit();

        if (list.size() < i) {
            if (this.aabb != null) {
                worldserver.getEntities(this.type, this.aabb.move(vec3d), predicate, list, i);
            } else {
                worldserver.getEntities(this.type, predicate, list, i);
            }

        }
    }

    private int getResultLimit() {
        return this.order == EntitySelector.ORDER_ARBITRARY ? this.maxResults : Integer.MAX_VALUE;
    }

    public EntityPlayer findSinglePlayer(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException {
        this.checkPermissions(commandlistenerwrapper);
        List<EntityPlayer> list = this.findPlayers(commandlistenerwrapper);

        if (list.size() != 1) {
            throw ArgumentEntity.NO_PLAYERS_FOUND.create();
        } else {
            return (EntityPlayer) list.get(0);
        }
    }

    public List<EntityPlayer> findPlayers(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException {
        this.checkPermissions(commandlistenerwrapper);
        EntityPlayer entityplayer;

        if (this.playerName != null) {
            entityplayer = commandlistenerwrapper.getServer().getPlayerList().getPlayerByName(this.playerName);
            return (List) (entityplayer == null ? Collections.emptyList() : Lists.newArrayList(new EntityPlayer[]{entityplayer}));
        } else if (this.entityUUID != null) {
            entityplayer = commandlistenerwrapper.getServer().getPlayerList().getPlayer(this.entityUUID);
            return (List) (entityplayer == null ? Collections.emptyList() : Lists.newArrayList(new EntityPlayer[]{entityplayer}));
        } else {
            Vec3D vec3d = (Vec3D) this.position.apply(commandlistenerwrapper.getPosition());
            Predicate<Entity> predicate = this.getPredicate(vec3d);

            if (this.currentEntity) {
                if (commandlistenerwrapper.getEntity() instanceof EntityPlayer) {
                    EntityPlayer entityplayer1 = (EntityPlayer) commandlistenerwrapper.getEntity();

                    if (predicate.test(entityplayer1)) {
                        return Lists.newArrayList(new EntityPlayer[]{entityplayer1});
                    }
                }

                return Collections.emptyList();
            } else {
                int i = this.getResultLimit();
                Object object;

                if (this.isWorldLimited()) {
                    object = commandlistenerwrapper.getLevel().getPlayers(predicate, i);
                } else {
                    object = Lists.newArrayList();
                    Iterator iterator = commandlistenerwrapper.getServer().getPlayerList().getPlayers().iterator();

                    while (iterator.hasNext()) {
                        EntityPlayer entityplayer2 = (EntityPlayer) iterator.next();

                        if (predicate.test(entityplayer2)) {
                            ((List) object).add(entityplayer2);
                            if (((List) object).size() >= i) {
                                return (List) object;
                            }
                        }
                    }
                }

                return this.sortAndLimit(vec3d, (List) object);
            }
        }
    }

    private Predicate<Entity> getPredicate(Vec3D vec3d) {
        Predicate<Entity> predicate = this.predicate;

        if (this.aabb != null) {
            AxisAlignedBB axisalignedbb = this.aabb.move(vec3d);

            predicate = predicate.and((entity) -> {
                return axisalignedbb.intersects(entity.getBoundingBox());
            });
        }

        if (!this.range.isAny()) {
            predicate = predicate.and((entity) -> {
                return this.range.matchesSqr(entity.distanceToSqr(vec3d));
            });
        }

        return predicate;
    }

    private <T extends Entity> List<T> sortAndLimit(Vec3D vec3d, List<T> list) {
        if (list.size() > 1) {
            this.order.accept(vec3d, list);
        }

        return list.subList(0, Math.min(this.maxResults, list.size()));
    }

    public static IChatBaseComponent joinNames(List<? extends Entity> list) {
        return ChatComponentUtils.formatList(list, Entity::getDisplayName);
    }
}
