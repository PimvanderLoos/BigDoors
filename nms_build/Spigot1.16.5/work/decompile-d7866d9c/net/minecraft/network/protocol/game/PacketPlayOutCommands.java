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
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
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

    private RootCommandNode<ICompletionProvider> a;

    public PacketPlayOutCommands() {}

    public PacketPlayOutCommands(RootCommandNode<ICompletionProvider> rootcommandnode) {
        this.a = rootcommandnode;
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        PacketPlayOutCommands.a[] apacketplayoutcommands_a = new PacketPlayOutCommands.a[packetdataserializer.i()];

        for (int i = 0; i < apacketplayoutcommands_a.length; ++i) {
            apacketplayoutcommands_a[i] = c(packetdataserializer);
        }

        a(apacketplayoutcommands_a);
        this.a = (RootCommandNode) apacketplayoutcommands_a[packetdataserializer.i()].e;
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        Object2IntMap<CommandNode<ICompletionProvider>> object2intmap = a(this.a);
        CommandNode<ICompletionProvider>[] acommandnode = a(object2intmap);

        packetdataserializer.d(acommandnode.length);
        CommandNode[] acommandnode1 = acommandnode;
        int i = acommandnode.length;

        for (int j = 0; j < i; ++j) {
            CommandNode<ICompletionProvider> commandnode = acommandnode1[j];

            a(packetdataserializer, commandnode, object2intmap);
        }

        packetdataserializer.d(object2intmap.get(this.a));
    }

    private static void a(PacketPlayOutCommands.a[] apacketplayoutcommands_a) {
        ArrayList arraylist = Lists.newArrayList(apacketplayoutcommands_a);

        boolean flag;

        do {
            if (arraylist.isEmpty()) {
                return;
            }

            flag = arraylist.removeIf((packetplayoutcommands_a) -> {
                return packetplayoutcommands_a.a(apacketplayoutcommands_a);
            });
        } while (flag);

        throw new IllegalStateException("Server sent an impossible command tree");
    }

    private static Object2IntMap<CommandNode<ICompletionProvider>> a(RootCommandNode<ICompletionProvider> rootcommandnode) {
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

    private static CommandNode<ICompletionProvider>[] a(Object2IntMap<CommandNode<ICompletionProvider>> object2intmap) {
        CommandNode<ICompletionProvider>[] acommandnode = (CommandNode[]) (new CommandNode[object2intmap.size()]);

        Entry entry;

        for (ObjectIterator objectiterator = Object2IntMaps.fastIterable(object2intmap).iterator(); objectiterator.hasNext(); acommandnode[entry.getIntValue()] = (CommandNode) entry.getKey()) {
            entry = (Entry) objectiterator.next();
        }

        return acommandnode;
    }

    private static PacketPlayOutCommands.a c(PacketDataSerializer packetdataserializer) {
        byte b0 = packetdataserializer.readByte();
        int[] aint = packetdataserializer.b();
        int i = (b0 & 8) != 0 ? packetdataserializer.i() : 0;
        ArgumentBuilder<ICompletionProvider, ?> argumentbuilder = a(packetdataserializer, b0);

        return new PacketPlayOutCommands.a(argumentbuilder, b0, i, aint);
    }

    @Nullable
    private static ArgumentBuilder<ICompletionProvider, ?> a(PacketDataSerializer packetdataserializer, byte b0) {
        int i = b0 & 3;

        if (i == 2) {
            String s = packetdataserializer.e(32767);
            ArgumentType<?> argumenttype = ArgumentRegistry.a(packetdataserializer);

            if (argumenttype == null) {
                return null;
            } else {
                RequiredArgumentBuilder<ICompletionProvider, ?> requiredargumentbuilder = RequiredArgumentBuilder.argument(s, argumenttype);

                if ((b0 & 16) != 0) {
                    requiredargumentbuilder.suggests(CompletionProviders.a(packetdataserializer.p()));
                }

                return requiredargumentbuilder;
            }
        } else {
            return i == 1 ? LiteralArgumentBuilder.literal(packetdataserializer.e(32767)) : null;
        }
    }

    private static void a(PacketDataSerializer packetdataserializer, CommandNode<ICompletionProvider> commandnode, Map<CommandNode<ICompletionProvider>, Integer> map) {
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
        packetdataserializer.d(commandnode.getChildren().size());
        Iterator iterator = commandnode.getChildren().iterator();

        while (iterator.hasNext()) {
            CommandNode<ICompletionProvider> commandnode1 = (CommandNode) iterator.next();

            packetdataserializer.d((Integer) map.get(commandnode1));
        }

        if (commandnode.getRedirect() != null) {
            packetdataserializer.d((Integer) map.get(commandnode.getRedirect()));
        }

        if (commandnode instanceof ArgumentCommandNode) {
            ArgumentCommandNode<ICompletionProvider, ?> argumentcommandnode = (ArgumentCommandNode) commandnode;

            packetdataserializer.a(argumentcommandnode.getName());
            ArgumentRegistry.a(packetdataserializer, argumentcommandnode.getType());
            if (argumentcommandnode.getCustomSuggestions() != null) {
                packetdataserializer.a(CompletionProviders.a(argumentcommandnode.getCustomSuggestions()));
            }
        } else if (commandnode instanceof LiteralCommandNode) {
            packetdataserializer.a(((LiteralCommandNode) commandnode).getLiteral());
        }

    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    static class a {

        @Nullable
        private final ArgumentBuilder<ICompletionProvider, ?> a;
        private final byte b;
        private final int c;
        private final int[] d;
        @Nullable
        private CommandNode<ICompletionProvider> e;

        private a(@Nullable ArgumentBuilder<ICompletionProvider, ?> argumentbuilder, byte b0, int i, int[] aint) {
            this.a = argumentbuilder;
            this.b = b0;
            this.c = i;
            this.d = aint;
        }

        public boolean a(PacketPlayOutCommands.a[] apacketplayoutcommands_a) {
            if (this.e == null) {
                if (this.a == null) {
                    this.e = new RootCommandNode();
                } else {
                    if ((this.b & 8) != 0) {
                        if (apacketplayoutcommands_a[this.c].e == null) {
                            return false;
                        }

                        this.a.redirect(apacketplayoutcommands_a[this.c].e);
                    }

                    if ((this.b & 4) != 0) {
                        this.a.executes((commandcontext) -> {
                            return 0;
                        });
                    }

                    this.e = this.a.build();
                }
            }

            int[] aint = this.d;
            int i = aint.length;

            int j;
            int k;

            for (k = 0; k < i; ++k) {
                j = aint[k];
                if (apacketplayoutcommands_a[j].e == null) {
                    return false;
                }
            }

            aint = this.d;
            i = aint.length;

            for (k = 0; k < i; ++k) {
                j = aint[k];
                CommandNode<ICompletionProvider> commandnode = apacketplayoutcommands_a[j].e;

                if (!(commandnode instanceof RootCommandNode)) {
                    this.e.addChild(commandnode);
                }
            }

            return true;
        }
    }
}
