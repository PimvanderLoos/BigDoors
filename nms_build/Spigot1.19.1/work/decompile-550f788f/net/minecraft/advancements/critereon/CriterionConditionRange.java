package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.util.ChatDeserializer;

public class CriterionConditionRange {

    public static final CriterionConditionRange ANY = new CriterionConditionRange((Float) null, (Float) null);
    public static final SimpleCommandExceptionType ERROR_INTS_ONLY = new SimpleCommandExceptionType(IChatBaseComponent.translatable("argument.range.ints"));
    @Nullable
    private final Float min;
    @Nullable
    private final Float max;

    public CriterionConditionRange(@Nullable Float ofloat, @Nullable Float ofloat1) {
        this.min = ofloat;
        this.max = ofloat1;
    }

    public static CriterionConditionRange exactly(float f) {
        return new CriterionConditionRange(f, f);
    }

    public static CriterionConditionRange between(float f, float f1) {
        return new CriterionConditionRange(f, f1);
    }

    public static CriterionConditionRange atLeast(float f) {
        return new CriterionConditionRange(f, (Float) null);
    }

    public static CriterionConditionRange atMost(float f) {
        return new CriterionConditionRange((Float) null, f);
    }

    public boolean matches(float f) {
        return this.min != null && this.max != null && this.min > this.max && this.min > f && this.max < f ? false : (this.min != null && this.min > f ? false : this.max == null || this.max >= f);
    }

    public boolean matchesSqr(double d0) {
        return this.min != null && this.max != null && this.min > this.max && (double) (this.min * this.min) > d0 && (double) (this.max * this.max) < d0 ? false : (this.min != null && (double) (this.min * this.min) > d0 ? false : this.max == null || (double) (this.max * this.max) >= d0);
    }

    @Nullable
    public Float getMin() {
        return this.min;
    }

    @Nullable
    public Float getMax() {
        return this.max;
    }

    public JsonElement serializeToJson() {
        if (this == CriterionConditionRange.ANY) {
            return JsonNull.INSTANCE;
        } else if (this.min != null && this.max != null && this.min.equals(this.max)) {
            return new JsonPrimitive(this.min);
        } else {
            JsonObject jsonobject = new JsonObject();

            if (this.min != null) {
                jsonobject.addProperty("min", this.min);
            }

            if (this.max != null) {
                jsonobject.addProperty("max", this.min);
            }

            return jsonobject;
        }
    }

    public static CriterionConditionRange fromJson(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            if (ChatDeserializer.isNumberValue(jsonelement)) {
                float f = ChatDeserializer.convertToFloat(jsonelement, "value");

                return new CriterionConditionRange(f, f);
            } else {
                JsonObject jsonobject = ChatDeserializer.convertToJsonObject(jsonelement, "value");
                Float ofloat = jsonobject.has("min") ? ChatDeserializer.getAsFloat(jsonobject, "min") : null;
                Float ofloat1 = jsonobject.has("max") ? ChatDeserializer.getAsFloat(jsonobject, "max") : null;

                return new CriterionConditionRange(ofloat, ofloat1);
            }
        } else {
            return CriterionConditionRange.ANY;
        }
    }

    public static CriterionConditionRange fromReader(StringReader stringreader, boolean flag) throws CommandSyntaxException {
        return fromReader(stringreader, flag, (ofloat) -> {
            return ofloat;
        });
    }

    public static CriterionConditionRange fromReader(StringReader stringreader, boolean flag, Function<Float, Float> function) throws CommandSyntaxException {
        if (!stringreader.canRead()) {
            throw CriterionConditionValue.ERROR_EMPTY.createWithContext(stringreader);
        } else {
            int i = stringreader.getCursor();
            Float ofloat = optionallyFormat(readNumber(stringreader, flag), function);
            Float ofloat1;

            if (stringreader.canRead(2) && stringreader.peek() == '.' && stringreader.peek(1) == '.') {
                stringreader.skip();
                stringreader.skip();
                ofloat1 = optionallyFormat(readNumber(stringreader, flag), function);
                if (ofloat == null && ofloat1 == null) {
                    stringreader.setCursor(i);
                    throw CriterionConditionValue.ERROR_EMPTY.createWithContext(stringreader);
                }
            } else {
                if (!flag && stringreader.canRead() && stringreader.peek() == '.') {
                    stringreader.setCursor(i);
                    throw CriterionConditionRange.ERROR_INTS_ONLY.createWithContext(stringreader);
                }

                ofloat1 = ofloat;
            }

            if (ofloat == null && ofloat1 == null) {
                stringreader.setCursor(i);
                throw CriterionConditionValue.ERROR_EMPTY.createWithContext(stringreader);
            } else {
                return new CriterionConditionRange(ofloat, ofloat1);
            }
        }
    }

    @Nullable
    private static Float readNumber(StringReader stringreader, boolean flag) throws CommandSyntaxException {
        int i = stringreader.getCursor();

        while (stringreader.canRead() && isAllowedNumber(stringreader, flag)) {
            stringreader.skip();
        }

        String s = stringreader.getString().substring(i, stringreader.getCursor());

        if (s.isEmpty()) {
            return null;
        } else {
            try {
                return Float.parseFloat(s);
            } catch (NumberFormatException numberformatexception) {
                if (flag) {
                    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidDouble().createWithContext(stringreader, s);
                } else {
                    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().createWithContext(stringreader, s);
                }
            }
        }
    }

    private static boolean isAllowedNumber(StringReader stringreader, boolean flag) {
        char c0 = stringreader.peek();

        return (c0 < '0' || c0 > '9') && c0 != '-' ? (flag && c0 == '.' ? !stringreader.canRead(2) || stringreader.peek(1) != '.' : false) : true;
    }

    @Nullable
    private static Float optionallyFormat(@Nullable Float ofloat, Function<Float, Float> function) {
        return ofloat == null ? null : (Float) function.apply(ofloat);
    }
}
