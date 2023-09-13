package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.ChatDeserializer;

public class CriterionConditionLight {

    public static final CriterionConditionLight a = new CriterionConditionLight(CriterionConditionValue.IntegerRange.e);
    private final CriterionConditionValue.IntegerRange b;

    private CriterionConditionLight(CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
        this.b = criterionconditionvalue_integerrange;
    }

    public boolean a(WorldServer worldserver, BlockPosition blockposition) {
        return this == CriterionConditionLight.a ? true : (!worldserver.p(blockposition) ? false : this.b.d(worldserver.getLightLevel(blockposition)));
    }

    public JsonElement a() {
        if (this == CriterionConditionLight.a) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("light", this.b.d());
            return jsonobject;
        }
    }

    public static CriterionConditionLight a(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "light");
            CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange = CriterionConditionValue.IntegerRange.a(jsonobject.get("light"));

            return new CriterionConditionLight(criterionconditionvalue_integerrange);
        } else {
            return CriterionConditionLight.a;
        }
    }
}
