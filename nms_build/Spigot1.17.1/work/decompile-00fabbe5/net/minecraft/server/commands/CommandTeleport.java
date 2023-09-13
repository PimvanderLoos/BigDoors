package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
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

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        LiteralCommandNode<CommandListenerWrapper> literalcommandnode = commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("teleport").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.a("location", (ArgumentType) ArgumentVec3.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), Collections.singleton(((CommandListenerWrapper) commandcontext.getSource()).g()), ((CommandListenerWrapper) commandcontext.getSource()).getWorld(), ArgumentVec3.b(commandcontext, "location"), VectorPosition.d(), (CommandTeleport.a) null);
        }))).then(net.minecraft.commands.CommandDispatcher.a("destination", (ArgumentType) ArgumentEntity.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), Collections.singleton(((CommandListenerWrapper) commandcontext.getSource()).g()), ArgumentEntity.a(commandcontext, "destination"));
        }))).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("targets", (ArgumentType) ArgumentEntity.multipleEntities()).then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("location", (ArgumentType) ArgumentVec3.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.b(commandcontext, "targets"), ((CommandListenerWrapper) commandcontext.getSource()).getWorld(), ArgumentVec3.b(commandcontext, "location"), (IVectorPosition) null, (CommandTeleport.a) null);
        })).then(net.minecraft.commands.CommandDispatcher.a("rotation", (ArgumentType) ArgumentRotation.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.b(commandcontext, "targets"), ((CommandListenerWrapper) commandcontext.getSource()).getWorld(), ArgumentVec3.b(commandcontext, "location"), ArgumentRotation.a(commandcontext, "rotation"), (CommandTeleport.a) null);
        }))).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("facing").then(net.minecraft.commands.CommandDispatcher.a("entity").then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("facingEntity", (ArgumentType) ArgumentEntity.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.b(commandcontext, "targets"), ((CommandListenerWrapper) commandcontext.getSource()).getWorld(), ArgumentVec3.b(commandcontext, "location"), (IVectorPosition) null, new CommandTeleport.a(ArgumentEntity.a(commandcontext, "facingEntity"), ArgumentAnchor.Anchor.FEET));
        })).then(net.minecraft.commands.CommandDispatcher.a("facingAnchor", (ArgumentType) ArgumentAnchor.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.b(commandcontext, "targets"), ((CommandListenerWrapper) commandcontext.getSource()).getWorld(), ArgumentVec3.b(commandcontext, "location"), (IVectorPosition) null, new CommandTeleport.a(ArgumentEntity.a(commandcontext, "facingEntity"), ArgumentAnchor.a(commandcontext, "facingAnchor")));
        }))))).then(net.minecraft.commands.CommandDispatcher.a("facingLocation", (ArgumentType) ArgumentVec3.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.b(commandcontext, "targets"), ((CommandListenerWrapper) commandcontext.getSource()).getWorld(), ArgumentVec3.b(commandcontext, "location"), (IVectorPosition) null, new CommandTeleport.a(ArgumentVec3.a(commandcontext, "facingLocation")));
        }))))).then(net.minecraft.commands.CommandDispatcher.a("destination", (ArgumentType) ArgumentEntity.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.b(commandcontext, "targets"), ArgumentEntity.a(commandcontext, "destination"));
        }))));

        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("tp").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).redirect(literalcommandnode));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, Collection<? extends Entity> collection, Entity entity) throws CommandSyntaxException {
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Entity entity1 = (Entity) iterator.next();

            a(commandlistenerwrapper, entity1, (WorldServer) entity.level, entity.locX(), entity.locY(), entity.locZ(), EnumSet.noneOf(PacketPlayOutPosition.EnumPlayerTeleportFlags.class), entity.getYRot(), entity.getXRot(), (CommandTeleport.a) null);
        }

        if (collection.size() == 1) {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.teleport.success.entity.single", new Object[]{((Entity) collection.iterator().next()).getScoreboardDisplayName(), entity.getScoreboardDisplayName()}), true);
        } else {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.teleport.success.entity.multiple", new Object[]{collection.size(), entity.getScoreboardDisplayName()}), true);
        }

        return collection.size();
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, Collection<? extends Entity> collection, WorldServer worldserver, IVectorPosition ivectorposition, @Nullable IVectorPosition ivectorposition1, @Nullable CommandTeleport.a commandteleport_a) throws CommandSyntaxException {
        Vec3D vec3d = ivectorposition.a(commandlistenerwrapper);
        Vec2F vec2f = ivectorposition1 == null ? null : ivectorposition1.b(commandlistenerwrapper);
        Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> set = EnumSet.noneOf(PacketPlayOutPosition.EnumPlayerTeleportFlags.class);

        if (ivectorposition.a()) {
            set.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.X);
        }

        if (ivectorposition.b()) {
            set.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.Y);
        }

        if (ivectorposition.c()) {
            set.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.Z);
        }

        if (ivectorposition1 == null) {
            set.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.X_ROT);
            set.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.Y_ROT);
        } else {
            if (ivectorposition1.a()) {
                set.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.X_ROT);
            }

            if (ivectorposition1.b()) {
                set.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.Y_ROT);
            }
        }

        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            if (ivectorposition1 == null) {
                a(commandlistenerwrapper, entity, worldserver, vec3d.x, vec3d.y, vec3d.z, set, entity.getYRot(), entity.getXRot(), commandteleport_a);
            } else {
                a(commandlistenerwrapper, entity, worldserver, vec3d.x, vec3d.y, vec3d.z, set, vec2f.y, vec2f.x, commandteleport_a);
            }
        }

        if (collection.size() == 1) {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.teleport.success.location.single", new Object[]{((Entity) collection.iterator().next()).getScoreboardDisplayName(), a(vec3d.x), a(vec3d.y), a(vec3d.z)}), true);
        } else {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.teleport.success.location.multiple", new Object[]{collection.size(), a(vec3d.x), a(vec3d.y), a(vec3d.z)}), true);
        }

        return collection.size();
    }

    private static String a(double d0) {
        return String.format(Locale.ROOT, "%f", d0);
    }

    private static void a(CommandListenerWrapper commandlistenerwrapper, Entity entity, WorldServer worldserver, double d0, double d1, double d2, Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> set, float f, float f1, @Nullable CommandTeleport.a commandteleport_a) throws CommandSyntaxException {
        BlockPosition blockposition = new BlockPosition(d0, d1, d2);

        if (!World.l(blockposition)) {
            throw CommandTeleport.INVALID_POSITION.create();
        } else {
            float f2 = MathHelper.g(f);
            float f3 = MathHelper.g(f1);

            if (entity instanceof EntityPlayer) {
                ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(new BlockPosition(d0, d1, d2));

                worldserver.getChunkProvider().addTicket(TicketType.POST_TELEPORT, chunkcoordintpair, 1, entity.getId());
                entity.stopRiding();
                if (((EntityPlayer) entity).isSleeping()) {
                    ((EntityPlayer) entity).wakeup(true, true);
                }

                if (worldserver == entity.level) {
                    ((EntityPlayer) entity).connection.a(d0, d1, d2, f2, f3, set);
                } else {
                    ((EntityPlayer) entity).a(worldserver, d0, d1, d2, f2, f3);
                }

                entity.setHeadRotation(f2);
            } else {
                float f4 = MathHelper.a(f3, -90.0F, 90.0F);

                if (worldserver == entity.level) {
                    entity.setPositionRotation(d0, d1, d2, f2, f4);
                    entity.setHeadRotation(f2);
                } else {
                    entity.decouple();
                    Entity entity1 = entity;

                    entity = entity.getEntityType().a((World) worldserver);
                    if (entity == null) {
                        return;
                    }

                    entity.t(entity1);
                    entity.setPositionRotation(d0, d1, d2, f2, f4);
                    entity.setHeadRotation(f2);
                    entity1.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
                    worldserver.addEntityTeleport(entity);
                }
            }

            if (commandteleport_a != null) {
                commandteleport_a.a(commandlistenerwrapper, entity);
            }

            if (!(entity instanceof EntityLiving) || !((EntityLiving) entity).isGliding()) {
                entity.setMot(entity.getMot().d(1.0D, 0.0D, 1.0D));
                entity.setOnGround(true);
            }

            if (entity instanceof EntityCreature) {
                ((EntityCreature) entity).getNavigation().o();
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
            this.position = argumentanchor_anchor.a(entity);
        }

        public a(Vec3D vec3d) {
            this.entity = null;
            this.position = vec3d;
            this.anchor = null;
        }

        public void a(CommandListenerWrapper commandlistenerwrapper, Entity entity) {
            if (this.entity != null) {
                if (entity instanceof EntityPlayer) {
                    ((EntityPlayer) entity).a(commandlistenerwrapper.k(), this.entity, this.anchor);
                } else {
                    entity.a(commandlistenerwrapper.k(), this.position);
                }
            } else {
                entity.a(commandlistenerwrapper.k(), this.position);
            }

        }
    }
}
