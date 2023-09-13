package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class IntRange {

    @Nullable
    final NumberProvider min;
    @Nullable
    final NumberProvider max;
    private final IntRange.b limiter;
    private final IntRange.a predicate;

    public Set<LootContextParameter<?>> getReferencedContextParams() {
        Builder<LootContextParameter<?>> builder = ImmutableSet.builder();

        if (this.min != null) {
            builder.addAll(this.min.getReferencedContextParams());
        }

        if (this.max != null) {
            builder.addAll(this.max.getReferencedContextParams());
        }

        return builder.build();
    }

    IntRange(@Nullable NumberProvider numberprovider, @Nullable NumberProvider numberprovider1) {
        this.min = numberprovider;
        this.max = numberprovider1;
        if (numberprovider == null) {
            if (numberprovider1 == null) {
                this.limiter = (loottableinfo, i) -> {
                    return i;
                };
                this.predicate = (loottableinfo, i) -> {
                    return true;
                };
            } else {
                this.limiter = (loottableinfo, i) -> {
                    return Math.min(numberprovider1.getInt(loottableinfo), i);
                };
                this.predicate = (loottableinfo, i) -> {
                    return i <= numberprovider1.getInt(loottableinfo);
                };
            }
        } else if (numberprovider1 == null) {
            this.limiter = (loottableinfo, i) -> {
                return Math.max(numberprovider.getInt(loottableinfo), i);
            };
            this.predicate = (loottableinfo, i) -> {
                return i >= numberprovider.getInt(loottableinfo);
            };
        } else {
            this.limiter = (loottableinfo, i) -> {
                return MathHelper.clamp(i, numberprovider.getInt(loottableinfo), numberprovider1.getInt(loottableinfo));
            };
            this.predicate = (loottableinfo, i) -> {
                return i >= numberprovider.getInt(loottableinfo) && i <= numberprovider1.getInt(loottableinfo);
            };
        }

    }

    public static IntRange exact(int i) {
        ConstantValue constantvalue = ConstantValue.exactly((float) i);

        return new IntRange(constantvalue, constantvalue);
    }

    public static IntRange range(int i, int j) {
        return new IntRange(ConstantValue.exactly((float) i), ConstantValue.exactly((float) j));
    }

    public static IntRange lowerBound(int i) {
        return new IntRange(ConstantValue.exactly((float) i), (NumberProvider) null);
    }

    public static IntRange upperBound(int i) {
        return new IntRange((NumberProvider) null, ConstantValue.exactly((float) i));
    }

    public int clamp(LootTableInfo loottableinfo, int i) {
        return this.limiter.apply(loottableinfo, i);
    }

    public boolean test(LootTableInfo loottableinfo, int i) {
        return this.predicate.test(loottableinfo, i);
    }

    @FunctionalInterface
    private interface b {

        int apply(LootTableInfo loottableinfo, int i);
    }

    @FunctionalInterface
    private interface a {

        boolean test(LootTableInfo loottableinfo, int i);
    }

    public static class c implements JsonDeserializer<IntRange>, JsonSerializer<IntRange> {

        public c() {}

        public IntRange deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) {
            if (jsonelement.isJsonPrimitive()) {
                return IntRange.exact(jsonelement.getAsInt());
            } else {
                JsonObject jsonobject = ChatDeserializer.convertToJsonObject(jsonelement, "value");
                NumberProvider numberprovider = jsonobject.has("min") ? (NumberProvider) ChatDeserializer.getAsObject(jsonobject, "min", jsondeserializationcontext, NumberProvider.class) : null;
                NumberProvider numberprovider1 = jsonobject.has("max") ? (NumberProvider) ChatDeserializer.getAsObject(jsonobject, "max", jsondeserializationcontext, NumberProvider.class) : null;

                return new IntRange(numberprovider, numberprovider1);
            }
        }

        public JsonElement serialize(IntRange intrange, Type type, JsonSerializationContext jsonserializationcontext) {
            JsonObject jsonobject = new JsonObject();

            if (Objects.equals(intrange.max, intrange.min)) {
                return jsonserializationcontext.serialize(intrange.min);
            } else {
                if (intrange.max != null) {
                    jsonobject.add("max", jsonserializationcontext.serialize(intrange.max));
                }

                if (intrange.min != null) {
                    jsonobject.add("min", jsonserializationcontext.serialize(intrange.min));
                }

                return jsonobject;
            }
        }
    }
}
