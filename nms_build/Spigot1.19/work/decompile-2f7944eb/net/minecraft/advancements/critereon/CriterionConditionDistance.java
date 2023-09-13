package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.util.MathHelper;

public class CriterionConditionDistance {

    public static final CriterionConditionDistance ANY = new CriterionConditionDistance(CriterionConditionValue.DoubleRange.ANY, CriterionConditionValue.DoubleRange.ANY, CriterionConditionValue.DoubleRange.ANY, CriterionConditionValue.DoubleRange.ANY, CriterionConditionValue.DoubleRange.ANY);
    private final CriterionConditionValue.DoubleRange x;
    private final CriterionConditionValue.DoubleRange y;
    private final CriterionConditionValue.DoubleRange z;
    private final CriterionConditionValue.DoubleRange horizontal;
    private final CriterionConditionValue.DoubleRange absolute;

    public CriterionConditionDistance(CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange, CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange1, CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange2, CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange3, CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange4) {
        this.x = criterionconditionvalue_doublerange;
        this.y = criterionconditionvalue_doublerange1;
        this.z = criterionconditionvalue_doublerange2;
        this.horizontal = criterionconditionvalue_doublerange3;
        this.absolute = criterionconditionvalue_doublerange4;
    }

    public static CriterionConditionDistance horizontal(CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange) {
        return new CriterionConditionDistance(CriterionConditionValue.DoubleRange.ANY, CriterionConditionValue.DoubleRange.ANY, CriterionConditionValue.DoubleRange.ANY, criterionconditionvalue_doublerange, CriterionConditionValue.DoubleRange.ANY);
    }

    public static CriterionConditionDistance vertical(CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange) {
        return new CriterionConditionDistance(CriterionConditionValue.DoubleRange.ANY, criterionconditionvalue_doublerange, CriterionConditionValue.DoubleRange.ANY, CriterionConditionValue.DoubleRange.ANY, CriterionConditionValue.DoubleRange.ANY);
    }

    public static CriterionConditionDistance absolute(CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange) {
        return new CriterionConditionDistance(CriterionConditionValue.DoubleRange.ANY, CriterionConditionValue.DoubleRange.ANY, CriterionConditionValue.DoubleRange.ANY, CriterionConditionValue.DoubleRange.ANY, criterionconditionvalue_doublerange);
    }

    public boolean matches(double d0, double d1, double d2, double d3, double d4, double d5) {
        float f = (float) (d0 - d3);
        float f1 = (float) (d1 - d4);
        float f2 = (float) (d2 - d5);

        return this.x.matches((double) MathHelper.abs(f)) && this.y.matches((double) MathHelper.abs(f1)) && this.z.matches((double) MathHelper.abs(f2)) ? (!this.horizontal.matchesSqr((double) (f * f + f2 * f2)) ? false : this.absolute.matchesSqr((double) (f * f + f1 * f1 + f2 * f2))) : false;
    }

    public static CriterionConditionDistance fromJson(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.convertToJsonObject(jsonelement, "distance");
            CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange = CriterionConditionValue.DoubleRange.fromJson(jsonobject.get("x"));
            CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange1 = CriterionConditionValue.DoubleRange.fromJson(jsonobject.get("y"));
            CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange2 = CriterionConditionValue.DoubleRange.fromJson(jsonobject.get("z"));
            CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange3 = CriterionConditionValue.DoubleRange.fromJson(jsonobject.get("horizontal"));
            CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange4 = CriterionConditionValue.DoubleRange.fromJson(jsonobject.get("absolute"));

            return new CriterionConditionDistance(criterionconditionvalue_doublerange, criterionconditionvalue_doublerange1, criterionconditionvalue_doublerange2, criterionconditionvalue_doublerange3, criterionconditionvalue_doublerange4);
        } else {
            return CriterionConditionDistance.ANY;
        }
    }

    public JsonElement serializeToJson() {
        if (this == CriterionConditionDistance.ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("x", this.x.serializeToJson());
            jsonobject.add("y", this.y.serializeToJson());
            jsonobject.add("z", this.z.serializeToJson());
            jsonobject.add("horizontal", this.horizontal.serializeToJson());
            jsonobject.add("absolute", this.absolute.serializeToJson());
            return jsonobject;
        }
    }
}
