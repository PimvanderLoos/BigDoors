package net.minecraft.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;

public class CriterionConditionDistance {

    public static final CriterionConditionDistance a = new CriterionConditionDistance(CriterionConditionValue.c.e, CriterionConditionValue.c.e, CriterionConditionValue.c.e, CriterionConditionValue.c.e, CriterionConditionValue.c.e);
    private final CriterionConditionValue.c b;
    private final CriterionConditionValue.c c;
    private final CriterionConditionValue.c d;
    private final CriterionConditionValue.c e;
    private final CriterionConditionValue.c f;

    public CriterionConditionDistance(CriterionConditionValue.c criterionconditionvalue_c, CriterionConditionValue.c criterionconditionvalue_c1, CriterionConditionValue.c criterionconditionvalue_c2, CriterionConditionValue.c criterionconditionvalue_c3, CriterionConditionValue.c criterionconditionvalue_c4) {
        this.b = criterionconditionvalue_c;
        this.c = criterionconditionvalue_c1;
        this.d = criterionconditionvalue_c2;
        this.e = criterionconditionvalue_c3;
        this.f = criterionconditionvalue_c4;
    }

    public static CriterionConditionDistance a(CriterionConditionValue.c criterionconditionvalue_c) {
        return new CriterionConditionDistance(CriterionConditionValue.c.e, CriterionConditionValue.c.e, CriterionConditionValue.c.e, criterionconditionvalue_c, CriterionConditionValue.c.e);
    }

    public static CriterionConditionDistance b(CriterionConditionValue.c criterionconditionvalue_c) {
        return new CriterionConditionDistance(CriterionConditionValue.c.e, criterionconditionvalue_c, CriterionConditionValue.c.e, CriterionConditionValue.c.e, CriterionConditionValue.c.e);
    }

    public boolean a(double d0, double d1, double d2, double d3, double d4, double d5) {
        float f = (float) (d0 - d3);
        float f1 = (float) (d1 - d4);
        float f2 = (float) (d2 - d5);

        return this.b.d(MathHelper.e(f)) && this.c.d(MathHelper.e(f1)) && this.d.d(MathHelper.e(f2)) ? (!this.e.a((double) (f * f + f2 * f2)) ? false : this.f.a((double) (f * f + f1 * f1 + f2 * f2))) : false;
    }

    public static CriterionConditionDistance a(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "distance");
            CriterionConditionValue.c criterionconditionvalue_c = CriterionConditionValue.c.a(jsonobject.get("x"));
            CriterionConditionValue.c criterionconditionvalue_c1 = CriterionConditionValue.c.a(jsonobject.get("y"));
            CriterionConditionValue.c criterionconditionvalue_c2 = CriterionConditionValue.c.a(jsonobject.get("z"));
            CriterionConditionValue.c criterionconditionvalue_c3 = CriterionConditionValue.c.a(jsonobject.get("horizontal"));
            CriterionConditionValue.c criterionconditionvalue_c4 = CriterionConditionValue.c.a(jsonobject.get("absolute"));

            return new CriterionConditionDistance(criterionconditionvalue_c, criterionconditionvalue_c1, criterionconditionvalue_c2, criterionconditionvalue_c3, criterionconditionvalue_c4);
        } else {
            return CriterionConditionDistance.a;
        }
    }

    public JsonElement a() {
        if (this == CriterionConditionDistance.a) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("x", this.b.d());
            jsonobject.add("y", this.c.d());
            jsonobject.add("z", this.d.d());
            jsonobject.add("horizontal", this.e.d());
            jsonobject.add("absolute", this.f.d());
            return jsonobject;
        }
    }
}
