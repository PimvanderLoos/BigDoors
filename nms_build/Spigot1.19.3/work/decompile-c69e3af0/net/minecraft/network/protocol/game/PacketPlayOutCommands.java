package net.minecraft.network.protocol.game;

import com.google.common.collect.Queues;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.function.BiPredicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.CompletionProviders;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.MinecraftKey;

public class PacketPlayOutCommands implements Packet<PacketListenerPlayOut> {

    private static final byte MASK_TYPE = 3;
    private static final byte FLAG_EXECUTABLE = 4;
    private static final byte FLAG_REDIRECT = 8;
    private static final byte FLAG_CUSTOM_SUGGESTIONS = 16;
    private static final byte TYPE_ROOT = 0;
    private static final byte TYPE_LITERAL = 1;
    private static final byte TYPE_ARGUMENT = 2;
    private final int rootIndex;
    private final List<PacketPlayOutCommands.b> entries;

    public PacketPlayOutCommands(RootCommandNode<ICompletionProvider> rootcommandnode) {
        Object2IntMap<CommandNode<ICompletionProvider>> object2intmap = enumerateNodes(rootcommandnode);

        this.entries = createEntries(object2intmap);
        this.rootIndex = object2intmap.getInt(rootcommandnode);
    }

    public PacketPlayOutCommands(PacketDataSerializer packetdataserializer) {
        this.entries = packetdataserializer.readList(PacketPlayOutCommands::readNode);
        this.rootIndex = packetdataserializer.readVarInt();
        validateEntries(this.entries);
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeCollection(this.entries, (packetdataserializer1, packetplayoutcommands_b) -> {
            packetplayoutcommands_b.write(packetdataserializer1);
        });
        packetdataserializer.writeVarInt(this.rootIndex);
    }

    private static void validateEntries(List<PacketPlayOutCommands.b> list, BiPredicate<PacketPlayOutCommands.b, IntSet> bipredicate) {
        IntOpenHashSet intopenhashset = new IntOpenHashSet(IntSets.fromTo(0, list.size()));

        boolean flag;

        do {
            if (intopenhashset.isEmpty()) {
                return;
            }

            flag = intopenhashset.removeIf((i) -> {
                return bipredicate.test((PacketPlayOutCommands.b) list.get(i), intopenhashset);
            });
        } while (flag);

        throw new IllegalStateException("Server sent an impossible command tree");
    }

