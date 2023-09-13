package net.minecraft.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;

public class CriterionConditionDistance {

    public static final CriterionConditionDistance a = new CriterionConditionDistance(CriterionConditionValue.a, CriterionConditionValue.a, CriterionConditionValue.a, CriterionConditionValue.a, CriterionConditionValue.a);
    private final CriterionConditionValue b;
    private final CriterionConditionValue c;
    private final CriterionConditionValue d;
    private final CriterionConditionValue e;
    private final CriterionConditionValue f;

    public CriterionConditionDistance(CriterionConditionValue criterionconditionvalue, CriterionConditionValue criterionconditionvalue1, CriterionConditionValue criterionconditionvalue2, CriterionConditionValue criterionconditionvalue3, CriterionConditionValue criterionconditionvalue4) {
        this.b = criterionconditionvalue;
        this.c = criterionconditionvalue1;
        this.d = criterionconditionvalue2;
        this.e = criterionconditionvalue3;
        this.f = criterionconditionvalue4;
    }

    public boolean a(double d0, double d1, double d2, double d3, double d4, double d5) {
        float f = (float) (d0 - d3);
        float f1 = (float) (d1 - d4);
        float f2 = (float) (d2 - d5);

        return this.b.a(MathHelper.e(f)) && this.c.a(MathHelper.e(f1)) && this.d.a(MathHelper.e(f2)) ? (!this.e.a((double) (f * f + f2 * f2)) ? false : this.f.a((double) (f * f + f1 * f1 + f2 * f2))) : false;
    }

    public static CriterionConditionDistance a(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "distance");
            CriterionConditionValue criterionconditionvalue = CriterionConditionValue.a(jsonobject.get("x"));
            CriterionConditionValue criterionconditionvalue1 = CriterionConditionValue.a(jsonobject.get("y"));
            CriterionConditionValue criterionconditionvalue2 = CriterionConditionValue.a(jsonobject.get("z"));
            CriterionConditionValue criterionconditionvalue3 = CriterionConditionValue.a(jsonobject.get("horizontal"));
            CriterionConditionValue criterionconditionvalue4 = CriterionConditionValue.a(jsonobject.get("absolute"));

            return new CriterionConditionDistance(criterionconditionvalue, criterionconditionvalue1, criterionconditionvalue2, criterionconditionvalue3, criterionconditionvalue4);
        } else {
            return CriterionConditionDistance.a;
        }
    }
}
