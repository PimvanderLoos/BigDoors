package net.minecraft.server;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;

public interface ArgumentCriterionValue<T extends CriterionConditionValue<?>> extends ArgumentType<T> {

    static default ArgumentCriterionValue.b a() {
        return new ArgumentCriterionValue.b();
    }

    public abstract static class c<T extends ArgumentCriterionValue<?>> implements ArgumentSerializer<T> {

        public c() {}

        public void a(T t0, PacketDataSerializer packetdataserializer) {}

        public void a(T t0, JsonObject jsonobject) {}
    }

    public static class a implements ArgumentCriterionValue<CriterionConditionValue.c> {

        private static final Collection<String> a = Arrays.asList(new String[] { "0..5.2", "0", "-5.4", "-100.76..", "..100"});

        public a() {}

        public CriterionConditionValue.c a(StringReader stringreader) throws CommandSyntaxException {
            return CriterionConditionValue.c.a(stringreader);
        }

        public Collection<String> getExamples() {
            return ArgumentCriterionValue.a.a;
        }

        public Object parse(StringReader stringreader) throws CommandSyntaxException {
            return this.a(stringreader);
        }

        public static class a extends ArgumentCriterionValue.c<ArgumentCriterionValue.a> {

            public a() {}

            public ArgumentCriterionValue.a a(PacketDataSerializer packetdataserializer) {
                return new ArgumentCriterionValue.a();
            }

            public ArgumentType b(PacketDataSerializer packetdataserializer) {
                return this.a(packetdataserializer);
            }
        }
    }

    public static class b implements ArgumentCriterionValue<CriterionConditionValue.d> {

        private static final Collection<String> a = Arrays.asList(new String[] { "0..5", "0", "-5", "-100..", "..100"});

        public b() {}

        public static CriterionConditionValue.d a(CommandContext<CommandListenerWrapper> commandcontext, String s) {
            return (CriterionConditionValue.d) commandcontext.getArgument(s, CriterionConditionValue.d.class);
        }

        public CriterionConditionValue.d a(StringReader stringreader) throws CommandSyntaxException {
            return CriterionConditionValue.d.a(stringreader);
        }

        public Collection<String> getExamples() {
            return ArgumentCriterionValue.b.a;
        }

        public Object parse(StringReader stringreader) throws CommandSyntaxException {
            return this.a(stringreader);
        }

        public static class a extends ArgumentCriterionValue.c<ArgumentCriterionValue.b> {

            public a() {}

            public ArgumentCriterionValue.b a(PacketDataSerializer packetdataserializer) {
                return new ArgumentCriterionValue.b();
            }

            public ArgumentType b(PacketDataSerializer packetdataserializer) {
                return this.a(packetdataserializer);
            }
        }
    }
}
