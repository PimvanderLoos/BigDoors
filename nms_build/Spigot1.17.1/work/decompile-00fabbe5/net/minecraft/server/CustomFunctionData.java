package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.CustomFunction;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.tags.Tag;
import net.minecraft.util.profiling.GameProfilerFiller;
import net.minecraft.world.level.GameRules;

public class CustomFunctionData {

    private static final IChatBaseComponent NO_RECURSIVE_TRACES = new ChatMessage("commands.debug.function.noRecursion");
    private static final MinecraftKey TICK_FUNCTION_TAG = new MinecraftKey("tick");
    private static final MinecraftKey LOAD_FUNCTION_TAG = new MinecraftKey("load");
    final MinecraftServer server;
    @Nullable
    private CustomFunctionData.a context;
    private List<CustomFunction> ticking = ImmutableList.of();
    private boolean postReload;
    private CustomFunctionManager library;

    public CustomFunctionData(MinecraftServer minecraftserver, CustomFunctionManager customfunctionmanager) {
        this.server = minecraftserver;
        this.library = customfunctionmanager;
        this.b(customfunctionmanager);
    }

    public int a() {
        return this.server.getGameRules().getInt(GameRules.RULE_MAX_COMMAND_CHAIN_LENGTH);
    }

    public CommandDispatcher<CommandListenerWrapper> getCommandDispatcher() {
        return this.server.getCommandDispatcher().a();
    }

    public void tick() {
        this.a((Collection) this.ticking, CustomFunctionData.TICK_FUNCTION_TAG);
        if (this.postReload) {
            this.postReload = false;
            Collection<CustomFunction> collection = this.library.b().b(CustomFunctionData.LOAD_FUNCTION_TAG).getTagged();

            this.a((Collection) collection, CustomFunctionData.LOAD_FUNCTION_TAG);
        }

    }

    private void a(Collection<CustomFunction> collection, MinecraftKey minecraftkey) {
        GameProfilerFiller gameprofilerfiller = this.server.getMethodProfiler();

        Objects.requireNonNull(minecraftkey);
        gameprofilerfiller.a(minecraftkey::toString);
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            CustomFunction customfunction = (CustomFunction) iterator.next();

            this.a(customfunction, this.d());
        }

