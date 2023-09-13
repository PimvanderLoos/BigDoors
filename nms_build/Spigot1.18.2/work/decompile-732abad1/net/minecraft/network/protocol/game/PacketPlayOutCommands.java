package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import javax.annotation.Nullable;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.commands.synchronization.ArgumentRegistry;
import net.minecraft.commands.synchronization.CompletionProviders;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayOutCommands implements Packet<PacketListenerPlayOut> {

    private static final byte MASK_TYPE = 3;
    private static final byte FLAG_EXECUTABLE = 4;
    private static final byte FLAG_REDIRECT = 8;
    private static final byte FLAG_CUSTOM_SUGGESTIONS = 16;
    private static final byte TYPE_ROOT = 0;
    private static final byte TYPE_LITERAL = 1;
    private static final byte TYPE_ARGUMENT = 2;
    private final RootCommandNode<ICompletionProvider> root;

    public PacketPlayOutCommands(RootCommandNode<ICompletionProvider> rootcommandnode) {
        this.root = rootcommandnode;
    }

    public PacketPlayOutCommands(PacketDataSerializer packetdataserializer) {
        List<PacketPlayOutCommands.a> list = packetdataserializer.readList(PacketPlayOutCommands::readNode);

        resolveEntries(list);
        int i = packetdataserializer.readVarInt();

        this.root = (RootCommandNode) ((PacketPlayOutCommands.a) list.get(i)).node;
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        Object2IntMap<CommandNode<ICompletionProvider>> object2intmap = enumerateNodes(this.root);
        List<CommandNode<ICompletionProvider>> list = getNodesInIdOrder(object2intmap);

        packetdataserializer.writeCollection(list, (packetdataserializer1, commandnode) -> {
            writeNode(packetdataserializer1, commandnode, object2intmap);
        });
        packetdataserializer.writeVarInt(object2intmap.get(this.root));
    }

    private static void resolveEntries(List<PacketPlayOutCommands.a> list) {
        ArrayList arraylist = Lists.newArrayList(list);

        boolean flag;

        do {
            if (arraylist.isEmpty()) {
                return;
            }

            flag = arraylist.removeIf((packetplayoutcommands_a) -> {
                return packetplayoutcommands_a.build(list);
            });
        } while (flag);

        throw new IllegalStateException("Server sent an impossible command tree");
    }

    private static Object2IntMap<CommandNode<ICompletionProvider>> enumerateNodes(RootCommandNode<ICompletionProvider> rootcommandnode) {
        Object2IntMap<CommandNode<ICompletionProvider>> object2intmap = new Object2IntOpenHashMap();
        Queue<CommandNode<ICompletionProvider>> queue = Queues.newArrayDeque();

        queue.add(rootcommandnode);

        CommandNode commandnode;

        while ((commandnode = (CommandNode) queue.poll()) != null) {
            if (!object2intmap.containsKey(commandnode)) {
                int i = object2intmap.size();

                object2intmap.put(commandnode, i);
                queue.addAll(commandnode.getChildren());
                if (commandnode.getRedirect() != null) {
                    queue.add(commandnode.getRedirect());
                }
            }
        }

        return object2intmap;
    }

    private static List<CommandNode<ICompletionProvider>> getNodesInIdOrder(Object2IntMap<CommandNode<ICompletionProvider>> object2intmap) {
        ObjectArrayList<CommandNode<ICompletionProvider>> objectarraylist = new ObjectArrayList(object2intmap.size());

        objectarraylist.size(object2intmap.size());
        ObjectIterator objectiterator = Object2IntMaps.fastIterable(object2intmap).iterator();

        while (objectiterator.hasNext()) {
            Entry<CommandNode<ICompletionProvider>> entry = (Entry) objectiterator.next();

            objectarraylist.set(entry.getIntValue(), (CommandNode) entry.getKey());
        }

        return objectarraylist;
    }

    private static PacketPlayOutCommands.a readNode(PacketDataSerializer packetdataserializer) {
        byte b0 = packetdataserializer.readByte();
        int[] aint = packetdataserializer.readVarIntArray();
        int i = (b0 & 8) != 0 ? packetdataserializer.readVarInt() : 0;
        ArgumentBuilder<ICompletionProvider, ?> argumentbuilder = createBuilder(packetdataserializer, b0);

        return new PacketPlayOutCommands.a(argumentbuilder, b0, i, aint);
    }

    @Nullable
    private static ArgumentBuilder<ICompletionProvider, ?> createBuilder(PacketDataSerializer packetdataserializer, byte b0) {
        int i = b0 & 3;

        if (i == 2) {
            String s = packetdataserializer.readUtf();
            ArgumentType<?> argumenttype = ArgumentRegistry.deserialize(packetdataserializer);

            if (argumenttype == null) {
                return null;
            } else {
                RequiredArgumentBuilder<ICompletionProvider, ?> requiredargumentbuilder = RequiredArgumentBuilder.argument(s, argumenttype);

                if ((b0 & 16) != 0) {
                    requiredargumentbuilder.suggests(CompletionProviders.getProvider(packetdataserializer.readResourceLocation()));
                }

                return requiredargumentbuilder;
            }
        } else {
            return i == 1 ? LiteralArgumentBuilder.literal(packetdataserializer.readUtf()) : null;
        }
    }

    private static void writeNode(PacketDataSerializer packetdataserializer, CommandNode<ICompletionProvider> commandnode, Map<CommandNode<ICompletionProvider>, Integer> map) {
        byte b0 = 0;

        if (commandnode.getRedirect() != null) {
            b0 = (byte) (b0 | 8);
        }

        if (commandnode.getCommand() != null) {
            b0 = (byte) (b0 | 4);
        }

        if (commandnode instanceof RootCommandNode) {
            b0 = (byte) (b0 | 0);
        } else if (commandnode instanceof ArgumentCommandNode) {
            b0 = (byte) (b0 | 2);
            if (((ArgumentCommandNode) commandnode).getCustomSuggestions() != null) {
                b0 = (byte) (b0 | 16);
            }
        } else {
            if (!(commandnode instanceof LiteralCommandNode)) {
                throw new UnsupportedOperationException("Unknown node type " + commandnode);
            }

            b0 = (byte) (b0 | 1);
        }

        packetdataserializer.writeByte(b0);
        packetdataserializer.writeVarInt(commandnode.getChildren().size());
        Iterator iterator = commandnode.getChildren().iterator();

        while (iterator.hasNext()) {
            CommandNode<ICompletionProvider> commandnode1 = (CommandNode) iterator.next();

            packetdataserializer.writeVarInt((Integer) map.get(commandnode1));
        }

        if (commandnode.getRedirect() != null) {
            packetdataserializer.writeVarInt((Integer) map.get(commandnode.getRedirect()));
        }

        if (commandnode instanceof ArgumentCommandNode) {
            ArgumentCommandNode<ICompletionProvider, ?> argumentcommandnode = (ArgumentCommandNode) commandnode;

            packetdataserializer.writeUtf(argumentcommandnode.getName());
            ArgumentRegistry.serialize(packetdataserializer, argumentcommandnode.getType());
            if (argumentcommandnode.getCustomSuggestions() != null) {
                packetdataserializer.writeResourceLocation(CompletionProviders.getName(argumentcommandnode.getCustomSuggestions()));
            }
        } else if (commandnode instanceof LiteralCommandNode) {
            packetdataserializer.writeUtf(((LiteralCommandNode) commandnode).getLiteral());
        }

    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleCommands(this);
    }

    public RootCommandNode<ICompletionProvider> getRoot() {
        return this.root;
    }

    private static class a {

        @Nullable
        private final ArgumentBuilder<ICompletionProvider, ?> builder;
        private final byte flags;
        private final int redirect;
        private final int[] children;
        @Nullable
        CommandNode<ICompletionProvider> node;

        a(@Nullable ArgumentBuilder<ICompletionProvider, ?> argumentbuilder, byte b0, int i, int[] aint) {
            this.builder = argumentbuilder;
            this.flags = b0;
            this.redirect = i;
            this.children = aint;
        }

        public boolean build(List<PacketPlayOutCommands.a> list) {
            if (this.node == null) {
                if (this.builder == null) {
                    this.node = new RootCommandNode();
                } else {
                    if ((this.flags & 8) != 0) {
                        if (((PacketPlayOutCommands.a) list.get(this.redirect)).node == null) {
                            return false;
                        }

                        this.builder.redirect(((PacketPlayOutCommands.a) list.get(this.redirect)).node);
                    }

                    if ((this.flags & 4) != 0) {
                        this.builder.executes((commandcontext) -> {
                            return 0;
                        });
                    }

                    this.node = this.builder.build();
                }
            }

            int[] aint = this.children;
            int i = aint.length;

            int j;
            int k;

            for (k = 0; k < i; ++k) {
                j = aint[k];
                if (((PacketPlayOutCommands.a) list.get(j)).node == null) {
                    return false;
                }
            }

            aint = this.children;
            i = aint.length;

            for (k = 0; k < i; ++k) {
                j = aint[k];
                CommandNode<ICompletionProvider> commandnode = ((PacketPlayOutCommands.a) list.get(j)).node;

                if (!(commandnode instanceof RootCommandNode)) {
                    this.node.addChild(commandnode);
                }
            }

            return true;
        }
    }
}
