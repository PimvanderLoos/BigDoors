package net.minecraft.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;

public class CriterionConditionValue {

    public static final CriterionConditionValue a = new CriterionConditionValue((Float) null, (Float) null);
    private final Float b;
    private final Float c;

    public CriterionConditionValue(@Nullable Float ofloat, @Nullable Float ofloat1) {
        this.b = ofloat;
        this.c = ofloat1;
    }

    public boolean a(float f) {
        return this.b != null && this.b.floatValue() > f ? false : this.c == null || this.c.floatValue() >= f;
    }

    public boolean a(double d0) {
        return this.b != null && (double) (this.b.floatValue() * this.b.floatValue()) > d0 ? false : this.c == null || (double) (this.c.floatValue() * this.c.floatValue()) >= d0;
    }

    public static CriterionConditionValue a(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            if (ChatDeserializer.b(jsonelement)) {
                float f = ChatDeserializer.e(jsonelement, "value");

                return new CriterionConditionValue(Float.valueOf(f), Float.valueOf(f));
            } else {
                JsonObject jsonobject = ChatDeserializer.m(jsonelement, "value");
                Float ofloat = jsonobject.has("min") ? Float.valueOf(ChatDeserializer.l(jsonobject, "min")) : null;
                Float ofloat1 = jsonobject.has("max") ? Float.valueOf(ChatDeserializer.l(jsonobject, "max")) : null;

                return new CriterionConditionValue(ofloat, ofloat1);
            }
        } else {
            return CriterionConditionValue.a;
        }
    }
}