        this.server.getMethodProfiler().exit();
    }

    public int a(CustomFunction customfunction, CommandListenerWrapper commandlistenerwrapper) {
        return this.a(customfunction, commandlistenerwrapper, (CustomFunctionData.c) null);
    }

    public int a(CustomFunction customfunction, CommandListenerWrapper commandlistenerwrapper, @Nullable CustomFunctionData.c customfunctiondata_c) {
        if (this.context != null) {
            if (customfunctiondata_c != null) {
                this.context.a(CustomFunctionData.NO_RECURSIVE_TRACES.getString());
                return 0;
            } else {
                this.context.a(customfunction, commandlistenerwrapper);
                return 0;
            }
        } else {
            int i;

            try {
                this.context = new CustomFunctionData.a(customfunctiondata_c);
                i = this.context.b(customfunction, commandlistenerwrapper);
            } finally {
                this.context = null;
            }

            return i;
        }
    }

    public void a(CustomFunctionManager customfunctionmanager) {
        this.library = customfunctionmanager;
        this.b(customfunctionmanager);
    }

    private void b(CustomFunctionManager customfunctionmanager) {
        this.ticking = ImmutableList.copyOf(customfunctionmanager.b().b(CustomFunctionData.TICK_FUNCTION_TAG).getTagged());
        this.postReload = true;
    }

    public CommandListenerWrapper d() {
        return this.server.getServerCommandListener().a(2).a();
    }

    public Optional<CustomFunction> a(MinecraftKey minecraftkey) {
        return this.library.a(minecraftkey);
    }

    public Tag<CustomFunction> b(MinecraftKey minecraftkey) {
        return this.library.b(minecraftkey);
    }

    public Iterable<MinecraftKey> e() {
        return this.library.a().keySet();
    }

    public Iterable<MinecraftKey> f() {
        return this.library.b().b();
    }

    public interface c {

        void a(int i, String s);

        void a(int i, String s, int j);

        void b(int i, String s);

        void a(int i, MinecraftKey minecraftkey, int j);
    }

    private class a {

        private int depth;
        @Nullable
        private final CustomFunctionData.c tracer;
        private final Deque<CustomFunctionData.b> commandQueue = Queues.newArrayDeque();
        private final List<CustomFunctionData.b> nestedCalls = Lists.newArrayList();

        a(@Nullable CustomFunctionData.c customfunctiondata_c) {
            this.tracer = customfunctiondata_c;
        }

        void a(CustomFunction customfunction, CommandListenerWrapper commandlistenerwrapper) {
            int i = CustomFunctionData.this.a();

            if (this.commandQueue.size() + this.nestedCalls.size() < i) {
                this.nestedCalls.add(new CustomFunctionData.b(commandlistenerwrapper, this.depth, new CustomFunction.d(customfunction)));
            }

        }

        int b(CustomFunction customfunction, CommandListenerWrapper commandlistenerwrapper) {
            int i = CustomFunctionData.this.a();
            int j = 0;
            CustomFunction.c[] acustomfunction_c = customfunction.b();

            for (int k = acustomfunction_c.length - 1; k >= 0; --k) {
                this.commandQueue.push(new CustomFunctionData.b(commandlistenerwrapper, 0, acustomfunction_c[k]));
            }

            do {
                if (this.commandQueue.isEmpty()) {
                    return j;
                }

                try {
                    CustomFunctionData.b customfunctiondata_b = (CustomFunctionData.b) this.commandQueue.removeFirst();
                    GameProfilerFiller gameprofilerfiller = CustomFunctionData.this.server.getMethodProfiler();

                    Objects.requireNonNull(customfunctiondata_b);
                    gameprofilerfiller.a(customfunctiondata_b::toString);
                    this.depth = customfunctiondata_b.depth;
                    customfunctiondata_b.a(CustomFunctionData.this, this.commandQueue, i, this.tracer);
                    if (!this.nestedCalls.isEmpty()) {
                        List list = Lists.reverse(this.nestedCalls);
                        Deque deque = this.commandQueue;

                        Objects.requireNonNull(this.commandQueue);
                        list.forEach(deque::addFirst);
                        this.nestedCalls.clear();
                    }
                } finally {
                    CustomFunctionData.this.server.getMethodProfiler().exit();
                }

                ++j;
            } while (j < i);

            return j;
        }

        public void a(String s) {
            if (this.tracer != null) {
                this.tracer.b(this.depth, s);
            }

        }
    }

    public static class b {

        private final CommandListenerWrapper sender;
        final int depth;
        private final CustomFunction.c entry;

        public b(CommandListenerWrapper commandlistenerwrapper, int i, CustomFunction.c customfunction_c) {
            this.sender = commandlistenerwrapper;
            this.depth = i;
            this.entry = customfunction_c;
        }

        public void a(CustomFunctionData customfunctiondata, Deque<CustomFunctionData.b> deque, int i, @Nullable CustomFunctionData.c customfunctiondata_c) {
            try {
                this.entry.a(customfunctiondata, this.sender, deque, i, this.depth, customfunctiondata_c);
            } catch (CommandSyntaxException commandsyntaxexception) {
                if (customfunctiondata_c != null) {
                    customfunctiondata_c.b(this.depth, commandsyntaxexception.getRawMessage().getString());
                }
            } catch (Exception exception) {
                if (customfunctiondata_c != null) {
                    customfunctiondata_c.b(this.depth, exception.getMessage());
                }
            }

        }

        public String toString() {
            return this.entry.toString();
        }
    }
}
