package net.minecraft.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public abstract class CriterionConditionValue<T extends Number> {

    public static final SimpleCommandExceptionType a = new SimpleCommandExceptionType(new ChatMessage("argument.range.empty", new Object[0]));
    public static final SimpleCommandExceptionType b = new SimpleCommandExceptionType(new ChatMessage("argument.range.swapped", new Object[0]));
    protected final T c;
    protected final T d;

    protected CriterionConditionValue(@Nullable T t0, @Nullable T t1) {
        this.c = t0;
        this.d = t1;
    }

    @Nullable
    public T a() {
        return this.c;
    }

    @Nullable
    public T b() {
        return this.d;
    }

    public boolean c() {
        return this.c == null && this.d == null;
    }

    public JsonElement d() {
        if (this.c()) {
            return JsonNull.INSTANCE;
        } else if (this.c != null && this.c.equals(this.d)) {
            return new JsonPrimitive(this.c);
        } else {
            JsonObject jsonobject = new JsonObject();

            if (this.c != null) {
                jsonobject.addProperty("min", this.c);
            }

            if (this.d != null) {
                jsonobject.addProperty("max", this.c);
            }

            return jsonobject;
        }
    }

    protected static <T extends Number, R extends CriterionConditionValue<T>> R a(@Nullable JsonElement jsonelement, R r0, BiFunction<JsonElement, String, T> bifunction, CriterionConditionValue.a<T, R> criterionconditionvalue_a) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            if (ChatDeserializer.b(jsonelement)) {
                Number number = (Number) bifunction.apply(jsonelement, "value");

                return criterionconditionvalue_a.create(number, number);
            } else {
                JsonObject jsonobject = ChatDeserializer.m(jsonelement, "value");
                Number number1 = jsonobject.has("min") ? (Number) bifunction.apply(jsonobject.get("min"), "min") : null;
                Number number2 = jsonobject.has("max") ? (Number) bifunction.apply(jsonobject.get("max"), "max") : null;

                return criterionconditionvalue_a.create(number1, number2);
            }
        } else {
            return r0;
        }
    }

    protected static <T extends Number, R extends CriterionConditionValue<T>> R a(StringReader stringreader, CriterionConditionValue.b<T, R> criterionconditionvalue_b, Function<String, T> function, Supplier<DynamicCommandExceptionType> supplier, Function<T, T> function1) throws CommandSyntaxException {
        if (!stringreader.canRead()) {
            throw CriterionConditionValue.a.createWithContext(stringreader);
        } else {
            int i = stringreader.getCursor();

            try {
                Number number = (Number) a(a(stringreader, function, supplier), function1);
                Number number1;

                if (stringreader.canRead(2) && stringreader.peek() == 46 && stringreader.peek(1) == 46) {
                    stringreader.skip();
                    stringreader.skip();
                    number1 = (Number) a(a(stringreader, function, supplier), function1);
                    if (number == null && number1 == null) {
                        throw CriterionConditionValue.a.createWithContext(stringreader);
                    }
                } else {
                    number1 = number;
                }

                if (number == null && number1 == null) {
                    throw CriterionConditionValue.a.createWithContext(stringreader);
                } else {
                    return criterionconditionvalue_b.create(stringreader, number, number1);
                }
            } catch (CommandSyntaxException commandsyntaxexception) {
                stringreader.setCursor(i);
                throw new CommandSyntaxException(commandsyntaxexception.getType(), commandsyntaxexception.getRawMessage(), commandsyntaxexception.getInput(), i);
            }
        }
    }

    @Nullable
    private static <T extends Number> T a(StringReader stringreader, Function<String, T> function, Supplier<DynamicCommandExceptionType> supplier) throws CommandSyntaxException {
        int i = stringreader.getCursor();

        while (stringreader.canRead() && a(stringreader)) {
            stringreader.skip();
        }

        String s = stringreader.getString().substring(i, stringreader.getCursor());

        if (s.isEmpty()) {
            return null;
        } else {
            try {
                return (Number) function.apply(s);
            } catch (NumberFormatException numberformatexception) {
                throw ((DynamicCommandExceptionType) supplier.get()).createWithContext(stringreader, s);
            }
        }
    }

    private static boolean a(StringReader stringreader) {
        char c0 = stringreader.peek();

        return (c0 < 48 || c0 > 57) && c0 != 45 ? (c0 != 46 ? false : !stringreader.canRead(2) || stringreader.peek(1) != 46) : true;
    }

    @Nullable
    private static <T> T a(@Nullable T t0, Function<T, T> function) {
        return t0 == null ? null : function.apply(t0);
    }

    @FunctionalInterface
    public interface b<T extends Number, R extends CriterionConditionValue<T>> {

        R create(StringReader stringreader, @Nullable T t0, @Nullable T t1) throws CommandSyntaxException;
    }

    @FunctionalInterface
    public interface a<T extends Number, R extends CriterionConditionValue<T>> {

        R create(@Nullable T t0, @Nullable T t1);
    }

    public static class c extends CriterionConditionValue<Float> {

        public static final CriterionConditionValue.c e = new CriterionConditionValue.c((Float) null, (Float) null);
        private final Double f;
        private final Double g;

        private static CriterionConditionValue.c a(StringReader stringreader, @Nullable Float ofloat, @Nullable Float ofloat1) throws CommandSyntaxException {
            if (ofloat != null && ofloat1 != null && ofloat.floatValue() > ofloat1.floatValue()) {
                throw CriterionConditionValue.c.b.createWithContext(stringreader);
            } else {
                return new CriterionConditionValue.c(ofloat, ofloat1);
            }
        }

        @Nullable
        private static Double a(@Nullable Float ofloat) {
            return ofloat == null ? null : Double.valueOf(ofloat.doubleValue() * ofloat.doubleValue());
        }

        private c(@Nullable Float ofloat, @Nullable Float ofloat1) {
            super(ofloat, ofloat1);
            this.f = a(ofloat);
            this.g = a(ofloat1);
        }

        public static CriterionConditionValue.c b(float f) {
            return new CriterionConditionValue.c(Float.valueOf(f), (Float) null);
        }

        public boolean d(float f) {
            return this.c != null && ((Float) this.c).floatValue() > f ? false : this.d == null || ((Float) this.d).floatValue() >= f;
        }

        public boolean a(double d0) {
            return this.f != null && this.f.doubleValue() > d0 ? false : this.g == null || this.g.doubleValue() >= d0;
        }

        public static CriterionConditionValue.c a(@Nullable JsonElement jsonelement) {
            return (CriterionConditionValue.c) a(jsonelement, CriterionConditionValue.c.e, ChatDeserializer::e, CriterionConditionValue.c::new);
        }

        public static CriterionConditionValue.c a(StringReader stringreader) throws CommandSyntaxException {
            return a(stringreader, (ofloat) -> {
                return ofloat;
            });
        }

        public static CriterionConditionValue.c a(StringReader stringreader, Function<Float, Float> function) throws CommandSyntaxException {
            CriterionConditionValue.b criterionconditionvalue_b = CriterionConditionValue.c::a;
            Function function1 = Float::parseFloat;
            BuiltInExceptionProvider builtinexceptionprovider = CommandSyntaxException.BUILT_IN_EXCEPTIONS;

            CommandSyntaxException.BUILT_IN_EXCEPTIONS.getClass();
            return (CriterionConditionValue.c) a(stringreader, criterionconditionvalue_b, function1, builtinexceptionprovider::readerInvalidFloat, function);
        }
    }

    public static class d extends CriterionConditionValue<Integer> {

        public static final CriterionConditionValue.d e = new CriterionConditionValue.d((Integer) null, (Integer) null);
        private final Long f;
        private final Long g;

        private static CriterionConditionValue.d a(StringReader stringreader, @Nullable Integer integer, @Nullable Integer integer1) throws CommandSyntaxException {
            if (integer != null && integer1 != null && integer.intValue() > integer1.intValue()) {
                throw CriterionConditionValue.d.b.createWithContext(stringreader);
            } else {
                return new CriterionConditionValue.d(integer, integer1);
            }
        }

        @Nullable
        private static Long a(@Nullable Integer integer) {
            return integer == null ? null : Long.valueOf(integer.longValue() * integer.longValue());
        }

        private d(@Nullable Integer integer, @Nullable Integer integer1) {
            super(integer, integer1);
            this.f = a(integer);
            this.g = a(integer1);
        }

        public static CriterionConditionValue.d a(int i) {
            return new CriterionConditionValue.d(Integer.valueOf(i), Integer.valueOf(i));
        }

        public static CriterionConditionValue.d b(int i) {
            return new CriterionConditionValue.d(Integer.valueOf(i), (Integer) null);
        }

        public boolean d(int i) {
            return this.c != null && ((Integer) this.c).intValue() > i ? false : this.d == null || ((Integer) this.d).intValue() >= i;
        }

        public static CriterionConditionValue.d a(@Nullable JsonElement jsonelement) {
            return (CriterionConditionValue.d) a(jsonelement, CriterionConditionValue.d.e, ChatDeserializer::g, CriterionConditionValue.d::new);
        }

        public static CriterionConditionValue.d a(StringReader stringreader) throws CommandSyntaxException {
            return a(stringreader, (integer) -> {
                return integer;
            });
        }

        public static CriterionConditionValue.d a(StringReader stringreader, Function<Integer, Integer> function) throws CommandSyntaxException {
            CriterionConditionValue.b criterionconditionvalue_b = CriterionConditionValue.d::a;
            Function function1 = Integer::parseInt;
            BuiltInExceptionProvider builtinexceptionprovider = CommandSyntaxException.BUILT_IN_EXCEPTIONS;

            CommandSyntaxException.BUILT_IN_EXCEPTIONS.getClass();
            return (CriterionConditionValue.d) a(stringreader, criterionconditionvalue_b, function1, builtinexceptionprovider::readerInvalidInt, function);
        }
    }
}
