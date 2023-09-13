package net.minecraft.server;

import com.google.common.collect.Lists;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class CustomFunction {

    private final CustomFunction.c[] a;
    private final MinecraftKey b;

    public CustomFunction(MinecraftKey minecraftkey, CustomFunction.c[] acustomfunction_c) {
        this.b = minecraftkey;
        this.a = acustomfunction_c;
    }

    public MinecraftKey a() {
        return this.b;
    }

    public CustomFunction.c[] b() {
        return this.a;
    }

    public static CustomFunction a(MinecraftKey minecraftkey, CustomFunctionData customfunctiondata, List<String> list) {
        ArrayList arraylist = Lists.newArrayListWithCapacity(list.size());

        for (int i = 0; i < list.size(); ++i) {
            String s = ((String) list.get(i)).trim();

            if (!s.startsWith("#") && !s.isEmpty()) {
                String[] astring = s.split(" ", 2);
                String s1 = astring[0];

                if (s1.startsWith("//")) {
                    throw new IllegalArgumentException("Unknown or invalid command \'" + s1 + "\' on line " + i + " (if you intended to make a comment, use \'#\' not \'//\')");
                }

                if (s1.startsWith("/") && s1.length() > 1) {
                    throw new IllegalArgumentException("Unknown or invalid command \'" + s1 + "\' on line " + i + " (did you mean \'" + s1.substring(1) + "\'? Do not use a preceding forwards slash.)");
                }

                try {
                    ParseResults parseresults = customfunctiondata.a().getCommandDispatcher().a().parse(s, customfunctiondata.f());

                    if (parseresults.getReader().canRead()) {
                        if (parseresults.getExceptions().size() == 1) {
                            throw (CommandSyntaxException) parseresults.getExceptions().values().iterator().next();
                        }

                        if (parseresults.getContext().getRange().isEmpty()) {
                            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(parseresults.getReader());
                        }

                        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(parseresults.getReader());
                    }

                    arraylist.add(new CustomFunction.b(parseresults));
                } catch (CommandSyntaxException commandsyntaxexception) {
                    throw new IllegalArgumentException("Whilst parsing command on line " + i + ": " + commandsyntaxexception.getMessage());
                }
            }
        }

        return new CustomFunction(minecraftkey, (CustomFunction.c[]) arraylist.toArray(new CustomFunction.c[arraylist.size()]));
    }

    public static class a {

        public static final CustomFunction.a a = new CustomFunction.a((MinecraftKey) null);
        @Nullable
        private final MinecraftKey b;
        private boolean c;
        private CustomFunction d;

        public a(@Nullable MinecraftKey minecraftkey) {
            this.b = minecraftkey;
        }

        public a(CustomFunction customfunction) {
            this.b = null;
            this.d = customfunction;
        }

        @Nullable
        public CustomFunction a(CustomFunctionData customfunctiondata) {
            if (!this.c) {
                if (this.b != null) {
                    this.d = customfunctiondata.a(this.b);
                }

                this.c = true;
            }

            return this.d;
        }

        @Nullable
        public MinecraftKey a() {
            return this.d != null ? this.d.b : this.b;
        }
    }

    public static class d implements CustomFunction.c {

        private final CustomFunction.a a;

        public d(CustomFunction customfunction) {
            this.a = new CustomFunction.a(customfunction);
        }

        public void a(CustomFunctionData customfunctiondata, CommandListenerWrapper commandlistenerwrapper, ArrayDeque<CustomFunctionData.a> arraydeque, int i) {
            CustomFunction customfunction = this.a.a(customfunctiondata);

            if (customfunction != null) {
                CustomFunction.c[] acustomfunction_c = customfunction.b();
                int j = i - arraydeque.size();
                int k = Math.min(acustomfunction_c.length, j);

                for (int l = k - 1; l >= 0; --l) {
                    arraydeque.addFirst(new CustomFunctionData.a(customfunctiondata, commandlistenerwrapper, acustomfunction_c[l]));
                }
            }

        }

        public String toString() {
            return "function " + this.a.a();
        }
    }

    public static class b implements CustomFunction.c {

        private final ParseResults<CommandListenerWrapper> a;

        public b(ParseResults<CommandListenerWrapper> parseresults) {
            this.a = parseresults;
        }

        public void a(CustomFunctionData customfunctiondata, CommandListenerWrapper commandlistenerwrapper, ArrayDeque<CustomFunctionData.a> arraydeque, int i) throws CommandSyntaxException {
            customfunctiondata.d().execute(new ParseResults(this.a.getContext().withSource(commandlistenerwrapper), this.a.getReader(), this.a.getExceptions()));
        }

        public String toString() {
            return this.a.getReader().getString();
        }
    }

    public interface c {

        void a(CustomFunctionData customfunctiondata, CommandListenerWrapper commandlistenerwrapper, ArrayDeque<CustomFunctionData.a> arraydeque, int i) throws CommandSyntaxException;
    }
}
