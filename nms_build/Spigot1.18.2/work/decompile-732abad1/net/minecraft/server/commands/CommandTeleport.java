package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentAnchor;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.commands.arguments.coordinates.ArgumentRotation;
import net.minecraft.commands.arguments.coordinates.ArgumentVec3;
import net.minecraft.commands.arguments.coordinates.IVectorPosition;
import net.minecraft.commands.arguments.coordinates.VectorPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.protocol.game.PacketPlayOutPosition;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec2F;
import net.minecraft.world.phys.Vec3D;

public class CommandTeleport {

    private static final SimpleCommandExceptionType INVALID_POSITION = new SimpleCommandExceptionType(new ChatMessage("commands.teleport.invalidPosition"));

    public CommandTeleport() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        LiteralCommandNode<CommandListenerWrapper> literalcommandnode = commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("teleport").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.argument("location", ArgumentVec3.vec3()).executes((commandcontext) -> {
            return teleportToPos((CommandListenerWrapper) commandcontext.getSource(), Collections.singleton(((CommandListenerWrapper) commandcontext.getSource()).getEntityOrException()), ((CommandListenerWrapper) commandcontext.getSource()).getLevel(), ArgumentVec3.getCoordinates(commandcontext, "location"), VectorPosition.current(), (CommandTeleport.a) null);
        }))).then(net.minecraft.commands.CommandDispatcher.argument("destination", ArgumentEntity.entity()).executes((commandcontext) -> {
            return teleportToEntity((CommandListenerWrapper) commandcontext.getSource(), Collections.singleton(((CommandListenerWrapper) commandcontext.getSource()).getEntityOrException()), ArgumentEntity.getEntity(commandcontext, "destination"));
        }))).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("targets", ArgumentEntity.entities()).then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("location", ArgumentVec3.vec3()).executes((commandcontext) -> {
            return teleportToPos((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getEntities(commandcontext, "targets"), ((CommandListenerWrapper) commandcontext.getSource()).getLevel(), ArgumentVec3.getCoordinates(commandcontext, "location"), (IVectorPosition) null, (CommandTeleport.a) null);
        })).then(net.minecraft.commands.CommandDispatcher.argument("rotation", ArgumentRotation.rotation()).executes((commandcontext) -> {
            return teleportToPos((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getEntities(commandcontext, "targets"), ((CommandListenerWrapper) commandcontext.getSource()).getLevel(), ArgumentVec3.getCoordinates(commandcontext, "location"), ArgumentRotation.getRotation(commandcontext, "rotation"), (CommandTeleport.a) null);
        }))).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("facing").then(net.minecraft.commands.CommandDispatcher.literal("entity").then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("facingEntity", ArgumentEntity.entity()).executes((commandcontext) -> {
            return teleportToPos((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getEntities(commandcontext, "targets"), ((CommandListenerWrapper) commandcontext.getSource()).getLevel(), ArgumentVec3.getCoordinates(commandcontext, "location"), (IVectorPosition) null, new CommandTeleport.a(ArgumentEntity.getEntity(commandcontext, "facingEntity"), ArgumentAnchor.Anchor.FEET));
        })).then(net.minecraft.commands.CommandDispatcher.argument("facingAnchor", ArgumentAnchor.anchor()).executes((commandcontext) -> {
            return teleportToPos((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getEntities(commandcontext, "targets"), ((CommandListenerWrapper) commandcontext.getSource()).getLevel(), ArgumentVec3.getCoordinates(commandcontext, "location"), (IVectorPosition) null, new CommandTeleport.a(ArgumentEntity.getEntity(commandcontext, "facingEntity"), ArgumentAnchor.getAnchor(commandcontext, "facingAnchor")));
        }))))).then(net.minecraft.commands.CommandDispatcher.argument("facingLocation", ArgumentVec3.vec3()).executes((commandcontext) -> {
            return teleportToPos((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getEntities(commandcontext, "targets"), ((CommandListenerWrapper) commandcontext.getSource()).getLevel(), ArgumentVec3.getCoordinates(commandcontext, "location"), (IVectorPosition) null, new CommandTeleport.a(ArgumentVec3.getVec3(commandcontext, "facingLocation")));
        }))))).then(net.minecraft.commands.CommandDispatcher.argument("destination", ArgumentEntity.entity()).executes((commandcontext) -> {
            return teleportToEntity((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getEntities(commandcontext, "targets"), ArgumentEntity.getEntity(commandcontext, "destination"));
        }))));

        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("tp").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).redirect(literalcommandnode));
    }

    private static int teleportToEntity(CommandListenerWrapper commandlistenerwrapper, Collection<? extends Entity> collection, Entity entity) throws CommandSyntaxException {
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Entity entity1 = (Entity) iterator.next();

            performTeleport(commandlistenerwrapper, entity1, (WorldServer) entity.level, entity.getX(), entity.getY(), entity.getZ(), EnumSet.noneOf(PacketPlayOutPosition.EnumPlayerTeleportFlags.class), entity.getYRot(), entity.getXRot(), (CommandTeleport.a) null);
        }

        if (collection.size() == 1) {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.teleport.success.entity.single", new Object[]{((Entity) collection.iterator().next()).getDisplayName(), entity.getDisplayName()}), true);
        } else {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.teleport.success.entity.multiple", new Object[]{collection.size(), entity.getDisplayName()}), true);
        }

        return collection.size();
    }

    private static int teleportToPos(CommandListenerWrapper commandlistenerwrapper, Collection<? extends Entity> collection, WorldServer worldserver, IVectorPosition ivectorposition, @Nullable IVectorPosition ivectorposition1, @Nullable CommandTeleport.a commandteleport_a) throws CommandSyntaxException {
        Vec3D vec3d = ivectorposition.getPosition(commandlistenerwrapper);
        Vec2F vec2f = ivectorposition1 == null ? null : ivectorposition1.getRotation(commandlistenerwrapper);
        Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> set = EnumSet.noneOf(PacketPlayOutPosition.EnumPlayerTeleportFlags.class);

        if (ivectorposition.isXRelative()) {
            set.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.X);
        }

        if (ivectorposition.isYRelative()) {
            set.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.Y);
        }

        if (ivectorposition.isZRelative()) {
            set.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.Z);
        }

        if (ivectorposition1 == null) {
            set.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.X_ROT);
            set.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.Y_ROT);
        } else {
            if (ivectorposition1.isXRelative()) {
                set.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.X_ROT);
            }

            if (ivectorposition1.isYRelative()) {
                set.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.Y_ROT);
            }
        }

        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            if (ivectorposition1 == null) {
                performTeleport(commandlistenerwrapper, entity, worldserver, vec3d.x, vec3d.y, vec3d.z, set, entity.getYRot(), entity.getXRot(), commandteleport_a);
            } else {
                performTeleport(commandlistenerwrapper, entity, worldserver, vec3d.x, vec3d.y, vec3d.z, set, vec2f.y, vec2f.x, commandteleport_a);
            }
        }

        if (collection.size() == 1) {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.teleport.success.location.single", new Object[]{((Entity) collection.iterator().next()).getDisplayName(), formatDouble(vec3d.x), formatDouble(vec3d.y), formatDouble(vec3d.z)}), true);
        } else {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.teleport.success.location.multiple", new Object[]{collection.size(), formatDouble(vec3d.x), formatDouble(vec3d.y), formatDouble(vec3d.z)}), true);
        }

        return collection.size();
    }

    private static String formatDouble(double d0) {
        return String.format(Locale.ROOT, "%f", d0);
    }

    private static void performTeleport(CommandListenerWrapper commandlistenerwrapper, Entity entity, WorldServer worldserver, double d0, double d1, double d2, Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> set, float f, float f1, @Nullable CommandTeleport.a commandteleport_a) throws CommandSyntaxException {
        BlockPosition blockposition = new BlockPosition(d0, d1, d2);

        if (!World.isInSpawnableBounds(blockposition)) {
            throw CommandTeleport.INVALID_POSITION.create();
        } else {
            float f2 = MathHelper.wrapDegrees(f);
            float f3 = MathHelper.wrapDegrees(f1);

            if (entity instanceof EntityPlayer) {
                ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(new BlockPosition(d0, d1, d2));

                worldserver.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkcoordintpair, 1, entity.getId());
                entity.stopRiding();
                if (((EntityPlayer) entity).isSleeping()) {
                    ((EntityPlayer) entity).stopSleepInBed(true, true);
                }

                if (worldserver == entity.level) {
                    ((EntityPlayer) entity).connection.teleport(d0, d1, d2, f2, f3, set);
                } else {
                    ((EntityPlayer) entity).teleportTo(worldserver, d0, d1, d2, f2, f3);
                }

                entity.setYHeadRot(f2);
            } else {
                float f4 = MathHelper.clamp(f3, -90.0F, 90.0F);

                if (worldserver == entity.level) {
                    entity.moveTo(d0, d1, d2, f2, f4);
                    entity.setYHeadRot(f2);
                } else {
                    entity.unRide();
                    Entity entity1 = entity;

                    entity = entity.getType().create(worldserver);
                    if (entity == null) {
                        return;
                    }

                    entity.restoreFrom(entity1);
                    entity.moveTo(d0, d1, d2, f2, f4);
                    entity.setYHeadRot(f2);
                    entity1.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
                    worldserver.addDuringTeleport(entity);
                }
            }

            if (commandteleport_a != null) {
                commandteleport_a.perform(commandlistenerwrapper, entity);
            }

            if (!(entity instanceof EntityLiving) || !((EntityLiving) entity).isFallFlying()) {
                entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D));
                entity.setOnGround(true);
            }

            if (entity instanceof EntityCreature) {
                ((EntityCreature) entity).getNavigation().stop();
            }

        }
    }

    private static class a {

        private final Vec3D position;
        private final Entity entity;
        private final ArgumentAnchor.Anchor anchor;

        public a(Entity entity, ArgumentAnchor.Anchor argumentanchor_anchor) {
            this.entity = entity;
            this.anchor = argumentanchor_anchor;
            this.position = argumentanchor_anchor.apply(entity);
        }

        public a(Vec3D vec3d) {
            this.entity = null;
            this.position = vec3d;
            this.anchor = null;
        }

        public void perform(CommandListenerWrapper commandlistenerwrapper, Entity entity) {
            if (this.entity != null) {
                if (entity instanceof EntityPlayer) {
                    ((EntityPlayer) entity).lookAt(commandlistenerwrapper.getAnchor(), this.entity, this.anchor);
                } else {
                    entity.lookAt(commandlistenerwrapper.getAnchor(), this.position);
                }
            } else {
                entity.lookAt(commandlistenerwrapper.getAnchor(), this.position);
            }

        }
    }
}
