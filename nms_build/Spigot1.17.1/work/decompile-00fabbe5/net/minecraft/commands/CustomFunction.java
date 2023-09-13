package net.minecraft.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.CustomFunctionData;

public class CustomFunction {

    private final CustomFunction.c[] entries;
    final MinecraftKey id;

    public CustomFunction(MinecraftKey minecraftkey, CustomFunction.c[] acustomfunction_c) {
        this.id = minecraftkey;
        this.entries = acustomfunction_c;
    }

    public MinecraftKey a() {
        return this.id;
    }

    public CustomFunction.c[] b() {
        return this.entries;
    }

    public static CustomFunction a(MinecraftKey minecraftkey, com.mojang.brigadier.CommandDispatcher<CommandListenerWrapper> com_mojang_brigadier_commanddispatcher, CommandListenerWrapper commandlistenerwrapper, List<String> list) {
        List<CustomFunction.c> list1 = Lists.newArrayListWithCapacity(list.size());

        for (int i = 0; i < list.size(); ++i) {
            int j = i + 1;
            String s = ((String) list.get(i)).trim();
            StringReader stringreader = new StringReader(s);

            if (stringreader.canRead() && stringreader.peek() != '#') {
                if (stringreader.peek() == '/') {
                    stringreader.skip();
                    if (stringreader.peek() == '/') {
                        throw new IllegalArgumentException("Unknown or invalid command '" + s + "' on line " + j + " (if you intended to make a comment, use '#' not '//')");
                    }

                    String s1 = stringreader.readUnquotedString();

                    throw new IllegalArgumentException("Unknown or invalid command '" + s + "' on line " + j + " (did you mean '" + s1 + "'? Do not use a preceding forwards slash.)");
                }

                try {
                    ParseResults<CommandListenerWrapper> parseresults = com_mojang_brigadier_commanddispatcher.parse(stringreader, commandlistenerwrapper);

                    if (parseresults.getReader().canRead()) {
                        throw CommandDispatcher.a(parseresults);
                    }

                    list1.add(new CustomFunction.b(parseresults));
                } catch (CommandSyntaxException commandsyntaxexception) {
                    throw new IllegalArgumentException("Whilst parsing command on line " + j + ": " + commandsyntaxexception.getMessage());
                }
            }
        }

        return new CustomFunction(minecraftkey, (CustomFunction.c[]) list1.toArray(new CustomFunction.c[0]));
    }

    @FunctionalInterface
    public interface c {

        void a(CustomFunctionData customfunctiondata, CommandListenerWrapper commandlistenerwrapper, Deque<CustomFunctionData.b> deque, int i, int j, @Nullable CustomFunctionData.c customfunctiondata_c) throws CommandSyntaxException;
    }

    public static class b implements CustomFunction.c {

        private final ParseResults<CommandListenerWrapper> parse;

        public b(ParseResults<CommandListenerWrapper> parseresults) {
            this.parse = parseresults;
        }

        @Override
        public void a(CustomFunctionData customfunctiondata, CommandListenerWrapper commandlistenerwrapper, Deque<CustomFunctionData.b> deque, int i, int j, @Nullable CustomFunctionData.c customfunctiondata_c) throws CommandSyntaxException {
            if (customfunctiondata_c != null) {
                String s = this.parse.getReader().getString();

                customfunctiondata_c.a(j, s);
                int k = this.a(customfunctiondata, commandlistenerwrapper);

                customfunctiondata_c.a(j, s, k);
            } else {
                this.a(customfunctiondata, commandlistenerwrapper);
            }

        }

        private int a(CustomFunctionData customfunctiondata, CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException {
            return customfunctiondata.getCommandDispatcher().execute(new ParseResults(this.parse.getContext().withSource(commandlistenerwrapper), this.parse.getReader(), this.parse.getExceptions()));
        }

        public String toString() {
            return this.parse.getReader().getString();
        }
    }

    public static class a {

        public static final CustomFunction.a NONE = new CustomFunction.a((MinecraftKey) null);
        @Nullable
        private final MinecraftKey id;
        private boolean resolved;
        private Optional<CustomFunction> function = Optional.empty();

        public a(@Nullable MinecraftKey minecraftkey) {
            this.id = minecraftkey;
        }

        public a(CustomFunction customfunction) {
            this.resolved = true;
            this.id = null;
            this.function = Optional.of(customfunction);
        }

        public Optional<CustomFunction> a(CustomFunctionData customfunctiondata) {
            if (!this.resolved) {
                if (this.id != null) {
                    this.function = customfunctiondata.a(this.id);
                }

                this.resolved = true;
            }

            return this.function;
        }

        @Nullable
        public MinecraftKey a() {
            return (MinecraftKey) this.function.map((customfunction) -> {
                return customfunction.id;
            }).orElse(this.id);
        }
    }

    public static class d implements CustomFunction.c {

        private final CustomFunction.a function;

        public d(CustomFunction customfunction) {
            this.function = new CustomFunction.a(customfunction);
        }

        @Override
        public void a(CustomFunctionData customfunctiondata, CommandListenerWrapper commandlistenerwrapper, Deque<CustomFunctionData.b> deque, int i, int j, @Nullable CustomFunctionData.c customfunctiondata_c) {
            SystemUtils.a(this.function.a(customfunctiondata), (customfunction) -> {
                CustomFunction.c[] acustomfunction_c = customfunction.b();

                if (customfunctiondata_c != null) {
                    customfunctiondata_c.a(j, customfunction.a(), acustomfunction_c.length);
                }

                int k = i - deque.size();
                int l = Math.min(acustomfunction_c.length, k);

                for (int i1 = l - 1; i1 >= 0; --i1) {
                    deque.addFirst(new CustomFunctionData.b(commandlistenerwrapper, j + 1, acustomfunction_c[i1]));
                }

            }, () -> {
                if (customfunctiondata_c != null) {
                    customfunctiondata_c.a(j, this.function.a(), -1);
                }

            });
        }

        public String toString() {
            return "function " + this.function.a();
        }
    }
}
