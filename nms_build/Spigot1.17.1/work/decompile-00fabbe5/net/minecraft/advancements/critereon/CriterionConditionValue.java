package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.util.ChatDeserializer;

public abstract class CriterionConditionValue<T extends Number> {

    public static final SimpleCommandExceptionType ERROR_EMPTY = new SimpleCommandExceptionType(new ChatMessage("argument.range.empty"));
    public static final SimpleCommandExceptionType ERROR_SWAPPED = new SimpleCommandExceptionType(new ChatMessage("argument.range.swapped"));
    protected final T min;
    protected final T max;

    protected CriterionConditionValue(@Nullable T t0, @Nullable T t1) {
        this.min = t0;
        this.max = t1;
    }

    @Nullable
    public T a() {
        return this.min;
    }

    @Nullable
    public T b() {
        return this.max;
    }

    public boolean c() {
        return this.min == null && this.max == null;
    }

    public JsonElement d() {
        if (this.c()) {
            return JsonNull.INSTANCE;
        } else if (this.min != null && this.min.equals(this.max)) {
            return new JsonPrimitive(this.min);
        } else {
            JsonObject jsonobject = new JsonObject();

            if (this.min != null) {
                jsonobject.addProperty("min", this.min);
            }

            if (this.max != null) {
                jsonobject.addProperty("max", this.max);
            }

            return jsonobject;
        }
    }

