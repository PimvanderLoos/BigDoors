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

    private static final SimpleCommandExceptionType a = new SimpleCommandExceptionType(new ChatMessage("commands.teleport.invalidPosition"));

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        LiteralCommandNode<CommandListenerWrapper> literalcommandnode = commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("teleport").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("targets", (ArgumentType) ArgumentEntity.multipleEntities()).then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("location", (ArgumentType) ArgumentVec3.a()).executes((commandcontext) -> {
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
        })))).then(net.minecraft.commands.CommandDispatcher.a("location", (ArgumentType) ArgumentVec3.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), Collections.singleton(((CommandListenerWrapper) commandcontext.getSource()).g()), ((CommandListenerWrapper) commandcontext.getSource()).getWorld(), ArgumentVec3.b(commandcontext, "location"), VectorPosition.d(), (CommandTeleport.a) null);
        }))).then(net.minecraft.commands.CommandDispatcher.a("destination", (ArgumentType) ArgumentEntity.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), Collections.singleton(((CommandListenerWrapper) commandcontext.getSource()).g()), ArgumentEntity.a(commandcontext, "destination"));
        })));

        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("tp").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).redirect(literalcommandnode));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, Collection<? extends Entity> collection, Entity entity) throws CommandSyntaxException {
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Entity entity1 = (Entity) iterator.next();

            a(commandlistenerwrapper, entity1, (WorldServer) entity.world, entity.locX(), entity.locY(), entity.locZ(), EnumSet.noneOf(PacketPlayOutPosition.EnumPlayerTeleportFlags.class), entity.yaw, entity.pitch, (CommandTeleport.a) null);
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
                a(commandlistenerwrapper, entity, worldserver, vec3d.x, vec3d.y, vec3d.z, set, entity.yaw, entity.pitch, commandteleport_a);
            } else {
                a(commandlistenerwrapper, entity, worldserver, vec3d.x, vec3d.y, vec3d.z, set, vec2f.j, vec2f.i, commandteleport_a);
            }
        }

        if (collection.size() == 1) {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.teleport.success.location.single", new Object[]{((Entity) collection.iterator().next()).getScoreboardDisplayName(), vec3d.x, vec3d.y, vec3d.z}), true);
        } else {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.teleport.success.location.multiple", new Object[]{collection.size(), vec3d.x, vec3d.y, vec3d.z}), true);
        }

        return collection.size();
    }

    private static void a(CommandListenerWrapper commandlistenerwrapper, Entity entity, WorldServer worldserver, double d0, double d1, double d2, Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> set, float f, float f1, @Nullable CommandTeleport.a commandteleport_a) throws CommandSyntaxException {
        BlockPosition blockposition = new BlockPosition(d0, d1, d2);

        if (!World.l(blockposition)) {
            throw CommandTeleport.a.create();
        } else {
            if (entity instanceof EntityPlayer) {
                ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(new BlockPosition(d0, d1, d2));

                worldserver.getChunkProvider().addTicket(TicketType.POST_TELEPORT, chunkcoordintpair, 1, entity.getId());
                entity.stopRiding();
                if (((EntityPlayer) entity).isSleeping()) {
                    ((EntityPlayer) entity).wakeup(true, true);
                }

                if (worldserver == entity.world) {
                    ((EntityPlayer) entity).playerConnection.a(d0, d1, d2, f, f1, set);
                } else {
                    ((EntityPlayer) entity).a(worldserver, d0, d1, d2, f, f1);
                }

                entity.setHeadRotation(f);
            } else {
                float f2 = MathHelper.g(f);
                float f3 = MathHelper.g(f1);

                f3 = MathHelper.a(f3, -90.0F, 90.0F);
                if (worldserver == entity.world) {
                    entity.setPositionRotation(d0, d1, d2, f2, f3);
                    entity.setHeadRotation(f2);
                } else {
                    entity.decouple();
                    Entity entity1 = entity;

                    entity = entity.getEntityType().a((World) worldserver);
                    if (entity == null) {
                        return;
                    }

                    entity.v(entity1);
                    entity.setPositionRotation(d0, d1, d2, f2, f3);
                    entity.setHeadRotation(f2);
                    worldserver.addEntityTeleport(entity);
                    entity1.dead = true;
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

    static class a {

        private final Vec3D a;
        private final Entity b;
        private final ArgumentAnchor.Anchor c;

        public a(Entity entity, ArgumentAnchor.Anchor argumentanchor_anchor) {
            this.b = entity;
            this.c = argumentanchor_anchor;
            this.a = argumentanchor_anchor.a(entity);
        }

        public a(Vec3D vec3d) {
            this.b = null;
            this.a = vec3d;
            this.c = null;
        }

        public void a(CommandListenerWrapper commandlistenerwrapper, Entity entity) {
            if (this.b != null) {
                if (entity instanceof EntityPlayer) {
                    ((EntityPlayer) entity).a(commandlistenerwrapper.k(), this.b, this.c);
                } else {
                    entity.a(commandlistenerwrapper.k(), this.a);
                }
            } else {
                entity.a(commandlistenerwrapper.k(), this.a);
            }

        }
    }
}