    private static void validateEntries(List<PacketPlayOutCommands.b> list) {
        validateEntries(list, PacketPlayOutCommands.b::canBuild);
        validateEntries(list, PacketPlayOutCommands.b::canResolve);
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

    private static List<PacketPlayOutCommands.b> createEntries(Object2IntMap<CommandNode<ICompletionProvider>> object2intmap) {
        ObjectArrayList<PacketPlayOutCommands.b> objectarraylist = new ObjectArrayList(object2intmap.size());

        objectarraylist.size(object2intmap.size());
        ObjectIterator objectiterator = Object2IntMaps.fastIterable(object2intmap).iterator();

        while (objectiterator.hasNext()) {
            Entry<CommandNode<ICompletionProvider>> entry = (Entry) objectiterator.next();

            objectarraylist.set(entry.getIntValue(), createEntry((CommandNode) entry.getKey(), object2intmap));
        }

        return objectarraylist;
    }

    private static PacketPlayOutCommands.b readNode(PacketDataSerializer packetdataserializer) {
        byte b0 = packetdataserializer.readByte();
        int[] aint = packetdataserializer.readVarIntArray();
        int i = (b0 & 8) != 0 ? packetdataserializer.readVarInt() : 0;
        PacketPlayOutCommands.e packetplayoutcommands_e = read(packetdataserializer, b0);

        return new PacketPlayOutCommands.b(packetplayoutcommands_e, b0, i, aint);
    }

    @Nullable
    private static PacketPlayOutCommands.e read(PacketDataSerializer packetdataserializer, byte b0) {
        int i = b0 & 3;
        String s;

        if (i == 2) {
            s = packetdataserializer.readUtf();
            int j = packetdataserializer.readVarInt();
            ArgumentTypeInfo<?, ?> argumenttypeinfo = (ArgumentTypeInfo) BuiltInRegistries.COMMAND_ARGUMENT_TYPE.byId(j);

            if (argumenttypeinfo == null) {
                return null;
            } else {
                ArgumentTypeInfo.a<?> argumenttypeinfo_a = argumenttypeinfo.deserializeFromNetwork(packetdataserializer);
                MinecraftKey minecraftkey = (b0 & 16) != 0 ? packetdataserializer.readResourceLocation() : null;

                return new PacketPlayOutCommands.a(s, argumenttypeinfo_a, minecraftkey);
            }
        } else if (i == 1) {
            s = packetdataserializer.readUtf();
            return new PacketPlayOutCommands.c(s);
        } else {
            return null;
        }
    }

    private static PacketPlayOutCommands.b createEntry(CommandNode<ICompletionProvider> commandnode, Object2IntMap<CommandNode<ICompletionProvider>> object2intmap) {
        int i = 0;
        int j;

        if (commandnode.getRedirect() != null) {
            i |= 8;
            j = object2intmap.getInt(commandnode.getRedirect());
        } else {
            j = 0;
        }

        if (commandnode.getCommand() != null) {
            i |= 4;
        }

        Object object;

        if (commandnode instanceof RootCommandNode) {
            i |= 0;
            object = null;
        } else if (commandnode instanceof ArgumentCommandNode) {
            ArgumentCommandNode<ICompletionProvider, ?> argumentcommandnode = (ArgumentCommandNode) commandnode;

            object = new PacketPlayOutCommands.a(argumentcommandnode);
            i |= 2;
            if (argumentcommandnode.getCustomSuggestions() != null) {
                i |= 16;
            }
        } else {
            if (!(commandnode instanceof LiteralCommandNode)) {
                throw new UnsupportedOperationException("Unknown node type " + commandnode);
            }

            LiteralCommandNode literalcommandnode = (LiteralCommandNode) commandnode;

            object = new PacketPlayOutCommands.c(literalcommandnode.getLiteral());
            i |= 1;
        }

        Stream stream = commandnode.getChildren().stream();

        Objects.requireNonNull(object2intmap);
        int[] aint = stream.mapToInt(object2intmap::getInt).toArray();

        return new PacketPlayOutCommands.b((PacketPlayOutCommands.e) object, i, j, aint);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleCommands(this);
    }

    public RootCommandNode<ICompletionProvider> getRoot(CommandBuildContext commandbuildcontext) {
        return (RootCommandNode) (new PacketPlayOutCommands.d(commandbuildcontext, this.entries)).resolve(this.rootIndex);
    }

    private static class b {

        @Nullable
        final PacketPlayOutCommands.e stub;
        final int flags;
        final int redirect;
        final int[] children;

        b(@Nullable PacketPlayOutCommands.e packetplayoutcommands_e, int i, int j, int[] aint) {
            this.stub = packetplayoutcommands_e;
            this.flags = i;
            this.redirect = j;
            this.children = aint;
        }

        public void write(PacketDataSerializer packetdataserializer) {
            packetdataserializer.writeByte(this.flags);
            packetdataserializer.writeVarIntArray(this.children);
            if ((this.flags & 8) != 0) {
                packetdataserializer.writeVarInt(this.redirect);
            }

            if (this.stub != null) {
                this.stub.write(packetdataserializer);
            }

        }

        public boolean canBuild(IntSet intset) {
            return (this.flags & 8) != 0 ? !intset.contains(this.redirect) : true;
        }

        public boolean canResolve(IntSet intset) {
            int[] aint = this.children;
            int i = aint.length;

            for (int j = 0; j < i; ++j) {
                int k = aint[j];

                if (intset.contains(k)) {
                    return false;
                }
            }

            return true;
        }
    }

    private interface e {

        ArgumentBuilder<ICompletionProvider, ?> build(CommandBuildContext commandbuildcontext);

        void write(PacketDataSerializer packetdataserializer);
    }

    private static class a implements PacketPlayOutCommands.e {

        private final String id;
        private final ArgumentTypeInfo.a<?> argumentType;
        @Nullable
        private final MinecraftKey suggestionId;

        @Nullable
        private static MinecraftKey getSuggestionId(@Nullable SuggestionProvider<ICompletionProvider> suggestionprovider) {
            return suggestionprovider != null ? CompletionProviders.getName(suggestionprovider) : null;
        }

        a(String s, ArgumentTypeInfo.a<?> argumenttypeinfo_a, @Nullable MinecraftKey minecraftkey) {
            this.id = s;
            this.argumentType = argumenttypeinfo_a;
            this.suggestionId = minecraftkey;
        }

        public a(ArgumentCommandNode<ICompletionProvider, ?> argumentcommandnode) {
            this(argumentcommandnode.getName(), ArgumentTypeInfos.unpack(argumentcommandnode.getType()), getSuggestionId(argumentcommandnode.getCustomSuggestions()));
        }

        @Override
        public ArgumentBuilder<ICompletionProvider, ?> build(CommandBuildContext commandbuildcontext) {
            ArgumentType<?> argumenttype = this.argumentType.instantiate(commandbuildcontext);
            RequiredArgumentBuilder<ICompletionProvider, ?> requiredargumentbuilder = RequiredArgumentBuilder.argument(this.id, argumenttype);

            if (this.suggestionId != null) {
                requiredargumentbuilder.suggests(CompletionProviders.getProvider(this.suggestionId));
            }

            return requiredargumentbuilder;
        }

        @Override
        public void write(PacketDataSerializer packetdataserializer) {
            packetdataserializer.writeUtf(this.id);
            serializeCap(packetdataserializer, this.argumentType);
            if (this.suggestionId != null) {
                packetdataserializer.writeResourceLocation(this.suggestionId);
            }

        }

        private static <A extends ArgumentType<?>> void serializeCap(PacketDataSerializer packetdataserializer, ArgumentTypeInfo.a<A> argumenttypeinfo_a) {
            serializeCap(packetdataserializer, argumenttypeinfo_a.type(), argumenttypeinfo_a);
        }

        private static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.a<A>> void serializeCap(PacketDataSerializer packetdataserializer, ArgumentTypeInfo<A, T> argumenttypeinfo, ArgumentTypeInfo.a<A> argumenttypeinfo_a) {
            packetdataserializer.writeVarInt(BuiltInRegistries.COMMAND_ARGUMENT_TYPE.getId(argumenttypeinfo));
            argumenttypeinfo.serializeToNetwork(argumenttypeinfo_a, packetdataserializer);
        }
    }

    private static class c implements PacketPlayOutCommands.e {

        private final String id;

        c(String s) {
            this.id = s;
        }

        @Override
        public ArgumentBuilder<ICompletionProvider, ?> build(CommandBuildContext commandbuildcontext) {
            return LiteralArgumentBuilder.literal(this.id);
        }

        @Override
        public void write(PacketDataSerializer packetdataserializer) {
            packetdataserializer.writeUtf(this.id);
        }
    }

    private static class d {

        private final CommandBuildContext context;
        private final List<PacketPlayOutCommands.b> entries;
        private final List<CommandNode<ICompletionProvider>> nodes;

        d(CommandBuildContext commandbuildcontext, List<PacketPlayOutCommands.b> list) {
            this.context = commandbuildcontext;
            this.entries = list;
            ObjectArrayList<CommandNode<ICompletionProvider>> objectarraylist = new ObjectArrayList();

            objectarraylist.size(list.size());
            this.nodes = objectarraylist;
        }

        public CommandNode<ICompletionProvider> resolve(int i) {
            CommandNode<ICompletionProvider> commandnode = (CommandNode) this.nodes.get(i);

            if (commandnode != null) {
                return commandnode;
            } else {
                PacketPlayOutCommands.b packetplayoutcommands_b = (PacketPlayOutCommands.b) this.entries.get(i);
                Object object;

                if (packetplayoutcommands_b.stub == null) {
                    object = new RootCommandNode();
                } else {
                    ArgumentBuilder<ICompletionProvider, ?> argumentbuilder = packetplayoutcommands_b.stub.build(this.context);

                    if ((packetplayoutcommands_b.flags & 8) != 0) {
                        argumentbuilder.redirect(this.resolve(packetplayoutcommands_b.redirect));
                    }

                    if ((packetplayoutcommands_b.flags & 4) != 0) {
                        argumentbuilder.executes((commandcontext) -> {
                            return 0;
                        });
                    }

                    object = argumentbuilder.build();
                }

                this.nodes.set(i, object);
                int[] aint = packetplayoutcommands_b.children;
                int j = aint.length;

                for (int k = 0; k < j; ++k) {
                    int l = aint[k];
                    CommandNode<ICompletionProvider> commandnode1 = this.resolve(l);

                    if (!(commandnode1 instanceof RootCommandNode)) {
                        ((CommandNode) object).addChild(commandnode1);
                    }
                }

                return (CommandNode) object;
            }
        }
    }
}