    protected static <T extends Number, R extends CriterionConditionValue<T>> R a(@Nullable JsonElement jsonelement, R r0, BiFunction<JsonElement, String, T> bifunction, CriterionConditionValue.a<T, R> criterionconditionvalue_a) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            if (ChatDeserializer.b(jsonelement)) {
                T t0 = (Number) bifunction.apply(jsonelement, "value");

                return criterionconditionvalue_a.create(t0, t0);
            } else {
                JsonObject jsonobject = ChatDeserializer.m(jsonelement, "value");
                T t1 = jsonobject.has("min") ? (Number) bifunction.apply(jsonobject.get("min"), "min") : null;
                T t2 = jsonobject.has("max") ? (Number) bifunction.apply(jsonobject.get("max"), "max") : null;

                return criterionconditionvalue_a.create(t1, t2);
            }
        } else {
            return r0;
        }
    }

    protected static <T extends Number, R extends CriterionConditionValue<T>> R a(StringReader stringreader, CriterionConditionValue.b<T, R> criterionconditionvalue_b, Function<String, T> function, Supplier<DynamicCommandExceptionType> supplier, Function<T, T> function1) throws CommandSyntaxException {
        if (!stringreader.canRead()) {
            throw CriterionConditionValue.ERROR_EMPTY.createWithContext(stringreader);
        } else {
            int i = stringreader.getCursor();

            try {
                T t0 = (Number) a(a(stringreader, function, supplier), function1);
                Number number;

                if (stringreader.canRead(2) && stringreader.peek() == '.' && stringreader.peek(1) == '.') {
                    stringreader.skip();
                    stringreader.skip();
                    number = (Number) a(a(stringreader, function, supplier), function1);
                    if (t0 == null && number == null) {
                        throw CriterionConditionValue.ERROR_EMPTY.createWithContext(stringreader);
                    }
                } else {
                    number = t0;
                }

                if (t0 == null && number == null) {
                    throw CriterionConditionValue.ERROR_EMPTY.createWithContext(stringreader);
                } else {
                    return criterionconditionvalue_b.create(stringreader, t0, number);
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

        return (c0 < '0' || c0 > '9') && c0 != '-' ? (c0 != '.' ? false : !stringreader.canRead(2) || stringreader.peek(1) != '.') : true;
    }

    @Nullable
    private static <T> T a(@Nullable T t0, Function<T, T> function) {
        return t0 == null ? null : function.apply(t0);
    }

    @FunctionalInterface
    protected interface a<T extends Number, R extends CriterionConditionValue<T>> {

        R create(@Nullable T t0, @Nullable T t1);
    }

    @FunctionalInterface
    protected interface b<T extends Number, R extends CriterionConditionValue<T>> {

        R create(StringReader stringreader, @Nullable T t0, @Nullable T t1) throws CommandSyntaxException;
    }

    public static class DoubleRange extends CriterionConditionValue<Double> {

        public static final CriterionConditionValue.DoubleRange ANY = new CriterionConditionValue.DoubleRange((Double) null, (Double) null);
        private final Double minSq;
        private final Double maxSq;

        private static CriterionConditionValue.DoubleRange a(StringReader stringreader, @Nullable Double odouble, @Nullable Double odouble1) throws CommandSyntaxException {
            if (odouble != null && odouble1 != null && odouble > odouble1) {
                throw CriterionConditionValue.DoubleRange.ERROR_SWAPPED.createWithContext(stringreader);
            } else {
                return new CriterionConditionValue.DoubleRange(odouble, odouble1);
            }
        }

        @Nullable
        private static Double a(@Nullable Double odouble) {
            return odouble == null ? null : odouble * odouble;
        }

        private DoubleRange(@Nullable Double odouble, @Nullable Double odouble1) {
            super(odouble, odouble1);
            this.minSq = a(odouble);
            this.maxSq = a(odouble1);
        }

        public static CriterionConditionValue.DoubleRange a(double d0) {
            return new CriterionConditionValue.DoubleRange(d0, d0);
        }

        public static CriterionConditionValue.DoubleRange a(double d0, double d1) {
            return new CriterionConditionValue.DoubleRange(d0, d1);
        }

        public static CriterionConditionValue.DoubleRange b(double d0) {
            return new CriterionConditionValue.DoubleRange(d0, (Double) null);
        }

        public static CriterionConditionValue.DoubleRange c(double d0) {
            return new CriterionConditionValue.DoubleRange((Double) null, d0);
        }

        public boolean d(double d0) {
            return this.min != null && (Double) this.min > d0 ? false : this.max == null || (Double) this.max >= d0;
        }

        public boolean e(double d0) {
            return this.minSq != null && this.minSq > d0 ? false : this.maxSq == null || this.maxSq >= d0;
        }

        public static CriterionConditionValue.DoubleRange a(@Nullable JsonElement jsonelement) {
            return (CriterionConditionValue.DoubleRange) a(jsonelement, CriterionConditionValue.DoubleRange.ANY, ChatDeserializer::d, CriterionConditionValue.DoubleRange::new);
        }

        public static CriterionConditionValue.DoubleRange a(StringReader stringreader) throws CommandSyntaxException {
            return a(stringreader, (odouble) -> {
                return odouble;
            });
        }

        public static CriterionConditionValue.DoubleRange a(StringReader stringreader, Function<Double, Double> function) throws CommandSyntaxException {
            CriterionConditionValue.b criterionconditionvalue_b = CriterionConditionValue.DoubleRange::a;
            Function function1 = Double::parseDouble;
            BuiltInExceptionProvider builtinexceptionprovider = CommandSyntaxException.BUILT_IN_EXCEPTIONS;

            Objects.requireNonNull(builtinexceptionprovider);
            return (CriterionConditionValue.DoubleRange) a(stringreader, criterionconditionvalue_b, function1, builtinexceptionprovider::readerInvalidDouble, function);
        }
    }

    public static class IntegerRange extends CriterionConditionValue<Integer> {

        public static final CriterionConditionValue.IntegerRange ANY = new CriterionConditionValue.IntegerRange((Integer) null, (Integer) null);
        private final Long minSq;
        private final Long maxSq;

        private static CriterionConditionValue.IntegerRange a(StringReader stringreader, @Nullable Integer integer, @Nullable Integer integer1) throws CommandSyntaxException {
            if (integer != null && integer1 != null && integer > integer1) {
                throw CriterionConditionValue.IntegerRange.ERROR_SWAPPED.createWithContext(stringreader);
            } else {
                return new CriterionConditionValue.IntegerRange(integer, integer1);
            }
        }

        @Nullable
        private static Long a(@Nullable Integer integer) {
            return integer == null ? null : integer.longValue() * integer.longValue();
        }

        private IntegerRange(@Nullable Integer integer, @Nullable Integer integer1) {
            super(integer, integer1);
            this.minSq = a(integer);
            this.maxSq = a(integer1);
        }

        public static CriterionConditionValue.IntegerRange a(int i) {
            return new CriterionConditionValue.IntegerRange(i, i);
        }

        public static CriterionConditionValue.IntegerRange a(int i, int j) {
            return new CriterionConditionValue.IntegerRange(i, j);
        }

        public static CriterionConditionValue.IntegerRange b(int i) {
            return new CriterionConditionValue.IntegerRange(i, (Integer) null);
        }

        public static CriterionConditionValue.IntegerRange c(int i) {
            return new CriterionConditionValue.IntegerRange((Integer) null, i);
        }

        public boolean d(int i) {
            return this.min != null && (Integer) this.min > i ? false : this.max == null || (Integer) this.max >= i;
        }

        public boolean a(long i) {
            return this.minSq != null && this.minSq > i ? false : this.maxSq == null || this.maxSq >= i;
        }

        public static CriterionConditionValue.IntegerRange a(@Nullable JsonElement jsonelement) {
            return (CriterionConditionValue.IntegerRange) a(jsonelement, CriterionConditionValue.IntegerRange.ANY, ChatDeserializer::g, CriterionConditionValue.IntegerRange::new);
        }

        public static CriterionConditionValue.IntegerRange a(StringReader stringreader) throws CommandSyntaxException {
            return a(stringreader, (integer) -> {
                return integer;
            });
        }

        public static CriterionConditionValue.IntegerRange a(StringReader stringreader, Function<Integer, Integer> function) throws CommandSyntaxException {
            CriterionConditionValue.b criterionconditionvalue_b = CriterionConditionValue.IntegerRange::a;
            Function function1 = Integer::parseInt;
            BuiltInExceptionProvider builtinexceptionprovider = CommandSyntaxException.BUILT_IN_EXCEPTIONS;

            Objects.requireNonNull(builtinexceptionprovider);
            return (CriterionConditionValue.IntegerRange) a(stringreader, criterionconditionvalue_b, function1, builtinexceptionprovider::readerInvalidInt, function);
        }
    }
}
