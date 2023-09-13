package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class CustomFunction {

    private final CustomFunction.c[] a;

    public CustomFunction(CustomFunction.c[] acustomfunction_c) {
        this.a = acustomfunction_c;
    }

    public CustomFunction.c[] a() {
        return this.a;
    }

    public static CustomFunction a(CustomFunctionData customfunctiondata, List<String> list) {
        ArrayList arraylist = Lists.newArrayListWithCapacity(list.size());
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();

            s = s.trim();
            if (!s.startsWith("#") && !s.isEmpty()) {
                String[] astring = s.split(" ", 2);
                String s1 = astring[0];

                if (!customfunctiondata.a().getCommands().containsKey(s1)) {
                    if (s1.startsWith("//")) {
                        throw new IllegalArgumentException("Unknown or invalid command \'" + s1 + "\' (if you intended to make a comment, use \'#\' not \'//\')");
                    }

                    if (s1.startsWith("/") && s1.length() > 1) {
                        throw new IllegalArgumentException("Unknown or invalid command \'" + s1 + "\' (did you mean \'" + s1.substring(1) + "\'? Do not use a preceding forwards slash.)");
                    }

                    throw new IllegalArgumentException("Unknown or invalid command \'" + s1 + "\'");
                }

                arraylist.add(new CustomFunction.b(s));
            }
        }

        return new CustomFunction((CustomFunction.c[]) arraylist.toArray(new CustomFunction.c[arraylist.size()]));
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

        public String toString() {
            return String.valueOf(this.b);
        }
    }

    public static class d implements CustomFunction.c {

        private final CustomFunction.a a;

        public d(CustomFunction customfunction) {
            this.a = new CustomFunction.a(customfunction);
        }

        public void a(CustomFunctionData customfunctiondata, ICommandListener icommandlistener, ArrayDeque<CustomFunctionData.a> arraydeque, int i) {
            CustomFunction customfunction = this.a.a(customfunctiondata);

            if (customfunction != null) {
                CustomFunction.c[] acustomfunction_c = customfunction.a();
                int j = i - arraydeque.size();
                int k = Math.min(acustomfunction_c.length, j);

                for (int l = k - 1; l >= 0; --l) {
                    arraydeque.addFirst(new CustomFunctionData.a(customfunctiondata, icommandlistener, acustomfunction_c[l]));
                }
            }

        }

        public String toString() {
            return "/function " + this.a;
        }
    }

    public static class b implements CustomFunction.c {

        private final String a;

        public b(String s) {
            this.a = s;
        }

        public void a(CustomFunctionData customfunctiondata, ICommandListener icommandlistener, ArrayDeque<CustomFunctionData.a> arraydeque, int i) {
            customfunctiondata.a().a(icommandlistener, this.a);
        }

        public String toString() {
            return "/" + this.a;
        }
    }

    public interface c {

        void a(CustomFunctionData customfunctiondata, ICommandListener icommandlistener, ArrayDeque<CustomFunctionData.a> arraydeque, int i);
    }
}
