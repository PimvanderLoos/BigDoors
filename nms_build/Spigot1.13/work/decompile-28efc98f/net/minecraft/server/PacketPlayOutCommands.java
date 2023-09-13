package net.minecraft.server;

import com.google.common.collect.Maps;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;

public class PacketPlayOutCommands implements Packet<PacketListenerPlayOut> {

    private RootCommandNode<ICompletionProvider> a;

    public PacketPlayOutCommands() {}

    public PacketPlayOutCommands(RootCommandNode<ICompletionProvider> rootcommandnode) {
        this.a = rootcommandnode;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        PacketPlayOutCommands.a[] apacketplayoutcommands_a = new PacketPlayOutCommands.a[packetdataserializer.g()];
        ArrayDeque arraydeque = new ArrayDeque(apacketplayoutcommands_a.length);

        for (int i = 0; i < apacketplayoutcommands_a.length; ++i) {
            apacketplayoutcommands_a[i] = this.c(packetdataserializer);
            arraydeque.add(apacketplayoutcommands_a[i]);
        }

        boolean flag;

        do {
            if (arraydeque.isEmpty()) {
                this.a = (RootCommandNode) apacketplayoutcommands_a[packetdataserializer.g()].e;
                return;
            }

            flag = false;
            Iterator iterator = arraydeque.iterator();

            while (iterator.hasNext()) {
                PacketPlayOutCommands.a packetplayoutcommands_a = (PacketPlayOutCommands.a) iterator.next();

                if (packetplayoutcommands_a.a(apacketplayoutcommands_a)) {
                    iterator.remove();
                    flag = true;
                }
            }
        } while (flag);

        throw new IllegalStateException("Server sent an impossible command tree");
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        HashMap hashmap = Maps.newHashMap();
        ArrayDeque arraydeque = new ArrayDeque();

        arraydeque.add(this.a);

        while (!arraydeque.isEmpty()) {
            CommandNode commandnode = (CommandNode) arraydeque.pollFirst();

            if (!hashmap.containsKey(commandnode)) {
                int i = hashmap.size();

                hashmap.put(commandnode, Integer.valueOf(i));
                arraydeque.addAll(commandnode.getChildren());
                if (commandnode.getRedirect() != null) {
                    arraydeque.add(commandnode.getRedirect());
                }
            }
        }

        CommandNode[] acommandnode = (CommandNode[]) (new CommandNode[hashmap.size()]);

        Entry entry;

        for (Iterator iterator = hashmap.entrySet().iterator(); iterator.hasNext(); acommandnode[((Integer) entry.getValue()).intValue()] = (CommandNode) entry.getKey()) {
            entry = (Entry) iterator.next();
        }

        packetdataserializer.d(acommandnode.length);
        CommandNode[] acommandnode1 = acommandnode;
        int j = acommandnode.length;

        for (int k = 0; k < j; ++k) {
            CommandNode commandnode1 = acommandnode1[k];

            this.a(packetdataserializer, commandnode1, hashmap);
        }

        packetdataserializer.d(((Integer) hashmap.get(this.a)).intValue());
    }

    private PacketPlayOutCommands.a c(PacketDataSerializer packetdataserializer) {
        byte b0 = packetdataserializer.readByte();
        int[] aint = packetdataserializer.b();
        int i = (b0 & 8) != 0 ? packetdataserializer.g() : 0;
        ArgumentBuilder argumentbuilder = this.a(packetdataserializer, b0);

        return new PacketPlayOutCommands.a(argumentbuilder, b0, i, aint, null);
    }

    @Nullable
    private ArgumentBuilder<ICompletionProvider, ?> a(PacketDataSerializer packetdataserializer, byte b0) {
        int i = b0 & 3;

        if (i == 2) {
            String s = packetdataserializer.e(32767);
            ArgumentType argumenttype = ArgumentRegistry.a(packetdataserializer);

            if (argumenttype == null) {
                return null;
            } else {
                RequiredArgumentBuilder requiredargumentbuilder = RequiredArgumentBuilder.argument(s, argumenttype);

                if ((b0 & 16) != 0) {
                    requiredargumentbuilder.suggests(CompletionProviders.a(packetdataserializer.l()));
                }

                return requiredargumentbuilder;
            }
        } else {
            return i == 1 ? LiteralArgumentBuilder.literal(packetdataserializer.e(32767)) : null;
        }
    }

    private void a(PacketDataSerializer packetdataserializer, CommandNode<ICompletionProvider> commandnode, Map<CommandNode<ICompletionProvider>, Integer> map) {
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
            CommandNode commandnode1 = (CommandNode) iterator.next();

            packetdataserializer.d(((Integer) map.get(commandnode1)).intValue());
        }

        if (commandnode.getRedirect() != null) {
            packetdataserializer.d(((Integer) map.get(commandnode.getRedirect())).intValue());
        }

        if (commandnode instanceof ArgumentCommandNode) {
            ArgumentCommandNode argumentcommandnode = (ArgumentCommandNode) commandnode;

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

            for (j = 0; j < i; ++j) {
                k = aint[j];
                if (apacketplayoutcommands_a[k].e == null) {
                    return false;
                }
            }

            aint = this.d;
            i = aint.length;

            for (j = 0; j < i; ++j) {
                k = aint[j];
                CommandNode commandnode = apacketplayoutcommands_a[k].e;

                if (!(commandnode instanceof RootCommandNode)) {
                    this.e.addChild(commandnode);
                }
            }

            return true;
        }

        a(ArgumentBuilder argumentbuilder, byte b0, int i, int[] aint, Object object) {
            this(argumentbuilder, b0, i, aint);
        }
    }
}
